package com.fathul.moviekujava.repositories;

import androidx.lifecycle.LiveData;

import com.fathul.moviekujava.models.MovieModel;
import com.fathul.moviekujava.requests.SearchMovieApiClient;

import java.util.List;

public class SearchMovieRepository {
    private static SearchMovieRepository instance;
    private SearchMovieApiClient searchMovieApiClient;

    private String query;
    private int page;

    public static SearchMovieRepository getInstance() {
        if (instance == null) {
            instance = new SearchMovieRepository();
        }

        return instance;
    }

    private SearchMovieRepository() {
        searchMovieApiClient = SearchMovieApiClient.getInstance();
    }

    public LiveData<List<MovieModel>> getSearchMovie() {
        return searchMovieApiClient.getSearchMovie();
    }

    public void getSearchMovie(String query,int page) {
        this.query = query;
        this.page = page;
        searchMovieApiClient.getSearchMovie(query,page);
    }

    // next page
    public void searchMovieNextPage() {
        getSearchMovie(query,page+1);
    }

}
