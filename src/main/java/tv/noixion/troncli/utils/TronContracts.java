package tv.noixion.troncli.utils;

import tv.noixion.troncli.models.TronAddress;
import com.google.protobuf.ByteString;
import org.spongycastle.util.encoders.Hex;
import org.tron.protos.Contract;
import org.tron.protos.Protocol;

import java.util.Map;

/**
 * Tron contracts creation logic.
 */
public class TronContracts {
    public static Contract.TransferContract createTransferContract(byte[] to, byte[] owner,
                                                                   long amount) {
        Contract.TransferContract.Builder builder = Contract.TransferContract.newBuilder();
        ByteString bsTo = ByteString.copyFrom(to);
        ByteString bsOwner = ByteString.copyFrom(owner);
        builder.setToAddress(bsTo);
        builder.setOwnerAddress(bsOwner);
        builder.setAmount(amount);

        return builder.build();
    }

    public static Contract.TransferAssetContract createTransferAssetContract(byte[] to,
                                                                             byte[] assertName, byte[] owner,
                                                                             long amount) {
        Contract.TransferAssetContract.Builder builder = Contract.TransferAssetContract.newBuilder();
        ByteString bsTo = ByteString.copyFrom(to);
        ByteString bsName = ByteString.copyFrom(assertName);
        ByteString bsOwner = ByteString.copyFrom(owner);
        builder.setToAddress(bsTo);
        builder.setAssetName(bsName);
        builder.setOwnerAddress(bsOwner);
        builder.setAmount(amount);

        return builder.build();
    }

    public static Contract.ParticipateAssetIssueContract participateAssetIssueContract(byte[] to,
                                                                                       byte[] assertName, byte[] owner,
                                                                                       long amount) {
        Contract.ParticipateAssetIssueContract.Builder builder = Contract.ParticipateAssetIssueContract
                .newBuilder();
        ByteString bsTo = ByteString.copyFrom(to);
        ByteString bsName = ByteString.copyFrom(assertName);
        ByteString bsOwner = ByteString.copyFrom(owner);
        builder.setToAddress(bsTo);
        builder.setAssetName(bsName);
        builder.setOwnerAddress(bsOwner);
        builder.setAmount(amount);

        return builder.build();
    }

    public static Contract.AccountUpdateContract createAccountUpdateContract(byte[] accountName,
                                                                             byte[] address) {
        Contract.AccountUpdateContract.Builder builder = Contract.AccountUpdateContract.newBuilder();
        ByteString basAddreess = ByteString.copyFrom(address);
        ByteString bsAccountName = ByteString.copyFrom(accountName);
        builder.setAccountName(bsAccountName);
        builder.setOwnerAddress(basAddreess);

        return builder.build();
    }

    public static Contract.SetAccountIdContract createSetAccountIdContract(byte[] accountId,
                                                                           byte[] address) {
        Contract.SetAccountIdContract.Builder builder = Contract.SetAccountIdContract.newBuilder();
        ByteString bsAddress = ByteString.copyFrom(address);
        ByteString bsAccountId = ByteString.copyFrom(accountId);
        builder.setAccountId(bsAccountId);
        builder.setOwnerAddress(bsAddress);

        return builder.build();
    }


    public static Contract.UpdateAssetContract createUpdateAssetContract(
            byte[] address,
            byte[] description,
            byte[] url,
            long newLimit,
            long newPublicLimit
    ) {
        Contract.UpdateAssetContract.Builder builder =
                Contract.UpdateAssetContract.newBuilder();
        ByteString basAddreess = ByteString.copyFrom(address);
        builder.setDescription(ByteString.copyFrom(description));
        builder.setUrl(ByteString.copyFrom(url));
        builder.setNewLimit(newLimit);
        builder.setNewPublicLimit(newPublicLimit);
        builder.setOwnerAddress(basAddreess);

        return builder.build();
    }

    public static Contract.AccountCreateContract createAccountCreateContract(byte[] owner,
                                                                             byte[] address) {
        Contract.AccountCreateContract.Builder builder = Contract.AccountCreateContract.newBuilder();
        builder.setOwnerAddress(ByteString.copyFrom(owner));
        builder.setAccountAddress(ByteString.copyFrom(address));

        return builder.build();
    }

