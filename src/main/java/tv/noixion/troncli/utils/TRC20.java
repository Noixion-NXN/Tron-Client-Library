package tv.noixion.troncli.utils;

import tv.noixion.troncli.TronClient;
import tv.noixion.troncli.exceptions.GRPCException;
import tv.noixion.troncli.exceptions.InvalidCallDataException;
import tv.noixion.troncli.exceptions.TransactionException;
import tv.noixion.troncli.models.*;
import tv.noixion.troncli.TronContractProxy;
import com.google.protobuf.InvalidProtocolBufferException;
import org.tron.core.exception.EncodingException;
import tv.noixion.troncli.models.*;

import java.math.BigInteger;
import java.util.Map;

public class TRC20 extends TronContractProxy {

private static final String CONTRACT_ABI_JSON = "{\"entrys\":[{\"constant\":true,\"name\":\"name\",\"outputs\":[{\"type\":\"string\"}],\"type\":\"Function\",\"stateMutability\":\"View\"},{\"name\":\"approve\",\"inputs\":[{\"name\":\"spender\",\"type\":\"address\"},{\"name\":\"value\",\"type\":\"uint256\"}],\"outputs\":[{\"type\":\"bool\"}],\"type\":\"Function\",\"stateMutability\":\"Nonpayable\"},{\"constant\":true,\"name\":\"totalSupply\",\"outputs\":[{\"type\":\"uint256\"}],\"type\":\"Function\",\"stateMutability\":\"View\"},{\"name\":\"transferFrom\",\"inputs\":[{\"name\":\"from\",\"type\":\"address\"},{\"name\":\"to\",\"type\":\"address\"},{\"name\":\"value\",\"type\":\"uint256\"}],\"outputs\":[{\"type\":\"bool\"}],\"type\":\"Function\",\"stateMutability\":\"Nonpayable\"},{\"name\":\"revokeAdmin\",\"inputs\":[{\"name\":\"_address\",\"type\":\"address\"}],\"type\":\"Function\",\"stateMutability\":\"Nonpayable\"},{\"constant\":true,\"name\":\"decimals\",\"outputs\":[{\"type\":\"uint8\"}],\"type\":\"Function\",\"stateMutability\":\"View\"},{\"name\":\"grantAdmin\",\"inputs\":[{\"name\":\"_address\",\"type\":\"address\"}],\"type\":\"Function\",\"stateMutability\":\"Nonpayable\"},{\"name\":\"increaseAllowance\",\"inputs\":[{\"name\":\"spender\",\"type\":\"address\"},{\"name\":\"addedValue\",\"type\":\"uint256\"}],\"outputs\":[{\"type\":\"bool\"}],\"type\":\"Function\",\"stateMutability\":\"Nonpayable\"},{\"name\":\"renameToken\",\"inputs\":[{\"name\":\"_name\",\"type\":\"string\"},{\"name\":\"_symbol\",\"type\":\"string\"}],\"type\":\"Function\",\"stateMutability\":\"Nonpayable\"},{\"name\":\"burn\",\"inputs\":[{\"name\":\"value\",\"type\":\"uint256\"}],\"outputs\":[{\"type\":\"bool\"}],\"type\":\"Function\",\"stateMutability\":\"Nonpayable\"},{\"name\":\"pauseContract\",\"type\":\"Function\",\"stateMutability\":\"Nonpayable\"},{\"constant\":true,\"name\":\"paused\",\"outputs\":[{\"type\":\"bool\"}],\"type\":\"Function\",\"stateMutability\":\"View\"},{\"constant\":true,\"name\":\"_balances\",\"inputs\":[{\"type\":\"address\"}],\"outputs\":[{\"type\":\"uint256\"}],\"type\":\"Function\",\"stateMutability\":\"View\"},{\"constant\":true,\"name\":\"balanceOf\",\"inputs\":[{\"name\":\"who\",\"type\":\"address\"}],\"outputs\":[{\"type\":\"uint256\"}],\"type\":\"Function\",\"stateMutability\":\"View\"},{\"constant\":true,\"name\":\"administrators\",\"inputs\":[{\"type\":\"address\"}],\"outputs\":[{\"type\":\"bool\"}],\"type\":\"Function\",\"stateMutability\":\"View\"},{\"constant\":true,\"name\":\"symbol\",\"outputs\":[{\"type\":\"string\"}],\"type\":\"Function\",\"stateMutability\":\"View\"},{\"name\":\"decreaseAllowance\",\"inputs\":[{\"name\":\"spender\",\"type\":\"address\"},{\"name\":\"subtractedValue\",\"type\":\"uint256\"}],\"outputs\":[{\"type\":\"bool\"}],\"type\":\"Function\",\"stateMutability\":\"Nonpayable\"},{\"name\":\"transfer\",\"inputs\":[{\"name\":\"to\",\"type\":\"address\"},{\"name\":\"value\",\"type\":\"uint256\"}],\"outputs\":[{\"type\":\"bool\"}],\"type\":\"Function\",\"stateMutability\":\"Nonpayable\"},{\"name\":\"pay\",\"inputs\":[{\"name\":\"seller\",\"type\":\"address\"},{\"name\":\"value\",\"type\":\"uint256\"},{\"name\":\"uint_param\",\"type\":\"uint256[]\"},{\"name\":\"adrr_param\",\"type\":\"address[]\"},{\"name\":\"bin_param\",\"type\":\"bytes32[]\"}],\"type\":\"Function\",\"stateMutability\":\"Nonpayable\"},{\"name\":\"transferRoot\",\"inputs\":[{\"name\":\"_address\",\"type\":\"address\"}],\"type\":\"Function\",\"stateMutability\":\"Nonpayable\"},{\"name\":\"resumeContract\",\"type\":\"Function\",\"stateMutability\":\"Nonpayable\"},{\"constant\":true,\"name\":\"allowance\",\"inputs\":[{\"name\":\"owner\",\"type\":\"address\"},{\"name\":\"spender\",\"type\":\"address\"}],\"outputs\":[{\"type\":\"uint256\"}],\"type\":\"Function\",\"stateMutability\":\"View\"},{\"constant\":true,\"name\":\"root\",\"outputs\":[{\"type\":\"address\"}],\"type\":\"Function\",\"stateMutability\":\"View\"},{\"inputs\":[{\"name\":\"initial_supply\",\"type\":\"uint256\"},{\"name\":\"token_name\",\"type\":\"string\"},{\"name\":\"token_symbol\",\"type\":\"string\"},{\"name\":\"token_decimals\",\"type\":\"uint8\"}],\"type\":\"Constructor\",\"stateMutability\":\"Nonpayable\"},{\"name\":\"Transfer\",\"inputs\":[{\"indexed\":true,\"name\":\"from\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"to\",\"type\":\"address\"},{\"name\":\"value\",\"type\":\"uint256\"}],\"type\":\"Event\"},{\"name\":\"Approval\",\"inputs\":[{\"indexed\":true,\"name\":\"owner\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"spender\",\"type\":\"address\"},{\"name\":\"value\",\"type\":\"uint256\"}],\"type\":\"Event\"}]}";

