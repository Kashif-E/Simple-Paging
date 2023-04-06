package com.kashif.paging

import kotlinx.coroutines.flow.Flow

/**
 * Represents the result of a pagination operation.
 *
 * @param T The type of the data being paginated.
 * @property pagedData The [Flow] of paginated data.
 * @property fetchNextPage A function to fetch the next page of data.
 * @property cancel A function to cancel the pagination operation.
 * @property refresh A function to refresh the paginated data.
 */
data class PaginationResult<T>(
    private val pagedData: Flow<Result<List<T>>>,
    private val fetchNextPage: () -> Unit,
    private val cancel: () -> Unit = {},
    private val refresh: () -> Unit = {}
)
