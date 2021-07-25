package pt.ipc.estgoh.ezshop.data.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import pt.ipc.estgoh.ezshop.data.api.Api;
import pt.ipc.estgoh.ezshop.data.api.UserService;
import pt.ipc.estgoh.ezshop.data.model.EzResponse;
import pt.ipc.estgoh.ezshop.data.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final UserService userService;

    public UserRepository(final Context aContext) {
        this.userService = Api.getInstance(aContext).create(UserService.class);
    }

    public LiveData<EzResponse<String>> login(final String aEmail, final String aPassword) {
        final MutableLiveData<EzResponse<String>> userMutableLiveData = new MutableLiveData<>();

        this.userService.login(aEmail, aPassword).enqueue(new Callback<EzResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<EzResponse<String>> call, @NonNull Response<EzResponse<String>> response) {
                userMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<EzResponse<String>> call, @NonNull Throwable t) {
                userMutableLiveData.postValue(null);
            }
        });

        return userMutableLiveData;
    }


    public LiveData<EzResponse<String>> register(User aUser) {
        final MutableLiveData<EzResponse<String>> userMutableLiveData = new MutableLiveData<>();

        this.userService.register(aUser).enqueue(new Callback<EzResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<EzResponse<String>> call, @NonNull Response<EzResponse<String>> response) {
                userMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<EzResponse<String>> call, @NonNull Throwable t) {
                userMutableLiveData.postValue(null);
            }
        });

        return userMutableLiveData;
    }
}
