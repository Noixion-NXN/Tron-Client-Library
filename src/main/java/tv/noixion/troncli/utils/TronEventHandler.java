package tv.noixion.troncli.utils;

import tv.noixion.troncli.TronClient;
import tv.noixion.troncli.models.TronBlock;
import tv.noixion.troncli.models.TronTransaction;
import tv.noixion.troncli.models.TronTransactionInformation;

/**
 * Represents an event handler of the org.tron network.
 */
public interface TronEventHandler {
    /**
     * Handles a block.
     *
     * @param client The client that got the block.
     * @param block  The block.
     */
    public void handleBlock(TronClient client, TronBlock block);

    /**
     * Handles a transaction.
     *
     * @param client The client that received the transaction.
     * @param tx     The transaction.
     * @param info   The transaction information.
     */
    public void handleTransaction(TronClient client, TronTransaction tx, TronTransactionInformation info);
}
