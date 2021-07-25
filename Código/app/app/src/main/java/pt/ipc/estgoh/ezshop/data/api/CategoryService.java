package pt.ipc.estgoh.ezshop.data.api;

import java.util.List;

import pt.ipc.estgoh.ezshop.data.model.Category;
import pt.ipc.estgoh.ezshop.data.model.EzResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryService {
    @GET("items/categories")
    Call<EzResponse<List<Category>>> getAllCategories();
}
