package pt.ipc.estgoh.ezshop.data.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import pt.ipc.estgoh.ezshop.R;
import pt.ipc.estgoh.ezshop.data.anotations.Exclude;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Api {
    private static Retrofit instance;
    private static final String BASE_URL = "http://192.168.1.39:8080/";

    private static final ExclusionStrategy strategy = new ExclusionStrategy() {
        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes field) {
            return field.getAnnotation(Exclude.class) != null;
        }
    };

    public static Retrofit getInstance(final Context aContext) {
        if (instance == null)
            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setExclusionStrategies(Api.strategy).create())).build();

        return instance.newBuilder().client(getHttpClient(getToken(aContext))).build();
    }

    private static String getToken(final Context aContext) {
        final SharedPreferences sharedPref = aContext.getSharedPreferences(aContext.getString(R.string.user_prefs), Context.MODE_PRIVATE);
        return sharedPref.getString("token", "");
    }

    private static OkHttpClient getHttpClient(final String aToken) {
        return new OkHttpClient.Builder().addInterceptor(chain -> {
            Request newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + aToken)
                    .build();
            return chain.proceed(newRequest);
        }).build();
    }
}
