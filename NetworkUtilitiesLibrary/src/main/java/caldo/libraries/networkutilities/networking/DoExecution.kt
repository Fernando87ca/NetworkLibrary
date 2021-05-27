package caldo.libraries.networkutilities.networking

import caldo.libraries.networkutilities.interactor.InteractorEmitter
import caldo.libraries.networkutilities.interactor.InteractorResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

suspend inline fun <reified T> doCompletable(
    dispatcher: CoroutineDispatcher,
    emitter: InteractorEmitter<Unit>,
    crossinline execute: () -> ResponseResult<T>,
    crossinline doOnSuccess: (data: T) -> Unit = { defaultSuccess(emitter) },
    crossinline doOnError: (error: Exception) -> Unit = { defaultError(it, emitter) }
) {
    kotlinx.coroutines.withContext(dispatcher) {
        execute(emitter, execute, doOnSuccess, doOnError)
    }
}

suspend inline fun <reified OUT, OUT_REPO> doSingle(
    dispatcher: CoroutineDispatcher,
    emitter: InteractorEmitter<OUT>,
    crossinline execute: () -> ResponseResult<OUT_REPO>,
    crossinline doOnSuccess: (data: OUT_REPO) -> OUT,
    crossinline doOnError: (error: Exception) -> Unit = { defaultError(it, emitter) }
) {
    kotlinx.coroutines.withContext(dispatcher) {
        execute(emitter, execute, doOnSuccess, doOnError)
    }
}

inline fun <reified OUT, OUT_REPO> execute(
    emitter: InteractorEmitter<OUT>,
    execute: () -> ResponseResult<OUT_REPO>,
    doOnSuccess: (data: OUT_REPO) -> OUT,
    doOnError: (error: Exception) -> Unit
) {
    var result: InteractorResult<OUT> = InteractorResult.loading()

    try {
        emitter.onInteractorEmitResult(result)

        when (val response = execute()) {
            is ResponseResult.Success -> {
                val success = doOnSuccess(response.data)

                result = InteractorResult.success(success)
                emitter.onInteractorEmitResult(result)
            }
            is ResponseResult.Error -> doOnError(response.error)
        }
    } catch (e: Exception) {
        result = InteractorResult.error(e)
        emitter.onInteractorEmitResult(result)
    }
}


suspend inline fun <reified OUT, OUT_REPO> doFlowable(
    dispatcher: CoroutineDispatcher,
    emitter: InteractorEmitter<OUT>,
    crossinline execute: () -> Flow<ResponseResult<OUT_REPO>>,
    crossinline doForEachSuccess: (data: OUT_REPO) -> OUT,
    crossinline doForEachError: (error: Exception) -> Unit = { defaultError(it, emitter) }
) {
    kotlinx.coroutines.withContext(dispatcher) {
        var result: InteractorResult<OUT> = InteractorResult.loading()

        try {
            emitter.onInteractorEmitResult(result)

            execute().collect {
                when (it) {
                    is ResponseResult.Success -> {
                        val success = doForEachSuccess(it.data)

                        result = InteractorResult.success(success)
                        emitter.onInteractorEmitResult(result)
                    }
                    is ResponseResult.Error -> doForEachError(it.error)
                }
            }
        } catch (e: Exception) {
            result = InteractorResult.error(e)
            emitter.onInteractorEmitResult(result)
        }
    }
}

fun defaultSuccess(emitter: InteractorEmitter<Unit>) {
    val result = InteractorResult.success<Unit>()
    emitter.onInteractorEmitResult(result)
}

inline fun <reified OUT> defaultError(it: Exception, emitter: InteractorEmitter<OUT>) {
    val result = InteractorResult.error<OUT>(it)
    emitter.onInteractorEmitResult(result)
}