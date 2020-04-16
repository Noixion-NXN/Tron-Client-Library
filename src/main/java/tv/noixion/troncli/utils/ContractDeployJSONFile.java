package tv.noixion.troncli.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ContractDeployJSONFile {
    private String name;
    private String abi;
    private String bytecode;
    private String constructorSignature;
    private List<String> params;

    public ContractDeployJSONFile() {
        this.abi = "";
        this.bytecode = "";
        this.constructorSignature = "";
        this.params = new ArrayList<>();
    }

    public ContractDeployJSONFile(File file) throws IOException {
        this();

        String json = new String(Files.readAllBytes(file.toPath()));

        JSONObject root = new JSONObject(json);

        this.name = root.getString("contractName");
        this.abi = root.getJSONArray("abi").toString();
        this.bytecode = root.getString("bytecode").substring(2);

        JSONArray arrayEntries = root.getJSONArray("abi");

        for (int i = 0; i < arrayEntries.length(); i++) {
            JSONObject obj = arrayEntries.getJSONObject(i);

            if (obj.getString("type").equals("constructor")) {
                String constructorSig = "constructor(";

                if (obj.has("inputs")) {
                    JSONArray arrayInputs = obj.getJSONArray("inputs");
                    for (int j = 0; j < arrayInputs.length(); j++) {
                        if (j > 0) {
                            constructorSig += ",";
                        }
                        constructorSig += arrayInputs.getJSONObject(j).get("type");
                        this.params.add(ConsoleUtils.readString("Input value for constructor param " + arrayInputs.getJSONObject(j).get("name") + " (" + arrayInputs.getJSONObject(j).get("type") + "): "));
                    }
                }

                constructorSig += ")";
                this.constructorSignature = constructorSig;
                break;
            }
        }
    }


    public String getAbi() {
        return abi;
    }

    public void setAbi(String abi) {
        this.abi = abi;
    }

    public String getBytecode() {
        return bytecode;
    }

    public void setBytecode(String bytecode) {
        this.bytecode = bytecode;
    }

    public String getConstructorSignature() {
        return constructorSignature;
    }

    public void setConstructorSignature(String constructorSignature) {
        this.constructorSignature = constructorSignature;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
