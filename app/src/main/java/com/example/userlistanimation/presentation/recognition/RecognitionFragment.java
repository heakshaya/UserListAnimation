package com.example.userlistanimation.presentation.recognition;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.userlistanimation.R;
import com.example.userlistanimation.data.model.post.Data;
import com.example.userlistanimation.presentation.Constants;
import com.example.userlistanimation.presentation.stories.ProgressBarAdapter;
import com.example.userlistanimation.presentation.stories.StoriesActivity;
import com.example.userlistanimation.presentation.viewmodel.TvViewModel;
import com.example.userlistanimation.presentation.viewmodel.TvViewModelFactory;

public class RecognitionFragment extends Fragment implements RecognitionAdapter.onTimeUpdateListener {
    ViewPager2 viewPager2;
    TvViewModel viewModel;
    TvViewModelFactory factory;
    RecognitionAdapter recognitionAdapter;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recognition, container, false);
        viewPager2 = view.findViewById(R.id.view_pager);
        handler = new Handler();
        recognitionAdapter = new RecognitionAdapter(getContext());
        recognitionAdapter.setOnTimeUpdateListener(this);
        factory = new TvViewModelFactory(getActivity().getApplication());
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
                Log.d("Recognition", "Inside onPageSelected position:" + position);
                viewPager2.post(() -> {
                    recognitionAdapter.notifyItemChanged(position, true);
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
        return view;
    }

    private void viewPostList() {
        viewModel.getStoriesList(0, 10);

        viewModel.getStoryMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {

                for (int i = 0; i < data.size(); i++) {
                    Data data0 = data.get(i);
                    if (i == 0) {
                        data0.setItemType(Constants.ITEM_TYPE_USER);
                    } else if (i == 1) {
                        data0.setItemType(Constants.ITEM_TYPE_MAIN_IMAGE);
                    } else if (i == 2) {
                        data0.setItemType(Constants.ITEM_TYPE_MEDIA_IMAGE);
                    } else if (i == 3) {
                        data0.setVideo(true);
                        data0.setItemType(Constants.ITEM_TYPE_MEDIA_VIDEO);
                        data0.setVideoUrl("https://cdn.demo-hubengage.com/demo/content/original/cebc2218-6a8f-4ea4-a62c-beba41160d65.mp4?Expires=1653638209&Signature=Z1s2IBVUE92mGa-E9Lf51VH7-CpUEmeWbVPw7fPD8bRZqUfdGOeyN16GMgYLAUHqpF0y127n6UwsPGRrOJa7tqgPhtT94~mvdvWhurpGpShFp2DEZ1XQkbusgtQXTs0iwKU48~U3~72XmmBnzJjKIDSref8dvHlPB4h7wK~OtCphaNdv15CNnYkmAfmqA2lDYArq0WrCEQCz8Pjkor6kCATXujLv1RGj-yyHbJHXmlle1l8GY8bFnAWOguczhrchkTd03Us4ONF7nvDk9STmJDtRKoFekE14O-Nw0fXsUZjW~QIMP8ndWfgrxFdP0jEzpGvDz80h~Tx5H6aOyX3-jA__&Key-Pair-Id=APKAJBMM4TGT3PFAWXWA");

                    } else {
                        data0.setItemType(Constants.ITEM_TYPE_MEDIA_IMAGE);
                    }
                }
                recognitionAdapter.setStoriesList(data);
                viewPager2.setAdapter(recognitionAdapter);
            }
        });
    }

    @Override
    public void onViewPlaying(long duration) {
        delay = duration;
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, delay);
        }
    }
}
