package tv.noixion.troncli.models;

import com.google.gson.JsonObject;
import com.google.protobuf.ByteString;
import org.tron.protos.Protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Represents a Tron proposal.
 */
public class TronProposal {
    /**
     * Represents a proposal status.
     */
    public enum ProposalStatus {
        PENDING,
        DISAPPROVED,
        APPROVED,
        CANCELED,
        UNRECOGNIZED;
    }

    private final long id;
    private final TronAddress proposer;
    private final Map<Long, Long> params;
    private final Date creationDate;
    private final Date expirationDate;
    private final ProposalStatus status;
    private final List<TronAddress> approvals;

    public TronProposal(Protocol.Proposal p) {
        this.id = p.getProposalId();
        this.proposer = new TronAddress(p.getProposerAddress().toByteArray());
        this.creationDate = new Date(p.getCreateTime());
        this.expirationDate = new Date(p.getExpirationTime());
        this.params = p.getParametersMap();
        switch (p.getState()) {
            case PENDING:
                this.status = ProposalStatus.PENDING;
                break;
            case DISAPPROVED:
                this.status = ProposalStatus.DISAPPROVED;
                break;
            case APPROVED:
                this.status = ProposalStatus.APPROVED;
                break;
            case CANCELED:
                this.status = ProposalStatus.CANCELED;
                break;
            default:
                this.status = ProposalStatus.UNRECOGNIZED;
        }
        this.approvals = new ArrayList<>();
        for (ByteString bs : p.getApprovalsList()) {
            this.approvals.add(new TronAddress(bs.toByteArray()));
        }
    }

    /**
     * @return The proposal Id.
     */
    public long getId() {
        return id;
    }

    /**
     * @return The proposer address.
     */
    public TronAddress getProposer() {
        return proposer;
    }

    /**
     * @return The creation date.
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * @return The expiration date.
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * @return The proposal parameters.
     */
    public Map<Long, Long> getParams() {
        return this.params;
    }

    /**
     * @return The proposal status.
     */
    public ProposalStatus getStatus() {
        return status;
    }

    /**
     * @return The proposal approvals.
     */
    public List<TronAddress> getApprovals() {
        return approvals;
    }

    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    public void print(String indent) {
        System.out.println(indent + "Id: " + this.getId());
        System.out.println(indent + "Proposer: " + this.getProposer().toString());
        System.out.println(indent + "Date: " + this.getCreationDate().toString());
        System.out.println(indent + "Expires: " + this.getExpirationDate().toString());
        System.out.println(indent + "Parameters:");
        for (long key : this.getParams().keySet()) {
            System.out.println(indent + "    " + key + " -> " + this.getParams().get(key));
        }
        System.out.println(indent + "Status: " + this.getStatus().toString());
        if (!this.getApprovals().isEmpty()) {
            System.out.println(indent + "Approvals:");
            for (TronAddress approval : this.getApprovals()) {
                System.out.println(indent + "    " + approval.toString());
            }
        }

    }
}
