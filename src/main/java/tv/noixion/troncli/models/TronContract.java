package tv.noixion.troncli.models;

import com.google.gson.JsonObject;

/**
 * Represents a contract on a transaction on the Tron network.
 */
public class TronContract {
    public enum Type {
        ACCOUNT_CREATION,
        TRANSFER,
        TRANSFER_ASSET,
        VOTE_ASSET,
        VOTE_WITNESS,
        CREATE_WITNESS,
        ASSET_ISSUE,
        WITNESS_UPDATE,
        PARTICIPATE_ASSET,
        ACCOUNT_UPDATE,
        FREEZE_BALANCE,
        UNFREEZE_BALANCE,
        WITHDRAW_BALANCE,
        UNFREEZE_ASSET,
        UPDATE_ASSET,
        CREATE_PROPOSAL,
        APPROVE_PROPOSAL,
        DELETE_PROPOSAL,
        SET_ACCOUNT_ID,
        CUSTOM,
        CREATE_SMART_CONTRACT,
        TRIGGER_SMART_CONTRACT,
        GET_CONTRACT,
        UPDATE_SETTING,
        EXCHANGE_CREATE,
        EXCHANGE_INJECT,
        EXCHANGE_WITHDRAW,
        EXCHANGE_TRANSACTION,
        UNKNOWN;
    }

    private final Type type;
    private final TronAddress ownerAddress;

    public TronContract(Type type, TronAddress ownerAddress) {
        this.ownerAddress = ownerAddress;
        this.type = type;
    }

    /**
     * @return The contract type.
     */
    public Type getType() {
        return type;
    }

    /**
     * @return The account of the owner of the contract.
     */
    public TronAddress getOwnerAddress() {
        return this.ownerAddress;
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
    public void print(String indent) {
        System.out.println(indent + "Sender: " + this.getOwnerAddress().toString());
    }
}
