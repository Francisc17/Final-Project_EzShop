package pt.ipc.estgoh.ezshop.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pt.ipc.estgoh.ezshop.data.model.EzResponse;
import pt.ipc.estgoh.ezshop.data.model.Item;
import pt.ipc.estgoh.ezshop.data.repositories.ItemRepository;

public class ItemViewModel extends AndroidViewModel {
    private final ItemRepository itemRepository;
    private final MutableLiveData<EzResponse<List<Item>>> items;

    public ItemViewModel(@NonNull Application application) {
        super(application);
        this.itemRepository = new ItemRepository(application.getApplicationContext());
        this.items = this.itemRepository.getListMutableLiveData();
    }

    public void searchItem(final String aName) {
        this.itemRepository.searchItem(aName);
    }

    public void clearItems() {
        this.items.setValue(new EzResponse<>());
    }

    public LiveData<EzResponse<String>> uploadPhoto(final MultipartBody.Part aImage, final RequestBody aName) {
        return this.itemRepository.uploadPhoto(aImage, aName);
    }

    public LiveData<EzResponse<List<Item>>> getItemsLiveData() {
        return this.items;
    }
}

