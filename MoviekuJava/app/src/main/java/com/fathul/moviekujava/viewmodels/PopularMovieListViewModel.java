package com.fathul.moviekujava.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.fathul.moviekujava.models.MovieModel;
import com.fathul.moviekujava.repositories.PopularMovieRepository;

import java.util.List;

public class PopularMovieListViewModel extends ViewModel {
    private PopularMovieRepository popularMovieRepository;

    public PopularMovieListViewModel(){
        popularMovieRepository = PopularMovieRepository.getInstance();
    }

    public LiveData<List<MovieModel>> getPopularMovie(){
        return popularMovieRepository.getPopularMovie();
    }

    public void getPopularMovie(int page){
        popularMovieRepository.getPopularMovie(page);
    }

    public void popularMovieNextPage(){
        popularMovieRepository.popularMovieNext();
    }
}
