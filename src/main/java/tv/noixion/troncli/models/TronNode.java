package tv.noixion.troncli.models;

/**
 * Represents a org.tron remote node.
 */
public class TronNode {
    private final String hostname;

    /**
     * Creates an instance of TronNode
     *
     * @param hostname The node hostname
     */
    public TronNode(String hostname) {
        this.hostname = hostname;
    }

    /**
     * Creates an instance of TronNode
     *
     * @param host The node host
     * @param port The node grpc port
     */
    public TronNode(String host, int port) {
        this.hostname = host + ":" + port;
    }

    /**
     * @return The node hostname (host:port)
     */
    public String getHostname() {
        return hostname;
    }
}
