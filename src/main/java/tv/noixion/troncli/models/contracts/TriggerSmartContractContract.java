package tv.noixion.troncli.models.contracts;

import tv.noixion.troncli.TronClient;
import tv.noixion.troncli.exceptions.InvalidCallDataException;
import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronContract;
import tv.noixion.troncli.models.TronCurrency;
import tv.noixion.troncli.models.TronSmartContract;
import com.google.gson.JsonObject;
import org.spongycastle.util.encoders.Hex;
import org.tron.common.crypto.Hash;
import org.tron.common.utils.AbiUtil;
import org.tron.protos.Contract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contract for triggering a contract method.
 */
public class TriggerSmartContractContract extends TronContract {
    private static Pattern paramTypeBytes = Pattern.compile("^bytes([0-9]*)$");
    private static Pattern paramTypeNumber = Pattern.compile("^(int)([0-9]*)$");
    private static Pattern paramTypeNumberUnsigned = Pattern.compile("^(uint)([0-9]*)$");
    private static Pattern paramTypeArray = Pattern.compile("^(.*)\\[([0-9]*)\\]$");
    private final TronAddress contractAddress;
    private final byte[] data;
    private final TronCurrency callValue;

    public TriggerSmartContractContract(Contract.TriggerSmartContract contract) {
        super(Type.TRIGGER_SMART_CONTRACT, new TronAddress(contract.getOwnerAddress().toByteArray()));
        this.contractAddress = new TronAddress(contract.getContractAddress().toByteArray());
        this.data = contract.getData().toByteArray();
        this.callValue = TronCurrency.sun(contract.getCallValue());
    }

    /**
     * @return The contract address.
     */
    public TronAddress getContractAddress() {
        return contractAddress;
    }

    /**
     * @return The call data (can be interpreted with the signature of the method).
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @return The call value.
     */
    public TronCurrency getCallValue() {
        return callValue;
    }

    /**
     * Interprets the call data using the method signature.
     * bool to Boolean
     * uint, int to BigInteger
     * address to TronAddress
     * bytes to byte[]
     *
     * @param methodSignature The method signature
     * @return The list of arguments
     * @throws InvalidCallDataException If the conversion fails
     */
    public List<Object> interpretData(String methodSignature) throws InvalidCallDataException {
        return interpretData(this.data, methodSignature);
    }

    /**
     * Interprets the call data using the method signature.
     * bool to Boolean
     * uint, int to BigInteger
     * address to TronAddress
     * bytes to byte[]
     *
     * @param callData        The call data
     * @param methodSignature The method signature
     * @return The list of arguments
     * @throws InvalidCallDataException If the conversion fails
     */
    public static List<Object> interpretData(byte[] callData, String methodSignature) throws InvalidCallDataException {
        try {
            byte[] selector = new byte[4];
            System.arraycopy(Hash.sha3(methodSignature.getBytes()), 0, selector, 0, 4);
            String[] types = AbiUtil.getTypes(methodSignature);

            if (!Arrays.equals(selector, Arrays.copyOfRange(callData, 0, selector.length))) {
                throw new InvalidCallDataException("The method does not match.");
            }

            return unpack(Arrays.asList(types), Arrays.copyOfRange(callData, selector.length, callData.length));
        } catch (Exception ex) {
            throw new InvalidCallDataException(ex.getMessage());
        }
    }

