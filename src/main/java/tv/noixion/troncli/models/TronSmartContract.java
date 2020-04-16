package tv.noixion.troncli.models;

import com.google.gson.JsonObject;
import tv.noixion.troncli.models.contracts.TriggerSmartContractContract;
import tv.noixion.troncli.utils.TriggerContractDataBuilder;
import org.spongycastle.util.encoders.Hex;
import org.tron.protos.Protocol;

import java.util.*;

/**
 * Represents an smart contract.
 */
public class TronSmartContract {

    private final Protocol.SmartContract.ABI abi;
    private final String byteCode;
    private final TronCurrency callValue;
    private final long consumeUserResourcePercent;


    public TronSmartContract(Protocol.SmartContract contract) {
        this.abi = contract.getAbi();
        this.byteCode = Hex.toHexString(contract.getBytecode().toByteArray());
        this.callValue = TronCurrency.sun(contract.getCallValue());
        this.consumeUserResourcePercent = contract.getConsumeUserResourcePercent();
    }

    public TronSmartContract(Protocol.SmartContract.ABI abi) {
        this.abi = abi;
        this.byteCode = "";
        this.callValue = TronCurrency.sun(0);
        this.consumeUserResourcePercent = 0L;
    }

    /**
     * @return The smart contract ABI
     */
    public Protocol.SmartContract.ABI getAbi() {
        return abi;
    }

    /**
     * @return The smart contract byte code
     */
    public String getByteCode() {
        return byteCode;
    }

    /**
     * @return The smart contract call value
     */
    public TronCurrency getCallValue() {
        return callValue;
    }

    /**
     * @return The % of energy consumed by the user if there is available energy from the owner fo the contract.
     */
    public long getConsumeUserResourcePercent() {
        return consumeUserResourcePercent;
    }

    /**
     * @return The list of methods available for this smart contract.
     */
    public List<String> getMethodsSignatures() {
        List<String> signatures = new ArrayList<>();
        for (Protocol.SmartContract.ABI.Entry entry : this.abi.getEntrysList()) {
            if (entry.getType() == Protocol.SmartContract.ABI.Entry.EntryType.Function) {
                String sig = entry.getName() + "(";
                boolean first = true;
                for (Protocol.SmartContract.ABI.Entry.Param param : entry.getInputsList()) {
                    if (first) {
                        first = false;
                    } else {
                        sig += ",";
                    }
                    sig += param.getType();
                }
                sig += ")";
                signatures.add(sig);
            }
        }
        return signatures;
    }

    /**
     * Gets a method signature from its name.
     *
     * @param methodName the method name.
     * @return The method signature
     */
    public String getMethodsSignature(String methodName) {
        for (Protocol.SmartContract.ABI.Entry entry : this.abi.getEntrysList()) {
            if (entry.getType() == Protocol.SmartContract.ABI.Entry.EntryType.Function && entry.getName().equals(methodName)) {
                String sig = entry.getName() + "(";
                boolean first = true;
                for (Protocol.SmartContract.ABI.Entry.Param param : entry.getInputsList()) {
                    if (first) {
                        first = false;
                    } else {
                        sig += ",";
                    }
                    sig += param.getType();
                }
                sig += ")";
                return sig;
            }
        }
        return methodName + "()";
    }

    /**
     * Checks if a method is read-only (pure or view).
     *
     * @param methodName The method name.
     * @return true if is read-only, false otherwise.
     */
    public boolean isReadOnly(String methodName) {
        for (Protocol.SmartContract.ABI.Entry entry : this.abi.getEntrysList()) {
            if (entry.getType() == Protocol.SmartContract.ABI.Entry.EntryType.Function && entry.getName().equals(methodName)) {
                return entry.getStateMutability() == Protocol.SmartContract.ABI.Entry.StateMutabilityType.View ||
                        entry.getStateMutability() == Protocol.SmartContract.ABI.Entry.StateMutabilityType.Pure;
            }
        }
        return false;
    }

    /**
     * gets the state mutability type for a method.
     *
     * @param methodName The method name.
     * @return The state mutability type.
     */
    public Protocol.SmartContract.ABI.Entry.StateMutabilityType getStateMutability(String methodName) {
        for (Protocol.SmartContract.ABI.Entry entry : this.abi.getEntrysList()) {
            if (entry.getType() == Protocol.SmartContract.ABI.Entry.EntryType.Function && entry.getName().equals(methodName)) {
                return entry.getStateMutability();
            }
        }
        return Protocol.SmartContract.ABI.Entry.StateMutabilityType.UNRECOGNIZED;
    }

    /**
     * @return The list of events available for this smart contract.
     */
    public List<String> getEventsSignatures() {
        List<String> signatures = new ArrayList<>();
        for (Protocol.SmartContract.ABI.Entry entry : this.abi.getEntrysList()) {
            if (entry.getType() == Protocol.SmartContract.ABI.Entry.EntryType.Event) {
                String sig = entry.getName() + "(";
                boolean first = true;
                for (Protocol.SmartContract.ABI.Entry.Param param : entry.getInputsList()) {
                    if (first) {
                        first = false;
                    } else {
                        sig += ",";
                    }
                    sig += param.getType();
                }
                sig += ")";
                signatures.add(sig);
            }
        }
        return signatures;
    }

    /**
     * Gets the return types of a method.
     *
     * @param method the method.
     * @return The return type.
     */
    public List<String> getResultTypeForMethod(String method) {
        List<String> types = new ArrayList<>();
        for (Protocol.SmartContract.ABI.Entry entry : this.abi.getEntrysList()) {
            if (entry.getType() == Protocol.SmartContract.ABI.Entry.EntryType.Function) {
                if (entry.getName().equals(method) && !entry.getOutputsList().isEmpty()) {
                    for (Protocol.SmartContract.ABI.Entry.Param out : entry.getOutputsList()) {
                        types.add(out.getType());
                    }
                    return types;
                }
            }
        }
        return null;
    }

