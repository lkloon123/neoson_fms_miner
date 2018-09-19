package app.Interface;

import app.Models.Miner;
import app.Models.Response.ErrorResponse;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public interface ApiHandler<T> {
    void handle(T data, ErrorResponse error);
}
