package tv.noixion.troncli.models.contracts;
import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import org.tron.protos.Contract;

/**
 * Contract for withdrawing balance.
 */
public class WithdrawBalanceContract extends TronContract {
    public WithdrawBalanceContract (Contract.WithdrawBalanceContract contract) {
        super(Type.WITHDRAW_BALANCE, new TronAddress(contract.getOwnerAddress().toByteArray()));
    }
}
