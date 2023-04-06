package com.kashif.paging

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
/**
 * A sealed interface representing the result of a operation that may succeed, fail or still be loading.
 *
 * @param T The type of data returned upon success.
 */
sealed interface Result<out T> {
    /**
     * A data class representing a successful operation with the resulting data.
     *
     * @param data The data returned upon success.
     */
    data class Success<T>(val data: T) : Result<T>

    /**
     * A data class representing a failed operation with an exception message.
     *
     * @param exception The exception message returned upon failure.
     */
    data class Error(val exception: String) : Result<Nothing>

    /**
     * An object representing a operation that is currently loading.
     */
    object Loading : Result<Nothing>

    /**
     * An object representing a pagination operation that is currently loading.
     */
    object PaginationLoading : Result<Nothing>
}

/**
 * An extension function to convert a [Flow] emitting data to a [Flow] emitting [Result]s.
 *
 * @param T The type of data emitted by the [Flow].
 *
 * @return A [Flow] that emits [Result]s based on the success or failure of the original [Flow].
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this.map<T, Result<T>> { Result.Success(it) }.onStart { emit(Result.Loading) }
        .catch { exception ->
            emit(Result.Error(exception.message ?: "Something went wrong, please try again."))
        }
}
