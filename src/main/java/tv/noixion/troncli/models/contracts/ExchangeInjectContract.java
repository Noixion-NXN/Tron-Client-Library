package tv.noixion.troncli.models.contracts;

import com.google.gson.JsonObject;
import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import org.tron.protos.Contract;

/**
 * Contract for injecting to an exchange.
 */
public class ExchangeInjectContract extends TronContract {
    private final long exchangeId;
    private final long amount;
    private final String tokenName;

    public ExchangeInjectContract(Contract.ExchangeInjectContract contract) {
        super(Type.EXCHANGE_INJECT, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.exchangeId = contract.getExchangeId();
        this.amount = contract.getQuant();
        this.tokenName = new String(contract.getTokenId().toByteArray());
    }

    /**
     * @return The exchange identifier
     */
    public long getExchangeId() {
        return exchangeId;
    }

    /**
     * @return The amount exchanged
     */
    public long getAmount() {
        return amount;
    }

    /**
     * @return the name of the exchanged token
     */
    public String getTokenName() {
        return tokenName;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "Exchange id: " + this.getExchangeId());
        System.out.println(indent + "Amount: " + this.getAmount());
        System.out.println(indent + "Asset: " + this.getTokenName());
    }
}
