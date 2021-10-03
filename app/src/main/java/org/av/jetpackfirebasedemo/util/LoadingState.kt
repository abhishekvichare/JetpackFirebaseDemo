package org.av.jetpackfirebasedemo.util

data class LoadingState private constructor(val status: Status, val msg: String? = null) {
  companion object {
    val LOADED = LoadingState(Status.SUCCESS)
    val IDLE = LoadingState(Status.IDLE)
    val LOADING = LoadingState(Status.RUNNING)
    val LOGGED_IN = LoadingState(Status.LOGGED_IN)
    fun error(msg: String?) = LoadingState(Status.FAILED, msg)
  }

  enum class Status {
    RUNNING,
    SUCCESS,
    FAILED,
    IDLE,
    LOGGED_IN
  }
}