package pt.ipc.estgoh.ezshop.data.api;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pt.ipc.estgoh.ezshop.data.model.EzResponse;
import pt.ipc.estgoh.ezshop.data.model.Item;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ItemService {
    @GET("items")
    Call<EzResponse<List<Item>>> getAllItems();

    @GET("items")
    Call<EzResponse<List<Item>>> searchItemsByName(@Query("name") final String aName);

    @Multipart
    @POST("items/uploadPhoto")
    Call<EzResponse<String>> uploadPhoto(@Part final MultipartBody.Part image, @Part("name") final RequestBody name);
}
