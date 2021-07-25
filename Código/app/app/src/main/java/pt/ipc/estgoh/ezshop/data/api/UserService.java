package pt.ipc.estgoh.ezshop.data.api;

import pt.ipc.estgoh.ezshop.data.model.EzResponse;
import pt.ipc.estgoh.ezshop.data.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {
    @FormUrlEncoded
    @POST("users/login")
    Call<EzResponse<String>> login(@Field("email") final String email, @Field("password") final String password);

    @POST("users")
    Call<EzResponse<String>> register(@Body final User user);
}
