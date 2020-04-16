package tv.noixion.troncli.models;

import com.google.gson.JsonObject;
import com.google.protobuf.ByteString;
import tv.noixion.troncli.TronClient;
import tv.noixion.troncli.exceptions.InvalidCallDataException;
import tv.noixion.troncli.models.contracts.TriggerSmartContractContract;
import tv.noixion.troncli.utils.TronUtils;
import org.spongycastle.util.encoders.Hex;
import org.tron.protos.Protocol;

import java.util.*;

/**
 * Represents the information of a confirmed transaction.
 */
public class TronTransactionInformation {

    /**
     * Represents a transaction log.
     */
    public class Log {
        private final TronAddress address;
        private final byte[] data;
        private final List<byte[]> topics;

        public Log(Protocol.TransactionInfo.Log log) {
            this.address = new TronAddress(log.getAddress().toByteArray());
            this.data = log.getData().toByteArray();
            this.topics = new ArrayList<>();
            for (ByteString topic : log.getTopicsList()) {
                this.topics.add(topic.toByteArray());
            }
        }

        /**
         * @return The log address.
         */
        public TronAddress getAddress() {
            return address;
        }

        /**
         * @return The log data.
         */
        public byte[] getData() {
            return data;
        }

        /**
         * @return the log topics.
         */
        public List<byte[]> getTopics() {
            return topics;
        }

        /**
         * Checks if the log matches an event.
         *
         * @param eventSignature The event signature
         * @return true if it matches, false otherwise.
         */
        public boolean checkEvent(String eventSignature) {
            if (topics.size() > 0) {
                return Arrays.equals(topics.get(0), TronUtils.keccak256(eventSignature.getBytes()));
            } else {
                return false;
            }
        }

        /**
         * Gets all the indexed fields
         *
         * @param types The list of types.
         * @return The list of values for the fields.
         * @throws InvalidCallDataException If the data is invalid.
         */
        public List<Object> getIndexedFields(List<String> types) throws InvalidCallDataException {
            try {
                List<Object> values = new ArrayList<>();
                for (int i = 1; i < topics.size(); i++) {
                    List<String> oneType = new ArrayList<>();
                    oneType.add(types.get(i - 1));
                    values.addAll(TriggerSmartContractContract.unpack(oneType, this.topics.get(i)));
                }
                return values;
            } catch (Exception ex) {
                throw new InvalidCallDataException(ex.getMessage());
            }
        }

        /**
         * Gets all the not-indexed fields.
         *
         * @param types the list of types.
         * @return The list of values for the fields.
         * @throws InvalidCallDataException If the data is invalid.
         */
        public List<Object> getNotIndexedFields(List<String> types) throws InvalidCallDataException {
            return TriggerSmartContractContract.unpack(types, this.data);
        }

        public void print(String indent) {
            System.out.println(indent + "LOG--------------------------------------");
            System.out.println(indent + "Address: " + this.getAddress().toString());
            System.out.println(indent + "Data: " + Hex.toHexString(this.getData()));
            System.out.println(indent + "Topics: ");
            for (byte[] topic : this.getTopics()) {
                System.out.println(indent + "    " + Hex.toHexString(topic));
            }
            System.out.println(indent + "-----------------------------------------");
        }
    }

    /**
     * Represents a call value.
     */
    public class CallValueInfo {
        private final String tokenName;
        private final long callValue;

        public CallValueInfo(Protocol.InternalTransaction.CallValueInfo info) {
            this.tokenName = info.getTokenId();
            this.callValue = info.getCallValue();
        }

        /**
         * @return The token name.
         */
        public String getTokenName() {
            return tokenName;
        }

        /**
         * @return The call value.
         */
        public long getCallValue() {
            return callValue;
        }
    }

    /**
     * Represents an internal transaction.
     */
    public class InternalTransaction {
        private final HashIdentifier hash;
        private final TronAddress caller;
        private final TronAddress toAddress;
        private final List<CallValueInfo> callvalueInfo;

        public InternalTransaction(Protocol.InternalTransaction tx) {
            hash = new HashIdentifier(tx.getHash().toByteArray());
            caller = new TronAddress(tx.getCallerAddress().toByteArray());
            toAddress = new TronAddress(tx.getTransferToAddress().toByteArray());
            callvalueInfo = new ArrayList<>();
            for (Protocol.InternalTransaction.CallValueInfo i : tx.getCallValueInfoList()) {
                callvalueInfo.add(new CallValueInfo(i));
            }
        }

        /**
         * @return The intrenal transaction hash.
         */
        public HashIdentifier getHash() {
            return hash;
        }

        /**
         * @return the caller address.
         */
        public TronAddress getCaller() {
            return caller;
        }

        /**
         * @return the destination address.
         */
        public TronAddress getToAddress() {
            return toAddress;
        }

        /**
         * @return The call values.
         */
        public List<CallValueInfo> getCallvalueInfo() {
            return callvalueInfo;
        }
    }

