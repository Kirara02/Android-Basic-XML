package com.fathul.moviekujava.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;


import com.fathul.moviekujava.models.MovieModel;
import com.fathul.moviekujava.repositories.SearchMovieRepository;

import java.util.List;

public class SearchMovieListViewModel extends ViewModel {
    private SearchMovieRepository searchMovieRepository;

    public SearchMovieListViewModel() {
        searchMovieRepository = SearchMovieRepository.getInstance();
    }

    public LiveData<List<MovieModel>> getSearchMovie() {
        return searchMovieRepository.getSearchMovie();
    }

    public void getSearchMovie(String query,int page) {
        searchMovieRepository.getSearchMovie(query,page);
    }

    // next page
    public void searchMovieNextPage() {
        searchMovieRepository.searchMovieNextPage();
    }
}
