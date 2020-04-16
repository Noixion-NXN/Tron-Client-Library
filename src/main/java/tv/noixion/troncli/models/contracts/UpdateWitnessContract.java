package tv.noixion.troncli.models.contracts;

import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import com.google.gson.JsonObject;
import org.tron.protos.Contract;

/**
 * Contract for updating a witness.
 */
public class UpdateWitnessContract extends TronContract {
    private final String newUrl;

    public UpdateWitnessContract(Contract.WitnessUpdateContract contract) {
        super(Type.WITNESS_UPDATE, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.newUrl = new String(contract.getUpdateUrl().toByteArray());
    }

    /**
     * @return The new URL.
     */
    public String getNewUrl() {
        return newUrl;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "URL: " + this.getNewUrl());
    }
}
