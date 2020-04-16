package tv.noixion.troncli.models.contracts;

import com.google.gson.JsonObject;
import com.google.protobuf.ByteString;
import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import org.tron.protos.Contract;

import java.util.ArrayList;
import java.util.List;

/**
 * Contract for voting an asset.
 */
public class VoteAssetContract extends TronContract {
    private final int count;
    private final boolean support;
    private final List<TronAddress> votedAddresses;

    public VoteAssetContract(Contract.VoteAssetContract contract) {
        super(Type.VOTE_ASSET, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.count = contract.getCount();
        this.support = contract.getSupport();
        this.votedAddresses = new ArrayList<>();
        for (ByteString bs : contract.getVoteAddressList()) {
            this.votedAddresses.add(new TronAddress(bs.toByteArray()));
        }
    }

    /**
     * @return The vote count.
     */
    public int getCount() {
        return count;
    }

    /**
     * @return The "support" flag.
     */
    public boolean isSupport() {
        return support;
    }

    /**
     * @return The voted addresses.
     */
    public List<TronAddress> getVotedAddresses() {
        return votedAddresses;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "Count: " + this.getCount());
        System.out.println(indent + "Support: " + this.isSupport());
        System.out.println(indent + "Voted addresses: ");
        for (TronAddress a : this.votedAddresses) {
            System.out.println(indent + "    " + a.toString());
        }
    }
}
