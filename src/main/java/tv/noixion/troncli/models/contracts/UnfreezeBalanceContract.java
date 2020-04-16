package tv.noixion.troncli.models.contracts;

import com.google.gson.JsonObject;
import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import tv.noixion.troncli.models.TronResource;
import org.tron.protos.Contract;

/**
 * Contract for unfreezing balance.
 */
public class UnfreezeBalanceContract extends TronContract {
    private final TronResource resource;

    public UnfreezeBalanceContract(Contract.UnfreezeBalanceContract contract) {
        super(Type.UNFREEZE_BALANCE, new TronAddress(contract.getOwnerAddress().toByteArray()));
        switch (contract.getResource()) {
            case ENERGY:
                this.resource = TronResource.ENERGY;
                break;
            case BANDWIDTH:
                this.resource = TronResource.BANDWIDTH;
                break;
            default:
                this.resource = TronResource.UNKNOWN;
        }
    }

    /**
     * @return The resource.
     */
    public TronResource getResource() {
        return resource;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "Resource: " + this.getResource().toString());
    }
}
