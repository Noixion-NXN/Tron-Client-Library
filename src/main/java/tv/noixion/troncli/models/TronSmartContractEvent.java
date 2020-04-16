package tv.noixion.troncli.models;

import java.util.Map;

/**
 * Represents an smart contract event.
 */
public class TronSmartContractEvent {
    private final TronAddress contractAddress;
    private final String eventName;
    private final String eventSignature;
    private final Map<String, String> types;
    private final Map<String, Object> values;

    private final boolean interpretable;

    public TronSmartContractEvent(TronAddress contractAddress, String eventName, String eventSignature, Map<String, String> types, Map<String, Object> values) {
        this.contractAddress = contractAddress;
        this.eventName = eventName;
        this.eventSignature = eventSignature;
        this.types = types;
        this.values = values;
        this.interpretable = true;
    }

    public TronSmartContractEvent(TronAddress contractAddress, String eventName, String eventSignature) {
        this.contractAddress = contractAddress;
        this.eventName = eventName;
        this.eventSignature = eventSignature;
        this.types = null;
        this.values = null;
        this.interpretable = false;
    }

    /**
     * @return The contract address.
     */
    public TronAddress getContractAddress() {
        return contractAddress;
    }

    /**
     * @return The event name.
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * @return The event signature.
     */
    public String getEventSignature() {
        return eventSignature;
    }

    /**
     * @return The mapping names to types.
     */
    public Map<String, String> getTypes() {
        return types;
    }

    /**
     * @return The mapping names to values.
     */
    public Map<String, Object> getValues() {
        return values;
    }

    /**
     * @return true if it is interpretable, false if is only the event name.
     */
    public boolean isInterpretable() {
        return interpretable;
    }
}
