package app.Models;

import com.google.gson.annotations.SerializedName;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public class Miner {
    @SerializedName("id")
    public String id;
    @SerializedName("miner_name")
    public String minerName;

    public Miner(String id, String minerName) {
        this.id = id;
        this.minerName = minerName;
    }
}
