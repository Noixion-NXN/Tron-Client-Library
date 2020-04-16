package tv.noixion.troncli.models;

import com.google.gson.JsonObject;
import org.tron.protos.Protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Represents an account on the Tron network.
 */
public class TronAccount {
    /**
     * Represents a frozen balance.
     */
    public class FrozenBalance {
        private final TronCurrency amount;
        private final Date expirationDate;

        public FrozenBalance(long amount, Date expirationDate) {
            this.amount = TronCurrency.sun(amount);
            this.expirationDate = expirationDate;
        }

        /**
         * @return The amount frozen.
         */
        public TronCurrency getAmount() {
            return amount;
        }

        /**
         * @return The expiration date for the amount frozen.
         */
        public Date getExpirationDate() {
            return expirationDate;
        }
    }

    /**
     * Account type
     */
    public enum Type {
        NORMAL,
        ASSET_ISSUE,
        CONTRACT,
        UNKNOWN
    }

    private final TronAddress address;
    private final String name;
    private final Type type;

    private final TronCurrency balance;

    private final Map<String, Long> assets;

    private final Date creationDate;

    private final long netUsage;
    private final long freeNetUsage;

    private final Date latestOperationDate;
    private final Date latestConsumptionDate;
    private final Date latestFreeConsumptionDate;

    private final TronCurrency allowance;
    private final Date latestAllowanceWithdrawTime;

    private final boolean witness;
    private final boolean committee;

    private final long energyUsage;
    private final Date latestEnergyConsumptionDate;

    private final long storageLimit;
    private final long storageUsage;
    private final Date latestExchangeStorageDate;

    private final List<FrozenBalance> frozenBalance;
    private final FrozenBalance frozenBalanceForEnergy;

    private final String assetName;

    public TronAccount(Protocol.Account account) {
        this.address = new TronAddress(account.getAddress().toByteArray());
        this.name = new String(account.getAccountName().toByteArray());
        this.balance = TronCurrency.sun(account.getBalance());
        this.freeNetUsage = account.getFreeNetUsage();
        this.netUsage = account.getNetUsage();
        this.allowance = TronCurrency.sun(account.getAllowance());
        this.witness = account.getIsWitness();
        this.committee = account.getIsCommittee();
        this.assetName = new String(account.getAssetIssuedName().toByteArray());

        this.creationDate = new Date(account.getCreateTime());
        this.latestOperationDate = new Date(account.getLatestOprationTime());
        this.latestConsumptionDate = new Date(account.getLatestConsumeTime());
        this.latestFreeConsumptionDate = new Date(account.getLatestConsumeFreeTime());
        this.latestAllowanceWithdrawTime = new Date(account.getLatestWithdrawTime());

        this.energyUsage = account.getAccountResource().getEnergyUsage();
        this.latestEnergyConsumptionDate = new Date(account.getAccountResource().getLatestConsumeTimeForEnergy());

        this.storageLimit = account.getAccountResource().getStorageLimit();
        this.storageUsage = account.getAccountResource().getStorageUsage();
        this.latestExchangeStorageDate = new Date(account.getAccountResource().getLatestExchangeStorageTime());

        if (account.getAccountResource().hasFrozenBalanceForEnergy()) {
            this.frozenBalanceForEnergy = new FrozenBalance(account.getAccountResource().getFrozenBalanceForEnergy().getFrozenBalance(),
                    new Date(account.getAccountResource().getFrozenBalanceForEnergy().getExpireTime()));
        } else {
            this.frozenBalanceForEnergy = null;
        }

        this.frozenBalance = new ArrayList<>();

        for (Protocol.Account.Frozen f : account.getFrozenList()) {
            this.frozenBalance.add(new FrozenBalance(f.getFrozenBalance(), new Date(f.getExpireTime())));
        }
        this.assets = account.getAssetMap();
        switch (account.getType()) {
            case Normal:
                this.type = Type.NORMAL;
                break;
            case AssetIssue:
                this.type = Type.ASSET_ISSUE;
                break;
            case Contract:
                this.type = Type.CONTRACT;
                break;
            default:
                this.type = Type.UNKNOWN;
        }
    }

    /**
     * @return The account address.
     */
    public TronAddress getAddress() {
        return address;
    }

    /**
     * @return The account name, or empty string if the account does not have name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The account type.
     */
    public Type getType() {
        return type;
    }

    /**
     * @return The balance of the account.
     */
    public TronCurrency getBalance() {
        return balance;
    }

    /**
     * @return The creation date of the account.
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * @return The net usage of the account.
     */
    public long getNetUsage() {
        return netUsage;
    }