    private final HashIdentifier transactionId;

    private final TronTransaction.Code result;
    private final String resultMessage;

    private final long blockNumber;
    private final Date blockDate;

    private final TronAddress contractAddress;
    private final List<byte[]> contractsResults;

    private final TronCurrency fee;

    private final TronCurrency unfreezeAmount;
    private final TronCurrency withdrawAmount;

    private final List<Log> logs;

    private final long energyUsage;
    private final TronCurrency energyFee;

    private final long originEnergyUsage;
    private final long totalEnergyUsage;

    private final long netUsage;
    private final TronCurrency netFee;

    private final List<InternalTransaction> internalTransactions;

    public TronTransactionInformation(Protocol.TransactionInfo tInf) {
        this.transactionId = new HashIdentifier(tInf.getId().toByteArray());
        switch (tInf.getResult()) {
            case SUCESS:
                this.result = TronTransaction.Code.SUCCESS;
                break;
            case FAILED:
                this.result = TronTransaction.Code.FAILED;
                break;
            default:
                this.result = TronTransaction.Code.UNRECOGNIZED;
        }
        this.resultMessage = new String(tInf.getResMessage().toByteArray());
        this.blockNumber = tInf.getBlockNumber();
        this.blockDate = new Date(tInf.getBlockTimeStamp());
        this.contractAddress = new TronAddress(tInf.getContractAddress().toByteArray());
        this.contractsResults = new ArrayList<>();
        for (ByteString bs : tInf.getContractResultList()) {
            this.contractsResults.add(bs.toByteArray());
        }
        this.fee = TronCurrency.sun(tInf.getFee());
        this.unfreezeAmount = TronCurrency.sun(tInf.getUnfreezeAmount());
        this.withdrawAmount = TronCurrency.sun(tInf.getWithdrawAmount());
        this.logs = new ArrayList<>();
        for (Protocol.TransactionInfo.Log log : tInf.getLogList()) {
            this.logs.add(new Log(log));
        }

        this.energyUsage = tInf.getReceipt().getEnergyUsage();
        this.energyFee = TronCurrency.sun(tInf.getReceipt().getEnergyFee());
        this.originEnergyUsage = tInf.getReceipt().getOriginEnergyUsage();
        this.totalEnergyUsage = tInf.getReceipt().getEnergyUsageTotal();
        this.netUsage = tInf.getReceipt().getNetUsage();
        this.netFee = TronCurrency.sun(tInf.getReceipt().getNetFee());

        internalTransactions = new ArrayList<>();
        for (Protocol.InternalTransaction it : tInf.getInternalTransactionsList()) {
            internalTransactions.add(new InternalTransaction(it));
        }
    }

    /**
     * @return The transaction identifier.
     */
    public HashIdentifier getTransactionId() {
        return transactionId;
    }

    /**
     * @return The transaction result code.
     */
    public TronTransaction.Code getResult() {
        return result;
    }

    /**
     * @return tne result message.
     */
    public String getResultMessage() {
        return resultMessage;
    }

    /**
     * @return The block number where the transaction was added.
     */
    public long getBlockNumber() {
        return blockNumber;
    }

    /**
     * @return The date of the block where the transaction was added.
     */
    public Date getBlockDate() {
        return blockDate;
    }

    /**
     * @return The smart contract address (for deploycontract and triggercontract)
     */
    public TronAddress getContractAddress() {
        return contractAddress;
    }

    /**
     * @return The result of the contracts.
     */
    public List<byte[]> getContractsResults() {
        return contractsResults;
    }

    /**
     * @return The fee for the transaction.
     */
    public TronCurrency getFee() {
        return fee;
    }

    /**
     * @return The amount unfrozen.
     */
    public TronCurrency getUnfreezeAmount() {
        return unfreezeAmount;
    }

    /**
     * @return The amount withdrawed.
     */
    public TronCurrency getWithdrawAmount() {
        return withdrawAmount;
    }

    /**
     * @return The logs of the transaction (smart contract events)
     */
    public List<Log> getLogs() {
        return logs;
    }

    /**
     * @return The energy usage of this transaction.
     */
    public long getEnergyUsage() {
        return energyUsage;
    }

    /**
     * @return The energy fee (if required).
     */
    public TronCurrency getEnergyFee() {
        return energyFee;
    }

    /**
     * @return The energy usage by the origin of the smart contract.
     */
    public long getOriginEnergyUsage() {
        return originEnergyUsage;
    }

    /**
     * @return The total energy usage by the transaction.
     */
    public long getTotalEnergyUsage() {
        return totalEnergyUsage;
    }

    /**
     * @return The net usage of the transaction.
     */
    public long getNetUsage() {
        return netUsage;
    }

    /**
     * @return the net fee (if required).
     */
    public TronCurrency getNetFee() {
        return netFee;
    }

