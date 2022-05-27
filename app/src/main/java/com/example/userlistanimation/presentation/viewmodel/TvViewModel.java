package com.example.userlistanimation.presentation.viewmodel;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.userlistanimation.data.model.post.APIResponse;
import com.example.userlistanimation.data.model.post.Data;
import com.example.userlistanimation.data.model.user.OwnerAPIResponse;
import com.example.userlistanimation.data.util.Resource;
import com.example.userlistanimation.domain.ApiRepository;
import com.example.userlistanimation.presentation.Constants;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class TvViewModel extends AndroidViewModel {

    @NonNull
    private final MutableLiveData<OwnerAPIResponse> users = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<APIResponse> posts = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<List<Data>> storyLiveData = new MutableLiveData<>();

    private final MutableLiveData<Resource.Status> statusMutableLiveData = new MutableLiveData<>();
    ExoPlayer exoPlayer;

    private Observer<OwnerAPIResponse> usersObserver = new Observer<OwnerAPIResponse>() {
        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onNext(OwnerAPIResponse apiResponse) {
            if (!apiResponse.getData().isEmpty()) {
                users.postValue(apiResponse);
            }

        }

        @Override
        public void onError(Throwable e) {
            Log.d("Error", "Error in Observer: " + e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    };

    private Observer<APIResponse> postsObserver = new Observer<APIResponse>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(APIResponse apiResponse) {
            if (!apiResponse.getData().isEmpty()) {
                posts.postValue(apiResponse);
            }

        }

        @Override
        public void onError(Throwable e) {
            Log.d("Error", "Error in Observer: " + e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    };

    private Observer<List<Data>> storyObserver = new Observer<List<Data>>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(List<Data> stories) {
            if (stories != null) {
                storyLiveData.postValue(stories);
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.d("Error", "Error in Observer: " + e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    };

    Application app;

    public TvViewModel(Application app) {
        super(app);
        this.app = app;
    }


    public void getUsersList(int page, int limit) {
        if (isNetworkAvailable(app)) {

            Observable<OwnerAPIResponse> apiResponseObservable = ApiRepository.getInstance().getUsers(page, limit);

            apiResponseObservable.doOnSubscribe(disposable -> {
                statusMutableLiveData.postValue(Resource.Status.LOADING);
            }).doFinally(() -> statusMutableLiveData.postValue(Resource.Status.LOADING_COMPLETED)
            ).onErrorReturn(new Function<Throwable, OwnerAPIResponse>() {
                @Override
                public OwnerAPIResponse apply(Throwable throwable) throws Exception {
                    return null;
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(usersObserver);
        }
    }

    public void getPostsList(int page, int limit) {
        if (isNetworkAvailable(app)) {
            Observable<APIResponse> apiResponseObservable = ApiRepository.getInstance().getPosts(page, limit);

            apiResponseObservable.doOnSubscribe(disposable -> {
                statusMutableLiveData.postValue(Resource.Status.LOADING);
            }).doFinally(() -> statusMutableLiveData.postValue(Resource.Status.LOADING_COMPLETED)
            ).onErrorReturn(throwable -> null).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(postsObserver);
        }
    }

    public void getStoriesList(int page, int limit) {
        if (isNetworkAvailable(app)) {
            Observable<APIResponse> apiResponseObservable = ApiRepository.getInstance().getPosts(page, limit);

            apiResponseObservable.doOnSubscribe(disposable -> {
                statusMutableLiveData.postValue(Resource.Status.LOADING);
            }).map(this::mapping).doFinally(() -> statusMutableLiveData.postValue(Resource.Status.LOADING_COMPLETED)
            ).onErrorReturn(throwable -> null).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(storyObserver);
        }
    }

    private List<Data> mapping(APIResponse apiResponse) {
        List<Data> postsList = new ArrayList<>();
        if (!apiResponse.getData().isEmpty()) {
            postsList.addAll(apiResponse.getData());
          /*  if (!postsList.isEmpty()) {
                for (int i = 0; i < postsList.size(); i++) {
                    Data story = postsList.get(i);

                    long storyTime = 0;
                    story.setVideo(true);

                    if (story.isVideo()) {
                        Log.d("UserStories", "Inside Mapping position at :" + i);
                        while (story.getTime() == 0) {
                            getVideoTime(story, i);
                        }
                    } else {
                        storyTime = Constants.DELAY_TIME;
                    }

                    story.setTime(storyTime);
                }
                Log.d("UserStories", "Inside Mapping completed");
            }*/
        }
        return postsList;
    }

    public void getVideoTime(Data story, int i) {
        Handler mainHandler = new Handler(app.getApplicationContext().getMainLooper());
        Runnable runnable = () -> {
            {
                String videoURL = "https://media.geeksforgeeks.org/wp-content/uploads/20201217163353/Screenrecorder-2020-12-17-16-32-03-350.mp4";
                if (exoPlayer == null) {
                    exoPlayer = new ExoPlayer.Builder(app.getApplicationContext()).build();
                    Log.d("UserStories", "Exoplayer was null at: " + i);
                    try {
                        MediaItem mediaItem = MediaItem.fromUri(videoURL); // story.getVideoURL
                        exoPlayer.setMediaItem(mediaItem);
                        exoPlayer.prepare();
                        exoPlayer.addListener(new Player.Listener() {
                            @Override
                            public void onPlaybackStateChanged(int playbackState) {
                                Player.Listener.super.onPlaybackStateChanged(playbackState);
                                if (playbackState == ExoPlayer.STATE_READY) {
                                    story.setTime(exoPlayer.getDuration());
                                    Log.d("UserStories", "Inside viewModel Exo STATE_READY :" + i);
                                    Log.d("UserStories", "Inside Exo Time:" + story.getTime());
                                    exoPlayer.release();
                                    exoPlayer = null;
                                }
                            }
                        });
                    } catch (Exception ignored) {
                    }
                }
            }
        };
        mainHandler.post(runnable);
    }

    public MutableLiveData<OwnerAPIResponse> getUsersMutableLiveData() {
        return users;
    }

    public MutableLiveData<List<Data>> getStoryMutableLiveData() {
        return storyLiveData;
    }

    public MutableLiveData<APIResponse> getPostsMutableLiveData() {
        return posts;
    }

    private boolean isNetworkAvailable(@NonNull Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

            if (capabilities != null) {
                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
            }
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;

    }
  /*  @Override
    protected void onCleared() {
        disposable.clear();
    }
*/
}
