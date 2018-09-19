package app.Interface;

import app.Models.Miner;
import app.Models.MinerSummary;
import app.Models.Response.GetResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public interface ApiMethod {

    @FormUrlEncoded
    @POST("modules/miner/get")
    Call<GetResponse<Miner>> getMiner(@Field("api_token") String apiToken);

    @POST("public/miner/summary")
    Call<Object> sendSummary(@Body MinerSummary minerSummary);
}
