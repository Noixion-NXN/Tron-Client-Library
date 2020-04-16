import tv.noixion.troncli.TronClient;
import tv.noixion.troncli.TronContractProxy;
import tv.noixion.troncli.exceptions.GRPCException;
import tv.noixion.troncli.exceptions.InvalidCallDataException;
import tv.noixion.troncli.exceptions.TransactionException;
import tv.noixion.troncli.utils.TriggerContractDataBuilder;
import tv.noixion.troncli.utils.TronUtils;
import org.tron.core.exception.EncodingException;

import java.util.Map;

public class TestProxy extends TronContractProxy {

    // This wallet is used for view and pure methods,
    // is just a random wallet used to put an address in the call message.
    private final TronWallet viewWallet;

    public TestProxy (TronClient client, TronAddress contractAddress) throws GRPCException {
        super(client, contractAddress);
        this.viewWallet = new TronWallet();
    }

    public String f() throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(this.viewWallet, new TriggerContractDataBuilder(getContract().getMethodsSignature("f")), TronCurrency.ZERO, TronCurrency.ZERO).getResultAsString();
    }


    public String g() throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(this.viewWallet, new TriggerContractDataBuilder(getContract().getMethodsSignature("g")), TronCurrency.ZERO, TronCurrency.ZERO).getResultAsString();
    }


    @Override
    public void handleEvent(String eventName, String eventSignature, Map<String, String> types,Map<String, Object> values) {
        // Handle events
    }

    @Override
    public void handleNotInterpretableEvent(TronTransactionInformation.Log event) {
        // Handle unknown events
    }

}