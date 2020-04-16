package tv.noixion.troncli.exceptions;

import org.tron.api.GrpcAPI;

public class TransactionException extends Exception {
    private GrpcAPI.Return.response_code code;

    public TransactionException(GrpcAPI.Return.response_code code, String message) {
        super(message);
        this.code = code;
    }

    public GrpcAPI.Return.response_code getCode() {
        return code;
    }
}
