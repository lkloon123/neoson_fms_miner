package app.Helper;

import app.Interface.ApiHandler;
import app.Interface.Logging;
import app.Models.ErrorData;
import app.Models.Response.ErrorResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public class ApiWrapper<T> implements Logging {
    public void execute(Call<T> call, final ApiHandler<T> handler) {
        Callback<T> baseCallback = new Callback<T>() {
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    handler.handle(response.body(), null);
                } else {
                    try {
                        //convert the errror
                        Converter<ResponseBody, ErrorResponse> converter = ApiClient.getClient().responseBodyConverter(ErrorResponse.class, new Annotation[0]);
                        ErrorResponse errorResponse = converter.convert(response.errorBody());
                        logger.error(errorResponse.errorData.message);
                        handler.handle(null, errorResponse);
                    } catch (IOException ex) {
                        logger.error(ex.getMessage());
                        LogExceptions.trace(ex);
                        handler.handle(null, new ErrorResponse(false, new ErrorData(ex.getMessage(), "500")));
                    }
                }
            }

            public void onFailure(Call<T> call, Throwable throwable) {
                logger.error(throwable.getMessage());
                LogExceptions.trace(new Exception(throwable));
                handler.handle(null, new ErrorResponse(false, new ErrorData(throwable.getMessage(), "500")));
            }
        };

        call.enqueue(baseCallback);
    }
}
