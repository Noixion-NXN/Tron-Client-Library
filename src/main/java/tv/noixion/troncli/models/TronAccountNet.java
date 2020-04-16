package tv.noixion.troncli.models;

import org.tron.api.GrpcAPI;

import java.util.Map;

/**
 * Represents an account net usage information.
 */
public class TronAccountNet {
    private final long freeNetUsed;
    private final long freeNetLimit;
    private final long netUsed;
    private final long netLimit;
    private final long totalNetLimit;
    private final long netWeight;

    private final Map<String, Long> assetNetUsedMap;
    private final Map<String, Long> assetNetLimitMap;

    public TronAccountNet(GrpcAPI.AccountNetMessage msg) {
        this.freeNetLimit = msg.getFreeNetLimit();
        this.freeNetUsed = msg.getFreeNetUsed();
        this.netUsed = msg.getNetUsed();
        this.netLimit = msg.getNetLimit();
        this.totalNetLimit = msg.getTotalNetLimit();
        this.netWeight = msg.getTotalNetWeight();

        this.assetNetUsedMap = msg.getAssetNetUsedMap();
        this.assetNetLimitMap = msg.getAssetNetLimitMap();
    }

    /**
     * @return the free net used.
     */
    public long getFreeNetUsed() {
        return freeNetUsed;
    }

    /**
     * @return the free net limit.
     */
    public long getFreeNetLimit() {
        return freeNetLimit;
    }

    /**
     * @return the non-free net used.
     */
    public long getNetUsed() {
        return netUsed;
    }

    /**
     * @return the non-free net limit.
     */
    public long getNetLimit() {
        return netLimit;
    }

    /**
     * @return the total net limit.
     */
    public long getTotalNetLimit() {
        return totalNetLimit;
    }

    /**
     * @return the total net weight.
     */
    public long getNetWeight() {
        return netWeight;
    }

    /**
     * @return A map mapping each asset with the net usage.
     */
    public Map<String, Long> getAssetNetUsedMap() {
        return assetNetUsedMap;
    }

    /**
     * @return A map mapping each asset with the net limit.
     */
    public Map<String, Long> getAssetNetLimitMap() {
        return assetNetLimitMap;
    }
}
