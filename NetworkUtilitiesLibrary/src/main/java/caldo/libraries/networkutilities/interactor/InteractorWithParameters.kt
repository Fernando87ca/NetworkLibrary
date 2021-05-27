package caldo.libraries.networkutilities.interactor

interface InteractorWithParameters<T, V> {
    suspend fun execute(param: T, emitter: InteractorEmitter<V>)
}