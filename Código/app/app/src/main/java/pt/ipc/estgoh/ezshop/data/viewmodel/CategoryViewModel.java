package pt.ipc.estgoh.ezshop.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pt.ipc.estgoh.ezshop.data.model.Category;
import pt.ipc.estgoh.ezshop.data.model.EzResponse;
import pt.ipc.estgoh.ezshop.data.repositories.CategoryRepository;

public class CategoryViewModel extends AndroidViewModel {
    private final CategoryRepository categoryRepository;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        this.categoryRepository = new CategoryRepository(application.getApplicationContext());
    }

    public LiveData<EzResponse<List<Category>>> getCategories() {
        return this.categoryRepository.getCategoryService();
    }
}
