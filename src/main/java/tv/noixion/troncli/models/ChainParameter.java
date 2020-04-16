package tv.noixion.troncli.models;

import org.tron.protos.Protocol;

/**
 * Chain parameter
 */
public class ChainParameter {
    private int id;
    private String key;
    private long value;

    public ChainParameter(int id, Protocol.ChainParameters.ChainParameter p) {
        this.id = id;
        this.key = p.getKey();
        this.value = p.getValue();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
