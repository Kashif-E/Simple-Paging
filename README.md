# Simple-Paging
A simple paging library to help in paginating data in KMM/Android Apps.

First create a Paging Source (Yes I know):

```
class LatestMoviesPagingSource(private val ktorService: AbstractKtorService) :
    PagingSource<Int, MoviesDomainModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MoviesDomainModel> {

        return try {
            val page = params.key ?: 1
            val response = ktorService.getPopularMovies(page)
            val movies = response.movies.asDomainModel()
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (movies.isEmpty()) null else page + 1
            LoadResult.Success(movies, prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
```

Inside your repository:

```
class Repository(private val ktorService: AbstractKtorService) : AbstractRepository() {


    override  fun getLatesMoviesPagingSource(): LatestMoviesPagingSource = LatestMoviesPagingSource(ktorService)


}
```

Inside Your screen model:

```
  val pagination =
        viewModelScope.paginate(useCase = respository.getLatesMoviesPagingSource())
```

Inside Compose:

```
//first is the state which contains the list data
 val latestMovies by screenModel.pagination.first.collectAsState()
 //second is the load more pages function that loads next page
    val loadMorePages = screenModel.pagination.second
    
     when(latestMovies){
        is Result.Error -> {
            Text("Error")
        }
        Result.Loading -> {
            Text("Loading")
        }
        is Result.Success -> {
            val data =    (latestMovies as Result.Success<List<MoviesDomainModel>>).data

            LazyColumn(
                modifier =
                Modifier.fillMaxSize().background(MaterialTheme.colors.background).padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                contentPadding = PaddingValues(vertical = 16.dp),
                state = rememberLazyListState(),
            ) {
           

                items(data) { item ->
                    if (item == data.last()) {
                        loadMorePages()
                    }
                    MovieCard(item) {}
                }
        }
    }
    }
    ```
    
 Thats all you need to do <3
