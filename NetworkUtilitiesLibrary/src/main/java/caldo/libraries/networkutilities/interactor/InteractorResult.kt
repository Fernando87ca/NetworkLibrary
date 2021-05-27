package caldo.libraries.networkutilities.interactor

sealed class InteractorResult<T> {
    class Loading<T> : InteractorResult<T>()
    class Success<T>(val data: T? = null) : InteractorResult<T>()
    class Error<T>(val error: Exception) : InteractorResult<T>()

    companion object {
        fun <T> success(data: T? = null): InteractorResult<T> {
            return Success(data)
        }

        fun <T> error(error: Exception): InteractorResult<T> {
            return Error(error)
        }

        fun <T> loading(): InteractorResult<T> {
            return Loading()
        }
    }
}