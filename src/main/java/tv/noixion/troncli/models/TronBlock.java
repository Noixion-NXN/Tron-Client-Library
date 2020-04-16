package tv.noixion.troncli.models;

import com.google.common.primitives.Longs;
import com.google.gson.JsonObject;
import tv.noixion.troncli.TronClient;
import org.spongycastle.util.encoders.Hex;
import org.tron.api.GrpcAPI;
import org.tron.protos.Protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a block of the Tron blockchain.
 */
public class TronBlock {

    private final HashIdentifier id;

    private final int version;
    private final long number;
    private final HashIdentifier parentHash;
    private final Date date;

    private final byte[] merkleTreeRoot;

    private final List<TronTransaction> transactions;

    private final long witnessId;
    private final TronAddress witnessAddress;
    private final byte[] witnessSignature;

    public TronBlock(HashIdentifier id, Protocol.Block block) {
        this.id = id;
        version = block.getBlockHeader().getRawData().getVersion();
        this.number = block.getBlockHeader().getRawData().getNumber();
        this.parentHash = new HashIdentifier(block.getBlockHeader().getRawData().getParentHash().toByteArray());
        this.date = new Date(block.getBlockHeader().getRawData().getTimestamp());
        this.merkleTreeRoot = block.getBlockHeader().getRawData().getTxTrieRoot().toByteArray();
        this.transactions = new ArrayList<>();
        for (Protocol.Transaction tx : block.getTransactionsList()) {
            transactions.add(new TronTransaction(tx));
        }
        this.witnessId = block.getBlockHeader().getRawData().getWitnessId();
        this.witnessAddress = new TronAddress(block.getBlockHeader().getRawData().getWitnessAddress().toByteArray());
        this.witnessSignature = block.getBlockHeader().getWitnessSignature().toByteArray();
    }

    public TronBlock(GrpcAPI.BlockExtention blockEx) {
        this.id = new HashIdentifier(blockEx.getBlockid().toByteArray());
        version = blockEx.getBlockHeader().getRawData().getVersion();
        this.number = blockEx.getBlockHeader().getRawData().getNumber();
        this.parentHash = new HashIdentifier(blockEx.getBlockHeader().getRawData().getParentHash().toByteArray());
        this.date = new Date(blockEx.getBlockHeader().getRawData().getTimestamp());
        this.merkleTreeRoot = blockEx.getBlockHeader().getRawData().getTxTrieRoot().toByteArray();
        this.transactions = new ArrayList<>();
        for (GrpcAPI.TransactionExtention tx : blockEx.getTransactionsList()) {
            transactions.add(new TronTransaction(tx.getTransaction()));
        }
        this.witnessId = blockEx.getBlockHeader().getRawData().getWitnessId();
        this.witnessAddress = new TronAddress(blockEx.getBlockHeader().getRawData().getWitnessAddress().toByteArray());
        this.witnessSignature = blockEx.getBlockHeader().getWitnessSignature().toByteArray();
    }

    private byte[] generateBlockId(long blockNum, byte[] blockHash) {
        byte[] numBytes = Longs.toByteArray(blockNum);
        byte[] hash = blockHash;
        System.arraycopy(numBytes, 0, hash, 0, 8);
        return hash;
    }

    /**
     * @return The block version.
     */
    public int getVersion() {
        return this.version;
    }

    /**
     * @return The block number.
     */
    public long getNumber() {
        return number;
    }

    /**
     * @return The parent block hash.
     */
    public HashIdentifier getParentHash() {
        return parentHash;
    }

    /**
     * @return The date of the block (timestamp).
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return The root of the merkle tree.
     */
    public byte[] getMerkleTreeRoot() {
        return merkleTreeRoot;
    }

    /**
     * @return The list of transaction in the block.
     */
    public List<TronTransaction> getTransactions() {
        return transactions;
    }

    /**
     * @return the witness id.
     */
    public long getWitnessId() {
        return witnessId;
    }

    /**
     * @return The witness address.
     */
    public TronAddress getWitnessAddress() {
        return witnessAddress;
    }

    /**
     * @return the witness signature.
     */
    public byte[] getWitnessSignature() {
        return witnessSignature;
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
     * @param indent The indent to use.
     */
    public void print(String indent) {
        this.print(indent, null);
    }

    /**
     * Prints the object in stdout.
     * @param indent The indent to use.
     * @param client The client to interpret the contracts
     */
    public void print(String indent, TronClient client) {
        System.out.println(indent + "Version: " + this.getVersion());
        System.out.println(indent + "Block Number: " + this.getNumber());
        System.out.println(indent + "Date: " + this.getDate().toString());
        System.out.println(indent + "Parent block: " + this.getParentHash().toString());
        System.out.println(indent + "Merkle root: " + Hex.toHexString(this.getMerkleTreeRoot()));
        System.out.println(indent + "Witness ID: " + this.getWitnessId());
        System.out.println(indent + "Witness Address: " + this.getWitnessAddress().toString());
        System.out.println(indent + "Witness Signature: " + Hex.toHexString(this.getWitnessSignature()));
        if (this.getTransactions().isEmpty()) {
            System.out.println(indent + "Transactions: (none)");
        } else {
            System.out.println(indent + "Transactions: ");
            for (TronTransaction tx : this.getTransactions()) {
                System.out.println();
                System.out.println(indent + "    " + "TRANSACTION-----------------------------------");
                tx.print(indent + "    ", client);
                System.out.println(indent + "    " + "----------------------------------------------");
                System.out.println();
            }
        }
    }

    public HashIdentifier getId() {
        return id;
    }
}
