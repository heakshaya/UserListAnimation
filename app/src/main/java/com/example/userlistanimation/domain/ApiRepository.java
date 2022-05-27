package com.example.userlistanimation.domain;

import android.util.Log;

import com.example.userlistanimation.data.api.APIService;
import com.example.userlistanimation.data.api.RetrofitService;
import com.example.userlistanimation.data.model.post.APIResponse;
import com.example.userlistanimation.data.model.user.OwnerAPIResponse;

import io.reactivex.Observable;

public class ApiRepository {

    private APIService apiService;
    private static ApiRepository apiRepository;

    private ApiRepository(){
        try {
            apiService= RetrofitService.getInstance();
        }catch (Exception e){
            Log.d("Error","Retrofit Instance error: "+e.getMessage());
        }
    }
    synchronized public static ApiRepository getInstance() {
        if (apiRepository == null) {
            apiRepository = new ApiRepository();
        }
        return apiRepository;
    }
    public Observable<APIResponse> getPosts(int page, int limit) {
                return apiService.getPosts(page, limit);
               /* remoteDataSource.getPosts(page, limit)*/
    }
    public Observable<OwnerAPIResponse> getUsers(int page, int limit) {
        return apiService.getUsers(page, limit);
        /* remoteDataSource.getPosts(page, limit)*/
    }

    /*private Resource<APIResponse> responseToResource(Observable<APIResponse> response) {
        response.map((Function<APIResponse, Object>) apiResponse -> {
            if(apiResponse.getData()!=null){
                return Resource.success(apiResponse);
            }else{
                return Resource.error("Error",null);
            }
        });
        return Resource.error("Error",null);
    }
*/
}


