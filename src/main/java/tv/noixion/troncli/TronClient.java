package tv.noixion.troncli;

import tv.noixion.troncli.exceptions.ConfirmationTimeoutException;
import tv.noixion.troncli.exceptions.GRPCException;
import tv.noixion.troncli.exceptions.TransactionException;
import tv.noixion.troncli.models.*;
import tv.noixion.troncli.grpc.GrpcClient;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import javafx.util.Pair;
import org.spongycastle.util.encoders.Hex;
import org.tron.api.GrpcAPI;
import org.tron.core.exception.EncodingException;
import org.tron.protos.Contract;
import org.tron.protos.Protocol;
import tv.noixion.troncli.models.*;
import tv.noixion.troncli.utils.*;

import java.util.*;

/**
 * Represents a org.tron client.
 */
public class TronClient {
    private final List<GrpcClient> clients;
    private boolean has_solidity_node;

    /**
     * Creates a new client for the Tron network.
     *
     * @param fullNode     Full node
     * @param solidityNode Solidity Node
     */
    public TronClient(TronNode fullNode, TronNode solidityNode) {
        this.clients = new ArrayList<>();
        if (solidityNode == null) {
            this.clients.add(new GrpcClient(fullNode.getHostname(), ""));
            has_solidity_node = false;
        } else {
            this.clients.add(new GrpcClient(fullNode.getHostname(), solidityNode.getHostname()));
            has_solidity_node = true;
        }
    }

    /**
     * Creates a new client for the Tron network.
     *
     * @param fullNodes     List of trusted full nodes.
     * @param solidityNodes List of trusted solidity nodes.
     */
    public TronClient(List<TronNode> fullNodes, List<TronNode> solidityNodes) {
        this.clients = new ArrayList<>();
        int maxLength = Math.max(fullNodes.size(), solidityNodes.size());
        if (fullNodes.isEmpty()) {
            throw new IllegalArgumentException("At least one full node is required.");
        }
        for (int i = 0; i < maxLength; i++) {
            TronNode fullNode;
            if (i < fullNodes.size()) {
                fullNode = fullNodes.get(i);
            } else {
                fullNode = fullNodes.get(0);
            }
            TronNode solidityNode = null;
            if (i < solidityNodes.size()) {
                solidityNode = solidityNodes.get(i);
                has_solidity_node = true;
            }
            this.clients.add(new GrpcClient(fullNode.getHostname(),
                    solidityNode == null ? "" : solidityNode.getHostname()));
        }
    }

    /**
     * Creates a new client for the Tron network.
     *
     * @param fullNode Full node
     */
    public TronClient(TronNode fullNode) {
        this(fullNode, null);
    }

