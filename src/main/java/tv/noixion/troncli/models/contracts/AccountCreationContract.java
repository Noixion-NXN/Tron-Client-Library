package tv.noixion.troncli.models.contracts;

import tv.noixion.troncli.models.TronAccount;
import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import com.google.gson.JsonObject;
import org.tron.protos.Contract;

/**
 * Contract for creating an account.
 */
public class AccountCreationContract extends TronContract {
    private final TronAddress address;
    private final TronAccount.Type accountType;

    public AccountCreationContract(Contract.AccountCreateContract contract) {
        super(Type.ACCOUNT_CREATION, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.address = new TronAddress((contract.getAccountAddress().toByteArray()));
        switch (contract.getType()) {
            case Normal:
                this.accountType = TronAccount.Type.NORMAL;
                break;
            case AssetIssue:
                this.accountType = TronAccount.Type.ASSET_ISSUE;
                break;
            case Contract:
                this.accountType = TronAccount.Type.CONTRACT;
                break;
            default:
                this.accountType = TronAccount.Type.UNKNOWN;
        }
    }

    /**
     * @return The account address.
     */
    public TronAddress getAddress() {
        return address;
    }

    /**
     * @return the account type.
     */
    public TronAccount.Type getAccountType() {
        return accountType;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "Account created: " + this.getAddress().toString());
        System.out.println(indent + "Account type: " + this.accountType.toString());
    }
}
