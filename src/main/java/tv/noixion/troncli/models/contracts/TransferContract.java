package tv.noixion.troncli.models.contracts;

import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import tv.noixion.troncli.models.TronCurrency;
import com.google.gson.JsonObject;
import org.tron.protos.Contract;

/**
 * Contract for transferring coins (TRX)
 */
public class TransferContract extends TronContract {
    private final TronAddress toAddress;
    private final TronCurrency amount;

    public TransferContract(Contract.TransferContract contract) {
        super(Type.TRANSFER, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.toAddress = new TronAddress(contract.getToAddress().toByteArray());
        this.amount = TronCurrency.sun(contract.getAmount());
    }

    /**
     * @return The beneficiary address.
     */
    public TronAddress getToAddress() {
        return toAddress;
    }

    /**
     * @return The amount transferred.
     */
    public TronCurrency getAmount() {
        return amount;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "Amount: " + String.format("%.0f TRX", this.getAmount().getTRX()));
        System.out.println(indent + "To: " + this.getToAddress());
    }
}
