package caldo.libraries.networkutilities.interactor

interface InteractorEmitter<T> {
    fun onInteractorEmitResult(result: InteractorResult<T>)
}