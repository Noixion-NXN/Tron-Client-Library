package tv.noixion.troncli.models.contracts;

import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import com.google.gson.JsonObject;
import org.tron.protos.Contract;

public class SetAccountIdContract extends TronContract {
    private final String accountId;

    public SetAccountIdContract(Contract.SetAccountIdContract contract) {
        super(Type.SET_ACCOUNT_ID, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.accountId = new String(contract.getAccountId().toByteArray());
    }

    /**
     * @return The account identifier set.
     */
    public String getAccountId() {
        return accountId;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "Id: " + this.getAccountId());
    }
}
