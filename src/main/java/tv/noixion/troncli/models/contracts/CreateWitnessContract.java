package tv.noixion.troncli.models.contracts;
import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import com.google.gson.JsonObject;
import org.tron.protos.Contract;

/**
 * Contract for creating a witness.
 */
public class CreateWitnessContract extends TronContract {
    private final String url;

    public CreateWitnessContract(Contract.WitnessCreateContract contract) {
        super(Type.CREATE_WITNESS, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.url = new String(contract.getUrl().toByteArray());
    }

    /**
     * @return
     */
    public String getUrl() {
        return url;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "URL: " + this.getUrl());
    }
}
