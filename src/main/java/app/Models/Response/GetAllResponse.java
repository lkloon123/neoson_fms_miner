package app.Models.Response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public class GetAllResponse<T> {
    @SerializedName("success")
    public boolean isSuccess;
    @SerializedName("data")
    public List<T> data;
}
