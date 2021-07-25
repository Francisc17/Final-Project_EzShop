package pt.ipc.estgoh.ezshop.data.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import pt.ipc.estgoh.ezshop.data.api.Api;
import pt.ipc.estgoh.ezshop.data.api.ShoppingListService;
import pt.ipc.estgoh.ezshop.data.model.EzResponse;
import pt.ipc.estgoh.ezshop.data.model.ListItem;
import pt.ipc.estgoh.ezshop.data.model.ShoppingList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingListRepository {
    private final ShoppingListService shoppingListService;

    public ShoppingListRepository(final Context aContext) {
        this.shoppingListService = Api.getInstance(aContext).create(ShoppingListService.class);
    }

    public LiveData<EzResponse<String>> createShoppingList(final ShoppingList aShoppingList) {
        final MutableLiveData<EzResponse<String>> listMutableLiveData = new MutableLiveData<>();

        this.shoppingListService.createList(aShoppingList).enqueue(new Callback<EzResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<EzResponse<String>> call, @NonNull Response<EzResponse<String>> response) {
                listMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<EzResponse<String>> call, @NonNull Throwable t) {
                listMutableLiveData.postValue(null);
            }
        });
        return listMutableLiveData;
    }

    public LiveData<EzResponse<ShoppingList>> getShoppingList(final long aId) {
        final MutableLiveData<EzResponse<ShoppingList>> listMutableLiveData = new MutableLiveData<>();

        this.shoppingListService.getList(aId).enqueue(new Callback<EzResponse<ShoppingList>>() {
            @Override
            public void onResponse(@NonNull Call<EzResponse<ShoppingList>> call, @NonNull Response<EzResponse<ShoppingList>> response) {
                listMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<EzResponse<ShoppingList>> call, @NonNull Throwable t) {
                listMutableLiveData.postValue(null);
            }
        });

        return listMutableLiveData;
    }

    public LiveData<EzResponse<String>> updateShoppingList(final long aId, final ShoppingList aShoppingList) {
        final MutableLiveData<EzResponse<String>> listMutableLiveData = new MutableLiveData<>();

        this.shoppingListService.updateList(aId, aShoppingList).enqueue(new Callback<EzResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<EzResponse<String>> call, @NonNull Response<EzResponse<String>> response) {
                listMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<EzResponse<String>> call, @NonNull Throwable t) {
                listMutableLiveData.postValue(null);
            }
        });

        return listMutableLiveData;
    }

    public LiveData<EzResponse<List<ShoppingList>>> getShoppingLists() {
        final MutableLiveData<EzResponse<List<ShoppingList>>> listMutableLiveData = new MutableLiveData<>();

        this.shoppingListService.getLists().enqueue(new Callback<EzResponse<List<ShoppingList>>>() {
            @Override
            public void onResponse(@NonNull Call<EzResponse<List<ShoppingList>>> call, @NonNull Response<EzResponse<List<ShoppingList>>> response) {
                listMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<EzResponse<List<ShoppingList>>> call, @NonNull Throwable t) {
                listMutableLiveData.postValue(null);
            }
        });
        return listMutableLiveData;
    }

    public LiveData<EzResponse<String>> removeShoppingList(final long aId) {
        final MutableLiveData<EzResponse<String>> listMutableLiveData = new MutableLiveData<>();

        this.shoppingListService.removeList(aId).enqueue(new Callback<EzResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<EzResponse<String>> call, @NonNull Response<EzResponse<String>> response) {
                listMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<EzResponse<String>> call, @NonNull Throwable t) {
                listMutableLiveData.postValue(null);
            }
        });
        return listMutableLiveData;
    }

    public LiveData<EzResponse<String>> updateListItem(final long aId, final ListItem aListItem) {
        final MutableLiveData<EzResponse<String>> listMutableLiveData = new MutableLiveData<>();

        this.shoppingListService.updateListItem(aId, aListItem).enqueue(new Callback<EzResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<EzResponse<String>> call, @NonNull Response<EzResponse<String>> response) {
                listMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<EzResponse<String>> call, @NonNull Throwable t) {
                listMutableLiveData.postValue(null);
            }
        });
        return listMutableLiveData;
    }

    public LiveData<EzResponse<String>> addItemToList(final long aId, final ListItem aListItem) {
        final MutableLiveData<EzResponse<String>> listMutableLiveData = new MutableLiveData<>();

        this.shoppingListService.addItemToList(aId, aListItem).enqueue(new Callback<EzResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<EzResponse<String>> call, @NonNull Response<EzResponse<String>> response) {
                listMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<EzResponse<String>> call, @NonNull Throwable t) {
                listMutableLiveData.postValue(null);
            }
        });

        return listMutableLiveData;
    }

    public LiveData<EzResponse<ListItem>> associateItemToList(final long aId, final ListItem aListItem) {
        final MutableLiveData<EzResponse<ListItem>> listMutableLiveData = new MutableLiveData<>();

        this.shoppingListService.associateItemToList(aId, aListItem).enqueue(new Callback<EzResponse<ListItem>>() {
            @Override
            public void onResponse(@NonNull Call<EzResponse<ListItem>> call, @NonNull Response<EzResponse<ListItem>> response) {
                listMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<EzResponse<ListItem>> call, @NonNull Throwable t) {
                listMutableLiveData.postValue(null);
            }
        });

        return listMutableLiveData;
    }

    public LiveData<EzResponse<String>> removeItemFromList(final long aListId, final long aItemId) {
        final MutableLiveData<EzResponse<String>> listMutableLiveData = new MutableLiveData<>();

        this.shoppingListService.removeItemFromList(aListId, aItemId).enqueue(new Callback<EzResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<EzResponse<String>> call, @NonNull Response<EzResponse<String>> response) {
                listMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<EzResponse<String>> call, @NonNull Throwable t) {
                listMutableLiveData.postValue(null);
            }
        });

        return listMutableLiveData;
    }

    public LiveData<EzResponse<String>> shareList(final long aListId, final String aEmail) {
        final MutableLiveData<EzResponse<String>> listMutableLiveData = new MutableLiveData<>();

        this.shoppingListService.shareList(aListId, aEmail).enqueue(new Callback<EzResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<EzResponse<String>> call, @NonNull Response<EzResponse<String>> response) {
                listMutableLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<EzResponse<String>> call, @NonNull Throwable t) {
                listMutableLiveData.postValue(null);
            }
        });

        return listMutableLiveData;
    }
}
