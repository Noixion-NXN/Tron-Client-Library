package tv.noixion.troncli;

import tv.noixion.troncli.models.*;
import tv.noixion.troncli.models.*;
import tv.noixion.troncli.utils.TriggerContractDataBuilder;
import tv.noixion.troncli.utils.TronEventHandler;
import org.tron.protos.Protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Smart contract events listener.
 */
public abstract class SmartContractEventListener implements TronEventHandler {
    private final TronAddress contractAddress;
    private final TronSmartContract contract;
    private final Protocol.SmartContract.ABI contractABI;

    /**
     * Creates a new instance of SmartContractEventListener
     *
     * @param contractAddress The contract address.
     * @param contractABI     The contract name.
     */
    public SmartContractEventListener(TronAddress contractAddress, Protocol.SmartContract.ABI contractABI) {
        this.contractAddress = contractAddress;
        this.contractABI = contractABI;
        this.contract = new TronSmartContract(contractABI);
    }

    /**
     * @return The smart contract.
     */
    public TronSmartContract getContract() {
        return this.contract;
    }

    /**
     * Handles a contract event.
     *
     * @param eventName      Event name.
     * @param eventSignature Event signature.
     * @param types          Mapping parameter names to types.
     * @param values         Mapping parameter names to values.
     */
    public abstract void handleEvent(String eventName, String eventSignature, Map<String, String> types,
                                     Map<String, Object> values);

    /**
     * handles an event that cannot be interpreted bases on the contract ABI.
     *
     * @param event the event data.
     */
    public abstract void handleNotInterpretableEvent(TronTransactionInformation.Log event);

    @Override
    public void handleBlock(TronClient client, TronBlock block) {
    }

    @Override
    public void handleTransaction(TronClient client, TronTransaction tx, TronTransactionInformation info) {
        for (TronTransactionInformation.Log log : info.getLogs()) {
            if (!this.contractAddress.equals(log.getAddress())) {
                continue; // Not the contract we are looking for.
            }
            boolean matches = false;
            for (String event : contract.getEventsSignatures()) {
                if (log.checkEvent(event)) {
                    matches = true;
                    List<String> indexedNames = new ArrayList<>();
                    List<String> notIndexedNames = new ArrayList<>();
                    List<String> indexedTypes = new ArrayList<>();
                    List<String> notIndexedTypes = new ArrayList<>();
                    contract.fillEventInformation(TriggerContractDataBuilder.getMethodFromCall(event),
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

                        this.handleEvent(TriggerContractDataBuilder.getMethodFromCall(event), event, typeMap, valueMap);
                    } catch (Exception ex) {
                        //ex.printStackTrace();
                        this.handleNotInterpretableEvent(log);
                    }
                    break;
                }
            }
            if (!matches) {
                this.handleNotInterpretableEvent(log);
            }
        }
    }
}
