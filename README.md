# Simple-Paging

Simple Paging is a Kotlin Multiplatform library for Android, Desktop and iOS that provides simple pagination for list views.

## Getting Started
Prerequisites
To use Simple Paging, you will need to have the following installed:

Kotlin 1.5.21 or later
Gradle 7.0 or later
Xcode 12.5 or later (if using the iOS platform)
## Installation
To install Simple Paging, add the following to your build.gradle.kts file:
```kotlin
dependencies {
    implementation("io.github.kashif-e:simple-paging:<version>")
}
```
Replace <version> with the version of Simple Paging you want to use.

## Usage

Here is an example of how to use Simple Paging:

```kotlin
// Create a PagingSource
val pagingSource = { MyPagingSource() }

// Create a PaginationResult
val paginationResult = coroutineScope.paginate(pagingSourceProvider = pagingSource)

// Observe the paged data
paginationResult.pagedData.collect { result ->
    when (result) {
        is Result.Success -> {
            // Update your UI with the loaded data
            val data = result.data
        }
        is Result.Error -> {
            // Handle the error
            val error = result.exception
        }
        Result.Loading -> {
            // Show a loading indicator
        }
        Result.PaginationLoading -> {
            // Show a pagination loading indicator
        }
    }
}

// Fetch the next page
paginationResult.fetchNextPage()

// Refresh the data
paginationResult.refresh()

// Cancel the pagination
paginationResult.cancel()
```
Replace MyPagingSource with your own implementation of the PagingSource class.

Sample PagingSource:

```kotlin
class MyPagingSource(private val database: MyDatabase) : PagingSource<Int, MyData>() {
    override suspend fun load(params: LoadParams<Int>): Result<Page<Int, MyData>> {
        return try {
            val data = database.loadData(params.key ?: 0, params.loadSize)
            val prevKey = if (params.key == 0) null else params.key - 1
            val nextKey = if (data.size < params.loadSize) null else params.key + 1
            Result.Success(Page(data, prevKey, nextKey))
        } catch (exception: Exception) {
            Result.Error(exception.message ?: "Something went wrong, please try again.")
        }
    }
}

```

## License
Simple Paging is licensed under the MIT License. See the LICENSE file for details.

## Acknowledgments
This library is inspired by the Android Jetpack Paging library.

