package caldo.libraries.networkutilities.networking

import kotlinx.coroutines.flow.flow

inline fun <OUT, OUT_REMOTE> fetchData(
    crossinline fetchFromLocal: () -> OUT,
    crossinline shouldFetchFromRemote: (OUT?) -> Boolean = { true },
    crossinline fetchFromRemote: suspend () -> OUT_REMOTE,
    crossinline saveRemoteData: (OUT_REMOTE) -> Unit,
    crossinline onFetchFailed: (error: Exception) -> Unit = { _: Exception -> Unit }
) = flow {

    try {
        val localData = fetchFromLocal()
        emit(ResponseResult.Success(localData))

        if (shouldFetchFromRemote(localData)) {
            val response = fetchFromRemote()

            try {
                saveRemoteData(response)
                val reFetchFromLocal = fetchFromLocal()
                emit(ResponseResult.Success(reFetchFromLocal))
            } catch (e: Exception) {
                onFetchFailed(e)
                emit(ResponseResult.Error(e))
            }
        }
    } catch (e: Exception) {
        emit(ResponseResult.Error(e))
    }
}