    public static Contract.WitnessCreateContract createWitnessCreateContract(byte[] owner,
                                                                             byte[] url) {
        Contract.WitnessCreateContract.Builder builder = Contract.WitnessCreateContract.newBuilder();
        builder.setOwnerAddress(ByteString.copyFrom(owner));
        builder.setUrl(ByteString.copyFrom(url));

        return builder.build();
    }

    public static Contract.WitnessUpdateContract createWitnessUpdateContract(byte[] owner,
                                                                             byte[] url) {
        Contract.WitnessUpdateContract.Builder builder = Contract.WitnessUpdateContract.newBuilder();
        builder.setOwnerAddress(ByteString.copyFrom(owner));
        builder.setUpdateUrl(ByteString.copyFrom(url));

        return builder.build();
    }

    public static Contract.VoteWitnessContract createVoteWitnessContract(byte[] owner,
                                                                         Map<TronAddress, Long> witness) {
        Contract.VoteWitnessContract.Builder builder = Contract.VoteWitnessContract.newBuilder();
        builder.setOwnerAddress(ByteString.copyFrom(owner));
        for (TronAddress addressBase58 : witness.keySet()) {
            long count = witness.get(addressBase58);
            Contract.VoteWitnessContract.Vote.Builder voteBuilder = Contract.VoteWitnessContract.Vote
                    .newBuilder();
            byte[] address = addressBase58.getBytes();
            if (address == null) {
                continue;
            }
            voteBuilder.setVoteAddress(ByteString.copyFrom(address));
            voteBuilder.setVoteCount(count);
            builder.addVotes(voteBuilder.build());
        }

        return builder.build();
    }

    public static Contract.FreezeBalanceContract createFreezeBalanceContract(byte[] address, long frozen_balance,
                                                                       long frozen_duration, int resourceCode) {
        Contract.FreezeBalanceContract.Builder builder = Contract.FreezeBalanceContract.newBuilder();
        ByteString byteAddress = ByteString.copyFrom(address);
        builder.setOwnerAddress(byteAddress).setFrozenBalance(frozen_balance)
                .setFrozenDuration(frozen_duration).setResourceValue(resourceCode);

        return builder.build();
    }

    public static Contract.BuyStorageContract
        createBuyStorageContract(byte[] address, long quantity) {
        Contract.BuyStorageContract.Builder builder = Contract.BuyStorageContract.newBuilder();
        ByteString byteAddress = ByteString.copyFrom(address);
        builder.setOwnerAddress(byteAddress).setQuant(quantity);

        return builder.build();
    }

    public static Contract.BuyStorageBytesContract createBuyStorageBytesContract(byte[] address, long bytes) {
        Contract.BuyStorageBytesContract.Builder builder = Contract.BuyStorageBytesContract
                .newBuilder();
        ByteString byteAddress = ByteString.copyFrom(address);
        builder.setOwnerAddress(byteAddress).setBytes(bytes);

        return builder.build();
    }

    public static Contract.SellStorageContract createSellStorageContract(byte[] address, long storageBytes) {
        Contract.SellStorageContract.Builder builder = Contract.SellStorageContract.newBuilder();
        ByteString byteAddress = ByteString.copyFrom(address);
        builder.setOwnerAddress(byteAddress).setStorageBytes(storageBytes);

        return builder.build();
    }

    public static Contract.UnfreezeBalanceContract createUnfreezeBalanceContract(byte[] address, int resourceCode) {
        Contract.UnfreezeBalanceContract.Builder builder = Contract.UnfreezeBalanceContract
                .newBuilder();
        ByteString byteAddreess = ByteString.copyFrom(address);
        builder.setOwnerAddress(byteAddreess).setResourceValue(resourceCode);

        return builder.build();
    }

    public static Contract.UnfreezeAssetContract createUnfreezeAssetContract(byte[] address) {
        Contract.UnfreezeAssetContract.Builder builder = Contract.UnfreezeAssetContract
                .newBuilder();
        ByteString byteAddreess = ByteString.copyFrom(address);
        builder.setOwnerAddress(byteAddreess);
        return builder.build();
    }