    // This wallet is used for view and pure methods,
    // is just a random wallet used to put an address in the call message.
    private final TronWallet viewWallet;

    private String name = null;

    private int _decimals = 0;
    private boolean _decimals_set = false;

    public TRC20(TronClient client, TronAddress contractAddress) throws GRPCException, InvalidProtocolBufferException {
        super(client, contractAddress, TronUtils.jsonToABI(CONTRACT_ABI_JSON));
        this.viewWallet = new TronWallet();
    }

    private void fetchDecimals() {
        if (!_decimals_set) {
            try {
                _decimals = decimals().intValue();
                _decimals_set = true;
            } catch (Exception ex) {
                ex.printStackTrace();
                _decimals = 0;
            }
        }
    }


    /**
     * Retrieves a value without decimals.
     *
     * @param value The value.
     * @return the value without decimals.
     */
    public BigInteger withoutDecimals(double value) {
        fetchDecimals();
        return BigInteger.valueOf(Math.round(value * Math.pow(10, _decimals)));
    }

    /**
     * Retrieves a value with decimals.
     *
     * @param value the value.
     * @return the value with decimals.
     */
    public double withDecimals(BigInteger value) {
        fetchDecimals();
        return ((double) value.longValue()) / Math.pow(10, _decimals);
    }

