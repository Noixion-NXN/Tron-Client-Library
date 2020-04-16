package tv.noixion.troncli.models.contracts;

import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import com.google.gson.JsonObject;
import org.tron.protos.Contract;

/**
 * Contract for updating setting of a smart contract.
 */
public class UpdateSettingContract extends TronContract {
    private final TronAddress contractAddress;
    private final long consumeUserResourcePercent;

    public UpdateSettingContract(Contract.UpdateSettingContract contract) {
        super(Type.UPDATE_SETTING, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.contractAddress = new TronAddress(contract.getContractAddress().toByteArray());
        this.consumeUserResourcePercent = contract.getConsumeUserResourcePercent();
    }

    /**
     * @return The contract address
     */
    public TronAddress getContractAddress() {
        return contractAddress;
    }

    /**
     * @return The new value for consume user resource percent.
     */
    public long getConsumeUserResourcePercent() {
        return consumeUserResourcePercent;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "Contract: " + this.getContractAddress());
        System.out.println(indent + "Consume user: " + this.getConsumeUserResourcePercent());
    }
}
