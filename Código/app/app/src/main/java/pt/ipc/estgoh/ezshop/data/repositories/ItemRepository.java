package pt.ipc.estgoh.ezshop.data.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pt.ipc.estgoh.ezshop.data.api.Api;
import pt.ipc.estgoh.ezshop.data.api.ItemService;
import pt.ipc.estgoh.ezshop.data.model.EzResponse;
import pt.ipc.estgoh.ezshop.data.model.Item;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemRepository implements Callback<EzResponse<List<Item>>> {

    private final ItemService itemService;
    private final MutableLiveData<EzResponse<List<Item>>> listMutableLiveData;

    public ItemRepository(final Context aContext) {
        this.listMutableLiveData = new MutableLiveData<>();
        this.itemService = Api.getInstance(aContext).create(ItemService.class);
    }

    public void getAll() {
        this.itemService.getAllItems().enqueue(this);
    }

    public void searchItem(final String aName) {
        this.itemService.searchItemsByName(aName).enqueue(this);
    }

    public LiveData<EzResponse<String>> uploadPhoto(final MultipartBody.Part aImage, final RequestBody aName) {
        final MutableLiveData<EzResponse<String>> mutableLiveData = new MutableLiveData<>();

        this.itemService.uploadPhoto(aImage, aName).enqueue(new Callback<EzResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<EzResponse<String>> call, @NonNull Response<EzResponse<String>> response) {
                mutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<EzResponse<String>> call, @NonNull Throwable t) {
                mutableLiveData.postValue(null);
            }
        });

        return mutableLiveData;
    }


    @Override
    public void onResponse(@NonNull Call<EzResponse<List<Item>>> call, @NonNull Response<EzResponse<List<Item>>> response) {
        listMutableLiveData.postValue(response.body());
    }

    @Override
    public void onFailure(@NonNull Call<EzResponse<List<Item>>> call, @NonNull Throwable t) {
        listMutableLiveData.postValue(null);
    }


    public MutableLiveData<EzResponse<List<Item>>> getListMutableLiveData() {
        return listMutableLiveData;
    }
}
