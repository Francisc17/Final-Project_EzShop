package pt.ipc.estgoh.ezshop.data.api;

import java.util.List;

import pt.ipc.estgoh.ezshop.data.model.EzResponse;
import pt.ipc.estgoh.ezshop.data.model.ListItem;
import pt.ipc.estgoh.ezshop.data.model.ShoppingList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ShoppingListService {
    @POST("/lists")
    Call<EzResponse<String>> createList(@Body final ShoppingList shoppingList);

    @GET("/lists/{id}")
    Call<EzResponse<ShoppingList>> getList(@Path("id") long aId);

    @PUT("/lists/{id}")
    Call<EzResponse<String>> updateList(@Path("id") final long aId, @Body final ShoppingList shoppingList);

    @GET("/lists")
    Call<EzResponse<List<ShoppingList>>> getLists();

    @DELETE("/lists/{id}")
    Call<EzResponse<String>> removeList(@Path("id") final long id);

    @PUT("/lists/{id}/items")
    Call<EzResponse<String>> updateListItem(@Path("id") final long id, @Body final ListItem listItem);

    @PATCH("/lists/{id}/items")
    Call<EzResponse<ListItem>> associateItemToList(@Path("id") final long id, @Body final ListItem listItem);

    @POST("/lists/{id}/items")
    Call<EzResponse<String>> addItemToList(@Path("id") final long id, @Body final ListItem listItem);

    @DELETE("/lists/{listId}/items/{itemId}")
    Call<EzResponse<String>> removeItemFromList(@Path("listId") final long listId, @Path("itemId") final long itemId);

    @FormUrlEncoded
    @POST("/lists/{id}/share")
    Call<EzResponse<String>> shareList(@Path("id") final long listId, @Field("email") final String email);
}