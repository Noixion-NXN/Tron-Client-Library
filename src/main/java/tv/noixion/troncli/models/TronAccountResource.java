package tv.noixion.troncli.models;

import com.google.gson.JsonObject;
import org.tron.api.GrpcAPI;

public class TronAccountResource {
    private final long freeNetUsed;
    private final long freeNetLimit;
    private final long netUsed;
    private final long netLimit;
    private final long totalNetLimit;
    private final long totalNetWeight;

    private final long energyUsed;
    private final long energyLimit;
    private final long totalEnergyLimit;
    private final long totalEnergyWeight;

    public TronAccountResource(GrpcAPI.AccountResourceMessage msg) {
        this.freeNetUsed = msg.getFreeNetUsed();
        this.freeNetLimit = msg.getFreeNetLimit();
        this.netUsed = msg.getNetUsed();
        this.netLimit = msg.getNetLimit();
        this.totalNetLimit = msg.getTotalNetLimit();
        this.totalNetWeight = msg.getTotalNetWeight();
        this.energyUsed = msg.getEnergyUsed();
        this.energyLimit = msg.getEnergyLimit();
        this.totalEnergyLimit = msg.getTotalEnergyLimit();
        this.totalEnergyWeight = msg.getTotalEnergyWeight();
    }

    /**
     * @return The free net used by the account.
     */
    public long getFreeNetUsed() {
        return freeNetUsed;
    }

    /**
     * @return The free net limit.
     */
    public long getFreeNetLimit() {
        return freeNetLimit;
    }

    /**
     * @return The not-free net used by the account.
     */
    public long getNetUsed() {
        return netUsed;
    }

    /**
     * @return the not-free net limit for this account.
     */
    public long getNetLimit() {
        return netLimit;
    }

    /**
     * @return The total net limit for this account.
     */
    public long getTotalNetLimit() {
        return totalNetLimit;
    }

    /**
     * @return The total net weight.
     */
    public long getTotalNetWeight() {
        return totalNetWeight;
    }

    /**
     * @return The energy used by this account.
     */
    public long getEnergyUsed() {
        return energyUsed;
    }

    /**
     * @return The energy limit for this account.
     */
    public long getEnergyLimit() {
        return energyLimit;
    }

    /**
     * @return The total energy limit for this account.
     */
    public long getTotalEnergyLimit() {
        return totalEnergyLimit;
    }

    /**
     * @return The total energy weight.
     */
    public long getTotalEnergyWeight() {
        return totalEnergyWeight;
    }

    /**
     * @return The object as Json.
     */
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    /**
     * Prints the object in stdout.
     */
    public void print() {
        System.out.println("Net used:             " + this.netUsed);
        System.out.println("Net limit:            " + this.netLimit);
        System.out.println("Net Used (Free):      " + this.freeNetUsed);
        System.out.println("Net limit (Free):     " + this.freeNetLimit);
        System.out.println("Total net limit:      " + this.totalNetLimit);
        System.out.println("Total net weight:     " + this.totalNetWeight);
        System.out.println();
        System.out.println("Energy used:          " + this.energyUsed);
        System.out.println("Energy limit:         " + this.energyLimit);
        System.out.println("Total energy limit:   " + this.totalEnergyLimit);
        System.out.println("Total energy weight:  " + this.totalEnergyWeight);
    }
}
