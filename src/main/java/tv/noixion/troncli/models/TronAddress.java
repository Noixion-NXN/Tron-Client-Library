package tv.noixion.troncli.models;

import tv.noixion.troncli.utils.TronUtils;
import org.spongycastle.util.encoders.Hex;

import java.util.Arrays;

/**
 * Represents a org.tron address.
 */
public class TronAddress implements Comparable {
    public static final String HEX_PREFIX = "41";
    public static final int HEX_ADDRESS_LENGTH = 42;

    private byte[] bytes;

    /**
     * Creates a new instance of TronAddress.
     *
     * @param bytes The address as an array of bytes.
     */
    public TronAddress(byte[] bytes) {
        this.bytes = bytes;
        String hex = removeLeftZeroes(Hex.toHexString(this.bytes));
        if (hex.length() < HEX_ADDRESS_LENGTH) {
            while (hex.length() < HEX_ADDRESS_LENGTH - 2) {
                hex = "0" + hex;
            }
            hex = HEX_PREFIX + hex;

        }
        this.bytes = Hex.decode(hex);
    }

    /**
     * Creates a new instance of TronAddress
     *
     * @param address The address as a base-58 string.
     */
    public TronAddress(String address) {
        this(TronUtils.decodeFromBase58(address));
    }

    /**
     * @return The address as Base-58 string.
     */
    public String toBase58() {
        return TronUtils.encodeToBase58Check(this.bytes);
    }

    /**
     * @return The address as an array of bytes
     */
    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public String toString() {
        return this.toBase58();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof TronAddress && Arrays.equals(this.bytes, ((TronAddress) o).getBytes()));
    }

    @Override
    public int compareTo(Object o) {
        return this.toString().compareTo(o.toString());
    }

    private String removeLeftZeroes(String input) {
        String result = "";
        boolean firstNonZero = false;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != '0') {
                firstNonZero = true;
            }
            if (firstNonZero) {
                result += c;
            }
        }
        return result;
    }
}
