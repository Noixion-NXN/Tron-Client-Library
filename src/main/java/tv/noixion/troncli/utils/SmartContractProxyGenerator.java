package tv.noixion.troncli.utils;

import tv.noixion.troncli.models.TronSmartContract;
import org.tron.protos.Protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Code generator for Smart contract proxies.
 */
public class SmartContractProxyGenerator {
    private static Pattern paramTypeArray = Pattern.compile("^(.*)\\[([0-9]*)\\]$");

    /**
     * Generates a smart contract proxy.
     *
     * @param contractName The contract name
     * @param contract     the contract.
     * @return The generated JAVA class.
     */
    public static String generateProxyForSmartContract(String pkg, String contractName, TronSmartContract contract) {
        Protocol.SmartContract.ABI abi = contract.getAbi();

        String result = "";

        /* Package */

        if(!pkg.equals("")) {
            result += "package " + pkg + ";\n\n";
        }

        /* Imports */

        result += "import tv.noixion.troncli.TronClient;\n" +
                "import tv.noixion.troncli.TronContractProxy;\n" +
                "import tv.noixion.troncli.exceptions.GRPCException;\n" +
                "import tv.noixion.troncli.exceptions.InvalidCallDataException;\n" +
                "import tv.noixion.troncli.exceptions.TransactionException;\n" +
                "import tv.noixion.troncli.utils.TriggerContractDataBuilder;\n" +
                "import tv.noixion.troncli.utils.TronUtils;\n" +
                "import org.tron.core.exception.EncodingException;\n" +
                "\n" +
                "import java.util.Map;" + "\n\n";

        /* Class start */

        result += "public class " + contractName + "Proxy extends TronContractProxy {" + "\n\n";

        //result += "private static final String CONTRACT_ABI_JSON = \"" + TronUtils.minimizeJson(TronUtils.abi2Json(contract.getAbi())) + "\";" + "\n\n";

        /* Default random wallet */

        result += "    // This wallet is used for view and pure methods,\n" +
                "    // is just a random wallet used to put an address in the call message.\n" +
                "    private final TronWallet viewWallet;\n\n";

        /* Constructor */

        result += "    public " + contractName + "Proxy (TronClient client, TronAddress contractAddress) throws GRPCException {\n" +
                "        super(client, contractAddress);\n" +
                "        this.viewWallet = new TronWallet();\n" +
                "    }\n\n";


        /* Pure methods */
        for (Protocol.SmartContract.ABI.Entry entry : abi.getEntrysList()) {
            if (entry.getType() == Protocol.SmartContract.ABI.Entry.EntryType.Function
                    && entry.getStateMutability() == Protocol.SmartContract.ABI.Entry.StateMutabilityType.Pure) {
                result += generateMethod(entry) + "\n\n";
            }
        }

        /* View methods */
        for (Protocol.SmartContract.ABI.Entry entry : abi.getEntrysList()) {
            if (entry.getType() == Protocol.SmartContract.ABI.Entry.EntryType.Function
                    && entry.getStateMutability() == Protocol.SmartContract.ABI.Entry.StateMutabilityType.View) {
                result += generateMethod(entry) + "\n\n";
            }
        }

        /* NonPayable methods */
        for (Protocol.SmartContract.ABI.Entry entry : abi.getEntrysList()) {
            if (entry.getType() == Protocol.SmartContract.ABI.Entry.EntryType.Function
                    && entry.getStateMutability() == Protocol.SmartContract.ABI.Entry.StateMutabilityType.Nonpayable) {
                result += generateMethod(entry) + "\n\n";
            }
        }

        /* payable methods */
        for (Protocol.SmartContract.ABI.Entry entry : abi.getEntrysList()) {
            if (entry.getType() == Protocol.SmartContract.ABI.Entry.EntryType.Function
                    && entry.getStateMutability() == Protocol.SmartContract.ABI.Entry.StateMutabilityType.Payable) {
                result += generateMethod(entry) + "\n\n";
            }
        }

        /* Events */

        result += "    @Override\n";
        result += "    public void handleEvent(String eventName, String eventSignature, Map<String, String> types," +
                "Map<String, Object> values) {\n";
        result += "        // Handle events\n";
        for (Protocol.SmartContract.ABI.Entry entry : abi.getEntrysList()) {
            if (entry.getType() == Protocol.SmartContract.ABI.Entry.EntryType.Event) {
                result += "        // Event: " + entry.getName() + "(";
                boolean f = true;
                for (Protocol.SmartContract.ABI.Entry.Param p : entry.getInputsList()) {
                    if (f) {
                        f = false;
                    } else {
                        result += ", ";
                    }
                    result += p.getType() + " " + p.getName();
                }
                result += ")\n";
            }
        }
        result += "    }\n\n";

        result += "    @Override\n";
        result += "    public void handleNotInterpretableEvent(TronTransactionInformation.Log event) {\n";
        result += "        // Handle unknown events\n";
        result += "    }\n\n";

        /* Class end */

        result += "}\n";

        return result;
    }

