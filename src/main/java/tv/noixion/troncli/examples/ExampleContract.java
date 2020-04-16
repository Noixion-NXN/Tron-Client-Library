package tv.noixion.troncli.examples;

import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronNode;
import tv.noixion.troncli.TronBlockChainWatcher;
import tv.noixion.troncli.TronClient;

public class ExampleContract {

    public static final String ADDRESS = "TFA1qpUkQ1yBDw4pgZKx25wEZAqkjGoZo1";
    public static final String CONTRACT_ADDRESS = "THLT8tm9SznCoHLRZRe2kxP3L7JHF3wtsX";

    public static void main(String[] argv) throws Exception {
        TronClient client = new TronClient(new TronNode("grpc.trongrid.io:50051"));

        IERC20Proxy proxy = new IERC20Proxy(client, new TronAddress(CONTRACT_ADDRESS));

        System.out.println("Total supply: " + proxy.totalSupply().toString());
        System.out.printf("Balance of %s is %.0f TRX\n", ADDRESS, proxy.balanceOf(new TronAddress(ADDRESS)).toString());

        TronBlockChainWatcher watcher = new TronBlockChainWatcher(client, client.getLastBlock().getNumber());

        watcher.addHandler(proxy);

        System.out.println("Listening for events...");
        watcher.startWatching();
    }
}
