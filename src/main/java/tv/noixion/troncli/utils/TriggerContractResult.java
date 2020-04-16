package tv.noixion.troncli.utils;

import tv.noixion.troncli.exceptions.InvalidCallDataException;
import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TVMTypes;
import tv.noixion.troncli.models.TronTransaction;
import tv.noixion.troncli.models.contracts.TriggerSmartContractContract;

import java.math.BigInteger;
import java.util.List;

/**
 * Represents the result of a trigger contract call.
 */
public class TriggerContractResult {
    private boolean is_transaction;
    private byte[] returnedData;
    private TronTransaction transaction;

    public TriggerContractResult(TronTransaction transaction) {
        this.transaction = transaction;
        this.returnedData = null;
        this.is_transaction = true;
    }

    public TriggerContractResult(byte[] returnedData) {
        this.transaction = null;
        this.returnedData = returnedData;
        this.is_transaction = false;
    }

    /**
     * @return true if contains a transaction, false otherwise.
     */
    public boolean isTransaction() {
        return is_transaction;
    }

    /**
     * @return The transaction.
     */
    public TronTransaction getTransaction() {
        return transaction;
    }

    /**
     * @return The returned data by the method call.
     */
    public byte[] getReturnedData() {
        return returnedData;
    }

    /**
     * Interprets the returned data.
     *
     * @param type the TVM type.
     * @return The list of values found.
     * @throws InvalidCallDataException If the data or the type are invalid.
     */
    public List<Object> interpretReturnedData(String type) throws InvalidCallDataException {
        return TriggerSmartContractContract.unpack(TVMTypes.makeListOfTypes(type), this.returnedData);
    }

    /**
     * Interprets the returned data.
     *
     * @param types the TVM types.
     * @return The list of values found.
     * @throws InvalidCallDataException If the data or the type are invalid.
     */
    public List<Object> interpretReturnedData(List<String> types) throws InvalidCallDataException {
        return TriggerSmartContractContract.unpack(types, this.returnedData);
    }

    /**
     * Interprets the result as boolean.
     *
     * @return the result.
     * @throws InvalidCallDataException If the data or the type are invalid.
     */
    public boolean getResultAsBool() throws InvalidCallDataException {
        try {
            return (Boolean) interpretReturnedData(TVMTypes.BOOLEAN_TYPE).get(0);
        } catch (Exception ex) {
            throw new InvalidCallDataException(ex.getMessage());
        }
    }

    /**
     * Interprets the result as an address.
     *
     * @return the result.
     * @throws InvalidCallDataException If the data or the type are invalid.
     */
    public TronAddress getResultAsAddress() throws InvalidCallDataException {
        try {
            return (TronAddress) interpretReturnedData(TVMTypes.ADDRESS_TYPE).get(0);
        } catch (Exception ex) {
            throw new InvalidCallDataException(ex.getMessage());
        }
    }

    /**
     * Interprets the result as an integer.
     *
     * @return the result.
     * @throws InvalidCallDataException If the data or the type are invalid.
     */
    public BigInteger getResultAsInt() throws InvalidCallDataException {
        try {
            return (BigInteger) interpretReturnedData(TVMTypes.INT_TYPE).get(0);
        } catch (Exception ex) {
            throw new InvalidCallDataException(ex.getMessage());
        }
    }

    /**
     * Interprets the result as an unsigned integer.
     *
     * @return the result.
     * @throws InvalidCallDataException If the data or the type are invalid.
     */
    public BigInteger getResultAsUInt() throws InvalidCallDataException {
        try {
            return (BigInteger) interpretReturnedData(TVMTypes.UINT_TYPE).get(0);
        } catch (Exception ex) {
            throw new InvalidCallDataException(ex.getMessage());
        }
    }

    /**
     * Interprets the result as an string.
     *
     * @return the result.
     * @throws InvalidCallDataException If the data or the type are invalid.
     */
    public String getResultAsString() throws InvalidCallDataException {
        try {
            return (String) interpretReturnedData(TVMTypes.STRING_TYPE).get(0);
        } catch (Exception ex) {
            throw new InvalidCallDataException(ex.getMessage());
        }
    }

