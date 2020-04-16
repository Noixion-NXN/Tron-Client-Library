package tv.noixion.troncli.models.contracts;
import tv.noixion.troncli.models.TronAssetIssue;
import org.tron.protos.Contract;

/**
 * Contract for creating an asset.
 */
public class AssetIssueContract extends TronAssetIssue {

    public AssetIssueContract(Contract.AssetIssueContract contract) {
        super(contract);
    }
}
