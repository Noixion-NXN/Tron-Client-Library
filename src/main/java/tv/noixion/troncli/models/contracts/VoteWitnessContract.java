package tv.noixion.troncli.models.contracts;

import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import com.google.gson.JsonObject;
import org.tron.protos.Contract;

import java.util.ArrayList;
import java.util.List;

/**
 * Contract for voting witnesses.
 */
public class VoteWitnessContract extends TronContract {
    /**
     * Represents a vote.
     */
    public class Vote {
        private final TronAddress voteAddress;
        private final long voteCount;

        public Vote(Contract.VoteWitnessContract.Vote vote) {
            this.voteAddress = new TronAddress(vote.getVoteAddress().toByteArray());
            this.voteCount = vote.getVoteCount();
        }

        /**
         * @return The vote address.
         */
        public TronAddress getVoteAddress() {
            return voteAddress;
        }

        /**
         * @return The vote count.
         */
        public long getVoteCount() {
            return voteCount;
        }
    }

    private final boolean support;
    private final List<Vote> votes;

    public VoteWitnessContract(Contract.VoteWitnessContract contract) {
        super(Type.VOTE_WITNESS, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.support = contract.getSupport();
        this.votes = new ArrayList<>();
        for (Contract.VoteWitnessContract.Vote vote : contract.getVotesList()) {
            this.votes.add(new Vote(vote));
        }
    }

    /***
     * @return The "support" flag.
     */
    public boolean isSupport() {
        return support;
    }

    /**
     * @return The votes.
     */
    public List<Vote> getVotes() {
        return votes;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "Support: " + this.isSupport());
        System.out.println(indent + "Votes: ");
        for (Vote v : this.votes) {
            System.out.println(indent + "    " + v.getVoteAddress() + " (" + v.getVoteCount() + " votes)");
        }
    }

}
