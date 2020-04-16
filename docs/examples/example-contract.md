# Contract proxy example

Here is an example of a contract proxy class. You may write your own for your contracts.

This class defines the contract methods as trigger calls to the contract using a org.tron wallet client.

Here is an example:

```java
package tv.noixion.troncli.examples;

import tv.noixion.tronclient;
import tv.noixion.troncli.TronContractProxy;
import tv.noixion.troncli.exceptions.GRPCException;
import tv.noixion.troncli.exceptions.InvalidCallDataException;
import tv.noixion.troncli.exceptions.TransactionException;
import tv.noixion.troncli.models.*;
import tv.noixion.troncli.utils.TriggerContractDataBuilder;
import tv.noixion.troncli.utils.TronUtils;
import org.org.tron.core.exception.EncodingException;

import java.math.BigInteger;
import java.util.Map;

/**
 * Proxy for the basic standard token ERC20.
 */
public class IERC20Proxy extends TronContractProxy {

    // This wallet is used for view and pure methods,
    // is just a random wallet used to put an address in the call message.
    private final TronWallet viewWallet;

    private static final TronCurrency FEE_LIMIT_FOR_CALLS = TronCurrency.trx(1000.0);

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
                TronCurrency.sun(0L),
                TronCurrency.sun(0L)).getResultAsUInt();
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
                TronCurrency.sun(0L),
                TronCurrency.sun(0L)).getResultAsUInt();
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
                TronCurrency.sun(0L),
                TronCurrency.sun(0L)).getResultAsUInt();
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
                FEE_LIMIT_FOR_CALLS,
                TronCurrency.sun(0L)).getTransaction();
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
                FEE_LIMIT_FOR_CALLS,
                TronCurrency.sun(0L)).getTransaction();
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
                FEE_LIMIT_FOR_CALLS,
                TronCurrency.sun(0L)).getTransaction();
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
```

Once the proxy classes are defined, you can interact with your smart contracts creating proxy instances and calling their methods.

Example:

```java
package tv.noixion.troncli.examples;

import tv.noixion.troncli.TronBlockChainWatcher;
import tv.noixion.tronclient;
import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronNode;

public class ExampleContract {

    public static final String ADDRESS = "TFA1qpUkQ1yBDw4pgZKx25wEZAqkjGoZo1";
    public static final String CONTRACT_ADDRESS = "THLT8tm9SznCoHLRZRe2kxP3L7JHF3wtsX";

    public static void main(String[] argv) throws Exception {
        TronClient client = new TronClient(new TronNode("grpc.trongrid.io:50051"));

        IERC20Proxy proxy = new IERC20Proxy(client, new TronAddress(CONTRACT_ADDRESS));

        System.out.println("Total supply: " + proxy.totalSupply().toString());
        System.out.printf("Balance of %s is %.0f TRX\n", ADDRESS, proxy.balanceOf(new TronAddress(ADDRESS)).toString());

        TronBlockChainWatcher watcher = new TronBlockChainWatcher(client, client.getLastBlock().getNumber());

        watcher.addHandler(proxy);

        System.out.println("Listening for events...");
        watcher.startWatching();
    }
}
```
