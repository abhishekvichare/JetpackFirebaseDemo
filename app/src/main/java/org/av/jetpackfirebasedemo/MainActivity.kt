package org.av.jetpackfirebasedemo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import org.av.jetpackfirebasedemo.ui.theme.JetpackFirebasedemoTheme
import org.av.jetpackfirebasedemo.util.LoadingState
import org.av.jetpackfirebasedemo.util.LoadingState.Companion.LOADING
import org.av.jetpackfirebasedemo.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackFirebasedemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    GoogleSignInComponent(loginViewModel = loginViewModel)
                }
            }
        }
    }
}

@Composable
fun GoogleSignInComponent(loginViewModel: LoginViewModel) {

    val state by loginViewModel.loadingState.collectAsState()

    // Equivalent of onActivityResult
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                loginViewModel.signWithCredential(credential)
            } catch (e: ApiException) {
                Log.e("TAG", "Google sign in failed", e)
            }
        }

    val context = LocalContext.current
    val token = stringResource(R.string.default_web_client_id)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            OutlinedButton(border = ButtonDefaults.outlinedBorder.copy(width = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp), onClick = {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(token)
                        .requestEmail()
                        .build()

                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    launcher.launch(googleSignInClient.signInIntent)
                },
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (state == LOADING) Arrangement.Center else Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            if (state == LOADING) {
                                CircularProgressIndicator()
                            } else {
                                Icon(
                                    tint = Color.Unspecified,
                                    painter = painterResource(id = R.drawable.googleg_standard_color_18),
                                    contentDescription = null,
                                )
                                Text(
                                    style = MaterialTheme.typography.button,
                                    color = MaterialTheme.colors.onSurface,
                                    text = "Sign in with Google"
                                )
                                Icon(
                                    tint = Color.Transparent,
                                    imageVector = Icons.Default.MailOutline,
                                    contentDescription = null,
                                )
                            }
                        }
                    )
                })

            when (state.status) {
                LoadingState.Status.SUCCESS -> {
                    Text(text = "Success")
                }
                LoadingState.Status.FAILED -> {
                    Text(text = state.msg ?: "Error")
                }
                LoadingState.Status.LOGGED_IN -> {
                    Text(text = "Already Logged In")
                }
                else -> {
                }
            }
        }
    )

}