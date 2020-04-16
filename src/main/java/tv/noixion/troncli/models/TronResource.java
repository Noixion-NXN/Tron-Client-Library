package tv.noixion.troncli.models;

/**
 * Represents a Tron resource (bandwidth or energy)
 */
public enum TronResource {
    ENERGY,
    BANDWIDTH,
    UNKNOWN;

    /**
     * @return The resource code.
     */
    public int getCode() {
        switch (this) {
            case ENERGY:
                return 1;
            case BANDWIDTH:
                return 0;
            default:
                return -1;
        }
    }
}
