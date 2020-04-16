package tv.noixion.troncli.utils;

import tv.noixion.troncli.models.TronAddress;
import com.google.gson.*;
import org.spongycastle.util.encoders.Hex;
import org.tron.common.crypto.Hash;
import org.tron.common.utils.AbiUtil;
import org.tron.core.exception.EncodingException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Trigger contract call builder.
 */
public class TriggerContractDataBuilder {
    private final String signature;
    private final List<AbiUtil.Coder> coders;
    private final List<Object> values;

    /**
     * Creates a new instance of TriggerContractDataBuilder
     */
    public TriggerContractDataBuilder() {
        this.coders = new ArrayList<>();
        this.values = new ArrayList<>();
        this.signature = "";
    }

    /**
     * Creates a new instance of TriggerContractDataBuilder
     *
     * @param methodSignature The method signature
     */
    public TriggerContractDataBuilder(String methodSignature) {
        this.coders = new ArrayList<>();
        this.values = new ArrayList<>();
        this.signature = methodSignature;
    }

    /**
     * Adds new address parameter.
     *
     * @param value The value of the parameter.
     * @return This object.
     */
    public TriggerContractDataBuilder paramAddress(TronAddress value) {
        this.coders.add(new AbiUtil.CoderAddress());
        this.values.add(value.toBase58());
        return this;
    }

    /**
     * Adds new boolean parameter.
     *
     * @param value The value of the parameter.
     * @return This object.
     */
    public TriggerContractDataBuilder paramBool(boolean value) {
        this.coders.add(new AbiUtil.CoderBool());
        this.values.add(value ? "1" : "0");
        return this;
    }

    /**
     * Adds new unsigned integer parameter.
     *
     * @param value The value of the parameter.
     * @return This object.
     */
    public TriggerContractDataBuilder paramUInt(BigInteger value) {
        this.coders.add(new AbiUtil.CoderNumber());
        this.values.add(value.abs().toString());
        return this;
    }

    /**
     * Adds new integer parameter.
     *
     * @param value The value of the parameter.
     * @return This object.
     */
    public TriggerContractDataBuilder paramInt(BigInteger value) {
        this.coders.add(new AbiUtil.CoderNumber());
        this.values.add(value.toString());
        return this;
    }

    /**
     * Adds new string parameter.
     *
     * @param value The value of the parameter.
     * @return This object.
     */
    public TriggerContractDataBuilder paramString(String value) {
        this.coders.add(new AbiUtil.CoderString());
        this.values.add(value);
        return this;
    }

    /**
     * Adds new fixed bytes parameter (1 to 32 bytes).
     *
     * @param value The value of the parameter.
     * @return This object.
     */
    public TriggerContractDataBuilder paramFixedBytes(byte[] value) {
        this.coders.add(new AbiUtil.CoderFixedBytes());
        this.values.add(Hex.toHexString(value));
        return this;
    }

    /**
     * Adds new dynamic bytes array parameter.
     *
     * @param value The value of the parameter.
     * @return This object.
     */
    public TriggerContractDataBuilder paramBytes(byte[] value) {
        this.coders.add(new AbiUtil.CoderDynamicBytes());
        this.values.add(Hex.toHexString(value));
        return this;
    }

    /**
     * Adds new boolean array parameter.
     *
     * @param array   The value of the parameter.
     * @param dynamic true if the array is dynamic sized
     * @return This object.
     */
    public TriggerContractDataBuilder paramArrayBool(boolean[] array, boolean dynamic) {
        this.coders.add(new AbiUtil.CoderArray("bool", dynamic ? -1 : array.length));
        this.values.add(TronUtils.asStringsList(array));
        return this;
    }

    /**
     * Adds new string array parameter.
     *
     * @param array   The value of the parameter.
     * @param dynamic true if the array is dynamic sized
     * @return This object.
     */
    public TriggerContractDataBuilder paramArrayString(String[] array, boolean dynamic) {
        this.coders.add(new AbiUtil.CoderArray("string", dynamic ? -1 : array.length));
        this.values.add(TronUtils.asStringsList(array));
        return this;
    }

    /**
     * Adds new integer array parameter.
     *
     * @param array   The value of the parameter.
     * @param dynamic true if the array is dynamic sized
     * @return This object.
     */
    public TriggerContractDataBuilder paramArrayInt(BigInteger[] array, boolean dynamic) {
        this.coders.add(new AbiUtil.CoderArray("int", dynamic ? -1 : array.length));
        this.values.add(TronUtils.asStringsList(array));
        return this;
    }

