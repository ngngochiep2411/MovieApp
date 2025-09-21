package com.example.movieapp.repository

import android.util.Log
import com.example.movieapp.database.MovieDao
import com.example.movieapp.model.BaseResponse
import com.example.movieapp.model.CartoonMovie
import com.example.movieapp.model.CategoryMovie
import com.example.movieapp.model.Comment
import com.example.movieapp.model.CommentData
import com.example.movieapp.model.CommentResponse
import com.example.movieapp.model.CountryMovie
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.model.GetReply
import com.example.movieapp.model.LatestMovie
import com.example.movieapp.model.Login
import com.example.movieapp.model.Movies
import com.example.movieapp.model.NewMovie
import com.example.movieapp.model.Register
import com.example.movieapp.model.Reply
import com.example.movieapp.model.ReplyData
import com.example.movieapp.model.ReplyResponse
import com.example.movieapp.model.ResponseListMovie
import com.example.movieapp.model.SearchMovie
import com.example.movieapp.model.SeriesMovie
import com.example.movieapp.model.TvShowsMovie
import com.example.movieapp.model.UserDetail
import com.example.movieapp.network.CommentAPIService
import com.example.movieapp.network.MovieApiService
import com.example.movieapp.util.NetworkResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val movieApiService: MovieApiService,
    private val commentApiService: CommentAPIService,
    private val movieDao: MovieDao
) {


    fun getMovies(
        page: Int = 1,
        category: String = "",
        country: String = "",
        year: String = ""
    ) = flow<NetworkResult<Movies>> {
        val movies = movieApiService.getMovies(
            page = page,
            category = category,
            country = country,
            year = year
        )
        emit(NetworkResult.Success(movies))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message))
    }

    fun getVietSub(
        page: Int = 1,
        category: String = "",
        country: String = "",
        year: String = ""
    ) = flow<NetworkResult<Movies>> {
        val movies = movieApiService.getVietSub(
            page = page,
            category = category,
            country = country,
            year = year
        )
        emit(NetworkResult.Success(movies))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message))
    }

    fun getThuyetMinh(
        page: Int = 1,
        category: String = "",
        country: String = "",
        year: String = ""
    ) = flow<NetworkResult<Movies>> {
        val movies = movieApiService.getPhimThuyetMinh(
            page = page,
            category = category,
            country = country,
            year = year
        )
        emit(NetworkResult.Success(movies))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message))
    }

    fun getLongTieng(
        page: Int = 1,
        category: String = "",
        country: String = "",
        year: String = ""
    ) = flow<NetworkResult<Movies>> {
        val response = movieApiService.getPhimLongTieng(
            page = page,
            category = category,
            country = country,
            year = year
        )
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message))

    }

    fun getSeriesMovie(
        page: Int = 1, category: String = "",
        country: String = "",
        year: String = ""
    ) = flow<NetworkResult<SeriesMovie>> {
        val response = movieApiService.getMovieSeries(
            page = page, country = country,
            category = category,
            year = year
        )
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message))
    }

    fun getCartoonMovie(
        page: Int = 1, category: String = "",
        country: String = "",
        year: String = ""
    ) = flow<NetworkResult<CartoonMovie>> {
        val response = movieApiService.getCartoonMovies(
            page = page,
            country = country,
            category = category,
            year = year
        )
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message))
    }

    fun getTvShows(
        page: Int = 1, category: String = "",
        country: String = "",
        year: String = ""
    ) = flow<NetworkResult<TvShowsMovie>> {
        val response = movieApiService.getTvShows(
            page = page,
            category = category,
            year = year,
            country = country
        )
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message))
    }

    fun getNewMovie(
        page: Int = 1, category: String = "",
        country: String = "",
        year: String = ""
    ) = flow<NetworkResult<NewMovie>> {
        val tvShows = movieApiService.getNewMovie(
            page = page,
            category = category,
            year = year,
            country = country
        )
        emit(NetworkResult.Success(tvShows))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message))
    }

    fun getDetailMovie(name: String) = flow<NetworkResult<DetailMovie>> {
        val detailMovie = movieApiService.getDetailMovie(movieName = name)
        emit(NetworkResult.Success(detailMovie))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message))
    }


    fun searchMovie(keyword: String) = flow<NetworkResult<SearchMovie>> {
        val result = movieApiService.searchMovie(keyword = keyword)
        emit(NetworkResult.Success(result))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message))
    }


    suspend fun getComment(video: String?, page: Int) = flow<BaseResponse<List<Comment>>> {
        val comments = commentApiService.getComment(video_id = video, page = page)
        emit(comments)
    }.catch { e ->
    }

    suspend fun comment(
        content: RequestBody,
        video_id: RequestBody,
        userId: RequestBody,
        image: MultipartBody.Part?
    ) = flow<BaseResponse<CommentResponse>> {
        val response = commentApiService.comment(
            content = content,
            videoId = video_id,
            userId = userId,
            image = image
        )
        emit(response)
    }.catch { e ->
    }

    suspend fun reply(
        user_id: RequestBody,
        content: RequestBody,
        comment_id: RequestBody,
        reply_user_id: RequestBody,
        image: MultipartBody.Part?
    ) = flow<BaseResponse<ReplyResponse>> {
        val response = commentApiService.reply(
            user_id = user_id,
            content = content,
            comment_id = comment_id,
            reply_user_id = reply_user_id,
            image = image
        )
        emit(response)

    }.catch { e ->
    }


    suspend fun login(email: String, password: String) = flow<BaseResponse<UserDetail>> {
        val response = commentApiService.login(Login(email, password))
        emit(response)
    }.catch { e ->
    }

    suspend fun register(email: String, password: String, userName: String) =
        flow<BaseResponse<UserDetail>> {
            val response = commentApiService.register(Register(email, password, userName))
            emit(response)
        }.catch { e ->
        }

    fun getReply(video_id: String?, comment_id: Int, page: Int) =
        flow<BaseResponse<List<Reply>>> {
            val response = commentApiService.getReply(
                video_id = video_id,
                comment_id = comment_id,
                page = page
            )
            emit(response)
        }.catch { e ->
        }


    fun getCategory() = flow<List<CategoryMovie>> {
        val response = movieApiService.getCategory()
        emit(response)
    }.catch { e ->
    }

    fun getCountry() = flow<List<CountryMovie>> {
        val response = movieApiService.getCountry()
        emit(response)
    }.catch { e ->
    }

//    fun updateUser(
//        user_id: RequestBody,
//        name: RequestBody,
//        avatar_url: MultipartBody.Part,
//        password: RequestBody
//    ) =
//        flow<BaseResponse<Any>> {
//            val response = commentApiService.updateUser(
//                user_id = user_id,
//                name = name,
////                password = password,
//                avatar_url = avatar_url
//            )
//            emit(response)
//            Log.d("testing", Gson().toJson(response))
//
//        }.catch { e ->
//            Log.d("testing", "$e")
//        }

    fun updateUser(
        user_id: RequestBody,
        name: RequestBody,
        avatar_url: MultipartBody.Part?,
        password: RequestBody,
    ) = flow<BaseResponse<Any>> {
        val response = commentApiService.updateUser(
            user_id = user_id,
            avatar_url = avatar_url,
            password = password,
            name = name
        )
        emit(response)
    }.catch { e ->
        Log.d("tes","$e")
    }

//    fun getWatchedEpisodes(string: String): Flow<List<Int>> {
//
//    }
}