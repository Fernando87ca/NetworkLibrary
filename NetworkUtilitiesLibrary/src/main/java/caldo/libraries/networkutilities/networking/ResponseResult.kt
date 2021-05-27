package caldo.libraries.networkutilities.networking

sealed class ResponseResult<out T> {
    data class Success<T>(val data: T) : ResponseResult<T>()
    data class Error(val error: Exception) : ResponseResult<Nothing>()

    companion object {
        inline fun <T> create(execute: () -> T): ResponseResult<T> {
            return try {
                val result = execute.invoke()
                Success(result)
            } catch (e: Exception) {
                Error(e)
            }
        }
    }

    fun <T> andThen(function: () -> ResponseResult<T>): ResponseResult<T> {
        return when (val result = this) {
            is Success -> function.invoke()
            is Error -> Error(result.error)
        }
    }
}