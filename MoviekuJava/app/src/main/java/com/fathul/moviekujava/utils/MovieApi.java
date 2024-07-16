package com.fathul.moviekujava.utils;

import com.fathul.moviekujava.responses.PopularMovieResponses;
import com.fathul.moviekujava.responses.SearchMovieResponses;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieApi {
    @GET("movie/popular")
    Call<PopularMovieResponses> getPopularMovie(
            @Query("api_key") String key,
            @Query("page") int page);

    @GET("search/movie/")
    Call<SearchMovieResponses> searchMovie(
            @Query("api_key") String key,
            @Query("query") String query,
            @Query("page") int page);
}
