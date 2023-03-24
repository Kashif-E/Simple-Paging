package com.kashif.paging

data class PagingConfig(
    val pageSize: Int,
    val initialLoadSize: Int = pageSize
)