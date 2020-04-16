package tv.noixion.troncli.models;

import org.spongycastle.util.encoders.Hex;

/**
 * Represents a org.tron private key.
 */
public class TronPrivateKey {
    private final byte[] bytes;

    /**
     * Creates an instance of TronPrivateKey
     *
     * @param bytes the private key as an array of bytes
     */
    public TronPrivateKey(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Creates an instance of TronPrivateKey
     *
     * @param hex the private key as hex string
     */
    public TronPrivateKey(String hex) {
        this.bytes = Hex.decode(hex);
    }

    /**
     * @return The private key as hex string.
     */
    public String toHex() {
        return Hex.toHexString(this.bytes).toUpperCase();
    }

    /**
     * @return the private key as an array of bytes
     */
    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public String toString() {
        return this.toHex();
    }
}