    /**
     * Gets the return names of a method.
     *
     * @param method the method.
     * @return The return names.
     */
    public List<String> getResultNameForMethod(String method) {
        List<String> names = new ArrayList<>();
        for (Protocol.SmartContract.ABI.Entry entry : this.abi.getEntrysList()) {
            if (entry.getType() == Protocol.SmartContract.ABI.Entry.EntryType.Function) {
                if (entry.getName().equals(method) && !entry.getOutputsList().isEmpty()) {
                    for (Protocol.SmartContract.ABI.Entry.Param out : entry.getOutputsList()) {
                        names.add(out.getName());
                    }
                    return names;
                }
            }
        }
        return null;
    }

    /**
     * Fills the params with fields information about an event.
     *
     * @param eventName       The event name
     * @param indexedNames    destination for the indexed names.
     * @param indexedTypes    destination for the indexed types.
     * @param notIndexedNames destination for the not indexed names.
     * @param notIndexedTypes destination for the not indexed types.
     */
    public void fillEventInformation(String eventName, List<String> indexedNames, List<String> indexedTypes,
                                     List<String> notIndexedNames, List<String> notIndexedTypes) {
        for (Protocol.SmartContract.ABI.Entry entry : this.abi.getEntrysList()) {
            if (entry.getType() == Protocol.SmartContract.ABI.Entry.EntryType.Event
                    && entry.getName().equals(eventName)) {
                for (Protocol.SmartContract.ABI.Entry.Param param : entry.getInputsList()) {
                    if (param.getIndexed()) {
                        indexedNames.add(param.getName());
                        indexedTypes.add(param.getType());
                    } else {
                        notIndexedNames.add(param.getName());
                        notIndexedTypes.add(param.getType());
                    }
                }
                break;
            }
        }
    }

    /**
     * Interprets a log as an event.
     *
     * @param log The log.
     * @return The event or null if is not interpretable.
     */
    public TronSmartContractEvent interpretLog(TronTransactionInformation.Log log) {
        boolean matches = false;
        for (String event : this.getEventsSignatures()) {
            if (log.checkEvent(event)) {
                matches = true;
                List<String> indexedNames = new ArrayList<>();
                List<String> notIndexedNames = new ArrayList<>();
                List<String> indexedTypes = new ArrayList<>();
                List<String> notIndexedTypes = new ArrayList<>();
                this.fillEventInformation(TriggerContractDataBuilder.getMethodFromCall(event),
                        indexedNames, indexedTypes, notIndexedNames, notIndexedTypes);
                List<Object> indexedParams;
                List<Object> notIndexedParams;

                try {
                    indexedParams = log.getIndexedFields(indexedTypes);
                    notIndexedParams = log.getNotIndexedFields(notIndexedTypes);

                    Map<String, String> typeMap = new TreeMap<>();
                    Map<String, Object> valueMap = new TreeMap<>();

                    for (int i = 0; i < indexedNames.size(); i++) {
                        typeMap.put(indexedNames.get(i), indexedTypes.get(i));
                        valueMap.put(indexedNames.get(i), indexedParams.get(i));
                    }
                    for (int i = 0; i < notIndexedNames.size(); i++) {
                        typeMap.put(notIndexedNames.get(i), notIndexedTypes.get(i));
                        valueMap.put(notIndexedNames.get(i), notIndexedParams.get(i));
                    }

                    return new TronSmartContractEvent(log.getAddress(),
                            TriggerContractDataBuilder.getMethodFromCall(event), event, typeMap, valueMap);

                } catch (Exception ex) {
                    return new TronSmartContractEvent(log.getAddress(),
                            TriggerContractDataBuilder.getMethodFromCall(event), event);
                }
            }
        }
        return null;
    }

    /**
     * Interprets a method call bases on the smart contract ABI.
     * @param data The call data
     * @return The interpretation or null if is not interpretable.
     */
    public String interpretCall(byte[] data) {

        Protocol.SmartContract.ABI abi;
        List<String> methods = this.getMethodsSignatures();

        for (String method : methods) {
            List<Object> result;
            try {
                result = TriggerSmartContractContract.interpretData(data, method);
            } catch (Exception ex) {
                continue;
            }
            String str = TriggerContractDataBuilder.getMethodFromCall(method) + "(";
            boolean f = true;
            for (Object arg : result) {
                if (f) {
                    f = false;
                } else {
                    str += ", ";
                }
                if (arg instanceof byte[]) {
                    str += Hex.toHexString((byte[]) arg);
                } else if (arg instanceof Object[]) {
                    str += Arrays.toString((Object[]) arg);
                } else {
                    str += arg.toString();
                }
            }
            str += ")";
            return str;
        }

        return null;
    }


    /**
     * @return The ABI as JSON.
     */
    public String getABIString() {
        try {
            return com.google.protobuf.util.JsonFormat.printer().print(this.abi);
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * @return The object as Json.
     */
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    /**
     * Prints in stdout.
     */
    public void print(String indent) {
        System.out.println(indent + "Entries: " + this.getABIString());
        System.out.println(indent + "ByteCode: " + this.byteCode);
        System.out.println(indent + "Constructor call value: " + String.format("%.0f TRX", this.getCallValue().getTRX()));
        System.out.println(indent + "Consume user: " + this.getConsumeUserResourcePercent() + "%");
    }
}
