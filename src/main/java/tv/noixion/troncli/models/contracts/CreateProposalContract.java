package tv.noixion.troncli.models.contracts;
import com.google.gson.JsonObject;
import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import org.tron.protos.Contract;

import java.util.Map;

/**
 * Contract for creating a proposal.
 */
public class CreateProposalContract extends TronContract {
    private final Map<Long, Long> parameters;

    public CreateProposalContract (Contract.ProposalCreateContract contract) {
        super(Type.CREATE_PROPOSAL, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.parameters = contract.getParametersMap();
    }

    /**
     * @return The parameters
     */
    public Map<Long, Long> getParameters() {
        return parameters;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "Parameters:");
        for (Long param : this.parameters.keySet()) {
            System.out.println(indent + "    " + param + " -> " + this.parameters.get(param));
        }
    }
}
