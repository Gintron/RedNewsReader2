package com.example.rednewsreader;

import android.content.Intent;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

public class NewsDetailActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView title, description;
    private String mImg,  mTitle, mDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        getSupportActionBar().setTitle("Title");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title = findViewById(R.id.title_full);
        description = findViewById(R.id.description_full);
        imageView = findViewById(R.id.imageView_full);

        Intent intent = getIntent();
        mImg = intent.getStringExtra("img");
        mTitle = intent.getStringExtra("title");
        mDescription = intent.getStringExtra("description");

        RequestOptions requestOptions = new RequestOptions();
        Glide.with(this).load(mImg)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
        title.setText(mTitle);
        description.setText(mDescription);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
