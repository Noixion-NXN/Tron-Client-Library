package tv.noixion.troncli.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Tron virtual machine supported types.
 */
public class TVMTypes {
    /**
     * String.
     */
    public static final String STRING_TYPE = "string";

    /**
     * Boolean.
     */
    public static final String BOOLEAN_TYPE = "bool";

    /**
     * Tron address.
     */
    public static final String ADDRESS_TYPE = "address";

    /**
     * Dynamic bytes array.
     */
    public static final String DYN_BYTES_TYPE = "bytes";

    /**
     * Unsigned integer of 256 bits.
     */
    public static final String UINT_TYPE = "uint";

    /**
     * Signed integer of 256 bits.
     */
    public static final String INT_TYPE = "int";

    /**
     * Unsigned integer.
     *
     * @param bits Number of bits (8 - 256)
     * @return The name of the type.
     */
    public static String UINT(int bits) {
        return "uint" + bits;
    }

    /**
     * Signed integer.
     *
     * @param bits Number of bits (8 - 256)
     * @return The name of the type.
     */
    public static String INT(int bits) {
        return "int" + bits;
    }

    /**
     * Fixed bytes array.
     *
     * @param len Number of bytes.
     * @return The name of the type.
     */
    public static String BYTES(int len) {
        return "bytes" + len;
    }

    /**
     * Array.
     *
     * @param type The type of the array
     * @return The name of the type.
     */
    public static String ARRAY(String type) {
        return type + "[]";
    }

    /**
     * Array.
     *
     * @param type The type of the array.
     * @param len  The length of the array.
     * @return The name of the type.
     */
    public static String ARRAY(String type, int len) {
        return type + "[" + len + "]";
    }

    /**
     * Makes a list of types.
     *
     * @param arguments The list of types
     * @return The list of types in a List collection.
     */
    public static List<String> makeListOfTypes(String... arguments) {
        List<String> types = new ArrayList<>();
        for (String arg : arguments) {
            types.add(arg);
        }
        return types;
    }

    /**
     * Makes a method signature
     *
     * @param methodName The method name
     * @param types      the list of types of the arguments
     * @return
     */
    public static String signature(String methodName, String... types) {
        String sig = methodName + "(";
        boolean first = true;
        for (String type : types) {
            if (first) {
                first = false;
            } else {
                sig += ",";
            }
            sig += type;
        }
        sig += ")";
        return sig;
    }
}
