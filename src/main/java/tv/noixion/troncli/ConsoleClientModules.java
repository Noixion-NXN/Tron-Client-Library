package tv.noixion.troncli;

import tv.noixion.troncli.exceptions.ConfirmationTimeoutException;
import tv.noixion.troncli.exceptions.GRPCException;
import tv.noixion.troncli.models.*;
import tv.noixion.troncli.models.contracts.TriggerSmartContractContract;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.util.Pair;
import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;
import org.spongycastle.util.encoders.Hex;
import org.tron.core.exception.CipherException;
import org.tron.keystore.WalletFile;
import org.tron.keystore.WalletUtils;
import org.tron.protos.Protocol;
import tv.noixion.troncli.models.*;
import tv.noixion.troncli.utils.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.*;

class ConsoleClientModules {

    public static int ERROR_STATUS = 1;
    public static int PARAMS_ERROR_STATUS = 2;

    public static void createPrivateKey(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = new TronWallet();
        if (options.containsKey("verbose")) {
            System.out.println("Address:     " + wallet.getAddress().toString());
            System.out.println("Private key: " + wallet.getPrivateKey().toString());
        } else {
            JsonObject object = new JsonObject();
            object.addProperty("address", wallet.getAddress().toString());
            object.addProperty("privateKey", wallet.getPrivateKey().toString());
            Gson gson = new GsonBuilder().create();
            System.out.println(gson.toJson(object));
        }
    }

    public static void createWallet(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet;
        String password;
        if (params.containsKey("password")) {
            password = (String) params.get("password");
        } else {
            System.out.println("Input a password for the wallet.");
            password = ConsoleUtils.readPassword("Password: ");
            if (!ConsoleUtils.readPassword("Password (again): ").equals(password)) {
                System.err.println("Error: Passwords do not match.");
                System.exit(PARAMS_ERROR_STATUS);
                return;
            }
        }
        if (params.containsKey("PK")) {
            wallet = new TronWallet((TronPrivateKey) params.get("PK"));
        } else {
            wallet = new TronWallet();
        }
        String name;
        try {
            name = wallet.saveToFile(new File(System.getProperty("user.dir")), password);
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(ERROR_STATUS);
            return;
        }
        System.out.println("Wallet created: " + name);
    }