    private static String generateMethod(Protocol.SmartContract.ABI.Entry entry) {
        String result = "";

        String returnType = "byte[]";
        String returnMethod = "getReturnedData()";

        if (entry.getStateMutability() == Protocol.SmartContract.ABI.Entry.StateMutabilityType.Payable
                || entry.getStateMutability() == Protocol.SmartContract.ABI.Entry.StateMutabilityType.Nonpayable) {
            returnType = "TronTransaction";
            returnMethod = "getTransaction()";
        } else if (entry.getOutputsCount() > 1) {
            returnType = "List<Object>";
            returnMethod = "interpretReturnedData(this.getContract().getResultTypeForMethod(\"" + entry.getName() + "\"))";
        } else if (entry.getOutputsCount() == 1) {
            switch (entry.getOutputs(0).getType()) {
                case "string":
                    returnType = "String";
                    returnMethod = "getResultAsString()";
                    break;
                case "string[]":
                    returnType = "String[]";
                    returnMethod = "getResultAsArrayString()";
                    break;
                case "address":
                    returnType = "TronAddress";
                    returnMethod = "getResultAsAddress()";
                    break;
                case "trcToken":
                    returnType = "BigInteger";
                    returnMethod = "getResultAsInt()";
                    break;
                case "trcToken[]":
                    returnType = "BigInteger[]";
                    returnMethod = "getResultAsArrayUInt()";
                    break;
                case "address[]":
                    returnType = "TronAddress";
                    returnMethod = "getResultAsArrayAddress()";
                    break;
                case "bool":
                    returnType = "boolean";
                    returnMethod = "getResultAsBool()";
                    break;
                case "bool[]":
                    returnType = "boolean";
                    returnMethod = "getResultAsArrayBool()";
                    break;
                case "bytes":
                    returnType = "byte[]";
                    returnMethod = "getResultAsBytes()";
                    break;
                default:
                    if (entry.getOutputs(0).getType().startsWith("int")) {
                        if (entry.getOutputs(0).getType().endsWith("[]")) {
                            returnType = "BigInteger[]";
                            returnMethod = "getResultAsArrayInt()";
                        } else {
                            returnType = "BigInteger";
                            returnMethod = "getResultAsInt()";
                        }
                    } else if (entry.getOutputs(0).getType().startsWith("uint")) {
                        if (entry.getOutputs(0).getType().endsWith("[]")) {
                            returnType = "BigInteger[]";
                            returnMethod = "getResultAsArrayUInt()";
                        } else {
                            returnType = "BigInteger";
                            returnMethod = "getResultAsInt()";
                        }
                    } else if (entry.getOutputs(0).getType().startsWith("bytes")) {
                        returnType = "byte[]";
                        returnMethod = "getResultAsFixedBytes(32)";
                    } else {
                        returnType = "List<Object>";
                        returnMethod = "interpretReturnedData(this.getContract().getResultTypeForMethod(\"" + entry.getName() + "\"))";
                    }
            }
        }

        List<String> paramsNames = new ArrayList<>();
        List<String> paramsTypes = new ArrayList<>();

        int paramNum = 0;
        for (Protocol.SmartContract.ABI.Entry.Param input : entry.getInputsList()) {
            paramNum++;
            if (input.getName().length() > 0) {
                paramsNames.add(input.getName());
            } else {
                paramsNames.add("parameter" + paramNum);
            }
            switch (input.getType()) {
                case "string":
                    paramsTypes.add("String");
                    break;
                case "string[]":
                    paramsTypes.add("String[]");
                    break;
                case "address":
                    paramsTypes.add("TronAddress");
                    break;
                case "address[]":
                    paramsTypes.add("TronAddress[]");
                    break;
                case "trcToken":
                    paramsTypes.add("BigInteger");
                    break;
                case "trcToken[]":
                    paramsTypes.add("BigInteger[]");
                    break;
                case "bool":
                    paramsTypes.add("boolean");
                    break;
                case "bool[]":
                    paramsTypes.add("boolean[]");
                    break;
                case "bytes":
                    paramsTypes.add("byte[]");
                    break;
                default:
                    if (input.getType().startsWith("int")) {
                        if (input.getType().endsWith("[]")) {
                            paramsTypes.add("BigInteger[]");
                        } else {
                            paramsTypes.add("BigInteger");
                        }
                    } else if (input.getType().startsWith("uint")) {
                        if (input.getType().endsWith("[]")) {
                            paramsTypes.add("BigInteger[]");
                        } else {
                            paramsTypes.add("BigInteger");
                        }
                    } else if (input.getType().startsWith("bytes")) {
                        paramsTypes.add("byte[]");
                    } else {
                        paramsTypes.add("Object");
                    }
            }
        }

        String signature = "public " + returnType + " " + entry.getName() + "(";
        String walletToUse = "this.viewWallet";
        boolean f = true;

        if (entry.getStateMutability() == Protocol.SmartContract.ABI.Entry.StateMutabilityType.Payable
                || entry.getStateMutability() == Protocol.SmartContract.ABI.Entry.StateMutabilityType.Nonpayable) {
            f = false;
            signature += "TronWallet sender";
            walletToUse = "sender";
        }

        for (int i = 0; i < paramsNames.size(); i++) {
            if (f) {
                f = false;
            } else {
                signature += ", ";
            }

            signature += paramsTypes.get(i) + " " + paramsNames.get(i);
        }

        if (entry.getStateMutability() == Protocol.SmartContract.ABI.Entry.StateMutabilityType.Payable) {
            if (f) {
                f = false;
            } else {
                signature += ", ";
            }

            signature += "TronCurrency callValue";
        }

        signature += ")";

        String paramNamesList = "";
        boolean pf = true;
        for (String paramName : paramsNames) {
            if (pf) {
                pf = false;
            } else {
                paramNamesList += ", ";
            }
            paramNamesList += paramName;
        }


        result += "    " + signature
                + " throws GRPCException, TransactionException, EncodingException, InvalidCallDataException" + " {\n";

        result += "        " + "return this.callMethod(" + walletToUse + ", "
                + "new TriggerContractDataBuilder(getContract().getMethodsSignature(\"" + entry.getName() + "\"))"
                + (paramsNames.isEmpty() ? "" : (".params(" + paramNamesList + ")")) + ", ";

        if (entry.getStateMutability() == Protocol.SmartContract.ABI.Entry.StateMutabilityType.Payable
                || entry.getStateMutability() == Protocol.SmartContract.ABI.Entry.StateMutabilityType.Nonpayable) {
            result += "TronCurrency.MAX_FEE_LIMIT, ";
        } else {
            result += "TronCurrency.ZERO, ";
        }

        if (entry.getStateMutability() == Protocol.SmartContract.ABI.Entry.StateMutabilityType.Payable) {
            result += "callValue";
        } else {
            result += "TronCurrency.ZERO";
        }

        result += ")." + returnMethod + ";\n";

        result += "    }\n";

        return result;
    }
}
