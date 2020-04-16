package tv.noixion.troncli.examples;

import tv.noixion.troncli.models.TronAddress;
import tv.noixion.troncli.models.TronNode;
import tv.noixion.troncli.TronClient;
import tv.noixion.troncli.models.TronCurrency;

public class Example {
    public static final String ADDRESS = "TFA1qpUkQ1yBDw4pgZKx25wEZAqkjGoZo1";

    public static void main(String argv[]) throws Exception {
        TronClient client = new TronClient(new TronNode("grpc.trongrid.io:50051"));

        TronCurrency balance = client.getAccountByAddress(new TronAddress(ADDRESS)).getBalance();

        System.out.printf("Balance of %s is %.0f TRX\n", ADDRESS, balance.getTRX());
    }
}
