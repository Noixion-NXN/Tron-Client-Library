package tv.noixion.troncli.models.contracts;
import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import org.tron.protos.Contract;

/**
 * Contract for unfreezing assets.
 */
public class UnfreezeAssetContract extends TronContract {
   public UnfreezeAssetContract(Contract.UnfreezeAssetContract contract) {
       super(Type.UNFREEZE_ASSET, new TronAddress(contract.getOwnerAddress().toByteArray()));
   }
}
