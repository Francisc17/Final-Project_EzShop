package pt.ipc.estgoh.ezshop.data.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import pt.ipc.estgoh.ezshop.data.api.Api;
import pt.ipc.estgoh.ezshop.data.api.CategoryService;
import pt.ipc.estgoh.ezshop.data.model.Category;
import pt.ipc.estgoh.ezshop.data.model.EzResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    private final CategoryService categoryService;

    public CategoryRepository(final Context aContext) {
        this.categoryService = Api.getInstance(aContext).create(CategoryService.class);
    }

    public LiveData<EzResponse<List<Category>>> getCategoryService() {
        final   MutableLiveData<EzResponse<List<Category>>> categoriesMutableLiveData = new MutableLiveData<>();

        this.categoryService.getAllCategories().enqueue(new Callback<EzResponse<List<Category>>>() {
            @Override
            public void onResponse(@NonNull Call<EzResponse<List<Category>>> call, @NonNull Response<EzResponse<List<Category>>> response) {
                categoriesMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<EzResponse<List<Category>>> call, @NonNull Throwable t) {
                categoriesMutableLiveData.postValue(null);
            }
        });
        return categoriesMutableLiveData;
    }
}
