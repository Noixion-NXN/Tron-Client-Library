package tv.noixion.troncli.utils;

import tv.noixion.troncli.exceptions.TransactionException;
import tv.noixion.troncli.grpc.GrpcClient;
import tv.noixion.troncli.models.TronPrivateKey;
import tv.noixion.troncli.models.TronTransaction;
import org.tron.api.GrpcAPI;
import org.tron.common.crypto.ECKey;
import org.tron.protos.Protocol;


/**
 * Utils for transactions.
 */
public class TransactionUtils {
    /**
     * Processes and broadcast a transaction.
     *
     * @param client               The GRPC client.
     * @param transactionExtention The transaction extension.
     * @param privateKey           The private key to sign the transaction.
     * @return The transaction.
     * @throws TransactionException If the transaction fails.
     */
    public static TronTransaction processTransactionExtention(GrpcClient client, GrpcAPI.TransactionExtention transactionExtention, TronPrivateKey privateKey)
            throws TransactionException {
        if (transactionExtention == null) {
            throw new IllegalArgumentException("The transaction is null.");
        }
        GrpcAPI.Return ret = transactionExtention.getResult();
        if (!ret.getResult()) {
            throw new TransactionException(ret.getCode(), ret.getMessage().toStringUtf8());
        }
        Protocol.Transaction transaction = transactionExtention.getTransaction();
        if (transaction == null || transaction.getRawData().getContractCount() == 0) {
            throw new TransactionException(null, "Transaction is empty");
        }
        transaction = signTransaction(transaction, privateKey);
        return new TronTransaction(client.broadcastTransaction(transaction));
    }

    /**
     * Processes and broadcast a transaction.
     *
     * @param client      The GRPC client.
     * @param transaction The transaction.
     * @param privateKey  The private key to sign the transaction.
     * @return The transaction.
     * @throws TransactionException If the transaction fails.
     */
    public static TronTransaction processTransaction(GrpcClient client, Protocol.Transaction transaction, TronPrivateKey privateKey)
            throws TransactionException {
        if (transaction == null || transaction.getRawData().getContractCount() == 0) {
            throw new TransactionException(null, "Transaction is empty");
        }
        transaction = signTransaction(transaction, privateKey);
        return new TronTransaction(client.broadcastTransaction(transaction));
    }

    /**
     * Signs a transaction.
     *
     * @param transaction The transaction
     * @param privateKey  The private key.
     * @return The signed transaction.
     */
    public static Protocol.Transaction signTransaction(Protocol.Transaction transaction, TronPrivateKey privateKey) {
        if (transaction.getRawData().getTimestamp() == 0) {
            transaction = org.tron.common.utils.TransactionUtils.setTimestamp(transaction);
        }
        return org.tron.common.utils.TransactionUtils.sign(transaction, ECKey.fromPrivate(privateKey.getBytes()));
    }
}
