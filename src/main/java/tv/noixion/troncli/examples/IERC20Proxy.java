package tv.noixion.troncli.examples;

import tv.noixion.troncli.exceptions.GRPCException;
import tv.noixion.troncli.exceptions.InvalidCallDataException;
import tv.noixion.troncli.exceptions.TransactionException;
import tv.noixion.troncli.models.*;
import tv.noixion.troncli.TronClient;
import tv.noixion.troncli.TronContractProxy;
import tv.noixion.troncli.models.*;
import tv.noixion.troncli.utils.TriggerContractDataBuilder;
import tv.noixion.troncli.utils.TronUtils;
import org.tron.core.exception.EncodingException;

import java.math.BigInteger;
import java.util.Map;

/**
 * Proxy for the basic standard token ERC20.
 */
public class IERC20Proxy extends TronContractProxy {

    // This wallet is used for view and pure methods,
    // is just a random wallet used to put an address in the call message.
    private final TronWallet viewWallet;

    /**
     * Creates a new instance of IERC20Proxy.
     *
     * @param client
     * @param contractAddress
     * @throws GRPCException
     */
    public IERC20Proxy(TronClient client,
                       TronAddress contractAddress)
            throws GRPCException {
        super(client, contractAddress);
        this.viewWallet = new TronWallet();
    }

    /* View methods */

    /**
     * Retrieves the total supply of token.
     *
     * @return the total supply of token.
     * @throws GRPCException            If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException     If The transaction fails to be sent.
     * @throws EncodingException        If the method call is invalid
     * @throws InvalidCallDataException If the method result is invalid
     */
    public BigInteger totalSupply()
            throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(this.viewWallet,
                new TriggerContractDataBuilder("totalSupply()"),
                TronCurrency.ZERO,
                TronCurrency.ZERO).getResultAsUInt();
    }

    /**
     * Retrieves the balance of an account.
     *
     * @param address The address of the account.
     * @return The balance of the account
     * @throws GRPCException            If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException     If The transaction fails to be sent.
     * @throws EncodingException        If the method call is invalid
     * @throws InvalidCallDataException If the method result is invalid
     */
    public BigInteger balanceOf(TronAddress address)
            throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(this.viewWallet,
                new TriggerContractDataBuilder("balanceOf(address)")
                        .paramAddress(address),
                TronCurrency.ZERO,
                TronCurrency.ZERO).getResultAsUInt();
    }

    /**
     * Retrieves the allowance of an account.
     *
     * @param address The account with the founds.
     * @param spender The spender account.
     * @return The value allowed to spend.
     * @throws GRPCException            If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException     If The transaction fails to be sent.
     * @throws EncodingException        If the method call is invalid
     * @throws InvalidCallDataException If the method result is invalid
     */
    public BigInteger allowance(TronAddress address, TronAddress spender)
            throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(this.viewWallet,
                new TriggerContractDataBuilder("allowance(address,address)")
                        .paramAddress(address)
                        .paramAddress(spender),
                TronCurrency.ZERO,
                TronCurrency.ZERO).getResultAsUInt();
    }

    /* Non payable methods */

    /**
     * Transfers tokens.
     *
     * @param sender The sender of the transaction (owner of the tokens).
     * @param to     The receiver.
     * @param value  The amount.
     * @return The built transaction.
     * @throws GRPCException            If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException     If The transaction fails to be sent.
     * @throws EncodingException        If the method call is invalid
     * @throws InvalidCallDataException If the method result is invalid
     */
    public TronTransaction transfer(TronWallet sender, TronAddress to, BigInteger value)
            throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(sender,
                new TriggerContractDataBuilder("transfer(address,uint256)")
                        .paramAddress(to)
                        .paramUInt(value),
                TronCurrency.MAX_FEE_LIMIT,
                TronCurrency.ZERO).getTransaction();
    }

    /**
     * Approves another account to spend tokens.
     *
     * @param sender  The sender of the transaction (owner of the tokens).
     * @param spender The spender address.
     * @param value   The amount.
     * @return The built transaction.
     * @throws GRPCException            If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException     If The transaction fails to be sent.
     * @throws EncodingException        If the method call is invalid
     * @throws InvalidCallDataException If the method result is invalid
     */
    public TronTransaction approve(TronWallet sender, TronAddress spender, BigInteger value)
            throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(sender,
                new TriggerContractDataBuilder("approve(address,uint256)")
                        .paramAddress(spender)
                        .paramUInt(value),
                TronCurrency.MAX_FEE_LIMIT,
                TronCurrency.ZERO).getTransaction();
    }

    /**
     * Transfers from one account to another account. Must be allowed to spend.
     *
     * @param sender The sender of the transaction.
     * @param from   The owner of the tokens.
     * @param to     The receiver.
     * @param value  The amount.
     * @return The built transaction.
     * @throws GRPCException            If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException     If The transaction fails to be sent.
     * @throws EncodingException        If the method call is invalid
     * @throws InvalidCallDataException If the method result is invalid
     */
    public TronTransaction transferFrom(TronWallet sender, TronAddress from, TronAddress to, BigInteger value)
            throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(sender,
                new TriggerContractDataBuilder("transferFrom(address,address,uint256)")
                        .paramAddress(from)
                        .paramAddress(to)
                        .paramUInt(value),
                TronCurrency.MAX_FEE_LIMIT,
                TronCurrency.ZERO).getTransaction();
    }


    /* Event handling */

    @Override
    public void handleEvent(String eventName,
                            String eventSignature,
                            Map<String, String> types,
                            Map<String, Object> values) {
        System.out.println("------------------------------------------------------------------");
        System.out.println("[EVENT RECEIVED] " + eventName);
        for (String param : types.keySet()) {
            System.out.println("    " + param + " (" + types.get(param) + ") = " + TronUtils.valueToString(values.get(param)));
        }
        System.out.println("------------------------------------------------------------------");
    }

    @Override
    public void handleNotInterpretableEvent(TronTransactionInformation.Log event) {
        System.out.println("------------------------------------------------------------------");
        System.out.println("[EVENT RECEIVED] (Not interpretable)");
        System.out.println("    Address: " + event.getAddress().toString());
        System.out.println("    Data: " + new HashIdentifier(event.getData()).toString());
        System.out.println("    Topics:");
        for (byte[] topic : event.getTopics()) {
            System.out.println("        " + new HashIdentifier(topic).toString());
        }
        System.out.println("------------------------------------------------------------------");

    }
}
