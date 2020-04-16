package tv.noixion.troncli.models.contracts;

import tv.noixion.troncli.models.TronContract;
import org.tron.protos.Contract;
import org.tron.protos.Protocol;

public class TronContractFactory {
    public static TronContract makeContract(Protocol.Transaction.Contract contract) {
        try {
            switch (contract.getType()) {
                case AccountCreateContract:
                    return new AccountCreationContract(contract.getParameter().unpack(Contract.AccountCreateContract.class));
                case TransferContract:
                    return new TransferContract(contract.getParameter().unpack(Contract.TransferContract.class));
                case TransferAssetContract:
                    return new TransferAssetContract(contract.getParameter().unpack(Contract.TransferAssetContract.class));
                case VoteAssetContract:
                    return new VoteAssetContract(contract.getParameter().unpack(Contract.VoteAssetContract.class));
                case VoteWitnessContract:
                    return new VoteWitnessContract(contract.getParameter().unpack(Contract.VoteWitnessContract.class));
                case WitnessCreateContract:
                    return new CreateWitnessContract(contract.getParameter().unpack(Contract.WitnessCreateContract.class));
                case AssetIssueContract:
                    return new AssetIssueContract(contract.getParameter().unpack(Contract.AssetIssueContract.class));
                case WitnessUpdateContract:
                    return new UpdateWitnessContract(contract.getParameter().unpack(Contract.WitnessUpdateContract.class));
                case ParticipateAssetIssueContract:
                    return new ParticipateAssetContract(contract.getParameter().unpack(Contract.ParticipateAssetIssueContract.class));
                case AccountUpdateContract:
                    return new UpdateAccountContract(contract.getParameter().unpack(Contract.AccountUpdateContract.class));
                case FreezeBalanceContract:
                    return new FreezeBalanceContract(contract.getParameter().unpack(Contract.FreezeBalanceContract.class));
                case UnfreezeBalanceContract:
                    return new UnfreezeBalanceContract(contract.getParameter().unpack(Contract.UnfreezeBalanceContract.class));
                case WithdrawBalanceContract:
                    return new WithdrawBalanceContract(contract.getParameter().unpack(Contract.WithdrawBalanceContract.class));
                case UnfreezeAssetContract:
                    return new UnfreezeAssetContract(contract.getParameter().unpack(Contract.UnfreezeAssetContract.class));
                case UpdateAssetContract:
                    return new UpdateAssetContract(contract.getParameter().unpack(Contract.UpdateAssetContract.class));
                case ProposalCreateContract:
                    return new CreateProposalContract(contract.getParameter().unpack(Contract.ProposalCreateContract.class));
                case ProposalApproveContract:
                    return new ApproveProposalContract(contract.getParameter().unpack(Contract.ProposalApproveContract.class));
                case ProposalDeleteContract:
                    return new DeleteProposalContract(contract.getParameter().unpack(Contract.ProposalDeleteContract.class));
                case SetAccountIdContract:
                    return new SetAccountIdContract(contract.getParameter().unpack(Contract.SetAccountIdContract.class));
                case CustomContract:
                    return new TronContract(TronContract.Type.CUSTOM, null);
                case CreateSmartContract:
                    return new CreateSmartContractContract(contract.getParameter().unpack(Contract.CreateSmartContract.class));
                case TriggerSmartContract:
                    return new TriggerSmartContractContract(contract.getParameter().unpack(Contract.TriggerSmartContract.class));
                case GetContract:
                    return new TronContract(TronContract.Type.GET_CONTRACT, null);
                case UpdateSettingContract:
                    return new UpdateSettingContract(contract.getParameter().unpack(Contract.UpdateSettingContract.class));
                case ExchangeCreateContract:
                    return new CreateExchangeContract(contract.getParameter().unpack(Contract.ExchangeCreateContract.class));
                case ExchangeInjectContract:
                    return new ExchangeInjectContract(contract.getParameter().unpack(Contract.ExchangeInjectContract.class));
                case ExchangeWithdrawContract:
                    return new ExchangeWithdrawContract(contract.getParameter().unpack(Contract.ExchangeWithdrawContract.class));
                case ExchangeTransactionContract:
                    return new ExchangeTransactionContract(contract.getParameter().unpack(Contract.ExchangeTransactionContract.class));
                default:
                    return new TronContract(TronContract.Type.UNKNOWN, null);
            }
        } catch (Exception ex) {
            return new TronContract(TronContract.Type.UNKNOWN, null);
        }
    }
}
