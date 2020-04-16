package tv.noixion.troncli.models;

import org.spongycastle.util.encoders.Hex;

/**
 * Represents hash pointer.
 */
public class HashIdentifier {
    private final byte[] bytes;

    /**
     * Creates an instance of HashIdentifier
     *
     * @param bytes the hash as an array of bytes.
     */
    public HashIdentifier(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Creates an instance of HashIdentifier
     *
     * @param hex the hash as hex string
     */
    public HashIdentifier(String hex) {
        this.bytes = Hex.decode(hex);
    }

    /**
     * @return The hash as hex string.
     */
    public String toHex() {
        return Hex.toHexString(this.bytes).toLowerCase();
    }

    /**
     * @return the hash as an array of bytes
     */
    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public String toString() {
        return this.toHex();
    }
}
