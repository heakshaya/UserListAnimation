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
    private final MutableLiveData<APIResponse> posts = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<List<Data>> storyLiveData = new MutableLiveData<>();

    private final MutableLiveData<Resource.Status> statusMutableLiveData = new MutableLiveData<>();


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
            Log.d("Recognition", "Error in Observer: " + e.getMessage());
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

    public void getPostsList(int page, int limit) {
        Observable<APIResponse> apiResponseObservable = ApiRepository.getInstance().getPosts(page, limit);
        apiResponseObservable.doOnSubscribe(disposable -> {
            statusMutableLiveData.postValue(Resource.Status.LOADING);
        }).doFinally(() -> statusMutableLiveData.postValue(Resource.Status.LOADING_COMPLETED)
        ).onErrorReturn(throwable -> null).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(postsObserver);
    }

    public void getStoriesList(int page, int limit) {
        Observable<APIResponse> apiResponseObservable = ApiRepository.getInstance().getPosts(page, limit);
        apiResponseObservable.doOnSubscribe(disposable -> {
            statusMutableLiveData.postValue(Resource.Status.LOADING);
        }).map(this::mapping).doFinally(() -> statusMutableLiveData.postValue(Resource.Status.LOADING_COMPLETED)
        ).onErrorReturn(throwable -> null).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(storyObserver);
    }

    private List<Data> mapping(APIResponse apiResponse) {
        List<Data> postsList = new ArrayList<>();
        if (!apiResponse.getData().isEmpty()) {
            postsList.addAll(apiResponse.getData());
        }
        return postsList;
    }

    public MutableLiveData<List<Data>> getStoryMutableLiveData() {
        return storyLiveData;
    }

    public MutableLiveData<APIResponse> getPostsMutableLiveData() {
        return posts;
    }
}
