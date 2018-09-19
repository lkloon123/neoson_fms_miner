package app.Helper;

import app.Config.Config;
import app.Interface.ApiMethod;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public class ApiClient {
    public static ApiMethod getInterface() {
        return getClient().create(ApiMethod.class);
    }

    public static Retrofit getClient() {
        return new Retrofit.Builder()
                .baseUrl(Config.API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
