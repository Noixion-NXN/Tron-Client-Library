package tv.noixion.troncli.utils;

import tv.noixion.troncli.utils.TronUtils;
import org.junit.Assert;
import org.junit.Test;
import org.tron.protos.Contract;

import static tv.noixion.troncli.utils.TronContracts.*;

public class TronContractsTest {
    private String to = "TWiWt5SEDzaEqS6kE5gandWMNfxR2B5xzg";
    private String from = "TEydVQ17YRqnyJFnq29iBBh9rjUFexBz5i";
    private long amount = 100;
    private String assertName ="TF6i3aPkvhQ7Whqa8UDs7VXVhtURasnAMk";
    private String contractAddress = "TFn5CFSfyaZnaPmU9KieLYKVaKbjxHTcrP";
    private String accountName = "Test";
    private String url = "https://tron.network";
    private String accountId = "";
    private String description = "";
    private long newLimit = 0;
    private long newPublicLimit = 0;
    private long frozen_balance = 100;
    private long frozen_duration = 3;
    private int resource_code = 0;

    @Test
    public void createTransferContractTest(){
        Contract.TransferContract  transferContract= createTransferContract(TronUtils.decodeFromBase58(to), TronUtils.decodeFromBase58(from), amount);
        Assert.assertEquals(amount, transferContract.getAmount());
        Assert.assertEquals(to, TronUtils.encodeToBase58Check(transferContract.getToAddress().toByteArray()));
        Assert.assertEquals(from, TronUtils.encodeToBase58Check(transferContract.getOwnerAddress().toByteArray()));
    }

    @Test
    public void createTransferAssetContractTest(){
        Contract.TransferAssetContract  transferAssetContract= createTransferAssetContract(TronUtils.decodeFromBase58(to),TronUtils.decodeFromBase58(assertName), TronUtils.decodeFromBase58(from), amount);
        Assert.assertEquals(amount, transferAssetContract.getAmount());
        Assert.assertEquals(to, TronUtils.encodeToBase58Check(transferAssetContract.getToAddress().toByteArray()));
        Assert.assertEquals(from, TronUtils.encodeToBase58Check(transferAssetContract.getOwnerAddress().toByteArray()));
        Assert.assertEquals(assertName, TronUtils.encodeToBase58Check(transferAssetContract.getAssetName().toByteArray()));
    }

    @Test
    public void participateAssetIssueContractTest(){
        Contract.ParticipateAssetIssueContract  participateAssetIssueContract= participateAssetIssueContract(TronUtils.decodeFromBase58(to),TronUtils.decodeFromBase58(assertName), TronUtils.decodeFromBase58(from), amount);
        Assert.assertEquals(amount, participateAssetIssueContract.getAmount());
        Assert.assertEquals(to, TronUtils.encodeToBase58Check(participateAssetIssueContract.getToAddress().toByteArray()));
        Assert.assertEquals(from, TronUtils.encodeToBase58Check(participateAssetIssueContract.getOwnerAddress().toByteArray()));
        Assert.assertEquals(assertName, TronUtils.encodeToBase58Check(participateAssetIssueContract.getAssetName().toByteArray()));
    }

    @Test
    public void createAccountUpdateContractTest(){
        Contract.AccountUpdateContract  accountUpdateContract= createAccountUpdateContract(accountName.getBytes(),TronUtils.decodeFromBase58(to));
        Assert.assertEquals(accountName, accountUpdateContract.getAccountName().toStringUtf8());
        Assert.assertEquals(to,TronUtils.encodeToBase58Check(accountUpdateContract.getOwnerAddress().toByteArray()));
    }

    @Test
    public void createAccountCreateContractTest(){
        Contract.AccountCreateContract  accountCreateContract= createAccountCreateContract(TronUtils.decodeFromBase58(to),TronUtils.decodeFromBase58(contractAddress));
        Assert.assertEquals(to, TronUtils.encodeToBase58Check(accountCreateContract.getOwnerAddress().toByteArray()));
        Assert.assertEquals(contractAddress,TronUtils.encodeToBase58Check(accountCreateContract.getAccountAddress().toByteArray()));
    }

    @Test
    public void createWitnessCreateContractTest(){
        Contract.WitnessCreateContract  witnessCreateContract= createWitnessCreateContract(TronUtils.decodeFromBase58(to),url.getBytes());
        Assert.assertEquals(to, TronUtils.encodeToBase58Check(witnessCreateContract.getOwnerAddress().toByteArray()));
        Assert.assertEquals(url, witnessCreateContract.getUrl().toStringUtf8());
    }

    @Test
    public void createWitnessUpdateContractTest(){
        Contract.WitnessUpdateContract  witnessUpdateContract= createWitnessUpdateContract(TronUtils.decodeFromBase58(to),url.getBytes());
        Assert.assertEquals(to, TronUtils.encodeToBase58Check(witnessUpdateContract.getOwnerAddress().toByteArray()));
        Assert.assertEquals(url, witnessUpdateContract.getUpdateUrl().toStringUtf8());
    }

    @Test
    public void createFreezeBalanceContractTest(){
        Contract.FreezeBalanceContract  freezeBalanceContract= createFreezeBalanceContract(TronUtils.decodeFromBase58(to),frozen_balance, frozen_duration, resource_code);
        Assert.assertEquals(to, TronUtils.encodeToBase58Check(freezeBalanceContract.getOwnerAddress().toByteArray()));
        Assert.assertEquals(frozen_balance, freezeBalanceContract.getFrozenBalance());
        Assert.assertEquals(frozen_duration, freezeBalanceContract.getFrozenDuration());
        Assert.assertEquals(Contract.ResourceCode.BANDWIDTH, freezeBalanceContract.getResource());

    }


}
