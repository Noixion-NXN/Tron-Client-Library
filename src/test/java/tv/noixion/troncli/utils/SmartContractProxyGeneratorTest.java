package tv.noixion.troncli.utils;

import tv.noixion.troncli.models.TronSmartContract;
import tv.noixion.troncli.utils.ContractDeployFile;
import tv.noixion.troncli.utils.SmartContractProxyGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.tron.protos.Protocol;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static tv.noixion.troncli.utils.TronSmartContracts.jsonStr2ABI;

public class SmartContractProxyGeneratorTest {

    @Test
    public void generateProxyForSmartContractTest(){
        try {
            ContractDeployFile contractDeployFile = new ContractDeployFile(new File("src\\test\\resources\\testCompileContract.txt"));
            Protocol.SmartContract.ABI abi = jsonStr2ABI(contractDeployFile.getAbi());
            TronSmartContract tronSmartContract = new TronSmartContract(abi);
            String contractJavaFile = SmartContractProxyGenerator.generateProxyForSmartContract("","Test", tronSmartContract);
            List<String> lines = Files.readAllLines(Paths.get("src\\test\\resources\\TestProxy.java"));
            Assert.assertEquals(Arrays.asList(contractJavaFile.split("\n")), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
