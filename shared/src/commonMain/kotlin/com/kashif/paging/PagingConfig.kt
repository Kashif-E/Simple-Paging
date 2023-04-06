package com.kashif.paging


/**
 * Represents the configuration options for pagination.
 *
 * @property pageSize The number of items to load per page.
 * @property initialLoadSize The number of items to load initially.
 */
data class PagingConfig(
    val pageSize: Int,
    val initialLoadSize: Int = pageSize
)