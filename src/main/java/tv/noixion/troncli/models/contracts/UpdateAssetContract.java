package tv.noixion.troncli.models.contracts;
import com.google.gson.JsonObject;
import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import org.tron.protos.Contract;

/**
 * Contract for updating an asset.
 */
public class UpdateAssetContract extends TronContract {
    private final String description;
    private final String url;
    private final long newLimit;
    private final long newPublicLimit;

    public UpdateAssetContract(Contract.UpdateAssetContract contract) {
        super(Type.UPDATE_ASSET, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.description = new String(contract.getDescription().toByteArray());
        this.url = new String(contract.getUrl().toByteArray());
        this.newLimit = contract.getNewLimit();
        this.newPublicLimit = contract.getNewPublicLimit();
    }

    /**
     * @return The new description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The new URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return The new limit.
     */
    public long getNewLimit() {
        return newLimit;
    }

    /**
     * @return The new public limit.
     */
    public long getNewPublicLimit() {
        return newPublicLimit;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "URL: " + this.getUrl());
        System.out.println(indent + "Description: " + this.getDescription());
        System.out.println(indent + "New limit: " + this.getNewLimit());
        System.out.println(indent + "New public limit: " + this.getNewPublicLimit());
    }


}