    /**
     * Interprets the result as a fixed array of bytes.
     *
     * @param length The length of the array.
     * @return the result.
     * @throws InvalidCallDataException If the data or the type are invalid.
     */
    public byte[] getResultAsFixedBytes(int length) throws InvalidCallDataException {
        try {
            return (byte[]) interpretReturnedData(TVMTypes.BYTES(length)).get(0);
        } catch (Exception ex) {
            throw new InvalidCallDataException(ex.getMessage());
        }
    }

    /**
     * Interprets the result as a dynamic array of bytes
     *
     * @return the result.
     * @throws InvalidCallDataException If the data or the type are invalid.
     */
    public byte[] getResultAsBytes() throws InvalidCallDataException {
        try {
            return (byte[]) interpretReturnedData(TVMTypes.DYN_BYTES_TYPE).get(0);
        } catch (Exception ex) {
            throw new InvalidCallDataException(ex.getMessage());
        }
    }

    /**
     * Interprets the result as an array.
     *
     * @param type the type of the elements of the array.
     * @return the result.
     * @throws InvalidCallDataException If the data or the type are invalid.
     */
    public Object[] getResultAsArray(String type) throws InvalidCallDataException {
        try {
            return (Object[]) interpretReturnedData(TVMTypes.ARRAY(type)).get(0);
        } catch (Exception ex) {
            throw new InvalidCallDataException(ex.getMessage());
        }
    }

    /**
     * Interprets the result as an array.
     *
     * @param type   the type of the elements of the array.
     * @param length the length of the array
     * @return the result.
     * @throws InvalidCallDataException If the data or the type are invalid.
     */
    public Object[] getResultAsArray(String type, int length) throws InvalidCallDataException {
        try {
            return (Object[]) interpretReturnedData(TVMTypes.ARRAY(type, length)).get(0);
        } catch (Exception ex) {
            throw new InvalidCallDataException(ex.getMessage());
        }
    }

    /**
     * Interprets the result as an array of int.
     *
     * @return the result.
     * @throws InvalidCallDataException If the data or the type are invalid.
     */
    public BigInteger[] getResultAsArrayInt() throws InvalidCallDataException {
        try {
            return (BigInteger[]) interpretReturnedData(TVMTypes.ARRAY(TVMTypes.INT_TYPE)).get(0);
        } catch (Exception ex) {
            throw new InvalidCallDataException(ex.getMessage());
        }
    }

    /**
     * Interprets the result as an array of uint.
     *
     * @return the result.
     * @throws InvalidCallDataException If the data or the type are invalid.
     */
    public BigInteger[] getResultAsArrayUInt() throws InvalidCallDataException {
        try {
            return (BigInteger[]) interpretReturnedData(TVMTypes.ARRAY(TVMTypes.UINT_TYPE)).get(0);
        } catch (Exception ex) {
            throw new InvalidCallDataException(ex.getMessage());
        }
    }

    /**
     * Interprets the result as an array of string.
     *
     * @return the result.
     * @throws InvalidCallDataException If the data or the type are invalid.
     */
    public String[] getResultAsArrayString() throws InvalidCallDataException {
        try {
            return (String[]) interpretReturnedData(TVMTypes.ARRAY(TVMTypes.STRING_TYPE)).get(0);
        } catch (Exception ex) {
            throw new InvalidCallDataException(ex.getMessage());
        }
    }

    /**
     * Interprets the result as an array of bool.
     *
     * @return the result.
     * @throws InvalidCallDataException If the data or the type are invalid.
     */
    public boolean[] getResultAsArrayBool() throws InvalidCallDataException {
        try {
            return (boolean[]) interpretReturnedData(TVMTypes.ARRAY(TVMTypes.BOOLEAN_TYPE)).get(0);
        } catch (Exception ex) {
            throw new InvalidCallDataException(ex.getMessage());
        }
    }

    /**
     * Interprets the result as an array of address.
     *
     * @return the result.
     * @throws InvalidCallDataException If the data or the type are invalid.
     */
    public TronAddress[] getResultAsArrayAddress() throws InvalidCallDataException {
        try {
            return (TronAddress[]) interpretReturnedData(TVMTypes.ARRAY(TVMTypes.ADDRESS_TYPE)).get(0);
        } catch (Exception ex) {
            throw new InvalidCallDataException(ex.getMessage());
        }
    }
}
