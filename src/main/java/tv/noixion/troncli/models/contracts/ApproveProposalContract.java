package tv.noixion.troncli.models.contracts;

import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import com.google.gson.JsonObject;
import org.tron.protos.Contract;

/**
 * Contract for approving a proposal.
 */
public class ApproveProposalContract extends TronContract {
    private final long proposalId;
    private final boolean addProposal;

    public ApproveProposalContract(Contract.ProposalApproveContract contract) {
        super(Type.APPROVE_PROPOSAL, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.proposalId = contract.getProposalId();
        this.addProposal = contract.getIsAddApproval();
    }

    /**
     * @return The proposal identifier.
     */
    public long getProposalId() {
        return proposalId;
    }

    /**
     * @return true if it adds a proposal, false otherwise.
     */
    public boolean isAddProposal() {
        return addProposal;
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
        System.out.println(indent + "add_proposal: " + this.isAddProposal());
    }
}
