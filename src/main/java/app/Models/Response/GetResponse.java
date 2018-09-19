package app.Models.Response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public class GetResponse<T> {
    @SerializedName("success")
    public boolean isSuccess;
    @SerializedName("data")
    public T data;
}
