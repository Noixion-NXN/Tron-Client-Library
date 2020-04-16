package tv.noixion.troncli.utils;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.spongycastle.util.encoders.Hex;
import org.tron.common.crypto.Hash;
import org.tron.common.crypto.Sha256Hash;
import org.tron.common.utils.AbiUtil;
import org.tron.common.utils.Base58;
import org.tron.protos.Protocol;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TronUtils {
    public static final byte PREFIX_BYTE = (byte) 0x41;
    public static final int ADDRESS_SIZE = 21;


    /**
     * Decodes a base-58 address.
     *
     * @param addressBase58 The base-58 string.
     * @return The address as byte array.
     */
    public static byte[] decodeFromBase58(String addressBase58) {
        if (StringUtils.isEmpty(addressBase58)) {
            return null;
        }
        byte[] address = AbiUtil.decodeFromBase58Check(addressBase58);
        if (!addressValid(address)) {
            return null;
        }
        return address;
    }

    /**
     * Encodes to base-58 (with check code).
     *
     * @param input The byte array to encode.
     * @return The base-58 string.
     */
    public static String encodeToBase58Check(byte[] input) {
        byte[] hash0 = Sha256Hash.hash(input);
        byte[] hash1 = Sha256Hash.hash(hash0);
        byte[] inputCheck = new byte[input.length + 4];
        System.arraycopy(input, 0, inputCheck, 0, input.length);
        System.arraycopy(hash1, 0, inputCheck, input.length, 4);
        return Base58.encode(inputCheck);
    }

    /**
     * Validates an address.
     *
     * @param address The address.
     * @return true if the address is valid, false if is not a valid address.
     */
    public static boolean validateAddress(String address) {
        try {
            return (decodeFromBase58(address) != null);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Turns a double value into an integer.
     *
     * @param value    The double value.
     * @param decimals The number of decimals
     * @return The integer value.
     */
    public static BigInteger doubleToInt(double value, int decimals) {
        double amountInt = value * Math.pow(10, decimals);
        return BigInteger.valueOf(Math.round(amountInt));
    }

    /**
     * Turns an integer value into double with decimals.
     *
     * @param value    The integer value.
     * @param decimals The number of decimals.
     * @return the double value.
     */
    public static double intToDouble(BigInteger value, int decimals) {
        return value.doubleValue() / Math.pow(10, decimals);
    }

    /**
     * Calculates the hash of an input.
     *
     * @param input input data
     * @return Result hash
     */
    public static byte[] keccak256(byte[] input) {
        return Hash.sha3(input);
    }

    /**
     * Gets a value as string.
     *
     * @param value The value.
     * @return the value as string.
     */
    public static String valueToString(Object value) {
        if (value instanceof byte[]) {
            return Hex.toHexString((byte[]) value);
        } else if (value instanceof Object[]) {
            return Arrays.toString((Object[]) value);
        } else {
            return value.toString();
        }
    }

    /**
     * Turns an array into a string list.
     * @param array The array.
     * @return The strings list.
     */
    public static List<String> asStringsList(Object array) {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < Array.getLength(array); i++) {
            result.add(Array.get(array, i).toString());
        }

        return result;
    }

    public static String minimizeJson(String in) {
        String result = "";

        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);

            if (c == '"') {
                result += "\\\"";
            } else if (c == '\n' || c == ' ') {
                continue;
            } else if (c == '\\') {
                result += "\\\\";
            } else {
                result += c;
            }
        }

        return result;
    }

    public static String abi2Json(Protocol.SmartContract.ABI abi) {
        try {
            return com.google.protobuf.util.JsonFormat.printer().print(abi);
        } catch (InvalidProtocolBufferException e) {
            return "";
        }
    }

    public static Protocol.SmartContract.ABI jsonToABI(String json) throws InvalidProtocolBufferException {
        Protocol.SmartContract.ABI.Builder abiBuilder = Protocol.SmartContract.ABI.newBuilder();
        com.google.protobuf.util.JsonFormat.parser().merge(json, abiBuilder);
        return abiBuilder.build();
    }

    /* Private */

    private static boolean addressValid(byte[] address) {
        if (ArrayUtils.isEmpty(address)) {
            return false;
        }
        if (address.length != ADDRESS_SIZE) {
            return false;
        }
        byte preFixbyte = address[0];
        if (preFixbyte != PREFIX_BYTE) {
            return false;
        }
        return true;
    }
}