    /**
     * Gets a block by its number.
     *
     * @param number The block number.
     * @return The block.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public TronBlock getBlock(long number) throws GRPCException {
        GrpcAPI.BlockExtention block = null;
        GRPCException exception = new GRPCException("Could not fetch block.");
        for (GrpcClient client : clients) {
            try {
                block = client.getBlock2(number);
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        if (block == null) {
            throw exception;
        }

        return new TronBlock(block);
    }

    /**
     * Get block by its identifier / hash.
     *
     * @param id the identifier / hash of the block.
     * @return the block.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public TronBlock getBlock(HashIdentifier id) throws GRPCException {
        Optional<Protocol.Block> block = null;
        GRPCException exception = new GRPCException("Could not fetch block.");
        for (GrpcClient client : clients) {
            try {
                block = client.getBlockById(id.toString());
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        if (block == null || !block.isPresent()) {
            throw exception;
        }

        return new TronBlock(id, block.get());
    }

    /**
     * Gets a transaction.
     *
     * @param id the identifier of the transaction.
     * @return The transaction.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public TronTransaction getTransaction(HashIdentifier id) throws GRPCException {
        Optional<Protocol.Transaction> tx = null;
        GRPCException exception = new GRPCException("Could not fetch transaction.");
        for (GrpcClient client : clients) {
            try {
                tx = client.getTransactionById(id.toString());
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        if (tx == null || !tx.isPresent()) {
            throw exception;
        }

        return new TronTransaction(tx.get());
    }

    /**
     * Gets the execution information of a transaction,
     *
     * @param id the identifier of the transaction.
     * @return The transaction information.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public TronTransactionInformation getTransactionInformation(HashIdentifier id) throws GRPCException {
        Optional<Protocol.TransactionInfo> tx = null;
        GRPCException exception = new GRPCException("Could not fetch transaction information.");
        for (GrpcClient client : clients) {
            try {
                tx = client.getTransactionInfoById(id.toString());
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        if (tx == null || !tx.isPresent()) {
            throw exception;
        }

        return new TronTransactionInformation(tx.get());
    }

    /**
     * Gets the last bock.
     *
     * @return The last block.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public TronBlock getLastBlock() throws GRPCException {
        return this.getBlock(-1L);
    }

    /**
     * Queries tha information of an account by its address.
     *
     * @param address the account address.
     * @return The account information
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public TronAccount getAccountByAddress(TronAddress address) throws GRPCException {
        Protocol.Account account = null;
        GRPCException exception = new GRPCException("Could not fetch account information.");
        for (GrpcClient client : clients) {
            try {
                account = client.queryAccount(address.getBytes());
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        if (account == null) {
            throw exception;
        }

        return new TronAccount(account);
    }

    /**
     * Queries the information of an account by its name.
     *
     * @param name the account name.
     * @return the information of the account.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public TronAccount getAccountByName(String name) throws GRPCException {
        Protocol.Account account = null;
        GRPCException exception = new GRPCException("Could not fetch account information.");
        for (GrpcClient client : clients) {
            try {
                account = client.queryAccountById(name);
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        if (account == null) {
            throw exception;
        }

        return new TronAccount(account);
    }

    /**
     * Queries the account net usage information.
     *
     * @param address The address of the account.
     * @return The net usage information for the account.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public TronAccountNet getAccountNet(TronAddress address) throws GRPCException {
        GrpcAPI.AccountNetMessage msg = null;
        GRPCException exception = new GRPCException("Could not fetch account network information.");
        for (GrpcClient client : clients) {
            try {
                msg = client.getAccountNet(address.getBytes());
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        if (msg == null) {
            throw exception;
        }

        return new TronAccountNet(msg);
    }

    /**
     * Queries the account resources information.
     *
     * @param address The address of the account.
     * @return the resources information for the account.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public TronAccountResource getAccountResources(TronAddress address) throws GRPCException {
        GrpcAPI.AccountResourceMessage msg = null;
        GRPCException exception = new GRPCException("Could not fetch account resource information.");
        for (GrpcClient client : clients) {
            try {
                msg = client.getAccountResource(address.getBytes());
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        if (msg == null) {
            throw exception;
        }

        return new TronAccountResource(msg);
    }

    /**
     * Gets the information of an assets by its name.
     *
     * @param name the name of the asset.
     * @return The information about the asset.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public TronAssetIssue getAssetIssueByName(String name) throws GRPCException {
        Contract.AssetIssueContract contract = null;
        GRPCException exception = new GRPCException("Could not fetch asset issue information.");
        for (GrpcClient client : clients) {
            try {
                contract = client.getAssetIssueByName(name);
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        if (contract == null) {
            throw exception;
        }

        return new TronAssetIssue(contract);
    }

    /**
     * Gets the list of assets owned by an address.
     *
     * @param address The address.
     * @return The list of assets.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public List<TronAssetIssue> getAssetIssueByOwnerAddress(TronAddress address) throws GRPCException {
        Optional<GrpcAPI.AssetIssueList> list = null;
        GRPCException exception = new GRPCException("Could not fetch asset issue information.");
        for (GrpcClient client : clients) {
            try {
                list = client.getAssetIssueByAccount(address.getBytes());
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        List<TronAssetIssue> result = new ArrayList<>();

        if (list != null && list.isPresent()) {
            for (Contract.AssetIssueContract contract : list.get().getAssetIssueList()) {
                result.add(new TronAssetIssue(contract));
            }
        } else {
            throw exception;
        }

        return result;
    }

    /**
     * Lists all assets present in the blockchain.
     *
     * @return The complete list of assets.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public List<TronAssetIssue> listAssetIssue() throws GRPCException {
        Optional<GrpcAPI.AssetIssueList> list = null;
        GRPCException exception = new GRPCException("Could not fetch asset issue information.");
        for (GrpcClient client : clients) {
            try {
                list = client.getAssetIssueList();
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        List<TronAssetIssue> result = new ArrayList<>();

        if (list != null && list.isPresent()) {
            for (Contract.AssetIssueContract contract : list.get().getAssetIssueList()) {
                result.add(new TronAssetIssue(contract));
            }
        } else {
            throw exception;
        }

        return result;
    }

    /**
     * Lists all witnesses.
     *
     * @return The complete list of witnesses.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public List<TronWitness> listWitnesses() throws GRPCException {
        Optional<GrpcAPI.WitnessList> list = null;
        GRPCException exception = new GRPCException("Could not fetch witnesses information.");
        for (GrpcClient client : clients) {
            try {
                list = client.listWitnesses();
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        List<TronWitness> result = new ArrayList<>();

        if (list != null && list.isPresent()) {
            for (Protocol.Witness w : list.get().getWitnessesList()) {
                result.add(new TronWitness(w));
            }
        } else {
            throw exception;
        }

        return result;
    }

    /**
     * List the known nodes by the org.tron node we are connecting to (Full nodes).
     *
     * @return The list of known nodes.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public List<TronNode> listNodes() throws GRPCException {
        Optional<GrpcAPI.NodeList> list = null;
        GRPCException exception = new GRPCException("Could not fetch nodes information.");
        for (GrpcClient client : clients) {
            try {
                list = client.listNodes();
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        List<TronNode> result = new ArrayList<>();

        if (list != null && list.isPresent()) {
            for (GrpcAPI.Node n : list.get().getNodesList()) {
                result.add(new TronNode(new String(n.getAddress().getHost().toByteArray()), n.getAddress().getPort()));
            }
        } else {
            throw exception;
        }

        return result;
    }

    /**
     * Gets a proposal information by its id.
     *
     * @param id the proposal id.
     * @return The proposal information.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public TronProposal getProposalById(long id) throws GRPCException {
        Optional<Protocol.Proposal> p = null;
        GRPCException exception = new GRPCException("Could not fetch proposal information.");
        for (GrpcClient client : clients) {
            try {
                p = client.getProposal("" + id);
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        if (p == null || !p.isPresent()) {
            throw exception;
        }

        return new TronProposal(p.get());
    }

    /**
     * Lists all proposals.pero
     *
     * @return The list of all proposals.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public List<TronProposal> listProposals() throws GRPCException {
        Optional<GrpcAPI.ProposalList> list = null;
        GRPCException exception = new GRPCException("Could not fetch proposal information.");
        for (GrpcClient client : clients) {
            try {
                list = client.listProposals();
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        List<TronProposal> result = new ArrayList<>();

        if (list != null && list.isPresent()) {
            for (Protocol.Proposal p : list.get().getProposalsList()) {
                result.add(new TronProposal(p));
            }
        } else {
            throw exception;
        }

        return result;
    }

    /**
     * Gets the transactions made by an address. (Requires solidity node)
     *
     * @param address The address.
     * @param offset  The offset.
     * @param limit   The max number of transactions to return.
     * @return The list of transactions.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public List<TronTransaction> getTransactionsFrom(TronAddress address, int offset, int limit) throws GRPCException {
        if (!has_solidity_node) {
            throw new GRPCException("This method requires a solidity node.");
        }
        Optional<GrpcAPI.TransactionList> list = null;
        GRPCException exception = new GRPCException("Could not fetch transactions.");
        for (GrpcClient client : clients) {
            try {
                list = client.getTransactionsFromThis(address.getBytes(), offset, limit);
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        List<TronTransaction> result = new ArrayList<>();

        if (list != null && list.isPresent()) {
            for (Protocol.Transaction t : list.get().getTransactionList()) {
                result.add(new TronTransaction(t));
            }
        } else {
            throw exception;
        }

        return result;
    }

    /**
     * Gets the transferences to an address. (Requires solidity node)
     *
     * @param address The address.
     * @param offset  The offset.
     * @param limit   The max number of transactions to return.
     * @return The list of transactions.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public List<TronTransaction> getTransactionsTo(TronAddress address, int offset, int limit) throws GRPCException {
        if (!has_solidity_node) {
            throw new GRPCException("This method requires a solidity node.");
        }
        Optional<GrpcAPI.TransactionList> list = null;
        GRPCException exception = new GRPCException("Could not fetch transactions.");
        for (GrpcClient client : clients) {
            try {
                list = client.getTransactionsToThis(address.getBytes(), offset, limit);
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        List<TronTransaction> result = new ArrayList<>();

        if (list != null && list.isPresent()) {
            for (Protocol.Transaction t : list.get().getTransactionList()) {
                result.add(new TronTransaction(t));
            }
        } else {
            throw exception;
        }

        return result;
    }

    /**
     * Gets an smart contract deployen on the Tron network.
     *
     * @param address The contract address.
     * @return The Smart contract.
     * @throws GRPCException If an error occurs in the GRPC protocol, generally connection problems.
     */
    public TronSmartContract getContract(TronAddress address) throws GRPCException {
        Protocol.SmartContract c = null;
        GRPCException exception = new GRPCException("Could not fetch contract information.");
        for (GrpcClient client : clients) {
            try {
                c = client.getContract(address.getBytes());
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }
        if (c == null) {
            throw exception;
        }
        return new TronSmartContract(c);
    }

    /**
     * Transfers coins (TRX / SUN)
     *
     * @param owner    The owner of the founds.
     * @param receiver The receiver of the founds.
     * @param amount   The amount of currency to  send
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction transfer(TronWallet owner, TronAddress receiver, TronCurrency amount)
            throws GRPCException, TransactionException {
        Contract.TransferContract contract = TronContracts.createTransferContract(receiver.getBytes(),
                owner.getAddress().getBytes(), amount.getSUN());

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.createTransaction2(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Transfers assets.
     *
     * @param owner     The owner of the assets.
     * @param receiver  the receiver of the assets.
     * @param assetName The name of the asset to send.
     * @param amount    the number of assets to send.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction transferAsset(TronWallet owner, TronAddress receiver, String assetName, long amount)
            throws GRPCException, TransactionException {
        Contract.TransferAssetContract contract = TronContracts.createTransferAssetContract(receiver.getBytes(),
                assetName.getBytes(), owner.getAddress().getBytes(), amount);
        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.createTransferAssetTransaction2(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Particiapes in an Asset, exchanging your currency for tokens.
     *
     * @param owner     Sender of the transaction.
     * @param receiver  The receiver of the tokens (can be your own account).
     * @param assetName The name of the asset.
     * @param amount    the amount of tokens you want.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction participateAsset(TronWallet owner, TronAddress receiver, String assetName, long amount)
            throws GRPCException, TransactionException {
        Contract.ParticipateAssetIssueContract contract = TronContracts
                .participateAssetIssueContract(receiver.getBytes(),
                        assetName.getBytes(), owner.getAddress().getBytes(), amount);
        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.createParticipateAssetIssueTransaction2(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Freezes your balance in exchange of energy or bandwidth.
     *
     * @param owner        Sender of the transaction.
     * @param amount       the amount to be frozen.
     * @param durationDays The duration (in days). It is forced to 3 days.
     * @param resource     The resource you want (energy or bandwidth)
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction freezeBalance(TronWallet owner, TronCurrency amount, int durationDays,
                                         TronResource resource) throws GRPCException, TransactionException {
        Contract.FreezeBalanceContract contract =
                TronContracts.createFreezeBalanceContract(owner.getAddress().getBytes(),
                        amount.getSUN(), durationDays, resource.getCode());
        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.createTransaction2(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Votes to a witness account. You need org.tron power. 1 Tron Power = 1 TRX frozen.
     *
     * @param owner Sender of the transaction.
     * @param votes The votes you want to emit (Address, number of votes)
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction voteWitness(TronWallet owner, Map<TronAddress, Long> votes) throws GRPCException, TransactionException {
        Contract.VoteWitnessContract contract = TronContracts.createVoteWitnessContract(owner.getAddress().getBytes(), votes);

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.voteWitnessAccount2(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Unfreezes your balance.
     *
     * @param owner    Sender of the transaction.
     * @param resource bandwidth or energy.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction unfreezeBalance(TronWallet owner, TronResource resource)
            throws GRPCException, TransactionException {
        Contract.UnfreezeBalanceContract contract = TronContracts
                .createUnfreezeBalanceContract(owner.getAddress().getBytes(), resource.getCode());

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.createTransaction2(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Creates a new Asset.
     *
     * @param owner                  Sender of the transaction.
     * @param name                   Name of the asset.
     * @param abbr                   Abbreviation.
     * @param totalSupply            Total supply of the asset.
     * @param conversion             Conversion between TRX and your asset.
     * @param start                  Start date for your asset.
     * @param end                    Expiration date for the asset.
     * @param description            Description of the asset.
     * @param url                    Url of the site mantaining the asset.
     * @param freeNetLimitPerAccount The free net limit for your asset.
     * @param publicNetLimit         The public net limit for your asset.
     * @param frozenSupply           The frozen suply of token.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction assetIssue(TronWallet owner, String name, String abbr, long totalSupply,
                                      TRXAssetConversion conversion,
                                      Date start, Date end, String description, String url,
                                      long freeNetLimitPerAccount, long publicNetLimit,
                                      List<TronAssetIssue.FrozenSupply> frozenSupply)
            throws GRPCException, TransactionException {
        return assetIssue(owner, name, abbr, totalSupply, conversion, start, end, description, url,
                0, freeNetLimitPerAccount, publicNetLimit, frozenSupply);
    }

    /**
     * Creates a new Asset.
     *
     * @param owner                  Sender of the transaction.
     * @param name                   Name of the asset.
     * @param abbr                   Abbreviation.
     * @param totalSupply            Total supply of the asset.
     * @param conversion             Conversion between TRX and your asset.
     * @param start                  Start date for your asset.
     * @param end                    Expiration date for the asset.
     * @param description            Description of the asset.
     * @param url                    Url of the site mantaining the asset.
     * @param decimals               Number of decimals to use.
     * @param freeNetLimitPerAccount The free net limit for your asset.
     * @param publicNetLimit         The public net limit for your asset.
     * @param frozenSupply           The frozen suply of token.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction assetIssue(TronWallet owner, String name, String abbr, long totalSupply,
                                      TRXAssetConversion conversion,
                                      Date start, Date end, String description, String url, int decimals,
                                      long freeNetLimitPerAccount, long publicNetLimit,
                                      List<TronAssetIssue.FrozenSupply> frozenSupply)
            throws GRPCException, TransactionException {
        return this.assetIssue(owner, name, abbr, totalSupply, conversion, 0, start, end, description, url, decimals, freeNetLimitPerAccount, publicNetLimit, frozenSupply);
    }

    /**
     * Creates a new Asset.
     *
     * @param owner                  Sender of the transaction.
     * @param name                   Name of the asset.
     * @param abbr                   Abbreviation.
     * @param totalSupply            Total supply of the asset.
     * @param conversion             Conversion between TRX and your asset.
     * @param precision              Number of decimals
     * @param start                  Start date for your asset.
     * @param end                    Expiration date for the asset.
     * @param description            Description of the asset.
     * @param url                    Url of the site mantaining the asset.
     * @param decimals               Number of decimals to use.
     * @param freeNetLimitPerAccount The free net limit for your asset.
     * @param publicNetLimit         The public net limit for your asset.
     * @param frozenSupply           The frozen suply of token.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction assetIssue(TronWallet owner, String name, String abbr, long totalSupply,
                                      TRXAssetConversion conversion, int precision,
                                      Date start, Date end, String description, String url, int decimals,
                                      long freeNetLimitPerAccount, long publicNetLimit,
                                      List<TronAssetIssue.FrozenSupply> frozenSupply)
            throws GRPCException, TransactionException {
        Contract.AssetIssueContract.Builder builder = Contract.AssetIssueContract.newBuilder();
        builder.setOwnerAddress(ByteString.copyFrom(owner.getAddress().getBytes()));
        builder.setName(ByteString.copyFrom(name.getBytes()));
        builder.setAbbr(ByteString.copyFrom(abbr.getBytes()));
        builder.setTotalSupply(totalSupply);
        builder.setTrxNum(conversion.getTrxNum());
        builder.setPrecision(precision);
        builder.setNum(conversion.getNum());
        builder.setStartTime(start.getTime());
        builder.setEndTime(end.getTime());
        builder.setVoteScore(0);
        builder.setPrecision(decimals);
        builder.setDescription(ByteString.copyFrom(description.getBytes()));
        builder.setUrl(ByteString.copyFrom(url.getBytes()));
        builder.setFreeAssetNetLimit(freeNetLimitPerAccount);
        builder.setPublicFreeAssetNetLimit(publicNetLimit);
        for (TronAssetIssue.FrozenSupply frozen : frozenSupply) {
            Contract.AssetIssueContract.FrozenSupply.Builder frozenSupplyBuilder
                    = Contract.AssetIssueContract.FrozenSupply.newBuilder();
            frozenSupplyBuilder.setFrozenAmount(frozen.getAmount());
            frozenSupplyBuilder.setFrozenDays(frozen.getDays());
            builder.addFrozenSupply(frozenSupplyBuilder.build());
        }

        Contract.AssetIssueContract contract = builder.build();
        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.createAssetIssue2(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Updates the settings of an asset.
     *
     * @param owner                  Sender of the transaction.
     * @param description            The new description.
     * @param url                    the new url.
     * @param freeNetLimitPerAccount the new free net limit.
     * @param publicNetLimit         the new public net limit.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction updateAsset(TronWallet owner, String description, String url, long freeNetLimitPerAccount,
                                       long publicNetLimit) throws GRPCException, TransactionException {
        Contract.UpdateAssetContract contract = TronContracts.createUpdateAssetContract(owner.getAddress().getBytes(),
                description.getBytes(), url.getBytes(), freeNetLimitPerAccount, publicNetLimit);

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.createTransaction2(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Unfreezes all your frozen supply of assets.
     *
     * @param owner Sender of the transaction.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction unfreezeAsset(TronWallet owner) throws GRPCException, TransactionException {
        Contract.UnfreezeAssetContract contract = TronContracts
                .createUnfreezeAssetContract(owner.getAddress().getBytes());

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.createTransaction2(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Creates an account.
     *
     * @param owner      Sender of the transaction.
     * @param newAccount The new account address.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction createAccount(TronWallet owner, TronAddress newAccount)
            throws GRPCException, TransactionException {
        Contract.AccountCreateContract contract = TronContracts
                .createAccountCreateContract(owner.getAddress().getBytes(), newAccount.getBytes());

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.createAccount2(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Updates an account, setting the name.
     *
     * @param owner Sender of the transaction.
     * @param name  the name of the account.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction updateAccount(TronWallet owner, String name) throws GRPCException, TransactionException {
        Contract.AccountUpdateContract contract = TronContracts.createAccountUpdateContract(name.getBytes(), owner.getAddress().getBytes());

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.createTransaction2(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Sets an account identifier.
     *
     * @param owner Sender of the transaction.
     * @param id    The account identifier to set.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction setAccountId(TronWallet owner, String id) throws GRPCException, TransactionException {
        Contract.SetAccountIdContract contract = TronContracts.createSetAccountIdContract(id.getBytes(), owner.getAddress().getBytes());

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                Protocol.Transaction ex = client.createTransaction(contract);
                return TransactionUtils.processTransaction(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Withdraws balance earned from mining blocks.
     *
     * @param owner Sender of the transaction.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction withdrawBalance(TronWallet owner) throws GRPCException, TransactionException {
        Contract.WithdrawBalanceContract contract = TronContracts
                .createWithdrawBalanceContract(owner.getAddress().getBytes());

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.createTransaction2(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Creates a witness account.
     *
     * @param owner Sender of the transaction.
     * @param url   the utl with the information about the witness.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction createWitness(TronWallet owner, String url) throws GRPCException, TransactionException {
        Contract.WitnessCreateContract contract = TronContracts
                .createWitnessCreateContract(owner.getAddress().getBytes(), url.getBytes());

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.createWitness2(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Updates a witness account.
     *
     * @param owner Sender of the transaction.
     * @param url   the new url.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction updateWitness(TronWallet owner, String url) throws GRPCException, TransactionException {
        Contract.WitnessUpdateContract contract = TronContracts
                .createWitnessUpdateContract(owner.getAddress().getBytes(), url.getBytes());

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.updateWitness2(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Creates a new proposal for the blockchain.
     *
     * @param owner  Sender of the transaction.
     * @param params List of parameters.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction createProposal(TronWallet owner, Map<Long, Long> params) throws GRPCException, TransactionException {
        Contract.ProposalCreateContract contract = TronContracts
                .createProposalCreateContract(owner.getAddress().getBytes(), params);

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.proposalCreate(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Approves a proposal, must be super representative.
     *
     * @param owner        Sender of the transaction.
     * @param id           Proposal identifier.
     * @param add_approval Flag "add_approval"
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction approveProposal(TronWallet owner, long id, boolean add_approval) throws GRPCException, TransactionException {
        Contract.ProposalApproveContract contract = TronContracts
                .createProposalApproveContract(owner.getAddress().getBytes(), id, add_approval);

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.proposalApprove(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Deletes a proposal. Must be the creator of the proposal.
     *
     * @param owner Sender of the transaction.
     * @param id    Proposal identifier.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction deleteProposal(TronWallet owner, long id) throws GRPCException, TransactionException {
        Contract.ProposalDeleteContract contract = TronContracts
                .createProposalDeleteContract(owner.getAddress().getBytes(), id);

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.proposalDelete(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Buys storage points.
     *
     * @param owner    Sender of the transaction.
     * @param quantity the amount of storage points you want to buy.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction buyStorage(TronWallet owner, long quantity) throws GRPCException, TransactionException {
        Contract.BuyStorageContract contract = TronContracts
                .createBuyStorageContract(owner.getAddress().getBytes(), quantity);

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.createTransaction(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Sells storage points.
     *
     * @param owner    Sender of the transaction.
     * @param quantity the amount of storage points you want to sell.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction sellStorage(TronWallet owner, long quantity) throws GRPCException, TransactionException {
        Contract.SellStorageContract contract = TronContracts
                .createSellStorageContract(owner.getAddress().getBytes(), quantity);

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.createTransaction(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Deploys an smart contract on the Tron network.
     *
     * @param owner                      Sender of the transaction.
     * @param contractName               The contract name (account name for the contract address)
     * @param ABI                        The ABI of the smart contract, in JSON format (must be an array).
     * @param byteCode                   the byte code of the smart contract in hexadecimal.
     * @param constructorCall            The constructor method call.
     * @param feeLimit                   The fee limit of the transaction.
     * @param consumeUserResourcePercent The % of energy an user will consume when calling a method of the contract.
     *                                   Set it to 0 to consume first the owner resources.
     * @param callValue                  The constructor method call value. Set it to 0 unless the constructor is Payable.
     * @param libraries                  The libraries used for the mart contract, including the name and the address of the libraries.
     * @return The transaction sent to the blockchain and the address of the smart contract.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     * @throws EncodingException    If the constructor call is invalid.
     */
    public Pair<TronTransaction, TronAddress> deploySmartContract(TronWallet owner,
                                                                  String contractName,
                                                                  String ABI,
                                                                  String byteCode,
                                                                  TriggerContractDataBuilder constructorCall,
                                                                  TronCurrency feeLimit,
                                                                  long consumeUserResourcePercent,
                                                                  TronCurrency callValue,
                                                                  Map<String, TronAddress> libraries)
            throws GRPCException, TransactionException, EncodingException {
        return this.deploySmartContract(owner, contractName, ABI, byteCode, constructorCall, feeLimit,
                consumeUserResourcePercent, callValue, 0, 0, Long.MAX_VALUE, libraries);
    }

    /**
     * Deploys an smart contract on the Tron network.
     *
     * @param owner                      Sender of the transaction.
     * @param contractName               The contract name (account name for the contract address)
     * @param ABI                        The ABI of the smart contract, in JSON format (must be an array).
     * @param byteCode                   the byte code of the smart contract in hexadecimal.
     * @param constructorCall            The constructor method call.
     * @param feeLimit                   The fee limit of the transaction.
     * @param consumeUserResourcePercent The % of energy an user will consume when calling a method of the contract.
     *                                   Set it to 0 to consume first the owner resources.
     * @param originEnergyLimit          The energy limit that can be consumed from the origin.
     * @param callValue                  The constructor method call value. Set it to 0 unless the constructor is Payable.
     * @param callValueTokenId           The token to send as a token call value
     * @param callValueToken             The number of tokens to send as call value
     * @param libraries                  The libraries used for the mart contract, including the name and the address of the libraries.
     * @return The transaction sent to the blockchain and the address of the smart contract.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     * @throws EncodingException    If the constructor call is invalid.
     */
    public Pair<TronTransaction, TronAddress> deploySmartContract(TronWallet owner,
                                                                  String contractName,
                                                                  String ABI,
                                                                  String byteCode,
                                                                  TriggerContractDataBuilder constructorCall,
                                                                  TronCurrency feeLimit,
                                                                  long consumeUserResourcePercent,
                                                                  TronCurrency callValue,
                                                                  long callValueTokenId,
                                                                  long callValueToken,
                                                                  long originEnergyLimit,
                                                                  Map<String, TronAddress> libraries)
            throws GRPCException, TransactionException, EncodingException {

        byteCode += Hex.toHexString(constructorCall.buildConstructorParams());

        Contract.CreateSmartContract contractDeployContract = TronContracts.createContractDeployContract(contractName,
                owner.getAddress().getBytes(),
                ABI, byteCode, callValue.getSUN(),
                consumeUserResourcePercent,
                originEnergyLimit,
                callValueTokenId, callValueToken, libraries);

        GrpcAPI.TransactionExtention transactionExtention = null;

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                transactionExtention = client.deployContract(contractDeployContract);
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        if (transactionExtention == null) {
            throw exception;
        }


        if (transactionExtention == null || !transactionExtention.getResult().getResult()) {
            if (transactionExtention != null) {
                throw new TransactionException(transactionExtention.getResult().getCode(),
                        transactionExtention.getResult().getMessage().toStringUtf8());
            } else {
                throw new TransactionException(null, "Transaction failed");
            }
        }

        GrpcAPI.TransactionExtention.Builder texBuilder = GrpcAPI.TransactionExtention.newBuilder();
        Protocol.Transaction.Builder transBuilder = Protocol.Transaction.newBuilder();
        Protocol.Transaction.raw.Builder rawBuilder = transactionExtention.getTransaction().getRawData()
                .toBuilder();
        rawBuilder.setFeeLimit(feeLimit.getSUN());
        transBuilder.setRawData(rawBuilder);
        for (int i = 0; i < transactionExtention.getTransaction().getSignatureCount(); i++) {
            ByteString s = transactionExtention.getTransaction().getSignature(i);
            transBuilder.setSignature(i, s);
        }
        for (int i = 0; i < transactionExtention.getTransaction().getRetCount(); i++) {
            Protocol.Transaction.Result r = transactionExtention.getTransaction().getRet(i);
            transBuilder.setRet(i, r);
        }
        texBuilder.setTransaction(transBuilder);
        texBuilder.setResult(transactionExtention.getResult());
        texBuilder.setTxid(transactionExtention.getTxid());
        transactionExtention = texBuilder.build();

        byte[] contractAddress = TronSmartContracts.generateContractAddress(owner.getAddress().getBytes(),
                transactionExtention.getTransaction());
        TronTransaction tx;

        for (GrpcClient client : clients) {
            try {
                tx = TransactionUtils.processTransactionExtention(client, transactionExtention, owner.getPrivateKey());
                return new Pair<>(tx, new TronAddress(contractAddress));
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Triggers a smart contract, calling one method.
     *
     * @param owner           Sender of the transaction.
     * @param contractAddress The address of the smart contract you want to trigger.
     * @param call            The smart contract method call.
     * @param feeLimit        The fee limit of the transaction.
     * @param callValue       the method call value.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     * @throws EncodingException    If the method call is invalid.
     */
    public TriggerContractResult triggerSmartContract(TronWallet owner,
                                                      TronAddress contractAddress,
                                                      TriggerContractDataBuilder call,
                                                      TronCurrency feeLimit,
                                                      TronCurrency callValue)
            throws GRPCException, TransactionException, EncodingException {
        return this.triggerSmartContract(owner, contractAddress, call, feeLimit, callValue, 0, 0);
    }

    /**
     * Triggers a smart contract, calling one method.
     *
     * @param owner            Sender of the transaction.
     * @param contractAddress  The address of the smart contract you want to trigger.
     * @param call             The smart contract method call.
     * @param feeLimit         The fee limit of the transaction.
     * @param callValue        the method call value.
     * @param callValueTokenId The token to send as a token call value
     * @param callValueToken   The number of tokens to send as call value
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     * @throws EncodingException    If the method call is invalid.
     */
    public TriggerContractResult triggerSmartContract(TronWallet owner,
                                                      TronAddress contractAddress,
                                                      TriggerContractDataBuilder call,
                                                      TronCurrency feeLimit,
                                                      TronCurrency callValue,
                                                      long callValueTokenId,
                                                      long callValueToken)
            throws GRPCException, TransactionException, EncodingException {
        Contract.TriggerSmartContract triggerContract = TronContracts.triggerCallContract(owner.getAddress().getBytes(),
                contractAddress.getBytes(),
                callValue.getSUN(),
                callValueTokenId,
                callValueToken,
                call.build());
        GrpcAPI.TransactionExtention transactionExtention = null;

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                transactionExtention = client.triggerContract(triggerContract);
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        if (transactionExtention == null) {
            throw exception;
        }

        if (transactionExtention == null || !transactionExtention.getResult().getResult()) {
            throw new TransactionException(transactionExtention.getResult().getCode(),
                    transactionExtention.getResult().getMessage().toStringUtf8());
        }

        Protocol.Transaction transaction = transactionExtention.getTransaction();
        if (transaction.getRetCount() != 0 &&
                transactionExtention.getConstantResult(0) != null &&
                transactionExtention.getResult() != null) {
            byte[] result = transactionExtention.getConstantResult(0).toByteArray();
            return new TriggerContractResult(result);
        }

        GrpcAPI.TransactionExtention.Builder texBuilder = GrpcAPI.TransactionExtention.newBuilder();
        Protocol.Transaction.Builder transBuilder = Protocol.Transaction.newBuilder();
        Protocol.Transaction.raw.Builder rawBuilder = transactionExtention.getTransaction().getRawData()
                .toBuilder();
        rawBuilder.setFeeLimit(feeLimit.getSUN());
        transBuilder.setRawData(rawBuilder);
        for (int i = 0; i < transactionExtention.getTransaction().getSignatureCount(); i++) {
            ByteString s = transactionExtention.getTransaction().getSignature(i);
            transBuilder.setSignature(i, s);
        }
        for (int i = 0; i < transactionExtention.getTransaction().getRetCount(); i++) {
            Protocol.Transaction.Result r = transactionExtention.getTransaction().getRet(i);
            transBuilder.setRet(i, r);
        }
        texBuilder.setTransaction(transBuilder);
        texBuilder.setResult(transactionExtention.getResult());
        texBuilder.setTxid(transactionExtention.getTxid());
        transactionExtention = texBuilder.build();

        TronTransaction tx;

        for (GrpcClient client : clients) {
            try {
                tx = TransactionUtils.processTransactionExtention(client, transactionExtention, owner.getPrivateKey());
                return new TriggerContractResult(tx);
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Updates the settings of a smart contract.
     *
     * @param owner                      Sender of the transaction.
     * @param contractAddress            the address of the contract.
     * @param consumeUserResourcePercent the new value for "consumeUserResourcePercent".
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction updateSmartContractSettings(TronWallet owner, TronAddress contractAddress,
                                                       long consumeUserResourcePercent)
            throws GRPCException, TransactionException {
        Contract.UpdateSettingContract contract = TronContracts
                .createUpdateSettingContract(owner.getAddress().getBytes(),
                        contractAddress.getBytes(), consumeUserResourcePercent);

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.updateSetting(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    /**
     * Updates the energy limit settings of a smart contract.
     *
     * @param owner             Sender of the transaction.
     * @param contractAddress   the address of the contract.
     * @param originEnergyLimit The new value for origin-energy-limit.
     * @return The transaction sent to the blockchain.
     * @throws GRPCException        If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException If The transaction fails to be sent.
     */
    public TronTransaction updateSmartContractEnergyLimit(TronWallet owner, TronAddress contractAddress,
                                                          long originEnergyLimit)
            throws GRPCException, TransactionException {
        Contract.UpdateEnergyLimitContract contract = Contract.UpdateEnergyLimitContract.newBuilder()
                .setOwnerAddress(ByteString.copyFrom(owner.getAddress().getBytes()))
                .setContractAddress(ByteString.copyFrom(contractAddress.getBytes()))
                .setOriginEnergyLimit(originEnergyLimit).build();

        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                GrpcAPI.TransactionExtention ex = client.updateEnergyLimit(contract);
                return TransactionUtils.processTransactionExtention(client, ex, owner.getPrivateKey());
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }


    /**
     * waits until a transaction is confirmed by the blockchain.
     *
     * @param txId       The transaction identifier.
     * @param maxRetries The max number of blocks to wait for.
     * @return The transaction information.
     * @throws GRPCException                If an error occurs in the GRPC protocol, generally connection problems.
     * @throws InterruptedException         If the thread is interrupted.
     * @throws ConfirmationTimeoutException The the transaction is not confirmed and max retries is reached.
     */
    public TronTransactionInformation waitForTransactionConfirmation(HashIdentifier txId, int maxRetries)
            throws GRPCException, InterruptedException, ConfirmationTimeoutException {
        int retries = 0;

        while (retries < maxRetries) {
            TronTransactionInformation txInfo = this.getTransactionInformation(txId);

            if (txInfo.getBlockNumber() > 0) {
                return txInfo;
            } else {
                // Not confirmed yet, wait for netct block
                Thread.sleep(3 * 1000);
                retries++;
            }
        }

        throw new ConfirmationTimeoutException("Max retries reached. Transaction not confirmed yet.");
    }

    /**
     * Broadcasts transaction (already signed).
     *
     * @param rawData    The transaction raw data
     * @param signatures The transaction signatures
     * @throws InvalidProtocolBufferException If the raw data is invalid.
     * @throws GRPCException                  If an error occurs in the GRPC protocol, generally connection problems.
     * @throws TransactionException           If the transaction fails to be broadcasted.
     */
    public void broadcastTransaction(byte[] rawData, List<byte[]> signatures) throws InvalidProtocolBufferException, GRPCException, TransactionException {
        Protocol.Transaction.raw raw = Protocol.Transaction.raw.parseFrom(rawData);
        Protocol.Transaction.Builder txBuilder = Protocol.Transaction.newBuilder().setRawData(raw);
        for (byte[] signature : signatures) {
            txBuilder.addSignature(ByteString.copyFrom(signature));
        }
        Protocol.Transaction tx = txBuilder.build();
        GRPCException exception = new GRPCException("Could not perform the action");
        for (GrpcClient client : clients) {
            try {
                client.broadcastTransaction(tx);
                return;
            } catch (TransactionException ex) {
                throw ex;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }

        throw exception;
    }

    public List<ChainParameter> getChainParameters() throws GRPCException {
        List<ChainParameter> result = new ArrayList<>();
        GRPCException exception = new GRPCException("Could not perform the action");
        Protocol.ChainParameters params = null;
        for (GrpcClient client : clients) {
            try {
                params = this.clients.get(0).getChainParameters().get();
                break;
            } catch (Exception ex) {
                exception = new GRPCException(ex.getMessage());
            }
        }
        if (params != null) {
            int i = 0;
            for (Protocol.ChainParameters.ChainParameter p : params.getChainParameterList()) {
                result.add(new ChainParameter(i, p));
                i++;
            }

            return result;
        }

        throw exception;
    }

    /**
     * Closes all connections.
     */
    public void shutdown() {
        for (GrpcClient client : clients) {
            try {
                client.shutdown();
            } catch (InterruptedException ex) {
            }
        }
    }
}
