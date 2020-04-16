package tv.noixion.troncli.models;

import org.tron.common.crypto.ECKey;
import org.tron.core.exception.CipherException;
import org.tron.keystore.StringUtils;
import org.tron.keystore.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * Represents a wallet.
 */
public class TronWallet {
    private final ECKey key;

    /**
     * Creates a new instance of TronWallet
     */
    public TronWallet() {
        key = new ECKey(new SecureRandom());
    }

    /**
     * Creates a new instance of TronWallet from a private key
     *
     * @param privKey The private key
     */
    public TronWallet(TronPrivateKey privKey) {
        key = ECKey.fromPrivate(privKey.getBytes());
    }

    /**
     * Creates a new instance of TronWallet from a file
     *
     * @param file     The file
     * @param password The password
     * @throws IOException     If the file read fails
     * @throws CipherException If the password is wrong
     */
    public TronWallet(File file, String password) throws IOException, CipherException {
        key = WalletUtils.loadCredentials(StringUtils.char2Byte(password.toCharArray()), file).getEcKeyPair();
    }

    /**
     * @return The key.
     */
    public ECKey getKey() {
        return key;
    }

    /**
     * @return The private key
     */
    public TronPrivateKey getPrivateKey() {
        return new TronPrivateKey(key.getPrivKeyBytes());
    }

    /**
     * @return The address for this wallet in the org.tron network.
     */
    public TronAddress getAddress() {
        return new TronAddress(key.getAddress());
    }

    /**
     * Saves a wallet to a file. Creates a new wallet file.
     *
     * @param dest     The destination directory
     * @param password The password for the wallet.
     * @return The filename
     * @throws IOException     If the file cannot be written.
     * @throws CipherException If the password is invalid.
     */
    public String saveToFile(File dest, String password) throws IOException, CipherException {
        return WalletUtils.generateWalletFile(StringUtils.char2Byte(password.toCharArray()), this.key, dest, true);
    }

    /**
     * Writes the wallet to a file.
     *
     * @param file     The file to write the wallet as JSON.
     * @param password The password for the wallet.
     * @throws CipherException If the password is invalid.
     * @throws IOException     If the file cannot be written.
     */
    public void writeToFile(File file, String password) throws CipherException, IOException {
        WalletUtils.updateWalletFile(StringUtils.char2Byte(password.toCharArray()), this.key, file, true);
    }

    /**
     * Obtains the wallet as JSON.
     *
     * @param password The password for the wallet.
     * @return The wallet as JSON.
     * @throws CipherException If the password is invalid.
     * @throws IOException     If the wallet cannot be serialized.
     */
    public String getWalletJson(String password) throws CipherException, IOException {
        return WalletUtils.generateWalletString(StringUtils.char2Byte(password.toCharArray()), this.key, true);
    }
}
