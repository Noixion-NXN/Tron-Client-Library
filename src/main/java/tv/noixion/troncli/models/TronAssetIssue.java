package tv.noixion.troncli.models;

import com.google.gson.JsonObject;
import org.tron.protos.Contract;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TronAssetIssue extends TronContract {
    /**
     * Represents a frozen asset supply.
     */
    public static class FrozenSupply {
        private final long amount;
        private final long days;

        /**
         * Creates a new instance of FrozenSupply.
         *
         * @param amount The amount.
         * @param days   The number of days frozen since the creation date.
         */
        public FrozenSupply(long amount, long days) {
            this.amount = amount;
            this.days = days;
        }

        /**
         * @return The amount frozen.
         */
        public long getAmount() {
            return amount;
        }

        /**
         * @return The number of days frozen since the creation date.
         */
        public long getDays() {
            return days;
        }
    }

    private final String name;
    private final String abbreviation;
    private final long totalSupply;
    private final List<FrozenSupply> frozenSupply;
    private final TRXAssetConversion conversion;
    private final Date startDate;
    private final Date endDate;
    private final long order;
    private final int voteScore;
    private final String description;
    private final String url;
    private final long freeAssetLimit;
    private final long publicFreeNetLimit;
    private final long publicFreeNetUsage;
    private final Date latestFreeNetUsage;
    private final int decimals;

    public TronAssetIssue(Contract.AssetIssueContract contract) {
        super(Type.ASSET_ISSUE, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.name = new String(contract.getName().toByteArray());
        this.abbreviation = new String(contract.getAbbr().toByteArray());
        this.totalSupply = contract.getTotalSupply();
        this.frozenSupply = new ArrayList<>();
        for (Contract.AssetIssueContract.FrozenSupply frozen : contract.getFrozenSupplyList()) {
            this.frozenSupply.add(new FrozenSupply(frozen.getFrozenAmount(), frozen.getFrozenDays()));
        }
        this.conversion = new TRXAssetConversion(contract.getNum(), contract.getTrxNum());
        this.startDate = new Date(contract.getStartTime());
        this.endDate = new Date(contract.getEndTime());
        this.order = contract.getOrder();
        this.voteScore = contract.getVoteScore();
        this.description = new String(contract.getDescription().toByteArray());
        this.url = new String(contract.getUrl().toByteArray());
        this.freeAssetLimit = contract.getFreeAssetNetLimit();
        this.publicFreeNetLimit = contract.getPublicFreeAssetNetLimit();
        this.publicFreeNetUsage = contract.getPublicFreeAssetNetUsage();
        this.latestFreeNetUsage = new Date(contract.getPublicLatestFreeNetTime());
        this.decimals = contract.getPrecision();
    }

    /**
     * @return The name of the asset.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The Abbreviation of the asset.
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * @return The total supply.
     */
    public long getTotalSupply() {
        return totalSupply;
    }

    /**
     * @return The frozen supply.
     */
    public List<FrozenSupply> getFrozenSupply() {
        return frozenSupply;
    }

    /**
     * @return The conversion TRX - Asset.
     */
    public TRXAssetConversion getConversion() {
        return conversion;
    }

    /**
     * @return The start date for this asset.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @return the expiration date for this asset.
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @return The order.
     */
    public long getOrder() {
        return order;
    }

    /**
     * @return the vote score.
     */
    public int getVoteScore() {
        return voteScore;
    }

    /**
     * @return the asset description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The asset organization url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return The free asset limit.
     */
    public long getFreeAssetLimit() {
        return freeAssetLimit;
    }

    /**
     * @return The free net limit for this asset.
     */
    public long getPublicFreeNetLimit() {
        return publicFreeNetLimit;
    }

    /**
     * @return The net usage by this asset.
     */
    public long getPublicFreeNetUsage() {
        return publicFreeNetUsage;
    }

    /**
     * @return The latest time the net was used by this asset.
     */
    public Date getLatestFreeNetUsage() {
        return latestFreeNetUsage;
    }

    /**
     * @return The precision (number of decimals).
     */
    public int getDecimals() {
        return decimals;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "Name:         " + this.getName());
        System.out.println(indent + "Abbreviation: " + this.getAbbreviation());
        System.out.println(indent + "Owner: " + this.getOwnerAddress().toString());
        System.out.println(indent + "Decimals: " + this.getDecimals());
        System.out.println(indent + "URL: " + this.getUrl());
        System.out.println(indent + "Description: " + this.getDescription());
        System.out.println(indent + "Order: " + this.getOrder());
        System.out.println(indent + "Vote Score: " + this.getVoteScore());
        System.out.println(indent + "Conversion: " + this.getConversion().getNum() + " " + this.getName() + " = "
                + this.getConversion().getTrxNum() + " TRX");
        System.out.println(indent + "Total Supply: " + this.getTotalSupply());
        System.out.println(indent + "Start Date: " + this.getStartDate().toString());
        System.out.println(indent + "End Date: " + this.getEndDate().toString());
        System.out.println(indent + "Free asset limit: " + this.getFreeAssetLimit());
        System.out.println(indent + "Free net usage: " + this.getPublicFreeNetUsage());
        System.out.println(indent + "Free net limit: " + this.getPublicFreeNetLimit());
        System.out.println(indent + "Last free net usage: " + this.getLatestFreeNetUsage().toString());
        if (!this.getFrozenSupply().isEmpty()) {
            System.out.println("Frozen supply: ");
            for (FrozenSupply f : this.getFrozenSupply()) {
                System.out.println("  " + f.getAmount() + " " + this.getName() + " frozen for " + f.getDays() + " days.");
            }
        }
    }

}
