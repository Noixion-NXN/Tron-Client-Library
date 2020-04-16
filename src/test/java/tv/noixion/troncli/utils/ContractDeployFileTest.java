package tv.noixion.troncli.utils;

import tv.noixion.troncli.utils.ContractDeployFile;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ContractDeployFileTest {
    private File file;
    private String abi = "[\n" +
            "\t{\n" +
            "\t\t\"constant\": true,\n" +
            "\t\t\"inputs\": [],\n" +
            "\t\t\"name\": \"f\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"string\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"payable\": false,\n" +
            "\t\t\"stateMutability\": \"pure\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"constant\": true,\n" +
            "\t\t\"inputs\": [],\n" +
            "\t\t\"name\": \"g\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"string\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"payable\": false,\n" +
            "\t\t\"stateMutability\": \"pure\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"name\": \"_stringParam\",\n" +
            "\t\t\t\t\"type\": \"string\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"name\": \"_uintParamm\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"payable\": false,\n" +
            "\t\t\"stateMutability\": \"nonpayable\",\n" +
            "\t\t\"type\": \"constructor\"\n" +
            "\t}\n" +
            "]";
    private String bytecode = "608060405234801561001057600080fd5b5060405161033038038061033083398101806040528101908080518201929190602001805190602001909291905050508160009080519060200190610056929190610065565b5080600181905550505061010a565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100a657805160ff19168380011785556100d4565b828001600101855582156100d4579182015b828111156100d35782518255916020019190600101906100b8565b5b5090506100e191906100e5565b5090565b61010791905b808211156101035760008160009055506001016100eb565b5090565b90565b610217806101196000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806326121ff014610051578063e2179b8e146100e1575b600080fd5b34801561005d57600080fd5b50610066610171565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156100a657808201518184015260208101905061008b565b50505050905090810190601f1680156100d35780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156100ed57600080fd5b506100f66101ae565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561013657808201518184015260208101905061011b565b50505050905090810190601f1680156101635780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b60606040805190810160405280600a81526020017f6d6574686f642066282900000000000000000000000000000000000000000000815250905090565b60606040805190810160405280600a81526020017f6d6574686f6420672829000000000000000000000000000000000000000000008152509050905600a165627a7a723058209fbfc66d121debd8b0c84a2c80c9de35458ea3102c34d097e9c7bc8a2ee6ae230029";
    private String constructorSignature = "constructor(string, uint256)";
    ContractDeployFile contractDeployFile;

    @Before
    public void setUp(){
        try {
            contractDeployFile = new ContractDeployFile(new File("src\\test\\resources\\testCompileContract.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAbi(){
        Assert.assertEquals(abi.replace("\t", "").replace("\n", ""), contractDeployFile.getAbi().replace("\t", "").replace("\n", ""));
    }

    @Test
    public void testByteCode(){
        Assert.assertEquals(bytecode, contractDeployFile.getBytecode());
    }

    @Test
    public void testConstructorSignature(){
        Assert.assertEquals(constructorSignature, contractDeployFile.getConstructorSignature());
    }

    @Test
    public  void testParams(){
        Assert.assertEquals(2, contractDeployFile.getParams().size());
    }



}
