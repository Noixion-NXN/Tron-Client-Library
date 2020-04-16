package tv.noixion.troncli.models.contracts;

import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import com.google.gson.JsonObject;
import org.tron.protos.Contract;

/**
 * Contract for creating an exchange.
 */
public class CreateExchangeContract extends TronContract {
    private final String firstToken;
    private final long firstTokenAmount;

    private final String secondToken;
    private final long secondTokenAmount;

    public CreateExchangeContract(Contract.ExchangeCreateContract contract) {
        super(Type.EXCHANGE_CREATE, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.firstToken = new String(contract.getFirstTokenId().toByteArray());
        this.firstTokenAmount = contract.getFirstTokenBalance();
        this.secondToken = new String(contract.getSecondTokenId().toByteArray());
        this.secondTokenAmount = contract.getSecondTokenBalance();
    }

    /**
     * @return The token exchanged.
     */
    public String getFirstToken() {
        return firstToken;
    }

    /**
     * @return The amount of token exchanged.
     */
    public long getFirstTokenAmount() {
        return firstTokenAmount;
    }

    /**
     * @return The token wanted.
     */
    public String getSecondToken() {
        return secondToken;
    }

    /**
     * @return The amount of token wanted.
     */
    public long getSecondTokenAmount() {
        return secondTokenAmount;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "First: " + this.getFirstTokenAmount() + " " + this.getFirstToken());
        System.out.println(indent + "Second: " + this.getSecondTokenAmount() + " " + this.getSecondToken());
    }
}