    /**
     * @return The free net usage of the account.
     */
    public long getFreeNetUsage() {
        return freeNetUsage;
    }

    /**
     * @return The last time the account did an operation.
     */
    public Date getLatestOperationDate() {
        return latestOperationDate;
    }

    /**
     * @return the last time tha account consumed resources.
     */
    public Date getLatestConsumptionDate() {
        return latestConsumptionDate;
    }

    /**
     * @return The assets of this account.
     */
    public Map<String, Long> getAssets() {
        return assets;
    }

    /**
     * @return The last time the account consumed free resources.
     */
    public Date getLatestFreeConsumptionDate() {
        return latestFreeConsumptionDate;
    }

    /**
     * @return The allowance (Block rewards that can be claimed)
     */
    public TronCurrency getAllowance() {
        return allowance;
    }

    /**
     * @return Get the last time the account got its allowance.
     */
    public Date getLatestAllowanceWithdrawTime() {
        return latestAllowanceWithdrawTime;
    }

    /**
     * @return true if is witness, false if not.
     */
    public boolean isWitness() {
        return witness;
    }

    /**
     * @return true if is a member of the committee, false if not.
     */
    public boolean isCommittee() {
        return committee;
    }

    /**
     * @return The energy used by this account.
     */
    public long getEnergyUsage() {
        return energyUsage;
    }

    /**
     * @return The last time this account consumed energy.
     */
    public Date getLatestEnergyConsumptionDate() {
        return latestEnergyConsumptionDate;
    }

    /**
     * @return The storage limit for this account.
     */
    public long getStorageLimit() {
        return storageLimit;
    }

    /**
     * @return the storage used by this account.
     */
    public long getStorageUsage() {
        return storageUsage;
    }

    /**
     * @return The last time this account did an storage exchange.
     */
    public Date getLatestExchangeStorageDate() {
        return latestExchangeStorageDate;
    }

    /**
     * @return The list of frozen balances
     */
    public List<FrozenBalance> getFrozenBalance() {
        return frozenBalance;
    }

    /**
     * @return The forzen balance for energy, or null if this account does not have balance frozen for energy.
     */
    public FrozenBalance getFrozenBalanceForEnergy() {
        return frozenBalanceForEnergy;
    }

    /**
     * @return The name of the asset created by this account, or empty string if this account has not created any asset yet.
     */
    public String getAssetName() {
        return assetName;
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
        System.out.println("Address:   " + this.getAddress().toString());
        System.out.println("Name:      " + this.getName());
        System.out.println("Type:      " + this.getType().toString());
        System.out.println("Witness:   " + (this.isWitness() ? "Yes" : "No"));
        System.out.println("Committee: " + (this.isCommittee() ? "Yes" : "No"));
        System.out.println("Created:   " + this.getCreationDate().toString());
        System.out.println("Balance:   " + String.format("%.0f TRX", this.getBalance().getTRX()));
        System.out.println("Allowance: " + String.format("%.0f TRX", this.getAllowance().getTRX()));
        if (!this.getFrozenBalance().isEmpty()) {
            System.out.println("Frozen balances:");
            for (FrozenBalance frozen : this.getFrozenBalance()) {
                System.out.println("  " + String.format("%.0f TRX", frozen.getAmount().getTRX()) + " / Expires: " + frozen.getExpirationDate().toString());
            }
        }
        if (this.frozenBalanceForEnergy != null) {
            System.out.println("Frozen balance for energy: ");
            System.out.println("  " + String.format("%.0f TRX", frozenBalanceForEnergy.getAmount().getTRX()) + " / Expires: " + frozenBalanceForEnergy.getExpirationDate().toString());
        }
        if (!this.getAssets().isEmpty()) {
            System.out.println("Assets:");
            for (String asset : this.getAssets().keySet()) {
                System.out.println("  " + asset + ": " + this.getAssets().get(asset).toString());
            }
        }
        if (!this.getAssetName().equals("")) {
            System.out.println("Asset name: " + this.getAssetName());
        }
        System.out.println("Last operation: " + this.getLatestOperationDate().toString());
        System.out.println("Last net consumption: " + this.getLatestConsumptionDate().toString());
        System.out.println("Last free net consumption: " + this.getLatestFreeConsumptionDate().toString());
        System.out.println("Last energy consumption: " + this.getLatestEnergyConsumptionDate().toString());
        System.out.println("Last withdraw: " + this.getLatestAllowanceWithdrawTime().toString());
        System.out.println("Last storage exchange: " + this.getLatestExchangeStorageDate().toString());
    }
}
