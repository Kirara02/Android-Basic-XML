package com.fathul.moviekujava.requests;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fathul.moviekujava.models.MovieModel;
import com.fathul.moviekujava.responses.PopularMovieResponses;
import com.fathul.moviekujava.utils.AppExecutor;
import com.fathul.moviekujava.utils.Credentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class PopularMovieApiClient {

    private static PopularMovieApiClient instance;

    public static PopularMovieApiClient getInstance() {
        if(instance == null){
            instance = new PopularMovieApiClient();
        }
        return instance;
    }

    // Live Data
    private MutableLiveData<List<MovieModel>> popularMovieLiveData;
    // global variable for runnable
    private PopularRunnable popularRunnable;
    private PopularMovieApiClient(){
        popularMovieLiveData = new MutableLiveData<>();
    }
    public LiveData<List<MovieModel>> getPopularMovie(){
        return popularMovieLiveData;
    }

    public void getPopularMovie(int page){
        if(popularRunnable != null){
            popularRunnable = null;
        }

        popularRunnable = new PopularRunnable(page);

        final Future handler = AppExecutor.getInstance().getNetworkIO().submit(popularRunnable);

        AppExecutor.getInstance().getNetworkIO().schedule(() -> {
            // canceling retrofit call
            handler.cancel(true);
        }, 3000, TimeUnit.MILLISECONDS);
    }

    // retrieve data from rest api using runnable
    private class PopularRunnable implements Runnable {

        private final int page;
        boolean cancelRequest;

        public PopularRunnable(int page){
            this.page = page;
            cancelRequest = false;
        }

        @Override
        public void run() {
            // getting request
            try {
                Response response = getPopularMovie(page).execute();
                if(cancelRequest){
                    return;
                }

                if(response.isSuccessful()){
                    // retrieve data
                    if(response.code() == 200) {
                        assert response.body() != null;
                        List<MovieModel> popularMovieList = new ArrayList<>(((PopularMovieResponses) response.body()).getMovies());

                        if(page == 1){
                            // send data to livedata
                            // post value -> using background thread
                            // set value
                            popularMovieLiveData.postValue(popularMovieList);
                        }else{
                            List<MovieModel> currentMovie = popularMovieLiveData.getValue();
                            currentMovie.addAll(popularMovieList);
                            popularMovieLiveData.postValue(currentMovie);
                        }
                    }else{
                        System.out.println(response.errorBody().string());
                        popularMovieLiveData.postValue(null);
                    }
                }else{
                    System.out.println("request isnt successfull");
                }
            } catch (IOException e){
                System.out.println(e.getMessage());
                popularMovieLiveData.postValue(null);
            }
        }

        // method getPopularMovie
        private Call<PopularMovieResponses> getPopularMovie(int page){
            return ApiService.getMovieApi().getPopularMovie(Credentials.KEY, page);
        }
    }
}
