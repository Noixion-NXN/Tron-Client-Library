package tv.noixion.troncli.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a file with the data for a smart contract deployment.
 */
public class ContractDeployFile {
    private String abi;
    private String bytecode;
    private String constructorSignature;
    private List<String> params;

    public ContractDeployFile() {
        this.abi = "";
        this.bytecode = "";
        this.constructorSignature = "";
        this.params = new ArrayList<>();
    }

    public ContractDeployFile(File file) throws IOException {
        this();
        List<String> lines = Files.readAllLines(file.toPath());

        int status = 0;
        for (String line : lines) {
            if (line.startsWith("#") || line.trim().length() == 0) {
                continue;
            }
            if (line.toUpperCase().startsWith("ABI:")) {
                this.abi = "";
                status = 1;
                if (line.length() > "ABI:".length()) {
                    this.abi = line.substring("ABI:".length());
                }
            } else if (line.toUpperCase().startsWith("BYTECODE:")) {
                this.bytecode = "";
                status = 2;
                if (line.length() > "BYTECODE:".length()) {
                    this.bytecode = line.substring("BYTECODE:".length());
                }
            } else if (line.toUpperCase().startsWith("CONSTRUCTOR_SIGNATURE:")) {
                this.constructorSignature = "";
                status = 3;
                if (line.length() > "CONSTRUCTOR_SIGNATURE:".length()) {
                    this.constructorSignature = line.substring("BYTECODE:".length());
                }
            } else if (line.toUpperCase().startsWith("CONSTRUCTOR_PARAMS:")) {
                status = 4;
            } else {
                switch (status) {
                    case 1:
                        this.abi += line;
                        break;
                    case 2:
                        this.bytecode += line;
                        break;
                    case 3:
                        this.constructorSignature += line;
                        break;
                    case 4:
                        this.params.add(line);
                        break;
                }
            }
        }

        this.abi = this.abi.trim();
        this.bytecode = this.bytecode.trim();
        this.constructorSignature = this.constructorSignature.trim();
    }

    @Override
    public String toString() {
        String result = "";

        result += "# Generated contract file" + "\n" + "\n";

        result += "ABI:\n\n" + this.abi + "\n\n";
        result += "BYTECODE:\n\n" + this.bytecode + "\n\n";
        result += "CONSTRUCTOR_SIGNATURE:\n\n" + this.constructorSignature + "\n\n";

        result += "CONSTRUCTOR_PARAMS:\n\n";

        for (String param : this.params) {
            result += param + "\n";
        }

        return result;
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
}
