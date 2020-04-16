package tv.noixion.troncli.models.contracts;
import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronSmartContract;
import tv.noixion.troncli.models.TronContract;
import com.google.gson.JsonObject;
import org.tron.protos.Contract;

/**
 * Contract for deploying a smart contract.
 */
public class CreateSmartContractContract extends TronContract {
    private final TronSmartContract smartContract;

    public CreateSmartContractContract(Contract.CreateSmartContract contract) {
        super(Type.CREATE_SMART_CONTRACT, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.smartContract = new TronSmartContract(contract.getNewContract());
    }

    /**
     * @return The smart contract created.
     */
    public TronSmartContract getSmartContract() {
        return smartContract;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        this.smartContract.print(indent);
    }
}
