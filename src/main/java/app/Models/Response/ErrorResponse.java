package app.Models.Response;

import app.Models.ErrorData;
import com.google.gson.annotations.SerializedName;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public class ErrorResponse {
    @SerializedName("success")
    public boolean isSuccess;
    @SerializedName("error")
    public ErrorData errorData;

    public ErrorResponse(boolean isSuccess, ErrorData errorData) {
        this.isSuccess = isSuccess;
        this.errorData = errorData;
    }

    @Override
    public String toString() {
        return "ErrorResponse {" +
                "\nisSuccess=" + isSuccess +
                "\nerrorData=" + errorData +
                '}';
    }
}