    public static Contract.WithdrawBalanceContract createWithdrawBalanceContract(byte[] address) {
        Contract.WithdrawBalanceContract.Builder builder = Contract.WithdrawBalanceContract
                .newBuilder();
        ByteString byteAddreess = ByteString.copyFrom(address);
        builder.setOwnerAddress(byteAddreess);

        return builder.build();
    }

    public static Contract.ProposalCreateContract createProposalCreateContract(byte[] owner,
                                                                               Map<Long, Long> parametersMap) {
        Contract.ProposalCreateContract.Builder builder = Contract.ProposalCreateContract.newBuilder();
        builder.setOwnerAddress(ByteString.copyFrom(owner));
        builder.putAllParameters(parametersMap);
        return builder.build();
    }

    public static Contract.ProposalApproveContract createProposalApproveContract(byte[] owner,
                                                                                 long id, boolean is_add_approval) {
        Contract.ProposalApproveContract.Builder builder = Contract.ProposalApproveContract
                .newBuilder();
        builder.setOwnerAddress(ByteString.copyFrom(owner));
        builder.setProposalId(id);
        builder.setIsAddApproval(is_add_approval);
        return builder.build();
    }

    public static Contract.ProposalDeleteContract createProposalDeleteContract(byte[] owner,
                                                                               long id) {
        Contract.ProposalDeleteContract.Builder builder = Contract.ProposalDeleteContract.newBuilder();
        builder.setOwnerAddress(ByteString.copyFrom(owner));
        builder.setProposalId(id);
        return builder.build();
    }

    public static Contract.UpdateSettingContract createUpdateSettingContract(byte[] owner,
                                                                             byte[] contractAddress, long consumeUserResourcePercent) {

        Contract.UpdateSettingContract.Builder builder = Contract.UpdateSettingContract.newBuilder();
        builder.setOwnerAddress(ByteString.copyFrom(owner));
        builder.setContractAddress(ByteString.copyFrom(contractAddress));
        builder.setConsumeUserResourcePercent(consumeUserResourcePercent);
        return builder.build();
    }

    public static Contract.CreateSmartContract createContractDeployContract(String contractName,
                                                                            byte[] address,
                                                                            String ABI, String code, long value, long consumeUserResourcePercent,
                                                                            long originEnergyLimit,
                                                                            long callValueTokenId, long callValueToken,
                                                                            Map<String, TronAddress> libraryAddressPair) {
        Protocol.SmartContract.ABI abi = TronSmartContracts.jsonStr2ABI(ABI);
        if (abi == null) {
            return null;
        }

        Protocol.SmartContract.Builder builder = Protocol.SmartContract.newBuilder();
        builder.setName(contractName);
        builder.setOriginAddress(ByteString.copyFrom(address));
        builder.setAbi(abi);
        builder.setConsumeUserResourcePercent(consumeUserResourcePercent);
        builder.setOriginEnergyLimit(originEnergyLimit);

        if (value != 0) {

            builder.setCallValue(value);
        }
        byte[] byteCode;
        if (null != libraryAddressPair) {
            byteCode = TronSmartContracts.replaceLibraryAddress(code, libraryAddressPair);
        } else {
            byteCode = Hex.decode(code);
        }

        builder.setBytecode(ByteString.copyFrom(byteCode));

        return Contract.CreateSmartContract.newBuilder().setOwnerAddress(ByteString.copyFrom(address))
                .setNewContract(builder.build())
                .setTokenId(callValueTokenId)
                .setCallTokenValue(callValueToken)
                .build();
    }

    public static Contract.TriggerSmartContract triggerCallContract(byte[] address,
                                                                    byte[] contractAddress,
                                                                    long callValue,
                                                                    long callValueTokenId,
                                                                    long callValueToken,
                                                                    byte[] data) {
        Contract.TriggerSmartContract.Builder builder = Contract.TriggerSmartContract.newBuilder();
        builder.setOwnerAddress(ByteString.copyFrom(address));
        builder.setContractAddress(ByteString.copyFrom(contractAddress));
        builder.setData(ByteString.copyFrom(data));
        builder.setCallValue(callValue);
        builder.setTokenId(callValueTokenId);
        builder.setCallTokenValue(callValueToken);
        return builder.build();
    }
}
