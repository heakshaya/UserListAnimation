package com.example.userlistanimation.presentation.useranimation;

import static java.lang.Math.abs;

import android.animation.Animator;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.example.userlistanimation.R;
import com.example.userlistanimation.data.model.post.APIResponse;
import com.example.userlistanimation.data.model.user.OwnerAPIResponse;
import com.example.userlistanimation.presentation.viewmodel.TvViewModel;
import com.example.userlistanimation.presentation.viewmodel.TvViewModelFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    UserAdapter userAdapter;
    ViewPager2 viewPager2;
    boolean alreadyVisible;
    TvViewModel viewModel;
    TvViewModelFactory factory;
    private LottieAnimationView animationView;
    private int page = 0;
    private Handler handler;
    private final int delay = 8100; //milliseconds
    Runnable runnable = new Runnable() {
        public void run() {
            page++;
            MyPagerHelper.setCurrentItem(viewPager2, page, 1000);
            handler.postDelayed(this, delay);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        viewPager2 = findViewById(R.id.view_pager);
        animationView = findViewById(R.id.animationView);
        userAdapter = new UserAdapter();
        viewPager2.setAdapter(userAdapter);
        factory = new TvViewModelFactory(this.getApplication());
        viewModel = new ViewModelProvider(this, factory).get(TvViewModel.class);

        viewPager2.setUserInputEnabled(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                if (position == 0) {
                    if (positionOffset == 0.0) {
                        viewPager2.post(() -> {
                            userAdapter.notifyItemChanged(position, true);
                        });

                    } else if (positionOffset >= 0.2 && positionOffset <= 0.4) {
                        viewPager2.post(() -> {
                            userAdapter.notifyItemChanged(position + 1, true);
                        });

                    } else {
                        viewPager2.post(() -> {
                            userAdapter.notifyItemChanged(position, false);
                        });
                    }
                    return;
                }
                if ((positionOffset >= 0.2 && positionOffset <= 0.4)) {
                    viewPager2.post(() -> {
                        userAdapter.notifyItemChanged(position + 1, true);

                        alreadyVisible = true;
                    });
                } else if (positionOffset != 0.0 && alreadyVisible) {
                    viewPager2.post(() -> {

                        userAdapter.notifyItemChanged(position, false);
                        alreadyVisible = false;
                    });
                }
            }


            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                page = position;
                displayAnimation();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        // To get device matrix
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //To get device width
        float deviceWidth = displaymetrics.widthPixels;
        float pageTranslationX = 2 * (deviceWidth / 3);

        ViewPager2.PageTransformer pageTransformer = (page, position) -> {
            page.setTranslationX(-pageTranslationX * position);
            // Next line scales the item's height.
            page.setScaleX(1 - (0.50f * abs(position)));
            // Next line scales the item's width.
            page.setScaleY(1 - (0.50f * abs(position)));
            // If you want a fading effect uncomment the next line:
            page.setAlpha(0.25f + (1 - abs(position)));
        };

        viewPager2.setPageTransformer(pageTransformer);

        viewPager2.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int horizontalMarginInPx = (int) deviceWidth / 3;
                outRect.right = horizontalMarginInPx;
                outRect.left = horizontalMarginInPx;
            }
        });

        viewPostList();
    }

    private void displayAnimation() {
        animationView.setVisibility(View.VISIBLE);
        animationView.playAnimation();
        Single.timer(8, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(Long aLong) {
                        animationView.setVisibility(View.GONE);
                        animationView.cancelAnimation();
                        animationView.clearAnimation();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    private void viewPostList() {
        viewModel.getPostsList(page, 10);
        viewModel.getPostsMutableLiveData().observe(this, apiResponse -> {
            if (apiResponse != null) {
                userAdapter.setPostsList(apiResponse.getData());
                handler.postDelayed(runnable, delay);
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
        if(handler!=null){
            handler.removeCallbacks(runnable);
            handler=null;
        }
    }

}