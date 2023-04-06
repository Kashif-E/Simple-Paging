package com.kashif.paging

/**

An abstract class that defines the contract for loading paginated data.

@param Key The type of the key used to load data.

@param Value The type of the data being loaded.
 */
abstract class PagingSource<Key : Any, Value : Any> {

    /**

    Loads a page of data based on the given [LoadParams].
    @param params The [LoadParams] object that contains the key and load size to use for loading data.
    @return A [Result] object that contains the loaded data, previous key, and next key on success or an error message on failure.
     */
    abstract suspend fun load(params: LoadParams<Key>): Result<Page<Key, Value>>
    /**

    A data class that represents the parameters for loading a page of data.
    @param key The key to use for loading data.
    @param loadSize The number of items to load.
     */
    data class LoadParams<Key : Any>(
        val key: Key?,
        val loadSize: Int
    )
    /**

    A data class that represents a page of loaded data.
    @param data The loaded data.
    @param prevKey The previous key to use for loading the previous page of data.
    @param nextKey The next key to use for loading the next page of data.
     */
    data class Page<Key : Any, Value : Any>(
        val data: List<Value>,
        val prevKey: Key?,
        val nextKey: Key?
    )
}