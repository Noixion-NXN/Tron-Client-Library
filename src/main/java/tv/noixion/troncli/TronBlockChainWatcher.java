package tv.noixion.troncli;

import tv.noixion.troncli.models.TronBlock;
import tv.noixion.troncli.models.TronTransaction;
import tv.noixion.troncli.models.TronTransactionInformation;
import tv.noixion.troncli.utils.TronEventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a watcher of the Tron blockchain.
 */
public class TronBlockChainWatcher extends Thread {
    public static final int TRON_BLOCK_INTERVAL = 3 * 1000; // 3 seconds

    private final TronClient client;
    private final long startBlock;
    private final List<TronEventHandler> handlers;
    private long nextBlock;
    private boolean stop;
    private boolean onlyBlocks;

    public TronBlockChainWatcher(TronClient client, long startBlock) {
        this(client, startBlock, false);
    }

    public TronBlockChainWatcher(TronClient client, long startBlock, boolean onlyBlocks) {
        this.client = client;
        this.startBlock = startBlock;
        this.stop = false;
        this.handlers = new ArrayList<>();
        this.onlyBlocks = onlyBlocks;
    }

    /**
     * Adds anew event handler.
     *
     * @param handler The event handler.
     */
    public void addHandler(TronEventHandler handler) {
        this.handlers.add(handler);
    }

    /**
     * Removes an event handler.
     *
     * @param handler The event handler.
     */
    public void removeHandler(TronEventHandler handler) {
        this.handlers.remove(handler);
    }

    /**
     * Starts the watching process.
     */
    public void startWatching() {
        this.start();
    }

    /**
     * Stop the watching process.
     */
    public void stopWatching() {
        stop = true;
    }

    @Override
    public void run() {
        nextBlock = startBlock;
        while (!stop) {
            if (!getNextBlock()) {
                try {
                    Thread.sleep(TRON_BLOCK_INTERVAL);
                } catch (InterruptedException ex) {
                    return;
                }
            }
        }
    }

    private boolean getNextBlock() {
        TronBlock block;

        try {
            block = client.getBlock(nextBlock);
        } catch (Exception ex) {
            return false;
        }

        if (block.getNumber() == nextBlock) {
            for (TronEventHandler handler : this.handlers) {
                try {
                    handler.handleBlock(this.client, block);
                } catch (Exception ex) {
                }
            }
            if (!onlyBlocks) {
                for (TronTransaction tx : block.getTransactions()) {
                    TronTransactionInformation info;

                    try {
                        info = client.getTransactionInformation(tx.getId());
                    } catch (Exception ex) {
                        return false;
                    }

                    for (TronEventHandler handler : this.handlers) {
                        try {
                            handler.handleTransaction(this.client, tx, info);
                        } catch (Exception ex) {
                        }
                    }
                }
            }
            nextBlock++;
            return true;
        } else {
            return false;
        }
    }
}
