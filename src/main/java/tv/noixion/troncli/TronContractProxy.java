package tv.noixion.troncli;

import tv.noixion.troncli.exceptions.GRPCException;
import tv.noixion.troncli.exceptions.TransactionException;
import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronCurrency;
import tv.noixion.troncli.models.TronWallet;
import tv.noixion.troncli.utils.TriggerContractDataBuilder;
import tv.noixion.troncli.utils.TriggerContractResult;
import org.tron.core.exception.EncodingException;
import org.tron.protos.Protocol;

/**
 * Represents a org.tron smart contract, allowing method calls.
 */
public abstract class TronContractProxy extends SmartContractEventListener {
    private TronClient client;
    private TronAddress contractAddress;

    /**
     * Creates a new instance of TronContractProxy.
     *
     * @param client          The client.
     * @param contractAddress The contract address.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public TronContractProxy(TronClient client, TronAddress contractAddress) throws GRPCException {
        this(client, contractAddress, client.getContract(contractAddress).getAbi());
    }

    /**
     * Creates a new instance of TronContractProxy.
     *
     * @param client          The client.
     * @param contractAddress the contract address.
     * @param contractABI     The contract ABI.
     */
    public TronContractProxy(TronClient client, TronAddress contractAddress, Protocol.SmartContract.ABI contractABI) {
        super(contractAddress, contractABI);
        this.client = client;
        this.contractAddress = contractAddress;
    }

    /**
     * Calls a method of the smart contract.
     *
     * @param sender    The sender of the transaction.
     * @param call      The method call.
     * @param feeLimit  The fee limit for the transaction.
     * @param callValue The call value.
     * @return The call result.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     * @throws EncodingException    If the method call is invalid.
     */
    public TriggerContractResult callMethod(TronWallet sender,
                                            TriggerContractDataBuilder call,
                                            TronCurrency feeLimit,
                                            TronCurrency callValue)
            throws GRPCException, TransactionException, EncodingException {
        return this.client.triggerSmartContract(sender, this.contractAddress, call, feeLimit,
                callValue, 0, 0);
    }

    /**
     * Calls a method of the smart contract.
     *
     * @param sender           The sender of the transaction.
     * @param call             The method call.
     * @param feeLimit         The fee limit for the transaction.
     * @param callValue        The call value.
     * @param callValueTokenId The token to send as a token call value
     * @param callValueToken   The number of tokens to send as call value
     * @return The call result.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     * @throws EncodingException    If the method call is invalid.
     */
    public TriggerContractResult callMethod(TronWallet sender,
                                            TriggerContractDataBuilder call,
                                            TronCurrency feeLimit,
                                            TronCurrency callValue,
                                            long callValueTokenId, long callValueToken)
            throws GRPCException, TransactionException, EncodingException {
        return this.client.triggerSmartContract(sender, this.contractAddress, call, feeLimit,
                callValue, callValueTokenId, callValueToken);
    }
}
