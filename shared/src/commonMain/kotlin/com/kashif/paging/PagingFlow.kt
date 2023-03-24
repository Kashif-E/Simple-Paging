package com.kashif.paging

import kotlin.reflect.KFunction0
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

fun <T : Any> CoroutineScope.paginate(
    useCase: () -> PagingSource<Int, T>,
    pagingConfig: PagingConfig = PagingConfig(pageSize = 20),
): Pair<MutableStateFlow<Result<List<T>>>, KFunction0<Unit>> {
    val _pagedData = MutableStateFlow<Result<List<T>>>(Result.Loading)
    var currentPage = 1
    var isLastPage = false

    fun fetchNextPage() {
        if (isLastPage) return

        launch(Dispatchers.IO) {
            try {
                val pagingSource = useCase()
                val loadResult = pagingSource.load(PagingSource.LoadParams(currentPage, pagingConfig.pageSize))
                if (loadResult is PagingSource.LoadResult.Success) {
                    currentPage = loadResult.nextKey ?: (currentPage + 1)
                    isLastPage = loadResult.nextKey == null

                    val currentData = (_pagedData.value as? Result.Success)?.data ?: emptyList()
                    _pagedData.value = Result.Success(currentData + loadResult.data)
                }
            } catch (exception: Exception) {
                _pagedData.value = Result.Error(exception.message ?: "Something went wrong, please try again.")
            }
        }
    }

    fetchNextPage()

    return _pagedData to ::fetchNextPage
}