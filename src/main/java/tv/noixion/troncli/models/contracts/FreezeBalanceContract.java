package tv.noixion.troncli.models.contracts;

import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import tv.noixion.troncli.models.TronCurrency;
import tv.noixion.troncli.models.TronResource;
import com.google.gson.JsonObject;
import org.tron.protos.Contract;

/**
 * Contract for freezing TRX.
 */
public class FreezeBalanceContract extends TronContract {
    private final long amount;
    private final long durationDays;
    private final TronResource resource;

    public FreezeBalanceContract(Contract.FreezeBalanceContract contract) {
        super(Type.FREEZE_BALANCE, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.amount = contract.getFrozenBalance();
        this.durationDays = contract.getFrozenDuration();
        switch (contract.getResource()) {
            case ENERGY:
                this.resource = TronResource.ENERGY;
                break;
            case BANDWIDTH:
                this.resource = TronResource.ENERGY;
                break;
            default:
                this.resource = TronResource.UNKNOWN;
        }
    }

    /**
     * @return The amount frozen
     */
    public long getAmount() {
        return amount;
    }

    /**
     * @return The duration of the freeze
     */
    public long getDurationDays() {
        return durationDays;
    }

    /**
     * @return The resource given in exchange
     */
    public TronResource getResource() {
        return resource;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "Amount: " + String.format("%.0f TRX", TronCurrency.sun(this.getAmount()).getTRX()));
        System.out.println(indent + "Duration: " + this.getDurationDays() + " days");
        System.out.println(indent + "Resource: " + this.getResource().toString());
    }
}
