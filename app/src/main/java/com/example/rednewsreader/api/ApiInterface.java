package com.example.rednewsreader.api;

import com.example.rednewsreader.models.News;

import retrofit2.Call;
import retrofit2.http.GET;


public interface ApiInterface {

    @GET("articles?source=bbc-news&sortBy=top&apiKey=03d01f5264b44388a3be19340bdf49e8")
    Call<News> getNews();

}
