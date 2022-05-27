package com.example.userlistanimation.presentation.stories;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.userlistanimation.R;
import com.example.userlistanimation.data.model.post.Data;
import com.example.userlistanimation.presentation.Constants;
import com.example.userlistanimation.presentation.viewmodel.TvViewModel;
import com.example.userlistanimation.presentation.viewmodel.TvViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class StoriesActivity extends AppCompatActivity implements StoriesAdapter.onTimeUpdateListener {
    StoriesAdapter storiesAdapter;
    ViewPager2 viewPager2;
    RecyclerView rvProgressBar;
    ProgressBarAdapter progressBarAdapter;
    TvViewModel viewModel;
    TvViewModelFactory factory;
    private ArrayList<Data> stories = new ArrayList<>();
    private int page = 0;
    private Handler handler;
    private long delay = 0; //milliseconds
    Runnable runnable = new Runnable() {
        public void run() {
            page++;
            viewPager2.setCurrentItem(page, true);
            Log.d("UserStories", "HANDLER position:" + page);
            handler.postDelayed(this, delay);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);
        handler = new Handler();
        viewPager2 = findViewById(R.id.view_pager);
        rvProgressBar = findViewById(R.id.rvprogress);
        storiesAdapter = new StoriesAdapter(this);
        storiesAdapter.setOnTimeUpdateListener(this);
        factory = new TvViewModelFactory(this.getApplication());
        viewModel = new ViewModelProvider(this, factory).get(TvViewModel.class);
        viewPager2.setUserInputEnabled(false);
        viewPager2.setOffscreenPageLimit(1);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                page = position;
                Log.d("UserStories", "Inside onPageSelected position:" + position);
                viewPager2.post(() -> {
                    storiesAdapter.notifyItemChanged(position, true);
                });

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        ViewPager2.PageTransformer pageTransformer = (page, position) -> {
            page.setTranslationX(page.getWidth() * -position);

            if (position <= -1.0F || position >= 1.0F) {
                page.setAlpha(0.0F);
            } else if (position == 0.0F) {
                page.setAlpha(1.0F);
            } else {
                // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                page.setAlpha(1.0F - Math.abs(position));
            }
        };
        viewPager2.setPageTransformer(pageTransformer);
        viewPostList();
    }

    private void viewPostList() {
        viewModel.getStoriesList(page, 10);

        viewModel.getStoryMutableLiveData().observe(this, data -> {
            if (data != null) {
                Data data1 = data.get(0);
                data1.setVideo(true);
                data1.setVideoUrl("https://cdn.demo-hubengage.com/demo/content/original/cebc2218-6a8f-4ea4-a62c-beba41160d65.mp4?Expires=1653638209&Signature=Z1s2IBVUE92mGa-E9Lf51VH7-CpUEmeWbVPw7fPD8bRZqUfdGOeyN16GMgYLAUHqpF0y127n6UwsPGRrOJa7tqgPhtT94~mvdvWhurpGpShFp2DEZ1XQkbusgtQXTs0iwKU48~U3~72XmmBnzJjKIDSref8dvHlPB4h7wK~OtCphaNdv15CNnYkmAfmqA2lDYArq0WrCEQCz8Pjkor6kCATXujLv1RGj-yyHbJHXmlle1l8GY8bFnAWOguczhrchkTd03Us4ONF7nvDk9STmJDtRKoFekE14O-Nw0fXsUZjW~QIMP8ndWfgrxFdP0jEzpGvDz80h~Tx5H6aOyX3-jA__&Key-Pair-Id=APKAJBMM4TGT3PFAWXWA");

                Data data2 = data.get(2);
                data2.setVideo(true);
                data2.setVideoUrl("https://cdn.demo-hubengage.com/demo/content/original/cebc2218-6a8f-4ea4-a62c-beba41160d65.mp4?Expires=1653638209&Signature=Z1s2IBVUE92mGa-E9Lf51VH7-CpUEmeWbVPw7fPD8bRZqUfdGOeyN16GMgYLAUHqpF0y127n6UwsPGRrOJa7tqgPhtT94~mvdvWhurpGpShFp2DEZ1XQkbusgtQXTs0iwKU48~U3~72XmmBnzJjKIDSref8dvHlPB4h7wK~OtCphaNdv15CNnYkmAfmqA2lDYArq0WrCEQCz8Pjkor6kCATXujLv1RGj-yyHbJHXmlle1l8GY8bFnAWOguczhrchkTd03Us4ONF7nvDk9STmJDtRKoFekE14O-Nw0fXsUZjW~QIMP8ndWfgrxFdP0jEzpGvDz80h~Tx5H6aOyX3-jA__&Key-Pair-Id=APKAJBMM4TGT3PFAWXWA");

                storiesAdapter.setPostsList(data);
                stories.addAll(data);
                rvProgressBar.setLayoutManager(new GridLayoutManager(StoriesActivity.this, data.size()));
                progressBarAdapter = new ProgressBarAdapter(StoriesActivity.this, viewPager2);
                rvProgressBar.setAdapter(progressBarAdapter);
                progressBarAdapter.setPostsList(data);
                viewPager2.setAdapter(storiesAdapter);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }

    @Override
    public void onViewPlaying(long duration) {
        delay = duration;
        progressBarAdapter.notifyItemChanged(page, true);
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, delay);
        }
    }
}