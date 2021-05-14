package ru.mrrobot1413.movieapp.ui

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import ru.mrrobot1413.movieapp.api.Api
import ru.mrrobot1413.movieapp.model.MovieNetwork
import ru.mrrobot1413.movieapp.repositories.MovieRepository
import java.io.IOException
import java.util.*

class MoviePagingSource(
    private val api: Api,
    private val query: String,
    private val genreId: Int?,
    private val typeOfRequest: String,
) : PagingSource<Int, MovieNetwork>() {
    override fun getRefreshKey(state: PagingState<Int, MovieNetwork>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieNetwork> {
        val position = params.key ?: 1

        return try {
            val list: List<MovieNetwork> = if (query == "") {
                when(typeOfRequest){
                    MovieRepository.REQUEST_TYPE_POPULAR -> api.getPopularMovies(page = position,
                        language = Locale.getDefault().language).moviesList

                    MovieRepository.REQUEST_TYPE_TOP_RATED -> api.getTopRatedMovies(page = position,
                        language = Locale.getDefault().language).moviesList

                    MovieRepository.REQUEST_TYPE_GENRE_SEARCH -> api.searchByGenre(id = genreId,
                        language = Locale.getDefault().language).moviesList

                    else -> api.getPopularMovies(page = position,
                        language = Locale.getDefault().language).moviesList
                }
            } else {
                api.searchMovie(page = position, language = Locale.getDefault().language,
                    query as String).moviesList
            }

            LoadResult.Page(
                data = list,
                prevKey = if (list.isEmpty()) null else position - 1,
                nextKey = if (list.isEmpty()) null else position + 1
            )
        } catch (e: IOException) {
            Log.d("Exception", e.message.toString())
            LoadResult.Error(e)
        } catch (e: HttpException) {
            Log.d("Exception", e.message.toString())
            LoadResult.Error(e)
        }
    }

}