    public static void getAddress(Map<String, Object> options, Map<String, Object> params) {
        if (params.containsKey("PK")) {
            TronWallet wallet = new TronWallet((TronPrivateKey) params.get("PK"));

            if (options.containsKey("verbose")) {
                System.out.println("Address: " + wallet.getAddress().toString());
            } else {
                JsonObject object = new JsonObject();
                object.addProperty("address", wallet.getAddress().toBase58());
                Gson gson = new GsonBuilder().create();
                System.out.println(gson.toJson(object));
            }
        } else if (params.containsKey("wallet")) {
            WalletFile wf;

            try {
                wf = WalletUtils.loadWalletFile((File) params.get("wallet"));
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            if (options.containsKey("verbose")) {
                System.out.println("Address: " + wf.getAddress());
            } else {
                JsonObject object = new JsonObject();
                object.addProperty("address", wf.getAddress());
                Gson gson = new GsonBuilder().create();
                System.out.println(gson.toJson(object));
            }
        } else {
            System.err.println("You must specify a private key or a wallet file");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }
    }

    public static void getKeyFromWallet(Map<String, Object> options, Map<String, Object> params) {
        if (params.containsKey("wallet")) {
            TronWallet wallet;
            String password;
            if (params.containsKey("password")) {
                password = (String) params.get("password");
            } else {
                System.out.println("Input password for the wallet.");
                password = ConsoleUtils.readPassword("Password: ");
            }
            try {
                wallet = new TronWallet((File) params.get("wallet"), password);
            } catch (CipherException ex) {
                System.err.println("Invalid password provided or invalid wallet file.");
                System.exit(ERROR_STATUS);
                return;
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            if (options.containsKey("verbose")) {
                System.out.println("Private key: " + wallet.getPrivateKey().toString());
            } else {
                JsonObject object = new JsonObject();
                object.addProperty("privateKey", wallet.getPrivateKey().toString());
                Gson gson = new GsonBuilder().create();
                System.out.println(gson.toJson(object));
            }
        } else {
            System.err.println("You must specify a wallet file");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }
    }

    public static void queryAccount(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        if (!params.containsKey("address")) {
            System.err.println("Address parameter is required.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }

        TronClient client = getClient(options, false);

        TronAccount account;

        account = client.getAccountByAddress((TronAddress) params.get("address"));

        if (options.containsKey("verbose")) {
            account.print();
        } else {
            Gson gson = new GsonBuilder().create();
            System.out.println(gson.toJson(account.toJson()));
        }

    }

    public static void queryAccountResource(Map<String, Object> options, Map<String, Object> params)
            throws GRPCException {
        if (!params.containsKey("address")) {
            System.err.println("Address parameter is required.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }
        TronClient client = getClient(options, false);
        TronAccountResource account;
        account = client.getAccountResources((TronAddress) params.get("address"));

        if (options.containsKey("verbose")) {
            account.print();
        } else {
            Gson gson = new GsonBuilder().create();
            System.out.println(gson.toJson(account.toJson()));
        }
    }

    public static void getBlock(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        TronClient client = getClient(options, false);
        TronBlock block;
        if (params.containsKey("num")) {
            block = client.getBlock((Long) params.get("num"));
        } else if (params.containsKey("hash")) {
            block = client.getBlock((HashIdentifier) params.get("hash"));
        } else {
            System.err.println("You must specify the block number or its identifier. Use --num or --id.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }

        if (options.containsKey("verbose")) {
            block.print("", !params.containsKey("raw") ? client : null);
        } else {
            Gson gson = new GsonBuilder().create();
            System.out.println(gson.toJson(block.toJson()));
        }

    }

    public static void getTransaction(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        if (!params.containsKey("hash")) {
            System.err.println("Transaction id is required. Use the -id parameter.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }

        TronClient client = getClient(options, false);

        TronTransaction tx = client.getTransaction((HashIdentifier) params.get("hash"));

        if (options.containsKey("verbose")) {
            tx.print("", !params.containsKey("raw") ? client : null);
        } else {
            Gson gson = new GsonBuilder().create();
            System.out.println(gson.toJson(tx.toJson()));
        }

    }

    public static void getTransactionInformation(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        if (!params.containsKey("hash")) {
            System.err.println("Transaction id is required. Use the --id parameter.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }

        TronClient client = getClient(options, false);

        TronTransactionInformation tx = client.getTransactionInformation((HashIdentifier) params.get("hash"));

        if (options.containsKey("verbose")) {
            tx.print("", !params.containsKey("raw") ? client : null);
        } else {
            Gson gson = new GsonBuilder().create();
            System.out.println(gson.toJson(tx.toJson()));
        }

    }

    public static void getAsset(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        TronClient client = getClient(options, false);
        if (params.containsKey("address")) {
            List<TronAssetIssue> assets = client.getAssetIssueByOwnerAddress((TronAddress) params.get("address"));

            if (assets.isEmpty()) {
                System.out.println("No assets found for " + params.get("address").toString());
            } else {
                System.out.println("List of assets issued by " + params.get("address").toString() + ":");

                for (TronAssetIssue asset : assets) {
                    System.out.println("    ASSET----------------------------------");
                    asset.print("    ");
                    System.out.println("    ---------------------------------------");
                    System.out.println();
                }
            }
        } else if (params.containsKey("asset")) {
            TronAssetIssue assetIssue = client.getAssetIssueByName(params.get("asset").toString());
            if (assetIssue == null) {
                System.out.println("Asset not found: " + params.get("asset").toString());
            } else {
                assetIssue.print("");
            }
        } else {
            System.err.println("This action requires the asset name or the owner address. Use --asset or --address.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }
    }

    public static void getNodes(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        TronClient client = getClient(options, false);

        List<TronNode> nodes = client.listNodes();

        if (nodes.isEmpty()) {
            System.out.println("No nodes found.");
        } else {
            System.out.println("Known nodes:");
            for (TronNode node : nodes) {
                System.out.println("    " + node.getHostname());
            }
        }

    }

    public static void getWitnesses(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        TronClient client = getClient(options, false);

        List<TronWitness> witnesses = client.listWitnesses();

        if (witnesses.isEmpty()) {
            System.out.println("No witnesses found.");
        } else {
            for (TronWitness w : witnesses) {
                System.out.println("WITNESS--------------------------------------------");
                w.print("");
                System.out.println("---------------------------------------------------");
                System.out.println();
            }
        }

    }

    public static void getAssets(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        TronClient client = getClient(options, false);

        List<TronAssetIssue> assets = client.listAssetIssue();

        if (assets.isEmpty()) {
            System.out.println("No assets found.");
        } else {
            for (TronAssetIssue asset : assets) {
                System.out.println("ASSET----------------------------------");
                asset.print("");
                System.out.println("---------------------------------------");
                System.out.println();
            }
        }
    }

    public static void getTransactionsFromThis(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        TronClient client = getClient(options, true);

        if (params.containsKey("address")) {
            TronAddress addr = (TronAddress) params.get("address");
            int offset = 0;
            int limit = 20;

            if (params.containsKey("offset")) {
                offset = ((Long) params.get("offset")).intValue();
            }

            if (params.containsKey("limit")) {
                limit = ((Long) params.get("limit")).intValue();
            }

            List<TronTransaction> txs = client.getTransactionsFrom(addr, offset, limit);

            if (txs.isEmpty()) {
                System.out.println("No transactions found.");
            } else {
                for (TronTransaction tx : txs) {
                    System.out.println("TRANSACTION----------------------------------");
                    tx.print("", !params.containsKey("raw") ? client : null);
                    System.out.println("---------------------------------------------");
                    System.out.println();
                }
            }
        } else {
            System.err.println("Required parameters: --address");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void getTransactionsToThis(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        TronClient client = getClient(options, true);

        if (params.containsKey("address")) {
            TronAddress addr = (TronAddress) params.get("address");
            int offset = 0;
            int limit = 20;

            if (params.containsKey("offset")) {
                offset = ((Long) params.get("offset")).intValue();
            }

            if (params.containsKey("limit")) {
                limit = ((Long) params.get("limit")).intValue();
            }

            List<TronTransaction> txs = client.getTransactionsTo(addr, offset, limit);

            if (txs.isEmpty()) {
                System.out.println("No transactions found.");
            } else {
                for (TronTransaction tx : txs) {
                    System.out.println("TRANSACTION----------------------------------");
                    tx.print("", !params.containsKey("raw") ? client : null);
                    System.out.println("---------------------------------------------");
                    System.out.println();
                }
            }
        } else {
            System.err.println("Required parameters: --address");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    private static TronWallet getCredentials(Map<String, Object> params) {
        if (params.containsKey("PK")) {
            return new TronWallet((TronPrivateKey) params.get("PK"));
        } else if (params.containsKey("wallet")) {
            String password;
            if (params.containsKey("password")) {
                password = (String) params.get("password");
            } else {
                System.out.println("Input password for the wallet.");
                password = ConsoleUtils.readPassword("Password: ");
            }
            try {
                return new TronWallet((File) params.get("wallet"), password);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return null;
            }
        } else {
            System.err.println("You must specify a private key or a wallet file");
            System.exit(PARAMS_ERROR_STATUS);
            return null;
        }
    }

    public static void getProposalById(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        TronClient client = getClient(options, false);

        if (params.containsKey("num")) {
            long id = (Long) params.get("num");

            TronProposal p = client.getProposalById(id);

            if (p != null) {
                p.print("");
            } else {
                System.out.println("Proposal not found.");
            }
        } else {
            System.err.println("You must specify the proposal id / number with the --num option.");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void listProposals(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        TronClient client = getClient(options, false);

        List<TronProposal> proposals = client.listProposals();

        if (proposals.isEmpty()) {
            System.out.println("No assests found.");
        } else {
            for (TronProposal proposal : proposals) {
                System.out.println("PROPOSAL----------------------------------");
                proposal.print("");
                System.out.println("------------------------------------------");
                System.out.println();
            }
        }
    }

    private static TronClient getClient(Map<String, Object> options, boolean solidityRequired) {
        if (options.containsKey("full")) {
            TronClient client;

            if (options.containsKey("solidity")) {
                client = new TronClient((TronNode) options.get("full"), (TronNode) options.get("solidity"));
            } else if (!solidityRequired) {
                client = new TronClient((TronNode) options.get("full"));
            } else {
                System.err.println("Solidity node required for this action. Set with --solidity option.");
                System.exit(PARAMS_ERROR_STATUS);
                return null;
            }

            return client;
        } else {
            System.err.println("Full node required for this action. Set with --full option.");
            System.exit(PARAMS_ERROR_STATUS);
            return null;
        }
    }

    public static void createAccount(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("address")) {
            TronAddress address = (TronAddress) params.get("address");
            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to create the account " + address.toString() + ".");
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.createAccount(wallet, address);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("Required parameters: --address");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void updateAccount(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("name")) {
            String name = params.get("name").toString();
            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to set your account name to " + name + ".");
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.updateAccount(wallet, name);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("Required parameters: --name");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void setAccountId(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("name")) {
            String name = params.get("name").toString();
            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to set your account id to " + name + ".");
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.setAccountId(wallet, name);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("Required parameters: --name");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void transferCoins(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("amount") && params.containsKey("address")) {
            TronAddress address = (TronAddress) params.get("address");
            TronCurrency amount = TronCurrency.sun((Long) params.get("amount"));
            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to transfer " + String.format("%.0f TRX", amount.getTRX()) + " to " + address.toString() + ".");
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.transfer(wallet, address, amount);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("Required parameters: --address, --trx");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void transferAsset(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("amount") && params.containsKey("address") && params.containsKey("asset")) {
            TronAddress address = (TronAddress) params.get("address");
            String asset = params.get("asset").toString();
            long amount = (Long) params.get("amount");
            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to transfer " + amount + " " + asset + " to " + address.toString() + ".");
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.transferAsset(wallet, address, asset, amount);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("Required parameters: --address, --amount, --asset");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void participateAsset(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("amount") && params.containsKey("asset")) {
            TronAddress address = wallet.getAddress();
            if (params.containsKey("address")) {
                address = (TronAddress) params.get("address");
            }

            String asset = params.get("asset").toString();
            long amount = (Long) params.get("amount");
            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to buy " + amount + " " + asset);
                System.out.println("The tokens will be transferred to " + address.toString());
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.participateAsset(wallet, address, asset, amount);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("Required parameters: --amount, --asset");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void assetIssue(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("name") && params.containsKey("abbr") && params.containsKey("totalSupply")
                && params.containsKey("conversion") && params.containsKey("url") && params.containsKey("desc")
                && params.containsKey("start") && params.containsKey("end")) {

            String name = params.get("name").toString();
            String abbr = params.get("abbr").toString();
            long supply = (Long) params.get("totalSupply");
            TRXAssetConversion conv = (TRXAssetConversion) params.get("conversion");
            String url = params.get("url").toString();
            String description = params.get("desc").toString();
            Date start = (Date) params.get("start");
            Date end = (Date) params.get("end");
            int decimals = 0;
            if (params.containsKey("decimals")) {
                decimals = (Integer) params.get("decimals");
            }
            List<TronAssetIssue.FrozenSupply> frozen = new ArrayList<>();

            if (params.containsKey("frozen")) {
                frozen = (List<TronAssetIssue.FrozenSupply>) params.get("frozen");
            }

            long free_net_limit = 0;
            if (params.containsKey("fnl")) {
                free_net_limit = (Long) params.get("fnl");
            }

            long public_net_limit = 0;
            if (params.containsKey("pnl")) {
                public_net_limit = (Long) params.get("pnl");
            }

            if (!options.containsKey("yes")) {
                System.out.println("You are going to create the following asset: ");

                String indent = "    ";

                System.out.println(indent + "Name:         " + name);
                System.out.println(indent + "Abbreviation: " + abbr);
                System.out.println(indent + "URL: " + url);
                System.out.println(indent + "Description: " + description);
                System.out.println(indent + "Conversion: " + conv.getNum() + " " + name + " = "
                        + conv.getTrxNum() + " TRX");
                System.out.println(indent + "Total Supply: " + supply);
                System.out.println(indent + "Decimals: " + decimals);
                System.out.println(indent + "Start Date: " + start.toString());
                System.out.println(indent + "End Date: " + end.toString());
                System.out.println(indent + "Free net limit: " + free_net_limit);
                System.out.println(indent + "Public net limit: " + public_net_limit);
                if (!frozen.isEmpty()) {
                    System.out.println("Frozen supply: ");
                    for (TronAssetIssue.FrozenSupply f : frozen) {
                        System.out.println("  " + f.getAmount() + " " + name + " frozen for " + f.getDays() + " days.");
                    }
                }

                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            TronTransaction tx;

            try {
                tx = client.assetIssue(wallet, name, abbr, supply, conv, start, end, description,
                        url, decimals, free_net_limit, public_net_limit, frozen);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());

        } else {
            System.err.println("Required parameters: Name, Abbreviation, Total supply, Conversion, URL, Description, Start, End");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void updateAsset(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("url") && params.containsKey("desc")
                && params.containsKey("fnl") && params.containsKey("pnl")) {

            String url = params.get("url").toString();
            String description = params.get("desc").toString();
            long free_net_limit = (Long) params.get("fnl");
            long public_net_limit = (Long) params.get("pnl");

            if (!options.containsKey("yes")) {
                System.out.println("You are going to update your asset with the following parameters: ");

                String indent = "    ";

                System.out.println(indent + "URL: " + url);
                System.out.println(indent + "Description: " + description);
                System.out.println(indent + "Free net limit: " + free_net_limit);
                System.out.println(indent + "Public net limit: " + public_net_limit);

                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            TronTransaction tx;

            try {
                tx = client.updateAsset(wallet, description, url, free_net_limit, public_net_limit);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());

        } else {
            System.err.println("Required parameters: URL, Description, Free net limit, Public net limit");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void unfreezeAsset(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);


        if (!options.containsKey("yes")) {
            System.out.println("You are going to unfreeze your asset frozen supply.");

            if (!ConsoleUtils.askConfirmation()) {
                System.out.println("Operation cancelled by user!");
                return;
            }
        }

        TronTransaction tx;

        try {
            tx = client.unfreezeAsset(wallet);
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(ERROR_STATUS);
            return;
        }

        System.out.println("Transaction: " + tx.getId().toString());

        System.out.println();

        waitForTransaction(client, tx.getId());
    }


    public static void deployContract(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("name") && params.containsKey("abi") && params.containsKey("bytecode")
                && params.containsKey("constructor")) {
            String name = params.get("name").toString();
            String abi = params.get("abi").toString();
            String bytecode = params.get("bytecode").toString();
            String constructor = params.get("constructor").toString();
            List<String> callParams = new ArrayList<>();
            if (params.containsKey("paramsList")) {
                callParams = (List<String>) params.get("paramsList");
            }
            TronCurrency feeLimit = TronCurrency.sun(0L);
            if (params.containsKey("feeLimit")) {
                feeLimit = TronCurrency.trx((Double) params.get("feeLimit"));
            }
            TronCurrency callValue = TronCurrency.sun(0L);
            if (params.containsKey("callValue")) {
                callValue = TronCurrency.trx((Double) params.get("callValue"));
            }
            long consumeUser = 0L;
            if (params.containsKey("consumeUser")) {
                consumeUser = (Long) params.get("consumeUser");
            }
            long tokenId = 0;
            if (params.containsKey("token-id")) {
                tokenId = (Long) params.get("token-id");
            }
            long tokenCallValue = 0;
            if (params.containsKey("token-value")) {
                tokenCallValue = (Long) params.get("token-value");
            }
            long originEnergyLimit = Long.MAX_VALUE;
            if (params.containsKey("origin-energy-limit")) {
                originEnergyLimit = (Integer) params.get("origin-energy-limit");
            }

            Map<String, TronAddress> libs = new TreeMap<>();
            if (params.containsKey("libraries")) {
                libs = (Map<String, TronAddress>) params.get("libraries");
            }

            if (!options.containsKey("yes")) {
                System.out.println("You are going to deploy the following contract:");
                System.out.println("  Name: " + name);
                System.out.println("  ABI: " + abi);
                System.out.println("  Bytecode: " + bytecode);
                System.out.println("  Libraries:");
                for (String lib : libs.keySet()) {
                    System.out.println("    " + lib + ": " + libs.get(lib).toString());
                }
                System.out.println("  Constructor: " + constructor);
                System.out.println("  Constructor paramsList: " + ConsoleUtils.listToString(callParams));
                System.out.println("  Consume user: " + consumeUser + "%");
                System.out.println("  Origin energy limit: " + originEnergyLimit);
                System.out.println("  Fee limit: " + String.format("%.0f TRX", feeLimit.getTRX()));
                System.out.println("  Call value: " + String.format("%.0f TRX", callValue.getTRX()));
                if (tokenCallValue > 0) {
                    System.out.println("  Token id " + tokenId);
                    System.out.println("  Token call value: " + tokenCallValue);
                }
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            Pair<TronTransaction, TronAddress> result;

            try {
                result = client.deploySmartContract(wallet, name, abi, bytecode,
                        new TriggerContractDataBuilder(constructor).paramsList(callParams),
                        feeLimit, consumeUser, callValue,
                        tokenId, tokenCallValue, originEnergyLimit, libs);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Contract address: " + result.getValue().toString());
            System.out.println("Transaction: " + result.getKey().getId().toString());

            System.out.println();

            TronTransactionInformation info;

            System.out.println("Waiting for confirmation...");

            try {
                info = client.waitForTransactionConfirmation(result.getKey().getId(), 20);
            } catch (ConfirmationTimeoutException ex) {
                System.err.println("Timeout! Transaction not confirmed yet.");
                System.err.println("Check the transaction later, it may be confirmed.");
                System.exit(ERROR_STATUS);
                return;
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            if (info.getResult() == TronTransaction.Code.SUCCESS) {
                System.out.println("Transaction confirmed!");
                System.out.println("The contract has been deployed. The transaction can be found on block "
                        + info.getBlockNumber());
                System.out.println("The transaction consumed " + info.getTotalEnergyUsage() + " points of energy.");
                System.out.println("Your contract address is " + info.getContractAddress());
            } else {
                System.err.println("Transaction failed!");
                System.err.println("Details: " + info.getResultMessage());
                System.exit(ERROR_STATUS);
            }
        } else {
            System.err.println("Required parameters: Name, ABI, ByteCode, Constructor.");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void freezeBalanceForEnergy(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("amount")) {
            TronCurrency amount = TronCurrency.sun((Long) params.get("amount"));
            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to freeze " + String.format("%.0f TRX", amount.getTRX()) + " for 3 days in exchange of energy.");
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.freezeBalance(wallet, amount, 3, TronResource.ENERGY);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("You must specify the amount to freeze, use the --trx or --sun options.");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void freezeBalanceForBandwidth(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("amount")) {
            TronCurrency amount = TronCurrency.sun((Long) params.get("amount"));
            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to freeze " + String.format("%.0f TRX", amount.getTRX()) + " for 3 days in exchange of bandwidth.");
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.freezeBalance(wallet, amount, 3, TronResource.BANDWIDTH);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("You must specify the amount to freeze, use the --trx or --sun options.");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void unfreezeBalanceForEnergy(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);
        TronTransaction tx;

        if (!options.containsKey("yes")) {
            System.out.println("You are going to unfreeze the balance you have frozen for energy.");
            if (!ConsoleUtils.askConfirmation()) {
                System.out.println("Operation cancelled by user!");
                return;
            }
        }

        try {
            tx = client.unfreezeBalance(wallet, TronResource.ENERGY);
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(ERROR_STATUS);
            return;
        }

        System.out.println("Transaction: " + tx.getId().toString());

        System.out.println();

        waitForTransaction(client, tx.getId());

    }

    public static void unfreezeBalanceForBandwidth(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);
        TronTransaction tx;

        if (!options.containsKey("yes")) {
            System.out.println("You are going to unfreeze the balance you have frozen for bandwidth.");
            if (!ConsoleUtils.askConfirmation()) {
                System.out.println("Operation cancelled by user!");
                return;
            }
        }

        try {
            tx = client.unfreezeBalance(wallet, TronResource.BANDWIDTH);
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(ERROR_STATUS);
            return;
        }

        System.out.println("Transaction: " + tx.getId().toString());

        System.out.println();

        waitForTransaction(client, tx.getId());
    }

    public static void createWitness(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("url")) {
            String url = params.get("url").toString();
            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to turn your account into a witness. URL: " + url);
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.createWitness(wallet, url);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("You must specify the URL with the --url option.");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void updateSmartContractSettings(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("address") && params.containsKey("consumeUser")) {
            TronAddress address = (TronAddress) params.get("address");
            long cu = (Long) params.get("consumeUser");

            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to change the user consuption of " + address.toString() + " to " + cu + " %.");
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.updateSmartContractSettings(wallet, address, cu);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("You must specify the contract address (--address) and the use consumption (--consume-user).");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void updateSmartContractEnergyLimit(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("address") && params.containsKey("origin-energy-limit")) {
            TronAddress address = (TronAddress) params.get("address");
            long el = (Long) params.get("origin-energy-limit");

            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to change origin energy limit of " + address.toString() + " to " + el + ".");
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.updateSmartContractEnergyLimit(wallet, address, el);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("You must specify the contract address (--address) and the origin energy limit (--origin-energy-limit).");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }


    public static void updateWitness(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("url")) {
            String url = params.get("url").toString();
            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to change your witness URL to " + url);
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.updateWitness(wallet, url);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("You must specify the URL with the --url option.");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void voteWitnesses(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("votes")) {
            Map<TronAddress, Long> votes = (Map<TronAddress, Long>) params.get("votes");
            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to vote for the following witnesses:");
                for (TronAddress addr : votes.keySet()) {
                    System.out.println("    Address: " + addr.toString() + " / Count: " + votes.get(addr));
                }
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.voteWitness(wallet, votes);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("You must specify votes with the --vote option.");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void getContract(Map<String, Object> options, Map<String, Object> params)
            throws GRPCException {
        if (!params.containsKey("address")) {
            System.err.println("Address parameter is required.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }
        TronClient client = getClient(options, false);
        TronSmartContract contract;
        contract = client.getContract((TronAddress) params.get("address"));

        if (options.containsKey("verbose")) {
            contract.print("");
        } else {
            Gson gson = new GsonBuilder().create();
            System.out.println(gson.toJson(contract.toJson()));
        }
    }

    public static void getContractMethods(Map<String, Object> options, Map<String, Object> params)
            throws GRPCException {
        if (!params.containsKey("address")) {
            System.err.println("Address parameter is required.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }
        TronClient client = getClient(options, false);
        TronSmartContract contract;
        contract = client.getContract((TronAddress) params.get("address"));

        List<String> result = contract.getMethodsSignatures();

        if (options.containsKey("verbose")) {
            for (String sig : result) {
                System.out.println(sig);
            }
        } else {
            JsonArray array = new JsonArray();
            for (String sig : result) {
                array.add(sig);
            }
            Gson gson = new GsonBuilder().create();
            System.out.println(gson.toJson(array));
        }
    }

    public static void getContractEvents(Map<String, Object> options, Map<String, Object> params)
            throws GRPCException {
        if (!params.containsKey("address")) {
            System.err.println("Address parameter is required.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }
        TronClient client = getClient(options, false);
        TronSmartContract contract;
        contract = client.getContract((TronAddress) params.get("address"));

        List<String> result = contract.getEventsSignatures();

        if (options.containsKey("verbose")) {
            for (String sig : result) {
                System.out.println(sig);
            }
        } else {
            JsonArray array = new JsonArray();
            for (String sig : result) {
                array.add(sig);
            }
            Gson gson = new GsonBuilder().create();
            System.out.println(gson.toJson(array));
        }
    }

    public static void triggerContract(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);
        if (!params.containsKey("address")) {
            System.err.println("Address parameter is required.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }
        if (!params.containsKey("method")) {
            System.err.println("You must specify the method signature with the --method option.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }

        TronAddress address = (TronAddress) params.get("address");
        String method = params.get("method").toString();
        List<String> callParams = new ArrayList<>();
        if (params.containsKey("paramsList")) {
            callParams = (List<String>) params.get("paramsList");
        }
        TronCurrency feeLimit = TronCurrency.sun(0L);
        if (params.containsKey("feeLimit")) {
            feeLimit = TronCurrency.trx((Double) params.get("feeLimit"));
        }
        TronCurrency callValue = TronCurrency.sun(0L);
        if (params.containsKey("callValue")) {
            callValue = TronCurrency.trx((Double) params.get("callValue"));
        }
        long tokenId = 0;
        if (params.containsKey("token-id")) {
            tokenId = (Long) params.get("token-id");
        }
        long tokenCallValue = 0;
        if (params.containsKey("token-value")) {
            tokenCallValue = (Long) params.get("token-value");
        }

        if (!options.containsKey("yes")) {
            System.out.println("You are going to trigger the following method:");
            System.out.println("  Contract: " + address.toString());
            System.out.println("  Method: " + method);
            System.out.println("  Params: " + ConsoleUtils.listToString(callParams));
            System.out.println("  Fee limit: " + String.format("%.0f TRX", feeLimit.getTRX()));
            System.out.println("  Call value: " + String.format("%.0f TRX", callValue.getTRX()));
            if (tokenCallValue > 0) {
                System.out.println("  Token id " + tokenId);
                System.out.println("  Token call value: " + tokenCallValue);
            }
            if (!ConsoleUtils.askConfirmation()) {
                System.out.println("Operation cancelled by user!");
                return;
            }
        }

        TriggerContractResult res;

        try {
            res = client.triggerSmartContract(wallet, address,
                    new TriggerContractDataBuilder(method).paramsList(callParams), feeLimit, callValue,
                    tokenId, tokenCallValue);
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(ERROR_STATUS);
            return;
        }

        if (res.isTransaction()) {
            System.out.println("Transaction: " + res.getTransaction().getId().toString());

            System.out.println();

            waitForTransaction(client, res.getTransaction().getId());
        } else {
            String output = "hex";
            if (params.containsKey("outputFormat")) {
                output = params.get("outputFormat").toString().toLowerCase();
            }
            if (output.equals("hex")) {
                System.out.println("Result: " + Hex.toHexString(res.getReturnedData()));
            } else {
                try {
                    System.out.println("Result: " + TronUtils.valueToString(res.interpretReturnedData(output).get(0)));
                } catch (Exception ex) {
                    System.out.println("Result: " + Hex.toHexString(res.getReturnedData()));
                }
            }
        }
    }

    public static void triggerContractInteractive(Map<String, Object> options, Map<String, Object> params)
            throws GRPCException, IOException {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);
        if (!params.containsKey("address")) {
            System.err.println("Address parameter is required.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }

        TronAddress address = (TronAddress) params.get("address");

        TronSmartContract contract;
        contract = client.getContract((TronAddress) params.get("address"));

        System.out.println("Contract loaded: " + address.toString());
        System.out.println("List of methods: ");
        List<String> methods = contract.getMethodsSignatures();
        for (String sig : methods) {
            System.out.println("  " + sig + " - " + contract.getStateMutability(TriggerContractDataBuilder.getMethodFromCall(sig)));
        }

        System.out.println();
        System.out.println("Use 'method(arg1, ...)' to call a method. Example: balanceOf(TRqpChxGoZJqKkx4bibKB1RuBC9j595bqK)");
        System.out.println("Use 'call call_value_trx fee_limit_trx method(arg1, ...)' to call a method, setting the call value and the fee limit.");
        System.out.println("By default, for testing, fee limit is 1000 TRX.");
        System.out.println("Use 'methods' to view the list of methods.");
        System.out.println("Use 'exit' to exit.");
        System.out.println();

        ConsoleReader reader = new ConsoleReader();
        ConsoleReader numberReader = new ConsoleReader();

        reader.addCompleter(new StringsCompleter(methods));

        while (true) {
            String line = reader.readLine("> ");
            if (line != null) {
                line = line.trim();
                String cmd = "";
                String arg = "";
                int spaceI = line.indexOf(" ");
                if (spaceI > 1 && spaceI < line.length() - 1) {
                    cmd = line.substring(0, spaceI);
                    arg = line.substring(spaceI + 1);
                } else {
                    cmd = line;
                }
                switch (cmd.toLowerCase()) {
                    case "exit":
                        return;
                    case "methods":
                        System.out.println("List of methods: ");
                        for (String sig : methods) {
                            System.out.println("  " + sig + " - " + contract.getStateMutability(TriggerContractDataBuilder.getMethodFromCall(sig)));
                        }
                        break;
                    default: {
                        TronCurrency callValue, feeLimit;
                        boolean defaultParams = false;
                        if (!cmd.equalsIgnoreCase("call")) {
                            arg = line;
                            callValue = TronCurrency.sun(0L);
                            feeLimit = TronCurrency.sun(0L);
                            defaultParams = true;
                        } else {
                            int spaceIndex = arg.indexOf(" ");
                            if (spaceIndex < 1 || spaceIndex >= arg.length() - 1) {
                                System.out.println("Use 'call call_value_trx fee_limit_trx method(arg1, ...)' to call a method.");
                                continue;
                            }
                            try {
                                callValue = TronCurrency.trx(Double.parseDouble(arg.substring(0, spaceIndex)));
                                arg = arg.substring(spaceIndex + 1);
                            } catch (NumberFormatException ex) {
                                System.out.println("Use 'call call_value_trx fee_limit_trx method(arg1, ...)' to call a method.");
                                continue;
                            }
                            spaceIndex = arg.indexOf(" ");
                            if (spaceIndex < 1 || spaceIndex >= arg.length() - 1) {
                                System.out.println("Use 'call call_value_trx fee_limit_trx method(arg1, ...)' to call a method.");
                                continue;
                            }
                            try {
                                feeLimit = TronCurrency.trx(Double.parseDouble(arg.substring(0, spaceIndex)));
                                arg = arg.substring(spaceIndex + 1);
                            } catch (NumberFormatException ex) {
                                System.out.println("Use 'call call_value_trx fee_limit_trx method(arg1, ...)' to call a method.");
                                continue;
                            }
                        }
                        String method, methodName;
                        List<String> callParams;
                        try {
                            methodName = TriggerContractDataBuilder.getMethodFromCall(arg);
                            method = contract.getMethodsSignature(methodName);
                            callParams = TriggerContractDataBuilder.getparamsFromCall(arg);
                        } catch (Exception ex) {
                            System.out.println("Use 'call call_value_trx fee_limit_trx method(arg1, ...)' to call a method.");
                            continue;
                        }

                        if (defaultParams && contract.getStateMutability(methodName)
                                == Protocol.SmartContract.ABI.Entry.StateMutabilityType.Payable) {
                            String cvStr = numberReader.readLine("Call value (TRX): ");
                            try {
                                callValue = TronCurrency.trx(Double.parseDouble(cvStr));
                            } catch (NumberFormatException ex) {
                                callValue = TronCurrency.sun(0L);
                            }
                        }

                        if (defaultParams && !contract.isReadOnly(methodName)) {
                            feeLimit = TronCurrency.trx(1000.0);
                        }

                        if (!contract.isReadOnly(methodName) && !options.containsKey("yes")) {
                            System.out.println("You are going to trigger the following method:");
                            System.out.println("  Contract: " + address.toString());
                            System.out.println("  Method: " + method);
                            System.out.println("  Params: " + ConsoleUtils.listToString(callParams));
                            System.out.println("  Fee limit: " + String.format("%.0f TRX", feeLimit.getTRX()));
                            System.out.println("  Call value: " + String.format("%.0f TRX", callValue.getTRX()));
                            if (!ConsoleUtils.askConfirmation()) {
                                System.out.println("Operation cancelled by user!");
                                continue;
                            }
                        }

                        TriggerContractResult res;

                        try {
                            res = client.triggerSmartContract(wallet, address,
                                    new TriggerContractDataBuilder(method).paramsList(callParams), feeLimit, callValue);
                        } catch (Exception ex) {
                            System.err.println("Error: " + ex.getMessage());
                            continue;
                        }

                        if (res.isTransaction()) {
                            System.out.println("Transaction: " + res.getTransaction().getId().toString());

                            System.out.println();

                            TronTransactionInformation info;

                            System.out.println("Waiting for confirmation...");

                            try {
                                info = client.waitForTransactionConfirmation(res.getTransaction().getId(), 20);
                            } catch (ConfirmationTimeoutException ex) {
                                System.err.println("Timeout! Transaction not confirmed yet.");
                                System.err.println("Check the transaction later, it may be confirmed.");
                                continue;
                            } catch (Exception ex) {
                                System.err.println("Error: " + ex.getMessage());
                                continue;
                            }

                            if (info.getResult() == TronTransaction.Code.SUCCESS) {
                                System.out.println("Transaction confirmed!");
                                System.out.println("Your transaction has been added to block " + info.getBlockNumber());
                                System.out.println("The transaction consumed " + info.getTotalEnergyUsage() + " points of energy.");
                            } else {
                                System.err.println("Transaction failed!");
                                System.err.println("Details: " + info.getResultMessage());
                            }
                        } else {
                            List<String> output = contract.getResultTypeForMethod(methodName);
                            List<String> names = contract.getResultNameForMethod(methodName);
                            if (output == null || names == null) {
                                System.out.println("Result: " + Hex.toHexString(res.getReturnedData()));
                            } else {
                                try {
                                    List<Object> values = res.interpretReturnedData(output);
                                    for (int i = 0; i < output.size(); i++) {
                                        if (names.get(i) != null && names.get(i).length() > 0) {
                                            System.out.println(names.get(i) + " = " + TronUtils.valueToString(values.get(i)));
                                        } else {
                                            System.out.println("Result(" + i + ") = " + TronUtils.valueToString(values.get(i)));
                                        }
                                    }
                                } catch (Exception ex) {
                                    System.out.println("Result: " + Hex.toHexString(res.getReturnedData()));
                                }
                            }
                        }

                    }
                    break;
                }
            } else {
                break;
            }
        }
    }

    public static void listenForContractEvents(Map<String, Object> options, Map<String, Object> params)
            throws GRPCException {
        TronClient client = getClient(options, false);
        if (!params.containsKey("address")) {
            System.err.println("Address parameter is required.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }

        final TronAddress address = (TronAddress) params.get("address");

        Protocol.SmartContract.ABI abi;

        final boolean show_blocks = params.containsKey("show-blocks");

        TronSmartContract contract;
        contract = client.getContract((TronAddress) params.get("address"));

        System.out.println("Contract loaded: " + address.toString());
        System.out.println("List of events: ");
        final List<String> events = contract.getEventsSignatures();
        for (String sig : events) {
            System.out.println("  " + sig);
        }

        if (params.containsKey("abi")) {
            String abiStr = params.get("abi").toString();
            abi = TronSmartContracts.jsonStr2ABI(abiStr);
        } else {
            abi = contract.getAbi();
        }

        long blockNum;

        try {
            blockNum = client.getLastBlock().getNumber();
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(ERROR_STATUS);
            return;
        }

        TronBlockChainWatcher watcher = new TronBlockChainWatcher(client, blockNum);

        watcher.addHandler(new SmartContractEventListener(address, abi) {
            @Override
            public void handleBlock(TronClient client, TronBlock block) {
                super.handleBlock(client, block);
                if (show_blocks) {
                    System.out.println("[BLOCK] " + block.getNumber());
                }
            }

            @Override
            public void handleEvent(String eventName, String eventSignature, Map<String, String> types, Map<String, Object> values) {
                System.out.println("[EVENT] " + eventName);
                for (String name : values.keySet()) {
                    System.out.println("   " + types.get(name) + " " + name + " = " + TronUtils.valueToString(values.get(name)));
                }
            }

            @Override
            public void handleNotInterpretableEvent(TronTransactionInformation.Log event) {
                System.out.println("[EVENT] " + event);
                System.out.println("Warning: Could not interpret event, make sure the ABI of the contract is correct.");
                System.out.println("Event Data: " + Hex.toHexString(event.getData()));
                System.out.println("Event topic list:");
                for (byte[] topic : event.getTopics()) {
                    System.out.println("    " + Hex.toHexString(topic));
                }
            }
        });

        System.out.println("Listening for events...");
        System.out.println("Type 'exit' to stop listening.");

        watcher.start();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine().toLowerCase();
            if (line.equals("exit") || line.equals("stop") || line.equals("quit") || line.equals("q")) {
                System.exit(0);
            }
        }
    }

    public static void listenFoAllEvents(Map<String, Object> options, Map<String, Object> params)
            throws GRPCException {
        TronClient client = getClient(options, false);
        Map<TronAddress, TronSmartContract> cache = new TreeMap<>();
        final boolean show_blocks = params.containsKey("show-blocks");

        long blockNum;

        try {
            blockNum = client.getLastBlock().getNumber();
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(ERROR_STATUS);
            return;
        }

        TronBlockChainWatcher watcher = new TronBlockChainWatcher(client, blockNum);

        watcher.addHandler(new TronEventHandler() {
            @Override
            public void handleBlock(TronClient client, TronBlock block) {
                if (show_blocks) {
                    System.out.println("[BLOCK] " + block.getNumber());
                }
            }

            @Override
            public void handleTransaction(TronClient client, TronTransaction tx, TronTransactionInformation info) {
                for (TronTransactionInformation.Log log : info.getLogs()) {
                    TronAddress address = log.getAddress();
                    TronSmartContract contract;
                    if (cache.containsKey(address)) {
                        contract = cache.get(address);
                    } else {
                        try {
                            contract = client.getContract(address);
                            cache.put(address, contract);
                        } catch (Exception ex) {
                            contract = null;
                        }
                    }

                    if (contract != null) {
                        TronSmartContractEvent event = contract.interpretLog(log);

                        if (event != null) {
                            if (event.isInterpretable()) {
                                System.out.println();
                                System.out.println("[" + (new Date()).toString() + "]");
                                System.out.println("EVENT-----------------------------------------");
                                System.out.println("Contract: " + log.getAddress().toString());
                                System.out.println("Event: " + event.getEventSignature());
                                System.out.println("Parameters: ");
                                for (String param : event.getTypes().keySet()) {
                                    System.out.println("     " + param + " ("
                                            + event.getTypes().get(param) + ") = "
                                            + TronUtils.valueToString(event.getValues().get(param)));
                                }
                                System.out.println("----------------------------------------------");
                            } else {
                                System.out.println();
                                System.out.println("[" + (new Date()).toString() + "]");
                                System.out.println("Warning: Event recognized but not interpretable. The contract ABi may be invalid or incomplete. ");
                                System.out.println("EVENT-----------------------------------------");
                                System.out.println("Contract: " + log.getAddress().toString());
                                System.out.println("Event: " + event.getEventSignature());
                                System.out.println("Data: " + Hex.toHexString(log.getData()));
                                System.out.println("Topics: ");
                                for (byte[] topic : log.getTopics()) {
                                    System.out.println("    " + Hex.toHexString(topic));
                                }
                                System.out.println("----------------------------------------------");
                            }
                        } else {
                            System.out.println();
                            System.out.println("[" + (new Date()).toString() + "]");
                            System.out.println("Warning: Unrecognized event. The contract ABi may be invalid or incomplete. Showing log raw data.");
                            log.print("");
                        }
                    } else {
                        System.out.println();
                        System.out.println("[" + (new Date()).toString() + "]");
                        System.out.println("Warning: Unable to obtain contract " + address.toString() + ". Showing log raw data.");
                        log.print("");
                    }
                }
            }
        });

        System.out.println("Listening for events...");
        System.out.println("Type 'exit' to stop listening.");

        watcher.start();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine().toLowerCase();
            if (line.equals("exit") || line.equals("stop") || line.equals("quit") || line.equals("q")) {
                System.exit(0);
            }
        }
    }

    public static void generateContractProxy(Map<String, Object> options, Map<String, Object> params)
            throws GRPCException {
        TronClient client = getClient(options, false);
        Protocol.SmartContract.ABI abi;

        String name;
        String pkg = "contracts";

        if (params.containsKey("name")) {
            name = params.get("name").toString();
        } else {
            System.err.println("Contract name is required. Use --name to set it.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }

        if (params.containsKey("package")) {
            pkg = params.get("package").toString();
        }

        if (params.containsKey("abi")) {
            String abiStr = params.get("abi").toString();
            abi = TronSmartContracts.jsonStr2ABI(abiStr);
        } else {
            if (!params.containsKey("address")) {
                System.err.println("Address parameter or ABI is required.");
                System.exit(PARAMS_ERROR_STATUS);
                return;
            }
            final TronAddress address = (TronAddress) params.get("address");

            TronSmartContract contract;
            contract = client.getContract((TronAddress) params.get("address"));

            abi = contract.getAbi();
        }

        TronSmartContract finalContract = new TronSmartContract(abi);

        System.out.println(SmartContractProxyGenerator.generateProxyForSmartContract(pkg, name, finalContract));
    }

    public static void logBlockChainBlocks(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        TronClient client = getClient(options, false);
        long blockNum;

        try {
            blockNum = client.getLastBlock().getNumber();
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(ERROR_STATUS);
            return;
        }

        long limit = 0;

        if (params.containsKey("count")) {
            limit = (Long) params.get("count");
        }

        final long finalLimit = limit;

        TronBlockChainWatcher watcher = new TronBlockChainWatcher(client, blockNum, true);

        watcher.addHandler(new TronEventHandler() {
            private long count = 0;

            @Override
            public void handleBlock(TronClient client, TronBlock block) {
                System.out.println("BLOCK-----------------------------------------------");
                block.print("", !params.containsKey("raw") ? client : null);
                System.out.println("----------------------------------------------------");
                count++;
                if (finalLimit > 0 && count >= finalLimit) {
                    System.exit(0);
                }
            }

            @Override
            public void handleTransaction(TronClient client, TronTransaction tx, TronTransactionInformation info) {
            }
        });

        System.out.println("Waiting for blocks...");
        System.out.println("Type 'exit' to stop.");

        watcher.start();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine().toLowerCase();
            if (line.equals("exit") || line.equals("stop") || line.equals("quit") || line.equals("q")) {
                System.exit(0);
            }
        }
    }

    public static void getChainParams(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        TronClient client = getClient(options, false);


        List<ChainParameter> chainP = client.getChainParameters();

        for (ChainParameter p : chainP) {
            System.out.println("#" + p.getId() + " (" + p.getKey() + ") = " + p.getValue());
        }
    }

    public static void interpretSmartContractCall(Map<String, Object> options, Map<String, Object> params) throws GRPCException {
        if (!params.containsKey("data")) {
            System.err.println("Data parameter is required. Use --data for that.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }

        byte[] data = (byte[]) params.get("data");

        Protocol.SmartContract.ABI abi;
        if (params.containsKey("abi")) {
            abi = TronSmartContracts.jsonStr2ABI(params.get("abi").toString());
        } else if (params.containsKey("address")) {
            TronClient client = getClient(options, false);
            TronSmartContract contract = client.getContract((TronAddress) params.get("address"));
            abi = contract.getAbi();
        } else {
            System.err.println("You must specify the address of the contract or the ABI.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }

        TronSmartContract contract = new TronSmartContract(abi);
        List<String> methods = contract.getMethodsSignatures();

        for (String method : methods) {
            List<Object> result;
            try {
                result = TriggerSmartContractContract.interpretData(data, method);
            } catch (Exception ex) {
                continue;
            }
            System.out.println("Method: " + method);
            int i = 0;
            for (Object object : result) {
                System.out.println("Parameters(" + i + ") = " + TronUtils.valueToString(object));
                i++;
            }
            return;
        }

        System.out.println("Could not interpret data. the call data does not match with any of the contract methods.");
    }

    public static void createProposal(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("params")) {
            Map<Long, Long> p = (Map<Long, Long>) params.get("params");
            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to create a proposal with the following parameters:");
                for (Long key : p.keySet()) {
                    System.out.println("    " + key + " -> " + p.get(key));
                }
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.createProposal(wallet, p);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("You must specify at least one parameter with the --param option.");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void approveProposal(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("num")) {
            long id = (Long) params.get("num");
            boolean add_approval = params.containsKey("add_approval");
            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to approve the proposal " + id + ".");
                System.out.println("Add approval = " + (add_approval ? "Yes" : "No"));
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.approveProposal(wallet, id, add_approval);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("You must specify the proposal id with the --num option.");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void deleteProposal(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("num")) {
            long id = (Long) params.get("num");
            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to delete the proposal " + id + ".");
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.deleteProposal(wallet, id);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("You must specify the proposal id with the --num option.");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void buyStorage(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("amount")) {
            long amount = (Long) params.get("amount");
            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to buy " + amount + " storage points.");
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.buyStorage(wallet, amount);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("Required parameters: --amount");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void sellStorage(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        if (params.containsKey("amount")) {
            long amount = (Long) params.get("amount");
            TronTransaction tx;

            if (!options.containsKey("yes")) {
                System.out.println("You are going to sell " + amount + " storage points.");
                if (!ConsoleUtils.askConfirmation()) {
                    System.out.println("Operation cancelled by user!");
                    return;
                }
            }

            try {
                tx = client.sellStorage(wallet, amount);
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(ERROR_STATUS);
                return;
            }

            System.out.println("Transaction: " + tx.getId().toString());

            System.out.println();

            waitForTransaction(client, tx.getId());
        } else {
            System.err.println("Required parameters: --amount");
            System.exit(PARAMS_ERROR_STATUS);
        }
    }

    public static void withdrawBalance(Map<String, Object> options, Map<String, Object> params) {
        TronWallet wallet = getCredentials(params);
        TronClient client = getClient(options, false);

        TronTransaction tx;

        if (!options.containsKey("yes")) {
            System.out.println("You are going to withdraw your allowance.");
            if (!ConsoleUtils.askConfirmation()) {
                System.out.println("Operation cancelled by user!");
                return;
            }
        }

        try {
            tx = client.withdrawBalance(wallet);
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(ERROR_STATUS);
            return;
        }

        System.out.println("Transaction: " + tx.getId().toString());

        System.out.println();

        waitForTransaction(client, tx.getId());
    }


    private static void waitForTransaction(TronClient client, HashIdentifier id) {
        TronTransactionInformation info;

        System.out.println("Waiting for confirmation...");

        try {
            info = client.waitForTransactionConfirmation(id, 20);
        } catch (ConfirmationTimeoutException ex) {
            System.err.println("Timeout! Transaction not confirmed yet.");
            System.err.println("Check the transaction later, it may be confirmed.");
            System.exit(ERROR_STATUS);
            return;
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(ERROR_STATUS);
            return;
        }

        if (info.getResult() == TronTransaction.Code.SUCCESS) {
            System.out.println("Transaction confirmed!");
            System.out.println("Your transaction has been added to block " + info.getBlockNumber());
        } else {
            System.err.println("Transaction failed!");
            System.err.println("Details: " + info.getResultMessage());
            System.exit(ERROR_STATUS);
        }
    }

    /* Contract Migration */

    public static void backupTRC20(Map<String, Object> options, Map<String, Object> params)
            throws Exception {
        TronClient client = getClient(options, false);
        if (!params.containsKey("address")) {
            System.err.println("Address parameter is required.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }

        if (!params.containsKey("out")) {
            System.err.println("Output (--out file) parameter is required.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }

        final TronAddress address = (TronAddress) params.get("address");
        final File resultFile = new File(params.get("out").toString());

        Protocol.SmartContract.ABI abi;

        TronSmartContract contract;
        contract = client.getContract((TronAddress) params.get("address"));

        System.out.println("Contract loaded: " + address.toString());

        if (params.containsKey("abi")) {
            String abiStr = params.get("abi").toString();
            abi = TronSmartContracts.jsonStr2ABI(abiStr);
        } else {
            abi = contract.getAbi();
        }

        long blockNum;

        try {
            blockNum = client.getLastBlock().getNumber();
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(ERROR_STATUS);
            return;
        }

        TRC20 proxy = new TRC20(client, address);

        System.out.println("TR20: " + proxy.name() + " | Symbol: " + proxy.symbol() + " | Decimals: " + proxy.decimals() + " | Total supply: " + proxy.totalSupply());

        Map<TronAddress, List<BigInteger>> balances = new TreeMap<>();

        long lastTimeReport = System.currentTimeMillis();

        System.out.println("Scanning the blockchain...");

        long blocksScannned = 0;
        while (blockNum >= 1) {
            TronBlock block = client.getBlock(blockNum);
            blockNum--;


            for (TronTransaction tx : block.getTransactions()) {
                boolean shouldParse = false;
                boolean shouldCheckCreate = false;
                boolean shouldStop = false;
                if (!tx.getContracts().isEmpty() && tx.getContracts().get(0).getType() == TronContract.Type.CREATE_SMART_CONTRACT) {
                    shouldCheckCreate = true;
                    shouldParse = true;
                } else if (!tx.getContracts().isEmpty() && tx.getContracts().get(0).getType() == TronContract.Type.TRIGGER_SMART_CONTRACT) {
                    shouldParse = true;
                }

                if (!shouldParse) {
                    continue;
                }

                // Get transaction info
                TronTransactionInformation info = client.getTransactionInformation(tx.getId());

                if (shouldCheckCreate) {
                    if (info.getContractAddress().equals(address)) {
                        shouldStop = true; // We reached the contract creation transaction, STOP
                    }
                }

                for (TronTransactionInformation.Log log : info.getLogs()) {
                    if (log.getAddress().equals(address)) {
                        TronSmartContractEvent event = proxy.getContract().interpretLog(log);

                        if (event.isInterpretable() && event.getEventName().equalsIgnoreCase("Transfer")) {
                            TronAddress from = (TronAddress) event.getValues().get("from");
                            TronAddress to = (TronAddress) event.getValues().get("to");
                            BigInteger value = (BigInteger) event.getValues().get("value");

                            if (!balances.containsKey(from)) {
                                balances.put(from, new ArrayList<>());
                            }

                            if (!balances.containsKey(to)) {
                                balances.put(to, new ArrayList<>());
                            }

                            balances.get(from).add(value.negate());
                            balances.get(to).add(value);
                        }
                    }
                }

                if (shouldStop) {
                    blockNum = 0;
                }
            }


            blocksScannned++;
            if (System.currentTimeMillis() - lastTimeReport > 5000) {
                lastTimeReport = System.currentTimeMillis();
                System.out.println("[PROGRESS REPORT] Current block scanned is " + block.getNumber() + " [" + block.getDate().toString() + "] | " + blocksScannned + " blocks scanned, " + balances.size() + " accounts found.");
            }
        }

        System.out.println("Done scanning blocks. Saving the results...");


        PrintWriter pw = new PrintWriter(resultFile);

        pw.println("ACCOUNT,BALANCE");

        for (TronAddress account : balances.keySet()) {
            if (account.equals(new TronAddress("T9yD14Nj9j7xAB4dbGeiX9h8unkKHxuWwb")));
            List<BigInteger> changesReverse = balances.get(account);

            BigInteger balanceNow = BigInteger.ZERO;

            for (int i = changesReverse.size() - 1; i >= 0; i--) {
                balanceNow = balanceNow.add(changesReverse.get(i));
            }

            if (balanceNow.compareTo(BigInteger.ZERO) > 0) {
                pw.println(account.toBase58() + "," + proxy.withDecimals(balanceNow) + "");
            }
        }

        pw.close();

        System.out.println("Done. Your balances backup has been saved to " + resultFile.toPath().toAbsolutePath().toString());
    }

    public static void restoreTRC20(Map<String, Object> options, Map<String, Object> params)
            throws Exception {

        TronClient client = getClient(options, false);
        TronWallet wallet = getCredentials(params);
        if (!params.containsKey("address")) {
            System.err.println("Address parameter is required.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }

        if (!params.containsKey("in")) {
            System.err.println("Input (--in file) parameter is required.");
            System.exit(PARAMS_ERROR_STATUS);
            return;
        }

        final TronAddress address = (TronAddress) params.get("address");
        final File resultFile = new File(params.get("in").toString());

        Protocol.SmartContract.ABI abi;

        TronSmartContract contract;
        contract = client.getContract((TronAddress) params.get("address"));

        System.out.println("Contract loaded: " + address.toString());

        if (params.containsKey("abi")) {
            String abiStr = params.get("abi").toString();
            abi = TronSmartContracts.jsonStr2ABI(abiStr);
        } else {
            abi = contract.getAbi();
        }

        long blockNum;

        try {
            blockNum = client.getLastBlock().getNumber();
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(ERROR_STATUS);
            return;
        }

        TRC20 proxy = new TRC20(client, address);

        System.out.println("TR20: " + proxy.name() + " | Symbol: " + proxy.symbol() + " | Decimals: " + proxy.decimals() + " | Total supply: " + proxy.totalSupply());

        Scanner sc = new Scanner(resultFile);

        if (!sc.nextLine().trim().equalsIgnoreCase("account,balance")) {
            System.err.println("Error: The input file does not follow the format 'ACCOUNT,BALANCE'");
            System.exit(ERROR_STATUS);
            return;
        }

        if (!options.containsKey("yes")) {
            System.out.println("You are going to perform multiple transfers stored in the file " + resultFile.toPath().toString());
            if (!ConsoleUtils.askConfirmation()) {
                System.out.println("Operation cancelled by user!");
                return;
            }
        }

        String symbol = proxy.symbol();

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(",");
            if (parts != null && parts.length == 2) {
                try {
                    TronAddress account = new TronAddress(parts[0]);
                    BigInteger balance = proxy.withoutDecimals(Double.parseDouble(parts[1]));

                    if (wallet.getAddress().equals(account)) {
                        System.out.println("SKIP (Self account) " + account.toBase58());
                        continue;
                    }

                    System.out.println("TRANSER " + proxy.withDecimals(balance) + " " + symbol + " to account " + account.toBase58());

                    TronTransaction tx = proxy.transfer(wallet, account, balance);

                    TronTransactionInformation tInf = client.waitForTransactionConfirmation(tx.getId(), 10);

                    System.out.println("TRANSACTION " + tx.getId().toString() + " >> " + tInf.getResult().toString() + " >> " + tInf.getResultMessage());

                } catch (Exception ex) {
                    System.err.println("Error: " + ex.getClass().getName() + ": " + ex.getMessage());
                }
            }
        }

        System.out.println("Done.");


        sc.close();
    }

}
