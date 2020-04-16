package tv.noixion.troncli.models.contracts;

import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import com.google.gson.JsonObject;
import org.tron.protos.Contract;

/**
 * Contract for updating the name of an account.
 */
public class UpdateAccountContract extends TronContract {
    private final String accountName;

    public UpdateAccountContract(Contract.AccountUpdateContract contract) {
        super(Type.ACCOUNT_UPDATE, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.accountName = new String(contract.getAccountName().toByteArray());
    }

    /**
     * @return The account name set.
     */
    public String getAccountName() {
        return accountName;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "Name: " + this.getAccountName());
    }
}