    public String name() throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        if (name != null) {
            return name;
        }
        name = this.callMethod(this.viewWallet, new TriggerContractDataBuilder(getContract().getMethodsSignature("name")), TronCurrency.ZERO, TronCurrency.ZERO).getResultAsString();
        return name;
    }


    public BigInteger totalSupply() throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(this.viewWallet, new TriggerContractDataBuilder(getContract().getMethodsSignature("totalSupply")), TronCurrency.ZERO, TronCurrency.ZERO).getResultAsInt();
    }


    public BigInteger decimals() throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(this.viewWallet, new TriggerContractDataBuilder(getContract().getMethodsSignature("decimals")), TronCurrency.ZERO, TronCurrency.ZERO).getResultAsInt();
    }

    public BigInteger balanceOf(TronAddress who) throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(this.viewWallet, new TriggerContractDataBuilder(getContract().getMethodsSignature("balanceOf")).params(who), TronCurrency.ZERO, TronCurrency.ZERO).getResultAsInt();
    }


    public String symbol() throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(this.viewWallet, new TriggerContractDataBuilder(getContract().getMethodsSignature("symbol")), TronCurrency.ZERO, TronCurrency.ZERO).getResultAsString();
    }


    public BigInteger allowance(TronAddress owner, TronAddress spender) throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(this.viewWallet, new TriggerContractDataBuilder(getContract().getMethodsSignature("allowance")).params(owner, spender), TronCurrency.ZERO, TronCurrency.ZERO).getResultAsInt();
    }


    public TronTransaction approve(TronWallet sender, TronAddress spender, BigInteger value) throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(sender, new TriggerContractDataBuilder(getContract().getMethodsSignature("approve")).params(spender, value), TronCurrency.MAX_FEE_LIMIT, TronCurrency.ZERO).getTransaction();
    }


    public TronTransaction transferFrom(TronWallet sender, TronAddress from, TronAddress to, BigInteger value) throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(sender, new TriggerContractDataBuilder(getContract().getMethodsSignature("transferFrom")).params(from, to, value), TronCurrency.MAX_FEE_LIMIT, TronCurrency.ZERO).getTransaction();
    }


    public TronTransaction increaseAllowance(TronWallet sender, TronAddress spender, BigInteger addedValue) throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(sender, new TriggerContractDataBuilder(getContract().getMethodsSignature("increaseAllowance")).params(spender, addedValue), TronCurrency.MAX_FEE_LIMIT, TronCurrency.ZERO).getTransaction();
    }


    public TronTransaction burn(TronWallet sender, BigInteger value) throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(sender, new TriggerContractDataBuilder(getContract().getMethodsSignature("burn")).params(value), TronCurrency.MAX_FEE_LIMIT, TronCurrency.ZERO).getTransaction();
    }

    public TronTransaction decreaseAllowance(TronWallet sender, TronAddress spender, BigInteger subtractedValue) throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(sender, new TriggerContractDataBuilder(getContract().getMethodsSignature("decreaseAllowance")).params(spender, subtractedValue), TronCurrency.MAX_FEE_LIMIT, TronCurrency.ZERO).getTransaction();
    }


    public TronTransaction transfer(TronWallet sender, TronAddress to, BigInteger value) throws GRPCException, TransactionException, EncodingException, InvalidCallDataException {
        return this.callMethod(sender, new TriggerContractDataBuilder(getContract().getMethodsSignature("transfer")).params(to, value), TronCurrency.MAX_FEE_LIMIT, TronCurrency.ZERO).getTransaction();
    }


    @Override
    public void handleEvent(String eventName, String eventSignature, Map<String, String> types,Map<String, Object> values) {
        // Handle events
        // Event: Transfer(address from, address to, uint256 value)
        // Event: Approval(address owner, address spender, uint256 value)
    }

    @Override
    public void handleNotInterpretableEvent(TronTransactionInformation.Log event) {
        // Handle unknown events
    }

}