    /**
     * Unpacks data
     *
     * @param types The list of types preset in the data
     * @param data  The data
     * @return The list of values present in the data
     * @throws InvalidCallDataException If the data is invalid
     */
    public static List<Object> unpack(List<String> types, byte[] data) throws InvalidCallDataException {
        List<Object> arguments = new ArrayList<>();
        int offset = 0;
        for (String type : types) {
            switch (type) {
                case "address":
                    arguments.add(new TronAddress(Arrays.copyOfRange(data, offset, offset + 32)));
                    offset += 32;
                    break;
                case "string": {
                    int offsetDyn = new BigInteger(1, Arrays.copyOfRange(data, offset, offset + 32)).intValue();
                    offset += 32;
                    int length = new BigInteger(1, Arrays.copyOfRange(data, offsetDyn, offsetDyn + 32)).intValue();
                    offsetDyn += 32;
                    arguments.add(new String(Arrays.copyOfRange(data, offsetDyn, offsetDyn + length)));
                    break;
                }
                case "bool":
                    arguments.add(!(new BigInteger(1, Arrays.copyOfRange(data, offset, offset + 32)).equals(BigInteger.ZERO)));
                    offset += 32;
                    break;
                case "bytes": {
                    int offsetDyn = new BigInteger(1, Arrays.copyOfRange(data, offset, offset + 32)).intValue();
                    offset += 32;
                    int length = new BigInteger(1, Arrays.copyOfRange(data, offsetDyn, offsetDyn + 32)).intValue();
                    offsetDyn += 32;
                    arguments.add(Arrays.copyOfRange(data, offsetDyn, offsetDyn + length));
                    break;
                }
                default:
                    if (paramTypeBytes.matcher(type).find()) {
                        arguments.add(Arrays.copyOfRange(data, offset, offset + 32));
                        offset += 32;
                    } else if (paramTypeNumber.matcher(type).find()) {
                        arguments.add(new BigInteger(1, Arrays.copyOfRange(data, offset, offset + 32)));
                        offset += 32;
                    } else if (paramTypeNumberUnsigned.matcher(type).find()) {
                        arguments.add(new BigInteger(1, Arrays.copyOfRange(data, offset, offset + 32)));
                        offset += 32;
                    } else {
                        Matcher m = paramTypeArray.matcher(type);
                        if (m.find()) {
                            String arrayType = m.group(1);
                            int length = -1;
                            if (!m.group(2).equals("")) {
                                length = Integer.valueOf(m.group(2));
                            }
                            AbiUtil.CoderArray coder = new AbiUtil.CoderArray(arrayType, length);
                            int arrayOffset;
                            if (coder.dynamic) {
                                arrayOffset = new BigInteger(1, Arrays.copyOfRange(data, offset, offset + 32)).intValue();
                                offset += 32;
                                length = new BigInteger(1, Arrays.copyOfRange(data, arrayOffset, arrayOffset + 32)).intValue();
                                arrayOffset += 32;
                            } else {
                                arrayOffset = offset;
                            }
                            List<String> arrTypes = new ArrayList<>();
                            for (int j = 0; j < length; j++) {
                                arrTypes.add(arrayType);
                            }
                            arguments.add(unpack(arrTypes, Arrays.copyOfRange(data, arrayOffset, data.length)));
                            if (!coder.dynamic) {
                                offset += (32 * length);
                            }
                        } else {
                            throw new InvalidCallDataException("Unrecognized type: " + type);
                        }
                    }
            }
        }
        return arguments;
    }

    @Override
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    @Override
    public void print(String indent) {
        super.print(indent);
        System.out.println(indent + "Contract: " + this.getContractAddress().toString());
        System.out.println(indent + "Data: " + Hex.toHexString(this.getData()));
        System.out.println(indent + "Call value: " + String.format("%.0f TRX", this.getCallValue().getTRX()));
    }

    /**
     * Prints to stdout with interpretation.
     * @param indent
     * @param client
     */
    public void printAndInterpret(String indent, TronClient client) {
        super.print(indent);
        System.out.println(indent + "Contract: " + this.getContractAddress().toString());
        System.out.println(indent + "Data: " + Hex.toHexString(this.getData()));
        try {
            TronSmartContract contract = client.getContract(this.getContractAddress());

            String interpretation = contract.interpretCall(this.getData());
            if (interpretation != null) {
                System.out.println(indent + "Interpretation: " + interpretation);
            } else {
                System.out.println(indent + "Interpretation: (Not interpretable)");
            }
        } catch (Exception ex) {
            System.out.println("Interpretation: (Not interpretable)");
        }
        System.out.println(indent + "Call value: " + String.format("%.0f TRX", this.getCallValue().getTRX()));
    }
}
