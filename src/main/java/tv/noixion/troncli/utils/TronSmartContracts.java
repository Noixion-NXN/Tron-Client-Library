package tv.noixion.troncli.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import tv.noixion.troncli.models.TronAddress;
import org.spongycastle.util.encoders.Hex;
import org.tron.common.crypto.Hash;
import org.tron.common.crypto.Sha256Hash;
import org.tron.protos.Protocol;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TronSmartContracts {
    public static Protocol.SmartContract.ABI.Entry.EntryType getEntryType(String type) {
        switch (type) {
            case "constructor":
                return Protocol.SmartContract.ABI.Entry.EntryType.Constructor;
            case "function":
                return Protocol.SmartContract.ABI.Entry.EntryType.Function;
            case "event":
                return Protocol.SmartContract.ABI.Entry.EntryType.Event;
            case "fallback":
                return Protocol.SmartContract.ABI.Entry.EntryType.Fallback;
            default:
                return Protocol.SmartContract.ABI.Entry.EntryType.UNRECOGNIZED;
        }
    }

    public static Protocol.SmartContract.ABI.Entry.StateMutabilityType getStateMutability(
            String stateMutability) {
        switch (stateMutability) {
            case "pure":
                return Protocol.SmartContract.ABI.Entry.StateMutabilityType.Pure;
            case "view":
                return Protocol.SmartContract.ABI.Entry.StateMutabilityType.View;
            case "nonpayable":
                return Protocol.SmartContract.ABI.Entry.StateMutabilityType.Nonpayable;
            case "payable":
                return Protocol.SmartContract.ABI.Entry.StateMutabilityType.Payable;
            default:
                return Protocol.SmartContract.ABI.Entry.StateMutabilityType.UNRECOGNIZED;
        }
    }

    public static Protocol.SmartContract.ABI jsonStr2ABI(String jsonStr) {
        if (jsonStr == null) {
            return null;
        }

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElementRoot = jsonParser.parse(jsonStr);
        JsonArray jsonRoot = jsonElementRoot.getAsJsonArray();
        Protocol.SmartContract.ABI.Builder abiBuilder = Protocol.SmartContract.ABI.newBuilder();
        for (int index = 0; index < jsonRoot.size(); index++) {
            JsonElement abiItem = jsonRoot.get(index);
            boolean anonymous = abiItem.getAsJsonObject().get("anonymous") != null ?
                    abiItem.getAsJsonObject().get("anonymous").getAsBoolean() : false;
            boolean constant = abiItem.getAsJsonObject().get("constant") != null ?
                    abiItem.getAsJsonObject().get("constant").getAsBoolean() : false;
            String name = abiItem.getAsJsonObject().get("name") != null ?
                    abiItem.getAsJsonObject().get("name").getAsString() : null;
            JsonArray inputs = abiItem.getAsJsonObject().get("inputs") != null ?
                    abiItem.getAsJsonObject().get("inputs").getAsJsonArray() : null;
            JsonArray outputs = abiItem.getAsJsonObject().get("outputs") != null ?
                    abiItem.getAsJsonObject().get("outputs").getAsJsonArray() : null;
            String type = abiItem.getAsJsonObject().get("type") != null ?
                    abiItem.getAsJsonObject().get("type").getAsString() : null;
            boolean payable = abiItem.getAsJsonObject().get("payable") != null ?
                    abiItem.getAsJsonObject().get("payable").getAsBoolean() : false;
            String stateMutability = abiItem.getAsJsonObject().get("stateMutability") != null ?
                    abiItem.getAsJsonObject().get("stateMutability").getAsString() : null;
            if (type == null) {
                return null;
            }
            if (!type.equalsIgnoreCase("fallback") && null == inputs) {
                return null;
            }

            Protocol.SmartContract.ABI.Entry.Builder entryBuilder = Protocol.SmartContract.ABI.Entry.newBuilder();
            entryBuilder.setAnonymous(anonymous);
            entryBuilder.setConstant(constant);
            if (name != null) {
                entryBuilder.setName(name);
            }

            /* {inputs : optional } since fallback function not requires inputs*/
            if (null != inputs) {
                for (int j = 0; j < inputs.size(); j++) {
                    JsonElement inputItem = inputs.get(j);
                    if (inputItem.getAsJsonObject().get("name") == null ||
                            inputItem.getAsJsonObject().get("type") == null) {
                        return null;
                    }
                    String inputName = inputItem.getAsJsonObject().get("name").getAsString();
                    String inputType = inputItem.getAsJsonObject().get("type").getAsString();
                    boolean inputIndexed =  false;
                    if (inputItem.getAsJsonObject().get("indexed") != null) {
                        inputIndexed = inputItem.getAsJsonObject().get("indexed").getAsBoolean();
                    }
                    Protocol.SmartContract.ABI.Entry.Param.Builder paramBuilder = Protocol.SmartContract.ABI.Entry.Param
                            .newBuilder();
                    paramBuilder.setIndexed(inputIndexed);
                    paramBuilder.setName(inputName);
                    paramBuilder.setType(inputType);
                    entryBuilder.addInputs(paramBuilder.build());
                }
            }

            /* { outputs : optional } */
            if (outputs != null) {
                for (int k = 0; k < outputs.size(); k++) {
                    JsonElement outputItem = outputs.get(k);
                    if (outputItem.getAsJsonObject().get("name") == null ||
                            outputItem.getAsJsonObject().get("type") == null) {
                        return null;
                    }
                    String outputName = outputItem.getAsJsonObject().get("name").getAsString();
                    String outputType = outputItem.getAsJsonObject().get("type").getAsString();
                    Protocol.SmartContract.ABI.Entry.Param.Builder paramBuilder = Protocol.SmartContract.ABI.Entry.Param
                            .newBuilder();
                    paramBuilder.setIndexed(false);
                    paramBuilder.setName(outputName);
                    paramBuilder.setType(outputType);
                    entryBuilder.addOutputs(paramBuilder.build());
                }
            }

            entryBuilder.setType(getEntryType(type));
            entryBuilder.setPayable(payable);
            if (stateMutability != null) {
                entryBuilder.setStateMutability(getStateMutability(stateMutability));
            }

            abiBuilder.addEntrys(entryBuilder.build());
        }

        return abiBuilder.build();
    }

    public static byte[] replaceLibraryAddress(String code, String libraryAddressPair) {

        String[] libraryAddressList = libraryAddressPair.split("[,]");

        for (int i = 0; i < libraryAddressList.length; i++) {
            String cur = libraryAddressList[i];

            int lastPosition = cur.lastIndexOf(":");
            if (-1 == lastPosition) {
                throw new RuntimeException("libraryAddress delimit by ':'");
            }
            String libraryName = cur.substring(0, lastPosition);
            String addr = cur.substring(lastPosition + 1);
            String libraryAddressHex;
            try {
                libraryAddressHex = (new String(Hex.encode(TronUtils.decodeFromBase58(addr)),
                        "US-ASCII")).substring(2);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);  // now ignore
            }
            String repeated = new String(new char[40 - libraryName.length() - 2]).replace("\0", "_");
            String beReplaced = "__" + libraryName + repeated;
            Matcher m = Pattern.compile(beReplaced).matcher(code);
            code = m.replaceAll(libraryAddressHex);
        }

        return Hex.decode(code);
    }

    public static byte[] replaceLibraryAddress(String code, Map<String, TronAddress> libraries) {
        for (String libraryName : libraries.keySet()) {
            String libraryAddressHex;
            try {
                libraryAddressHex = (new String(Hex.encode(libraries.get(libraryName).getBytes()),
                        "US-ASCII")).substring(2);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);  // now ignore
            }
            String repeated = new String(new char[40 - libraryName.length() - 2]).replace("\0", "_");
            String beReplaced = "__" + libraryName + repeated;
            Matcher m = Pattern.compile(beReplaced).matcher(code);
            code = m.replaceAll(libraryAddressHex);
        }

        return Hex.decode(code);
    }

    public static byte[] generateContractAddress(byte[] ownerAddress, Protocol.Transaction trx) {

        // get owner address
        // this address should be as same as the onweraddress in trx, DONNOT modify it

        // get tx hash
        byte[] txRawDataHash = Sha256Hash.of(trx.getRawData().toByteArray()).getBytes();

        // combine
        byte[] combined = new byte[txRawDataHash.length + ownerAddress.length];
        System.arraycopy(txRawDataHash, 0, combined, 0, txRawDataHash.length);
        System.arraycopy(ownerAddress, 0, combined, txRawDataHash.length, ownerAddress.length);

        return Hash.sha3omit12(combined, TronUtils.PREFIX_BYTE);

    }
}
