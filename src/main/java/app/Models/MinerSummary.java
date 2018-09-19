package app.Models;

import com.google.gson.annotations.SerializedName;

/**
 * @author Lam Kai Loon <lkloon123@hotmail.com>
 */
public class MinerSummary {
    @SerializedName("algo")
    public String algo;
    @SerializedName("gpu_count")
    public int gpuCount;
    @SerializedName("hashrate")
    public float hashrate;
    @SerializedName("accepted_hash")
    public int acceptedHash;
    @SerializedName("rejected_hash")
    public int rejectedHash;
    @SerializedName("up_time")
    public int upTime;
    @SerializedName("timestamp")
    public int timestamp;
    @SerializedName("api_token")
    public String apiToken;

    public MinerSummary() {
    }

    public MinerSummary(String algo, int gpuCount, float hashrate, int acceptedHash, int rejectedHash, int upTime, int timestamp, String apiToken) {
        this.algo = algo;
        this.gpuCount = gpuCount;
        this.hashrate = hashrate;
        this.acceptedHash = acceptedHash;
        this.rejectedHash = rejectedHash;
        this.upTime = upTime;
        this.timestamp = timestamp;
        this.apiToken = apiToken;
    }
}