    /**
     * @return the list of internal transactions.
     */
    public List<InternalTransaction> getInternalTransactions() {
        return internalTransactions;
    }

    /**
     * @return The object as Json.
     */
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    /**
     * Prints the object in stdout.
     *
     * @param indent The indent for the print.
     * @param client Client for interpreting the methods.
     */
    public void print(String indent, TronClient client) {
        System.out.println(indent + "Transaction id: " + this.getTransactionId().toString());
        System.out.println(indent + "Result: " + this.getResult().toString());
        System.out.println(indent + "Message: " + this.getResultMessage());
        System.out.println();
        System.out.println(indent + "Block: " + this.getBlockNumber());
        System.out.println(indent + "Block date: " + this.getBlockDate().toString());
        System.out.println();
        System.out.println(indent + "Contract address: " + this.getContractAddress());
        if (!this.getContractsResults().isEmpty()) {
            System.out.println(indent + "Contract results: ");
            for (byte[] res : this.getContractsResults()) {
                System.out.println(indent + "    " + Hex.toHexString(res));
            }
        }
        if (!this.getLogs().isEmpty()) {
            System.out.println(indent + "Logs: ");
            for (Log log : this.getLogs()) {
                if (client == null) {
                    log.print(indent + "    ");
                } else {
                    TronSmartContract contract;

                    try {
                        contract = client.getContract(log.getAddress());
                    } catch (Exception ex) {
                        log.print(indent + "    ");
                        continue;
                    }

                    TronSmartContractEvent event = contract.interpretLog(log);

                    if (event != null) {
                        if (event.isInterpretable()) {
                            System.out.println(indent + "    EVENT-----------------------------------------");
                            System.out.println(indent + "    " + "Contract: " + log.getAddress().toString());
                            System.out.println(indent + "    " + "Event: " + event.getEventSignature());
                            System.out.println(indent + "    " + "Parameters: ");
                            for (String param : event.getTypes().keySet()) {
                                System.out.println(indent + "    " + "    " + param + " ("
                                        + event.getTypes().get(param) + ") = "
                                        + TronUtils.valueToString(event.getValues().get(param)));
                            }
                            System.out.println(indent + "    ----------------------------------------------");
                        } else {
                            System.out.println(indent + "    EVENT-----------------------------------------");
                            System.out.println(indent + "    " + "Contract: " + log.getAddress().toString());
                            System.out.println(indent + "    " + "Event: " + event.getEventSignature());
                            System.out.println(indent + "    " + "Data: " + Hex.toHexString(log.getData()));
                            System.out.println(indent + "    " + "Topics: ");
                            for (byte[] topic : log.getTopics()) {
                                System.out.println(indent + "    " + "    " + Hex.toHexString(topic));
                            }
                            System.out.println(indent + "    ----------------------------------------------");
                        }
                    } else {
                        log.print(indent + "    ");
                    }


                }

            }
        }
        System.out.println();

        if (!this.internalTransactions.isEmpty()) {
            System.out.println(indent + "Internal Transactions: ");
            for (InternalTransaction t : this.internalTransactions) {
                System.out.println(indent + "    INTERNAL-TRANSACTION-------------------------------");
                System.out.println(indent + "    " + "Hash: " + t.getHash().toString());
                System.out.println(indent + "    " + "Caller: " + t.getCaller().toString());
                System.out.println(indent + "    " + "To: " + t.getToAddress().toString());
                if (!t.getCallvalueInfo().isEmpty()) {
                    System.out.println(indent + "    " + "Call Values:");
                    for (CallValueInfo cv : t.getCallvalueInfo()) {
                        System.out.println(indent + "    " + "    " + cv.getCallValue() + " " + cv.getTokenName());
                    }
                }
                System.out.println(indent + "    ---------------------------------------------------");
            }
            System.out.println();
        }

        System.out.println(indent + "Fee: " + String.format("%.0f TRX", this.getFee().getTRX()));
        System.out.println(indent + "Energy Fee: " + String.format("%.0f TRX", this.getEnergyFee().getTRX()));
        System.out.println(indent + "Net Fee: " + String.format("%.0f TRX", this.getNetFee().getTRX()));
        System.out.println();
        System.out.println(indent + "Energy usage: " + this.getEnergyUsage());
        System.out.println(indent + "Energy usage (Origin): " + this.getOriginEnergyUsage());
        System.out.println(indent + "Total energy usage: " + this.getTotalEnergyUsage());
        System.out.println();
        System.out.println(indent + "Net usage: " + this.getNetUsage());
        System.out.println();
        System.out.println(indent + "Unfrozen balance " + String.format("%.0f TRX", this.getUnfreezeAmount().getTRX()));
        System.out.println(indent + "Withdrawn balance: " + String.format("%.0f TRX", this.getWithdrawAmount().getTRX()));
    }

    public void print(String indent) {
        print(indent, null);
    }
}
