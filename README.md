# Tron Client Library

Java library and console client to access the Tron network. It implements the following features:

 - Creation and management of private keys and wallets.
 - Blockchain queries (Blocks, transactions, assets, accounts, witnesses, proposals, nodes, contracts)
 - Account creation and update (to set account name).
 - Transferences of TRX or tokens.
 - Creation and update of Assets.
 - Participation on assets.
 - Freeze / unfreeze balance for energy or bandwidth.
 - Voting to representatives / witnesses.
 - Creation, update and deletion of proposals.
 - Allowance withdrawal.
 - Creation and interaction with smart contracts.
 - Creation of wrappers/proxies files to interact with smart contracts in Java.
 - Listening for smart contract events.
 - Interpretation of smart contracts call results.
 
This project is an adaptation of [tronprotocol/wallet-cli](https://github.com/tronprotocol/wallet-cli).

## Library

You can use this project as a library, for creation of DApps or graphic clients. 

This library is uploaded on central Maven repository. In order to import to your project, add the following to your project `pom.xml`

```xml
<dependency>
    <groupId>tv.noixion</groupId>
    <artifactId>troncli</artifactId>
    <version>1.1.2</version>
</dependency>
```

Once imported, you can use the library classes. Here is an example:

```java
import tv.noixion.troncli.TronClient;
import tv.noixion.troncli.TronAddress;
import tv.noixion.troncli.TronCurrency;
import tv.noixion.troncli.TronNode;

public class Example {
    public static final String ADDRESS = "TFA1qpUkQ1yBDw4pgZKx25wEZAqkjGoZo1";
    
    public static void main(String argv[]) throws Exception {
        TronClient client = new TronClient(new TronNode("grpc.trongrid.io:50051"));

        TronCurrency balance = client.getAccountByAddress(new TronAddress(ADDRESS)).getBalance();

        System.out.printf("Balance of %s is %.0f TRX\n", ADDRESS, balance.getTRX());
    }
}
```

## Documentation

 - [Contract example](https://github.com/Noixion-NXN/Tron-Client-Library/blob/master/docs/examples/example-contract.md)
 - [Wiki](https://github.com/Noixion-NXN/Tron-Client-Library/wiki)

## Console Client

This project also implements a console client with usefull features:

 - Each action is a command, so you can easily make shell scripts that use this tool.
 - The program waits until a transaction is confirmed or fails, telling you in what block was it added.
 - It has an interactive mode for calling smart contract methods, useful for developing.
 

[Usage Examples](https://github.com/Noixion-NXN/Tron-Client-Library/blob/master/docs/examples/console-examples.md)

## Testing

```
mvn test
```

## Building

You can build the code with maven.

In order to build the library, run the following command:

```
mvn install
```

In order to build the console client, run the following command:

```
mvn compile assembly:single
```

The library jar goes to `target/troncli-VER.jar`

The client jar goes to `target/troncli-VER-jar-with-dependencies.jar`


## Console client launchers

In the launchers/bin directory there are two scripts (windows and linux) to facilitate the use of the console client.

You need to move the client jar to the launchers/bin folder and rename it to **troncli.jar**.

Once the client jar has been renamed and moved, it can be used in the following way:

```
troncli COMMAND [COMMAND PARAMETERS]
```
Examples:
```
troncli log-blocks
troncli address -pk 5d4ee5d...
```

## Websites
* [NXN](https://noixion.io)
* [Noixion TV](https://noixion.tv) 

## Contact
* info@noixion.io

