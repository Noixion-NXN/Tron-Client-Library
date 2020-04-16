package tv.noixion.troncli.models;

import com.google.gson.JsonObject;
import org.spongycastle.util.encoders.Hex;
import org.tron.protos.Protocol;

/**
 * Represents a org.tron witness (Super representative / Block producer)
 */
public class TronWitness {
    private final TronAddress address;
    private final long voteCount;
    private final byte[] publicKey;
    private final String url;
    private final long blocksProduced;
    private final long blocksMissed;
    private final long latestBlockNum;
    private final long latestSlotNum;
    private final boolean jobs;

    public TronWitness(Protocol.Witness witness) {
        this.address = new TronAddress(witness.getAddress().toByteArray());
        this.voteCount = witness.getVoteCount();
        this.publicKey = witness.getPubKey().toByteArray();
        this.url = witness.getUrl();
        this.blocksProduced = witness.getTotalProduced();
        this.blocksMissed = witness.getTotalMissed();
        this.latestBlockNum = witness.getLatestBlockNum();
        this.latestSlotNum = witness.getLatestSlotNum();
        this.jobs = witness.getIsJobs();
    }

    /**
     * @return The witness address.
     */
    public TronAddress getAddress() {
        return address;
    }

    /**
     * @return The vote count.
     */
    public long getVoteCount() {
        return voteCount;
    }

    /**
     * @return The public key (if any)
     */
    public byte[] getPublicKey() {
        return publicKey;
    }

    /**
     * @return The url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return The blocks produced.
     */
    public long getBlocksProduced() {
        return blocksProduced;
    }

    /**
     * @return The blocks missed.
     */
    public long getBlocksMissed() {
        return blocksMissed;
    }

    /**
     * @return The latest produced block number.
     */
    public long getLatestBlockNum() {
        return latestBlockNum;
    }

    /**
     * @return The latest slot number.
     */
    public long getLatestSlotNum() {
        return latestSlotNum;
    }

    /**
     * @return The witness productivity.
     */
    public double getProductivity() {
        return ((double) this.blocksProduced) / (this.blocksProduced + this.blocksMissed);
    }

    /**
     * @return The boolean flag "jobs"
     */
    public boolean isJobs() {
        return jobs;
    }

    /**
     * @return The object as Json.
     */
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    /**
     * Prints the object in stdout.
     */
    public void print(String indent) {
        System.out.println(indent + "Address: " + this.getAddress().toString());
        System.out.println(indent + "URL: " + this.getUrl());
        System.out.println(indent + "Vote count: " + this.getVoteCount());
        System.out.println(indent + "Public key: " + Hex.toHexString(this.getPublicKey()));
        System.out.println(indent + "Blocks: ");
        System.out.println(indent + "    " + "Produced: " + this.getBlocksProduced());
        System.out.println(indent + "    " + "Missed: " + this.getBlocksMissed());
        System.out.println(indent + "    " + "Productivity: " + String.format("%.2f ", this.getProductivity() * 100) + "%");
        System.out.println(indent + "Last block: " + this.getLatestBlockNum());
        System.out.println(indent + "Last slot: " + this.getLatestSlotNum());
        System.out.println(indent + "Active: " + (this.isJobs() ? "Yes" : "No"));
    }
}
