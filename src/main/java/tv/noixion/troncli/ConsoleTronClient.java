package tv.noixion.troncli;

import tv.noixion.troncli.models.*;
import tv.noixion.troncli.models.*;
import tv.noixion.troncli.utils.ConsoleUtils;
import tv.noixion.troncli.utils.ContractDeployFile;
import tv.noixion.troncli.utils.ContractDeployJSONFile;
import org.spongycastle.util.encoders.DecoderException;
import org.spongycastle.util.encoders.Hex;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Console Tron  Wallet Client.
 */
public class ConsoleTronClient {

    public static final String NAME = "Tron Wallet Client";
    public static final String VERSION = "1.4.2";

    public static int ERROR_STATUS = 1;
    public static int PARAMS_ERROR_STATUS = 2;

    /**
     * Main method.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Map<String, Object> options = new TreeMap<>();
        String command = "";
        Map<String, Object> params = new TreeMap<>();

        Map<String, TronAddress> libraries = new TreeMap<>();
        List<TronAssetIssue.FrozenSupply> frozen = new ArrayList<>();
        Map<TronAddress, Long> votes = new TreeMap<>();
        Map<Long, Long> proposalParams = new TreeMap<>();

        options.put("verbose", true); // TODO: JSON results

        int state = 0;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i].toLowerCase();
            switch (state) {
                case 0: {
                    if (arg.startsWith("-")) {
                        switch (arg) {
                            case "--verbose":
                            case "-verbose":
                            case "-v":
                                options.put("verbose", true);
                                break;
                            case "--yes":
                            case "-yes":
                            case "-y":
                                options.put("yes", true);
                                break;
                            case "--help":
                            case "-help":
                            case "-h":
                                printMan();
                                return;
                            case "--version":
                            case "-version":
                                System.out.println(NAME + " (Version " + VERSION + ")");
                                return;
                            case "--full":
                            case "-full":
                            case "--full-node":
                            case "-full-node":
                            case "-fn":
                                if (i + 1 >= args.length) {
                                    System.out.println("Option " + arg + " requires an argument.");
                                    System.exit(PARAMS_ERROR_STATUS);
                                    return;
                                }
                                options.put("full", new TronNode(args[i + 1]));
                                i++;
                                break;
                            case "--solidity":
                            case "--solidity-node":
                            case "-solidity":
                            case "-solidity-node":
                            case "-sn":
                                if (i + 1 >= args.length) {
                                    System.out.println("Option " + arg + " requires an argument.");
                                    System.exit(PARAMS_ERROR_STATUS);
                                    return;
                                }
                                options.put("solidity", new TronNode(args[i + 1]));
                                i++;
                                break;
                            default:
                                System.out.println("Unrecognized option: " + arg);
                                printMan();
                                return;
                        }
                    } else {
                        command = arg;
                        state = 1;
                    }
                }
                break;
                case 1: {
                    switch (arg) {
                        case "--private-key":
                        case "-private-key":
                        case "-pk":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("PK", new TronPrivateKey(args[i + 1]));
                            } catch (Exception ex) {
                                System.out.println("Parameter " + arg + " requires a valid private key.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--address":
                        case "-address":
                        case "-a":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("address", new TronAddress(args[i + 1]));
                            } catch (Exception ex) {
                                System.out.println("Parameter " + arg + " requires a valid address.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--wallet":
                        case "-wallet":
                        case "-w":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("wallet", new File(args[i + 1]));
                            } catch (Exception ex) {
                                System.out.println("Invalid path: " + args[i + 1]);
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--password":
                        case "-password":
                        case "-p":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            params.put("password", args[i + 1]);
                            i++;
                            break;
                        case "--name":
                        case "-name":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            params.put("name", args[i + 1]);
                            i++;
                            break;
                        case "--out":
                        case "-out":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            params.put("out", args[i + 1]);
                            i++;
                            break;
                        case "--in":
                        case "-in":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            params.put("in", args[i + 1]);
                            i++;
                            break;
                        case "--package":
                        case "-package":
                        case "-pkg":
                        case "--pkg":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            params.put("package", args[i + 1]);
                            i++;
                            break;
                        case "--deploy-file":
                        case "-deploy-file":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                ContractDeployFile file = new ContractDeployFile(new File(args[i + 1]));
                                params.put("abi", file.getAbi());
                                params.put("bytecode", file.getBytecode());
                                params.put("constructor", file.getConstructorSignature());
                                params.put("paramsList", file.getParams());
                            } catch (Exception ex) {
                                System.err.println("Error: " + ex.getMessage());
                                System.exit(ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--json":
                        case "-json":
                        case "--file":
                        case "-file":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                ContractDeployJSONFile file = new ContractDeployJSONFile(new File(args[i + 1]));
                                params.put("name", file.getName());
                                params.put("abi", file.getAbi());
                                params.put("bytecode", file.getBytecode());
                                params.put("constructor", file.getConstructorSignature());
                                params.put("paramsList", file.getParams());
                            } catch (Exception ex) {
                                System.err.println("Error: " + ex.getMessage());
                                System.exit(ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--abi":
                        case "-abi":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            params.put("abi", args[i + 1]);
                            i++;
                            break;
                        case "--abi-file":
                        case "-abi-file":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("abi", ConsoleUtils.readTextFile(new File(args[i + 1])).trim());
                            } catch (Exception ex) {
                                System.err.println("Error: " + ex.getMessage());
                                System.exit(ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--byte-code":
                        case "-bc":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            params.put("bytecode", args[i + 1]);
                            i++;
                            break;
                        case "--byte-code-file":
                        case "-byte-code-file":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("bytecode", ConsoleUtils.readTextFile(new File(args[i + 1])).trim());
                            } catch (Exception ex) {
                                System.err.println("Error: " + ex.getMessage());
                                System.exit(ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--constructor":
                        case "-constructor":
                        case "-c":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            params.put("constructor", args[i + 1]);
                            i++;
                            break;
                        case "--method":
                        case "-method":
                        case "-m":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            params.put("method", args[i + 1]);
                            i++;
                            break;
                        case "--output-format":
                        case "-output-format":
                        case "-of":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            params.put("outputFormat", args[i + 1]);
                            i++;
                            break;
                        case "--params":
                        case "-params": {
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires at least an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            List<String> callParams = new ArrayList<>();
                            for (int j = i + 1; j < args.length; j++) {
                                if (args[j].startsWith("-")) {
                                    i = j - 1;
                                    break;
                                } else {
                                    callParams.add(args[j]);
                                    i = j;
                                }
                            }
                            params.put("paramsList", callParams);
                        }
                        break;
                        case "--consume-user":
                        case "-consume-user":
                        case "-cu":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("consumeUser", Long.parseLong(args[i + 1]));
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires a valid number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--fee-limit":
                        case "-fee-limit":
                        case "-fl":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("feeLimit", Double.parseDouble(args[i + 1]));
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires a valid number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--call-value":
                        case "-call-value":
                        case "-cv":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("callvalue", Double.parseDouble(args[i + 1]));
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires a valid number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--trx":
                        case "-trx":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("amount", TronCurrency.trx(Double.parseDouble(args[i + 1])).getSUN());
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires a valid number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--sun":
                        case "--amount":
                        case "-sun":
                        case "-amount":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("amount", Long.parseLong(args[i + 1]));
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires a valid number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--decimals":
                        case "-decimals":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("decimals", Integer.parseInt(args[i + 1]));
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires a valid number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--origin-energy-limit":
                        case "-origin-energy-limit":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("origin-energy-limit", Long.parseLong(args[i + 1]));
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires a valid number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--token-id":
                        case "-token-id":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("token-id", Long.parseLong(args[i + 1]));
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires a valid number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--token-value":
                        case "-token-value":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("token-value", Long.parseLong(args[i + 1]));
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires a valid number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--library":
                        case "-library":
                        case "-lib":
                            if (i + 2 >= args.length) {
                                System.out.println("Parameter " + arg + " requires 2 arguments.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            libraries.put(args[i + 1], new TronAddress(args[i + 2]));
                            params.put("libraries", libraries);
                            i = i + 2;
                            break;
                        case "--asset":
                        case "--token":
                        case "-asset":
                        case "-token":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            params.put("asset", args[i + 1]);
                            i++;
                            break;
                        case "--id":
                        case "-id":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("hash", new HashIdentifier(args[i + 1]));
                            } catch (DecoderException ex) {
                                System.out.println("Parameter " + arg + " requires a valid hexadecimal identifier.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--number":
                        case "--num":
                        case "-num":
                        case "-n":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("num", Long.parseLong(args[i + 1]));
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires a valid number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--count":
                        case "-count":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("count", Long.parseLong(args[i + 1]));
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires a valid number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--data":
                        case "-data":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("data", Hex.decode(args[i + 1]));
                            } catch (Exception ex) {
                                System.out.println("Parameter " + arg + " must be in hexadecimal format.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--abbreviation":
                        case "--abbr":
                        case "-abbreviation":
                        case "-abbr":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            params.put("abbr", args[i + 1]);
                            i++;
                            break;
                        case "--url":
                        case "-url":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            params.put("url", args[i + 1]);
                            i++;
                            break;
                        case "--description":
                        case "--desc":
                        case "-description":
                        case "-desc":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            params.put("desc", args[i + 1]);
                            i++;
                            break;
                        case "--total-supply":
                        case "--supply":
                        case "-total-supply":
                        case "-supply":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("totalSupply", Long.parseLong(args[i + 1]));
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires a valid number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--offset":
                        case "-offset":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("offset", Long.parseLong(args[i + 1]));
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires a valid number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--limit":
                        case "-limit":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("offset", Long.parseLong(args[i + 1]));
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires a valid number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--conversion":
                        case "-conversion":
                            if (i + 2 >= args.length) {
                                System.out.println("Parameter " + arg + " requires 2 arguments.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("conversion", new TRXAssetConversion(Integer.parseInt(args[i + 1]), Integer.parseInt(args[i + 2])));
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires 2 valid numbers.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i = i + 2;
                            break;
                        case "--start-date":
                        case "--start":
                        case "-start-date":
                        case "-start":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }

                            try {
                                SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
                                params.put("start", f.parse(args[i + 1]));
                            } catch (ParseException ex) {
                                System.out.println("Parameter " + arg + " requires a valid date (Year/Month/day).");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--end-date":
                        case "--end":
                        case "-end-date":
                        case "-end":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }

                            try {
                                SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
                                params.put("end", f.parse(args[i + 1]));
                            } catch (ParseException ex) {
                                System.out.println("Parameter " + arg + " requires a valid date (Year/Month/Day).");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--frozen-supply":
                        case "--frozen":
                        case "-frozen-supply":
                        case "-frozen":
                            if (i + 2 >= args.length) {
                                System.out.println("Parameter " + arg + " requires 2 arguments.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }

                            try {
                                frozen.add(new TronAssetIssue.FrozenSupply(Long.parseLong(args[i + 1]), Long.parseLong(args[i + 2])));
                                params.put("frozen", frozen);
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires 2 valid numbers.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i = i + 2;
                            break;
                        case "--vote":
                        case "-vote":
                            if (i + 2 >= args.length) {
                                System.out.println("Parameter " + arg + " requires 2 arguments.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }

                            try {
                                votes.put(new TronAddress(args[i + 1]), Long.parseLong(args[i + 2]));
                                params.put("votes", votes);
                            } catch (Exception ex) {
                                System.out.println("Parameter " + arg + " requires 1 address and 1 number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i = i + 2;
                            break;
                        case "--support":
                        case "-support":
                            params.put("support", true);
                            break;
                        case "--show-blocks":
                        case "-show-blocks":
                            params.put("show-blocks", true);
                            break;
                        case "--add-approval":
                        case "-add-approval":
                            params.put("add_approval", true);
                            break;
                        case "--raw":
                        case "-raw":
                            params.put("raw", true);
                            break;
                        case "--free-net-limit":
                        case "-free-net-limit":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("fnl", Long.parseLong(args[i + 1]));
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires a valid number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--public-net-limit":
                        case "-public-net-limit":
                            if (i + 1 >= args.length) {
                                System.out.println("Parameter " + arg + " requires an argument.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            try {
                                params.put("pnl", Long.parseLong(args[i + 1]));
                            } catch (NumberFormatException ex) {
                                System.out.println("Parameter " + arg + " requires a valid number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i++;
                            break;
                        case "--param":
                        case "-param":
                            if (i + 2 >= args.length) {
                                System.out.println("Parameter " + arg + " requires 2 arguments.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }

                            try {
                                proposalParams.put(Long.parseLong(args[i + 1]), Long.parseLong(args[i + 2]));
                                params.put("params", proposalParams);
                            } catch (Exception ex) {
                                System.out.println("Parameter " + arg + " requires 1 address and 1 number.");
                                System.exit(PARAMS_ERROR_STATUS);
                                return;
                            }
                            i = i + 2;
                            break;
                        default:
                            System.out.println("Unrecognized parameter: " + arg);
                            printMan();
                            return;
                    }
                }
                break;
            }
        }
        try {
            switch (command) {
                case "create-key":
                    ConsoleClientModules.createPrivateKey(options, params);
                    break;
                case "create-wallet":
                    ConsoleClientModules.createWallet(options, params);
                    break;
                case "address":
                    ConsoleClientModules.getAddress(options, params);
                    break;
                case "key-from-wallet":
                    ConsoleClientModules.getKeyFromWallet(options, params);
                    break;
                case "nodes":
                    ConsoleClientModules.getNodes(options, params);
                    break;
                case "account":
                    ConsoleClientModules.queryAccount(options, params);
                    break;
                case "account-resource":
                    ConsoleClientModules.queryAccountResource(options, params);
                    break;
                case "block":
                    ConsoleClientModules.getBlock(options, params);
                    break;
                case "transaction":
                    ConsoleClientModules.getTransaction(options, params);
                    break;
                case "transaction-info":
                    ConsoleClientModules.getTransactionInformation(options, params);
                    break;
                case "asset":
                    ConsoleClientModules.getAsset(options, params);
                    break;
                case "transfer":
                    ConsoleClientModules.transferCoins(options, params);
                    break;
                case "transfer-asset":
                    ConsoleClientModules.transferAsset(options, params);
                    break;
                case "freeze-for-energy":
                    ConsoleClientModules.freezeBalanceForEnergy(options, params);
                    break;
                case "freeze-for-bandwidth":
                    ConsoleClientModules.freezeBalanceForBandwidth(options, params);
                    break;
                case "unfreeze-energy":
                    ConsoleClientModules.unfreezeBalanceForEnergy(options, params);
                    break;
                case "unfreeze-bandwidth":
                    ConsoleClientModules.unfreezeBalanceForBandwidth(options, params);
                    break;
                case "deploy":
                    ConsoleClientModules.deployContract(options, params);
                    break;
                case "contract":
                    ConsoleClientModules.getContract(options, params);
                    break;
                case "contract-events":
                    ConsoleClientModules.getContractEvents(options, params);
                    break;
                case "contract-methods":
                    ConsoleClientModules.getContractMethods(options, params);
                    break;
                case "trigger":
                    ConsoleClientModules.triggerContract(options, params);
                    break;
                case "contract-interactive":
                case "ci":
                    ConsoleClientModules.triggerContractInteractive(options, params);
                    break;
                case "contract-listen":
                    ConsoleClientModules.listenForContractEvents(options, params);
                    break;
                case "log-blocks":
                    ConsoleClientModules.logBlockChainBlocks(options, params);
                    break;
                case "interpret":
                    ConsoleClientModules.interpretSmartContractCall(options, params);
                    break;
                case "witnesses":
                    ConsoleClientModules.getWitnesses(options, params);
                    break;
                case "assets":
                    ConsoleClientModules.getAssets(options, params);
                    break;
                case "asset-issue":
                    ConsoleClientModules.assetIssue(options, params);
                    break;
                case "asset-update":
                    ConsoleClientModules.updateAsset(options, params);
                    break;
                case "asset-unfreeze":
                    ConsoleClientModules.unfreezeAsset(options, params);
                    break;
                case "create-witness":
                    ConsoleClientModules.createWitness(options, params);
                    break;
                case "update-witness":
                    ConsoleClientModules.updateWitness(options, params);
                    break;
                case "vote":
                    ConsoleClientModules.voteWitnesses(options, params);
                    break;
                case "update-settings":
                    ConsoleClientModules.updateSmartContractSettings(options, params);
                    break;
                case "proposal":
                    ConsoleClientModules.getProposalById(options, params);
                    break;
                case "proposals":
                    ConsoleClientModules.listProposals(options, params);
                    break;
                case "create-proposal":
                    ConsoleClientModules.createProposal(options, params);
                    break;
                case "approve-proposal":
                    ConsoleClientModules.approveProposal(options, params);
                    break;
                case "delete-proposal":
                    ConsoleClientModules.deleteProposal(options, params);
                    break;
                case "create-account":
                    ConsoleClientModules.createAccount(options, params);
                    break;
                case "update-account":
                    ConsoleClientModules.updateAccount(options, params);
                    break;
                case "set-account-id":
                    ConsoleClientModules.setAccountId(options, params);
                    break;
                case "participate-asset":
                    ConsoleClientModules.participateAsset(options, params);
                    break;
                case "withdraw":
                    ConsoleClientModules.withdrawBalance(options, params);
                    break;
                case "chain-params":
                case "chain-parameters":
                    ConsoleClientModules.getChainParams(options, params);
                    break;
                case "storage-buy":
                    ConsoleClientModules.buyStorage(options, params);
                    break;
                case "storage-sell":
                    ConsoleClientModules.sellStorage(options, params);
                    break;
                case "transactions-from":
                    ConsoleClientModules.getTransactionsFromThis(options, params);
                    break;
                case "transactions-to":
                    ConsoleClientModules.getTransactionsToThis(options, params);
                    break;
                case "contract-proxy-gen":
                case "proxy-gen":
                case "proxy":
                    ConsoleClientModules.generateContractProxy(options, params);
                    break;
                case "contract-listen-all":
                    ConsoleClientModules.listenFoAllEvents(options, params);
                    break;
                case "update-energy-limit":
                    ConsoleClientModules.updateSmartContractEnergyLimit(options, params);
                    break;
                case "trc20-backup":
                    ConsoleClientModules.backupTRC20(options, params);
                    break;
                case "trc20-backup-restore":
                    ConsoleClientModules.restoreTRC20(options, params);
                    break;
                default:
                    if (command.length() > 0) {
                        System.out.println("Unrecognized command: " + command);
                    }
                    printMan();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Error: " + ex.getMessage());
            System.exit(ERROR_STATUS);
            return;
        }

    }

    /**
     * Prints the manual.
     */
    public static void printMan() {
        System.out.println("NAME");
        System.out.println("    " + NAME + " (Version " + VERSION + ")");
        System.out.println("SYNOPSIS");
        System.out.println("    troncli [OPTIONS] COMMAND [COMMAND PARAMETERS]");
        System.out.println("OPTIONS");
        System.out.println("    --full <ip:port>        Set The full node to connect. Aliases -fn, --full-node");
        System.out.println("    --solidity <ip:port>    Set The solidity node to connect. Aliases -sn, --solidity-node");
        //System.out.println("    -v                      Turns on verbose mode. Other case it prints the result as JSON. Aliases --verbose");
        System.out.println("    -y                      Do not ask for confirmation.");
        System.out.println("    --help                  Shows the help. Aliases: -h");
        System.out.println("    --version               Shows the version. Aliases: -version");
        System.out.println("COMMANDS");
        System.out.println("   create-key               Creates a new private key and address.");
        System.out.println("   create-wallet            Creates a new wallet. You can specify the private key or create a brand new one.");
        System.out.println("                            Related options: --private-key, --password");
        System.out.println("   address                  Gets the address from a wallet or a private key.");
        System.out.println("                            Related options: --private-key, --wallet, --password");
        System.out.println("   key-from-wallet          Gets the private key from a wallet file.");
        System.out.println("                            Related options: --wallet, --password");
        System.out.println("   nodes                    Lists the known nodes.");
        System.out.println("   witnesses                Lists all the witnesses.");
        System.out.println("   assets                   Lists all the assets.");
        System.out.println("   proposals                Lists all the proposals.");
        System.out.println("   account                  Retrieves the information of an account.");
        System.out.println("                            Related options: --address");
        System.out.println("   account-resource         Retrieves the resource consumption information of an account.");
        System.out.println("                            Related options: --address");
        System.out.println("   block                    Retrieves a transaction from the blockchain.");
        System.out.println("                            Related options: --id, --num");
        System.out.println("   transaction              Retrieves a transaction from the blockchain.");
        System.out.println("                            Related options: --id");
        System.out.println("   transactions-from        Retrieves the transactions from an address. Requires a solidity node.");
        System.out.println("                            Related options: --address, --offset, --limit");
        System.out.println("   transactions-to          Retrieves the transactions to an address. Requires a solidity node.");
        System.out.println("                            Related options: --address, --offset, --limit");
        System.out.println("   transaction-info         Retrieves a transaction execution information from the blockchain.");
        System.out.println("                            Related options: --id");
        System.out.println("   asset                    Retrieves asset information from name or owner address.");
        System.out.println("                            Related options: --asset, --address");
        System.out.println("   proposal                 Retrieves proposal information.");
        System.out.println("                            Related options: --num");
        System.out.println("   create-account           Creates an account without transferring any money or tokens.");
        System.out.println("                            Related options: --address");
        System.out.println("   update-account           Updates your account name.");
        System.out.println("                            Related options: --name");
        System.out.println("   set-account-id           Sets your account id.");
        System.out.println("                            Related options: --name");
        System.out.println("   transfer                 Transfers founds to another account.");
        System.out.println("                            Related options: --address --trx, --sun");
        System.out.println("   transfer-asset           Transfers assets to another account.");
        System.out.println("                            Related options: --address, --asset, --amount");
        System.out.println("   participate-asset        Exchanges TRX for tokens and sends tokens to an address.");
        System.out.println("                            Related options: --address, --asset, --amount");
        System.out.println("   freeze-for-energy        Freezes balance for energy.");
        System.out.println("                            Related options: --trx, --sun");
        System.out.println("   freeze-for-bandwidth     Freezes balance for bandwidth.");
        System.out.println("                            Related options: --trx, --sun");
        System.out.println("   unfreeze-energy          Unfreezes balance frozen for energy.");
        System.out.println("   unfreeze-bandwidth       Unfreezes balance frozen for bandwidth.");
        System.out.println("   create-witness           Creates a witness account.");
        System.out.println("                            Related options: --url");
        System.out.println("   update-witness           Updates the witness URL.");
        System.out.println("                            Related options: --url");
        System.out.println("   vote                     Votes for witnesses.");
        System.out.println("                            Related options: --vote");
        System.out.println("   asset-issue              Creates a new asset.");
        System.out.println("                            Related options: --name, --total-supply, --conversion,");
        System.out.println("                                             --url, --description, --abbreviation,");
        System.out.println("                                             --free-net-limit, --public-net-limit,");
        System.out.println("                                             --start, --end, --frozen, --decimals");
        System.out.println("   asset-update             Updates an asset.");
        System.out.println("                            Related options: --url, --description,");
        System.out.println("                                             --free-net-limit, --public-net-limit,");
        System.out.println("   asset-unfreeze           Unfreezes the frozen supply for your asset.");
        System.out.println("   deploy                   Deploys an smart contract.");
        System.out.println("                            Related options: --name, --abi, --byte-code, --constructor, --params");
        System.out.println("                                             --consume-user, --fee-limit, --call-value, --library");
        System.out.println("                                             --origin-energy-limit");
        System.out.println("                                             --token-id, --token-value");
        System.out.println("   update-settings          Updates smart contract settings.");
        System.out.println("                            Related options: --address, --consume-user");
        System.out.println("   update-energy-limit      Updates smart contract energy limit settings.");
        System.out.println("                            Related options: --address, --origin-energy-limit");
        System.out.println("   contract                 Retrieves the information about a smart contract.");
        System.out.println("                            Related options: --address");
        System.out.println("   contract-methods         Retrieves the list of methods of a smart contract.");
        System.out.println("                            Related options: --address");
        System.out.println("   contract-events          Retrieves the list of events of a smart contract.");
        System.out.println("                            Related options: --address");
        System.out.println("   trigger                  Triggers a method of a smart contract.");
        System.out.println("                            Related options: --address, --method, --params, --fee-limit,");
        System.out.println("                                             --call-value, --output-format");
        System.out.println("                                             --token-id, --token-value");
        System.out.println("   contract-interactive     Interactive mode for triggering smart contract methods.");
        System.out.println("                            Related options: --address");
        System.out.println("   contract-listen          Listens for a specific smart contract events.");
        System.out.println("                            Related options: --address, --abi");
        System.out.println("   contract-listen-all      Listens for all smart contracts events.");
        System.out.println("   contract-proxy-gen       Generates a Java proxy class for the smart contract ABI.");
        System.out.println("                            Related options: --name, --address, --abi, --package");
        System.out.println("   interpret                Interprets a method call based on a smart contract.");
        System.out.println("                            Related options: --address, --abi, --data");
        System.out.println("   log-blocks               Listen the blockchain and logs the blocks.");
        System.out.println("   create-proposal          Creates a proposal.");
        System.out.println("                            Related options: --param");
        System.out.println("   approve-proposal         Approves a proposal (Must be super representative).");
        System.out.println("                            Related options: --num, --add-approval");
        System.out.println("   delete-proposal          Deletes a proposal (Must be the creator of the proposal).");
        System.out.println("                            Related options: --num");
        System.out.println("   storage-buy              Buys storage points.");
        System.out.println("                            Related options: --amount");
        System.out.println("   storage-sell             Sells storage points.");
        System.out.println("                            Related options: --amount");
        System.out.println("   withdraw                 Withdraws your allowance.");
        System.out.println("   chain-params             Displays the chain parameters.");
        System.out.println("   trc20-backup             Creates a backup of balances for TRC20 smart contract..");
        System.out.println("                            Related options: --address, --out");
        System.out.println("   trc20-backup-restore     Restores the balances for TRC20 smart contract.");
        System.out.println("                            Related options: --address, --in");
        System.out.println("COMMAND PARAMETERS");
        System.out.println("   --private-key <hex>      Private key parameter, in hexadecimal. Aliases: -pk");
        System.out.println("   --address <base-58>      Address parameter, in base 58 check. Aliases -a");
        System.out.println("   --id <hex>               Specifies an identifier (blocks, transactions). Aliases -id");
        System.out.println("   --num <number>           Specifies the block number or the proposal id. Aliases -n");
        System.out.println("   --wallet <file>          Specifies a wallet file. Aliases -w");
        System.out.println("   --password <password>    Specifies a password for the wallet. Aliases -p");
        System.out.println("   --trx <trx>              Specifies the amount in TRX");
        System.out.println("   --sun <trx>              Specifies the amount in SUN");
        System.out.println("   --amount <amount>        Specifies the amount for assets");
        System.out.println("   --asset <name>           Specifies the asset name. Aliases: --token");
        System.out.println("   --name <name>            Specifies a name for an account, smart contract or asset.");
        System.out.println("   --package <package>      Specifies the java package (for proxy generation).");
        System.out.println("   --abbr <abbr>            Specifies the abbreviation of an asset. Aliases: --abbreviation");
        System.out.println("   --url <url>              Specifies an url (for asset or witness).");
        System.out.println("   --desc <desc>            Specifies a description. Aliases: --description.");
        System.out.println("   --supply <number>        Specifies total supply of an asset. Aliases: --total-supply.");
        System.out.println("   --conversion <num> <trx> Specifies a conversion Asset to TRX.");
        System.out.println("   --decimals <num>         Specifies the number of decimals for the asset.");
        System.out.println("   --free-net-limit <limit> Specifies the free net limit for an asset.");
        System.out.println("   --public-net-limit <l>   Specifies the public net limit for an asset.");
        System.out.println("   --start <yyyy/mm/dd>     Specifies the start date for an asset. Aliases: --start-date");
        System.out.println("   --end <yyyy/mm/dd>       Specifies the expiration date for an asset. Aliases: --end-date");
        System.out.println("   --frozen <amount> <days> Specifies an amount of asset to freeze. Aliases: --frozen-supply");
        System.out.println("   --deploy-file <file>     Specifies a deployment file with the ABI, bytecode and constructor call.");
        System.out.println("   --json <file>            Specifies a deployment file (Tronbox JSON output). Aliases: --file");
        System.out.println("   --abi <ABI>              Specifies the ABI of a smart contract to deploy. Aliases: -abi");
        System.out.println("   --abi-file <file>        Specifies the ABI of a smart contract to deploy (stored in a file).");
        System.out.println("   --byte-code <code>       Specifies the bytecode of an smart contract to deploy. Aliases: -bc");
        System.out.println("   --byte-code-file <file>  Specifies the bytecode of an smart contract to deploy (stored in a file).");
        System.out.println("   --constructor <sign>     Specifies the constructor signature. Aliases: -c. Example: Contract(address,uint)");
        System.out.println("   --method <sign>          Specifies the method signature. Aliases: -c. Example: method(string)");
        System.out.println("   --params <param1> [...]  Specifies the method / constructor parameters. Example: --paramsList 1000 \"Example string\"");
        System.out.println("   --consume-user <0-100>   Specifies the user energy consumption percent. Default is 0. Aliases: -cu");
        System.out.println("   --origin-energy-limit    Specifies the energy limit to be consumed from the origin.");
        System.out.println("   --fee-limit <trx>        Specifies the fee limit (in TRX). Default is 0. Aliases: -fl");
        System.out.println("   --call-value <trx>       Specifies the call value (in TRX). Default id 0. Aliases: -cv");
        System.out.println("   --token-id <id>          Specifies the identifier of the token/asset to send as call value.");
        System.out.println("   --token-value <amount>   Specifies the amount of token to send as call value.");
        System.out.println("   --library <name> <addr>  Specifies a library for a smart contract. Aliases: -lib");
        System.out.println("   --output-format <type>   Specifies the trigger contract result type. Aliases: -of. Types: address, string, int, uint...");
        System.out.println("   --data <hex>             Specifies raw data in hexadecimal format.");
        System.out.println("   --count <hex>            Specifies the max number of blocks to log. Default unlimited.");
        System.out.println("   --vote <address> <count> Adds a vote.");
        System.out.println("   --param <ket> <val>      Adds a parameter to the proposal.");
        System.out.println("   --add-approval           Adds the approval to the list.");
        System.out.println("   --offset <offset>        Sets the offset for the transactions list. Default 0.");
        System.out.println("   --limit <offset>         Sets the max number of transactions to show. Default 20.");
        System.out.println("   --show-blocks            Shows blocks when listening.");
        System.out.println("   --raw                    Does not interpret smart contracts.");
        System.out.println("EXIT VALUE");
        System.out.println("   0                        Successful.");
        System.out.println("   1                        Failed.");
        System.out.println("   2                        Parameters error.");
        System.out.println("BUGS");
        System.out.println("   Report Bugs to: ");
        System.out.println("        https://github.com/Noixion/Tron-Client-Library/issues");
        System.out.println("EXAMPLES");
        System.out.println("   You can see some examples here: ");
        System.out.println("        https://github.com/Noixion/Tron-Client-Library/blob/master/docs/examples/console-examples.md");
        System.out.println();
    }
}
