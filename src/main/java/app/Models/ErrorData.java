package app.Models;

import com.google.gson.annotations.SerializedName;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public class ErrorData {
    @SerializedName("message")
    public String message;
    @SerializedName("status_code")
    public String code;

    public ErrorData(String message, String code) {
        this.message = message;
        this.code = code;
    }
}
