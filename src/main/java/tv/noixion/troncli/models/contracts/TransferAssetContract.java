package tv.noixion.troncli.models.contracts;
import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import com.google.gson.JsonObject;
import org.tron.protos.Contract;

/**
 * Contract for transferring assets.
 */
public class TransferAssetContract extends TronContract {
    private final long amount;
    private final String assetName;
    private final TronAddress toAddress;

    public TransferAssetContract(Contract.TransferAssetContract contract) {
        super(Type.TRANSFER_ASSET, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.amount = contract.getAmount();
        this.assetName = new String(contract.getAssetName().toByteArray());
        this.toAddress = new TronAddress(contract.getToAddress().toByteArray());
    }

    /**
     * @return The amount.
     */
    public long getAmount() {
        return amount;
    }

    /**
     * @return The asset name
     */
    public String getAssetName() {
        return assetName;
    }

    /**
     * @return The beneficiary address.
     */
    public TronAddress getToAddress() {
        return toAddress;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "Amount: " + this.getAmount());
        System.out.println(indent + "Asset: " + this.getAssetName());
        System.out.println(indent + "To: " + this.getToAddress());
    }

}
