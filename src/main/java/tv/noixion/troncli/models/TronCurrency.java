package tv.noixion.troncli.models;

/**
 * Represents a org.tron Balance (TRX)
 */
public class TronCurrency {
    /**
     * Number of decimals for the TRX currency.
     */
    public static final int DECIMALS = 6;

    /**
     * Value zero (0 TRX, 0 SUN)
     */
    public static final TronCurrency ZERO = TronCurrency.sun(0L);

    /**
     * Max limit for transactions (1000 TRX)
     */
    public static final TronCurrency MAX_FEE_LIMIT = TronCurrency.trx(1000);

    private long value;

    /**
     * Creates a new instance of TronCurrency
     *
     * @param value The value (SUN)
     */
    public TronCurrency(long value) {
        this.value = value;
    }

    /**
     * Creates a new instance of TronCurrency
     *
     * @param value The value (TRX)
     */
    public static TronCurrency trx(double value) {
        return new TronCurrency(new Double(Math.round(value * Math.pow(10, DECIMALS))).longValue());
    }

    /**
     * Creates a new instance of TronCurrency
     *
     * @param value The value (SUN)
     */
    public static TronCurrency sun(long value) {
        return new TronCurrency(value);
    }

    /**
     * @return The value as SUN
     */
    public long getSUN() {
        return this.value;
    }

    /**
     * @return The value as TRX
     */
    public double getTRX() {
        return ((double) this.value) / Math.pow(10, DECIMALS);
    }
}
