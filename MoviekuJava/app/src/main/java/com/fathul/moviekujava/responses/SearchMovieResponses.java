package com.fathul.moviekujava.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.fathul.moviekujava.models.MovieModel;

import java.util.List;

public class SearchMovieResponses {
    @SerializedName("results")
    @Expose
    private List<MovieModel> movies;

    public List<MovieModel> getMovies() {
        return movies;
    }

    @Override
    public String toString() {
        return "SearchMovieResponses{" +
                "movies=" + movies +
                '}';
    }
}

