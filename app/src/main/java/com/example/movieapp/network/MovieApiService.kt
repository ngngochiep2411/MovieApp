package com.example.movieapp.network


import com.example.movieapp.model.BaseResponse
import com.example.movieapp.model.CartoonMovie
import com.example.movieapp.model.Category
import com.example.movieapp.model.CategoryMovie
import com.example.movieapp.model.CountryMovie
import com.example.movieapp.model.DetailMovie
import com.example.movieapp.model.Movies
import com.example.movieapp.model.NewMovie
import com.example.movieapp.model.ResponseListMovie
import com.example.movieapp.model.SearchMovie
import com.example.movieapp.model.SeriesMovie
import com.example.movieapp.model.TvShowsMovie
import com.example.movieapp.model.UserUpdate
import com.example.movieapp.util.NetworkResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    @GET("v1/api/danh-sach/phim-le")
    suspend fun getMovies(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("category") category: String = "",
        @Query("country") country: String = "",
        @Query("year") year: String = "",
    ): Movies

    @GET("v1/api/danh-sach/phim-long-tieng")
    suspend fun getPhimLongTieng(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("category") category: String = "",
        @Query("country") country: String = "",
        @Query("year") year: String = "",
    ): Movies

    @GET("v1/api/danh-sach/phim-vietsub")
    suspend fun getVietSub(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("category") category: String = "",
        @Query("country") country: String = "",
        @Query("year") year: String = "",
    ): Movies

    @GET("v1/api/danh-sach/phim-thuyet-minh")
    suspend fun getPhimThuyetMinh(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("category") category: String = "",
        @Query("country") country: String = "",
        @Query("year") year: String = "",
    ): Movies

    @GET("v1/api/danh-sach/phim-bo")
    suspend fun getMovieSeries(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 50,
        @Query("category") category: String = "",
        @Query("country") country: String = "",
        @Query("year") year: String = "",
    ): SeriesMovie

    @GET("v1/api/danh-sach/hoat-hinh")
    suspend fun getCartoonMovies(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 50,
        @Query("category") category: String = "",
        @Query("country") country: String = "",
        @Query("year") year: String = "",

        ): CartoonMovie

    @GET("v1/api/danh-sach/tv-shows")
    suspend fun getTvShows(
        @Query("page") page: Int, @Query("limit") limit: Int = 50,
        @Query("category") category: String = "",
        @Query("country") country: String = "",
        @Query("year") year: String = "",
    ): TvShowsMovie

    @GET("danh-sach/phim-moi-cap-nhat")
    suspend fun getNewMovie(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 50,
        @Query("category") category: String = "",
        @Query("country") country: String = "",
        @Query("year") year: String = "",
    ): NewMovie

    @GET("phim/{movieName}")
    suspend fun getDetailMovie(@Path("movieName") movieName: String): DetailMovie

    //https://phimapi.com/v1/api/tim-kiem?keyword=xe&limit=50
    @GET("v1/api/tim-kiem")
    suspend fun searchMovie(
        @Query("keyword") keyword: String,
        @Query("limit") limit: Int = 50
    ): SearchMovie

    //- type_list = phim-bo, phim-le, tv-shows, hoat-hinh, phim-vietsub, phim-thuyet-minh, phim-long-tieng
    //- page = Số trang cần truy xuất, sử dụng [totalPages] để biết tổng trang khả dụng.
    //- sort_field = modified.time > tính theo thời gian cập nhật, _id > lấy theo ID của phim, year > lấy theo số năm phát hành của phim.
    //- sort_type = desc hoặc asc.
    //- sort_lang = vietsub > phim có Vietsub, thuyet-minh > phim có Thuyết Minh, long-tieng > phim có Lồng Tiếng.
    //- category = Thể loại phim cần lấy, sử dụng API phimapi.com/the-loai để lấy chi tiết slug.
    //- country = Quốc gia phim cần lấy, sử dụng API phimapi.com/quoc-gia để lấy chi tiết slug.
    //- year = Năm phát hành của phim (1970 - hiện tại).
    //- limit = Giới hạn kết quả (tối đa 64).

    @GET("v1/api/danh-sach/{type_list}")
    suspend fun getPhimList(
        @Path("type_list") typeList: String?,
        @Query("page") page: Int,
//        @Query("sort_field") sortField: String?,
//        @Query("sort_type") sortType: String?,
//        @Query("sort_lang") sortLang: String?,
//        @Query("category") category: String?,
//        @Query("country") country: String?,
//        @Query("year") year: Int,
        @Query("limit") limit: Int = 50
    ): ResponseListMovie


    @GET("the-loai")
    suspend fun getCategory(): List<CategoryMovie>

    @GET("quoc-gia")
    suspend fun getCountry(): List<CountryMovie>


}