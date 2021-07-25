package pt.ipc.estgoh.ezshop.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import pt.ipc.estgoh.ezshop.data.model.EzResponse;
import pt.ipc.estgoh.ezshop.data.model.ListItem;
import pt.ipc.estgoh.ezshop.data.model.ShoppingList;
import pt.ipc.estgoh.ezshop.data.repositories.ShoppingListRepository;

public class ShoppingListsViewModel extends AndroidViewModel {
    private final ShoppingListRepository shoppingListRepository;
    private final MutableLiveData<Boolean> refreshData = new MutableLiveData<>(false);
    private final MutableLiveData<ShoppingList> currentShoppingList = new MutableLiveData<>();

    public ShoppingListsViewModel(@NonNull Application application) {
        super(application);
        this.shoppingListRepository = new ShoppingListRepository(application.getApplicationContext());
    }

    public LiveData<EzResponse<String>> addShoppingList(final ShoppingList aList) {
        if (aList != null)
            return this.shoppingListRepository.createShoppingList(aList);
        return null;
    }

    public LiveData<EzResponse<ShoppingList>> getShoppingList(final long aId) {
        return this.shoppingListRepository.getShoppingList(aId);
    }


    public LiveData<EzResponse<String>> updateShoppingList(final long aListId, final ShoppingList aShoppingList) {
        return this.shoppingListRepository.updateShoppingList(aListId, aShoppingList);
    }

    public LiveData<EzResponse<List<ShoppingList>>> getShoppingLists() {
        return Transformations.switchMap(refreshData,
                shoppingListsObservables -> this.shoppingListRepository.getShoppingLists());
    }

    public LiveData<EzResponse<String>> removeShoppingList(final long aId) {
        return this.shoppingListRepository.removeShoppingList(aId);
    }

    public LiveData<EzResponse<String>> updateListItem(final long aId, final ListItem aListItem) {
        return this.shoppingListRepository.updateListItem(aId, aListItem);
    }

    public LiveData<EzResponse<String>> addItemToList(final long aId, final ListItem aListItem) {
        return this.shoppingListRepository.addItemToList(aId, aListItem);
    }

    public LiveData<EzResponse<ListItem>> associateItemToList(final long aId, final ListItem aListItem) {
        return this.shoppingListRepository.associateItemToList(aId, aListItem);
    }

    public LiveData<EzResponse<String>> removeItemFromList(final long aListId, final long aItemId) {
        return this.shoppingListRepository.removeItemFromList(aListId, aItemId);
    }

    public LiveData<EzResponse<String>> shareList(final long aListId, final String aEmail) {
        return this.shoppingListRepository.shareList(aListId, aEmail);
    }

    public void refreshData() {
        refreshData.setValue(true);
    }

    public void setCurrentShoppingList(final ShoppingList aShoppingList) {
        this.currentShoppingList.setValue(aShoppingList);
    }

    public LiveData<ShoppingList> getCurrentShoppingList() {
        return this.currentShoppingList;
    }
}
