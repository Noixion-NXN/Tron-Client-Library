package tv.noixion.troncli.models;

/**
 * Represents a conversion between an asset and TRX.
 */
public class TRXAssetConversion {
    private final int trxNum;
    private final int num;

    /**
     * Creates a new instance of TRXAssetConversion
     *
     * @param num    The number of assets
     * @param trxNum The number of TRX
     */
    public TRXAssetConversion(int num, int trxNum) {
        this.num = num;
        this.trxNum = trxNum;
    }

    /**
     * Converts TXT to assets.
     *
     * @param trx The number of TRX
     * @return The number of assets
     */
    public long trxToAsset(double trx) {
        return Math.round((trx * num) / trxNum);
    }

    /**
     * Converts assets to TRX
     *
     * @param assets The number of assets
     * @return The number of TRX
     */
    public double assetToTRX(long assets) {
        return (((double) assets) * trxNum) / num;
    }

    public int getTrxNum() {
        return trxNum;
    }

    public int getNum() {
        return num;
    }
}
