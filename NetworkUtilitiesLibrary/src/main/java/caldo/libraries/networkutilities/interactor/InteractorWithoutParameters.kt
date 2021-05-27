package caldo.libraries.networkutilities.interactor

interface InteractorWithoutParameters<V> {
    suspend fun execute(emitter: InteractorEmitter<V>)
}