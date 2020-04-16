package tv.noixion.troncli.models.contracts;
import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import com.google.gson.JsonObject;
import org.tron.protos.Contract;

/**
 * Contract for deleting a proposal.
 */
public class DeleteProposalContract extends TronContract {
    private final long proposalId;

    public DeleteProposalContract(Contract.ProposalDeleteContract contract) {
        super(Type.DELETE_PROPOSAL, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.proposalId = contract.getProposalId();
    }

    /**
     * @return The proposal id
     */
    public long getProposalId() {
        return proposalId;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "Proposal: " + this.getProposalId());
    }
}
