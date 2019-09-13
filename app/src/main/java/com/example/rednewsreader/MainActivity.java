package com.example.rednewsreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.rednewsreader.Adapter.Adapter;
import com.example.rednewsreader.api.ApiClient;
import com.example.rednewsreader.api.ApiInterface;
import com.example.rednewsreader.models.Article;
import com.example.rednewsreader.models.News;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import okhttp3.Cache;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private Adapter adapter;
    int cacheSize = 10 * 1024 * 1024; // 10 MiB

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        LoadJson();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void LoadJson(){
        try {

            Cache cache = new Cache(getCacheDir(), cacheSize);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Interceptor.Chain chain)
                                throws IOException {
                            Request request = chain.request();
                            if (!isNetworkAvailable()) {
                                int maxStale = 300;
                                request = request
                                        .newBuilder()
                                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                        .build();
                            }
                            return chain.proceed(request);
                        }
                    }).build();
            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Call<News> call;
            call = apiInterface.getNews();
            call.enqueue(new Callback<News>() {
                @Override
                public void onResponse(Call<News> call, Response<News> response) {
                    if(response.isSuccessful()&& response.body().getArticle() != null){
                        if(!articles.isEmpty()){
                            articles.clear();
                        }
                        articles = response.body().getArticle();

                        adapter = new Adapter(articles, MainActivity.this);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        initListener();


                    }else {
                    }
                }

                @Override
                public void onFailure(Call<News> call, Throwable t) {
                    showErrorMessage();
                }
            });
        }catch (Exception e){
            Log.d("Error", e.getMessage());
        }

    }
    private void initListener(){
        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);

                Article article = articles.get(position);
                intent.putExtra("title", article.getTitle());
                intent.putExtra("img", article.getUrlToImage());
                intent.putExtra("description", article.getDescription());
                startActivity(intent);
            }

        });
    }
public void showErrorMessage(){
    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
    alertDialog.setTitle("Greška");
    alertDialog.setMessage("Došlo je do pogreške");
    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Pokušaj ponovno",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    LoadJson();
                    dialog.dismiss();
                }
            });
    alertDialog.show();
}
}
