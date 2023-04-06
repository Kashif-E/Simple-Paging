package com.kashif.paging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
/**
 * Paginates the data returned by a [PagingSource].
 *
 * @param T The type of the data being paginated.
 * @param pagingSourceProvider A function to provide a [PagingSource] to load data from.
 * @param pagingConfig The configuration options for pagination.
 * @param errorHandler A function to handle errors that occur during pagination.
 * @param distinctUntilChanged Whether or not to skip emitting pages with the same data as the previous page.
 * @param paginationScope The [CoroutineScope] to use for pagination.
 *
 * @return A [PaginationResult] that can be used to observe, refresh, or cancel the pagination operation.
 */
fun <T : Any> CoroutineScope.paginate(
    pagingSourceProvider: () -> PagingSource<Int, T>,
    pagingConfig: PagingConfig = PagingConfig(pageSize = 20),
    errorHandler: (Throwable) -> String = { exception -> exception.message ?: "Something went wrong, please try again." },
    distinctUntilChanged: Boolean = true,
    paginationScope: CoroutineScope = this
): PaginationResult<T> {

    val pagedData = MutableStateFlow<Result<List<T>>>(Result.Loading)
    var currentPage = 1
    var isLastPage = false

    fun fetchNextPage() {
        if (isLastPage) return

        paginationScope.launch {
            pagedData.update {
                if (it is Result.Loading) Result.Loading else Result.PaginationLoading
            }

            val loadResult = try {
                val pagingSource = pagingSourceProvider()
                pagingSource.load(PagingSource.LoadParams(currentPage, pagingConfig.pageSize))
            } catch (exception: Exception) {
                Result.Error(errorHandler(exception))
            }

            when (loadResult) {
                is Result.Success -> {
                    val page = loadResult.data
                    currentPage = page.nextKey ?: (currentPage + 1)
                    isLastPage = page.nextKey == null

                    val currentData = (pagedData.value as? Result.Success)?.data ?: emptyList()
                    val newData = currentData + page.data

                    if (distinctUntilChanged) {
                        if (currentData == newData) return@launch
                    }

                    pagedData.update {
                        Result.Success(newData)
                    }
                }
                is Result.Error -> {
                    pagedData.update {
                        loadResult
                    }
                }
                else -> Unit
            }
        }
    }

    fetchNextPage()

    fun refresh() {
        currentPage = 1
        isLastPage = false
        pagedData.value = Result.Loading
        fetchNextPage()
    }

    val distinctPagedData = pagedData
        .distinctUntilChanged { old, new ->
            old is Result.Success && new is Result.Success && old.data == new.data
        }

    return PaginationResult(distinctPagedData, ::fetchNextPage, paginationScope::cancel, ::refresh)
}