    /**
     * Adds new unsigned integer array parameter.
     *
     * @param array   The value of the parameter.
     * @param dynamic true if the array is dynamic sized
     * @return This object.
     */
    public TriggerContractDataBuilder paramArrayUInt(BigInteger[] array, boolean dynamic) {
        this.coders.add(new AbiUtil.CoderArray("uint", dynamic ? -1 : array.length));
        this.values.add(TronUtils.asStringsList(array));
        return this;
    }

    /**
     * Sets all parameters to the trigger contract call
     *
     * @param arguments the arguments
     * @return This object.
     */
    public TriggerContractDataBuilder params(Object... arguments) {
        String[] types = AbiUtil.getTypes(this.signature);
        if (types.length != arguments.length) {
            throw new IllegalArgumentException("Invalid parameters length. Expected " + types.length + " and found " + arguments.length);
        }
        int i = 0;
        for (String type : types) {
            AbiUtil.Coder coder = AbiUtil.getParamCoder(type);
            if (coder == null) {
                throw new IllegalArgumentException("Unrecognized type: " + type);
            }
            this.coders.add(coder);
            Object value = arguments[i++];
            if (value instanceof TronAddress) {
                this.values.add(value.toString());
            } else if (value instanceof String) {
                this.values.add(value.toString());
            } else if (value instanceof BigInteger) {
                this.values.add(value.toString());
            } else if (value instanceof Short || value instanceof Long || value instanceof Integer) {
                this.values.add(value.toString());
            } else if (value instanceof Boolean) {
                this.values.add((Boolean) value ? "1" : "0");
            } else if(value instanceof byte[]) {
                this.values.add(Hex.toHexString((byte[]) value));
            } else if (value.getClass().isArray()) {
                this.values.add(TronUtils.asStringsList(value));
            } else {
                throw new IllegalArgumentException("Unsupported parameter: " + value.toString());
            }
        }
        return this;
    }

    /**
     * Sets all parameters to the trigger contract call
     *
     * @param arguments the arguments
     * @return This object.
     */
    public TriggerContractDataBuilder paramsList(List<String> arguments) {
        String[] types = AbiUtil.getTypes(this.signature);
        if (types.length != arguments.size()) {
            throw new IllegalArgumentException("Invalid parameters length. Expected " + types.length + " and found "
                    + arguments.size());
        }
        int i = 0;
        for (String type : types) {
            AbiUtil.Coder coder = AbiUtil.getParamCoder(type);
            if (coder == null) {
                throw new IllegalArgumentException("Unrecognized type: " + type);
            }
            this.coders.add(coder);

            if (coder instanceof AbiUtil.CoderArray) {
                JsonParser parser = new JsonParser();
                JsonArray array = (JsonArray) parser.parse(arguments.get(i));
                List<String> arrayAsList = new ArrayList<>();
                for (JsonElement e : array) {
                    arrayAsList.add(e.getAsString());
                }
                this.values.add(arrayAsList);
            } else {
                this.values.add(arguments.get(i));
            }
            i++;
        }
        return this;
    }

    /**
     * Builds the trigger call.
     *
     * @return The trigger call data.
     * @throws EncodingException If the parameters are wrong specified.
     */
    public byte[] build() throws EncodingException {
        byte[] selector = new byte[4];
        System.arraycopy(Hash.sha3(signature.getBytes()), 0, selector, 0, 4);
        return Hex.decode(Hex.toHexString(selector) + Hex.toHexString(AbiUtil.pack(coders, values)));
    }

    /**
     * Builds as constructor paramsList.
     *
     * @return The constructor paramsList data.
     * @throws EncodingException If the parameters are wrong specified.
     */
    public byte[] buildConstructorParams() throws EncodingException {
        return AbiUtil.pack(coders, values);
    }

    /**
     * Gets the method name from a call.
     *
     * @param call The call.
     * @return the method name.
     */
    public static String getMethodFromCall(String call) {
        int i = call.indexOf("(");
        if (i == 0) {
            return "";
        }
        if (i < 1) {
            throw new IllegalArgumentException("Invalid call.");
        }
        return call.substring(0, i).trim();
    }

    /**
     * Gets the parameters form a call.
     *
     * @param call The call.
     * @return The parameters.
     */
    public static List<String> getparamsFromCall(String call) {
        int i = call.indexOf("(");
        if (i < 0) {
            throw new IllegalArgumentException("Invalid call.");
        }
        if (call.length() <= 2) {
            return new ArrayList<>();
        }
        call = "[" + call.substring(i + 1, call.length() - 1) + "]";
        JsonArray array = (JsonArray) (new JsonParser().parse(call));
        List<String> result = new ArrayList<>();
        for (JsonElement elem : array) {
            if (elem.isJsonArray()) {
                Gson gson = new GsonBuilder().create();
                result.add(gson.toJson(elem));
            } else {
                result.add(elem.getAsString());
            }
        }
        return result;
    }
}
