# Tron Wallet Client - Console usage examples

List of commands you can use and usage examples.

NOTE: If you use the command examples as they are, you are connecting to the Tron main network. If you want to use a private network, use the options `--full` and `--solidity` to set full node and solidity node respectively.

## Wallets and keys

**create-key**: Creates a new private key (randomly generated).

```
> troncli create-key
Address: [Your address]
Private key: [Your private key]
```

**create-wallet**: Creates a new wallet file.

You can create one from a private key.
```
> troncli create-wallet -pk PRIVATE_KEY
```

You can also create a brand new one.
```
> troncli create-wallet
```

**address**: Gets the address from a wallet or private key.
```
> troncli address -pk PRIVATE_KEY
```
```
> troncli create-wallet -w your_wallet.json
```

**key-from-wallet**: Retrieves the private key from a wallet.
```
> troncli key-from-wallet -w your_wallet.json
```

## Blockchain queries

**block**: retrieves a block from the blockchain.

Get a block by number
```
> troncli block --num 1
```
Get a block by ID
```
> troncli block --id 000000000039049c051da94da60aaac11a413bcd42f5aadf57e91b86e93cb5fa
```

**transaction**: Retrieves a transaction
```
> troncli transaction --id 669f560edb9d9f106fa6cea1739e9014e52f308c652c513637949a42a0741b00
```

**transaction-info**: Retrieves execution information about a transaction.

```
> troncli transaction-info --id 669f560edb9d9f106fa6cea1739e9014e52f308c652c513637949a42a0741b00
```

**transactions-from**: Retrieves transactions that were created by a certain address. Requires a solidity node.
```
> troncli --solidity grpc.trongrid.io:50052 transactions-from -a TTFuSKNmjrDqvywhPCvNXP6ZovRGb2o48h
```

**transactions-to**: Retrieves transactions with the destination set to a certain address. Requires a solidity node.
```
> troncli --solidity grpc.trongrid.io:50052 transactions-to -a TTFuSKNmjrDqvywhPCvNXP6ZovRGb2o48h
```

**account**: Retrieves the information about an account
```
> troncli account -a TTFuSKNmjrDqvywhPCvNXP6ZovRGb2o48h
```

**asset**: Reprieves the information about an asset.

From name.
```
> troncli account --asset MyCoin
```
From owner address.
```
> troncli account -a TTFuSKNmjrDqvywhPCvNXP6ZovRGb2o48h
```

**proposal**: Retrieves the information about a proposal.
```
> troncli proposal --num 1
```

**nodes**: Retrieves the list of nodes.
```
> troncli nodes
```

**assets**: Retrieves the list of assets.
```
> troncli assets
```

**witnesses**: Retrieves the list of witnesses.
```
> troncli witnesses
```

**proposals**: Retrieves the list of proposals.
```
> troncli proposals
```

## Transferences

**transfer**: Transfers TRX to another account.
```
> troncli transfer -w YOUR_WALLET -a ADDRESS_TO_SEND --trx AMOUNT
```

**transfer-asset**: Transfers Assets to another account.
```
> troncli transfer -w YOUR_WALLET --asset ExampleCoin -a ADDRESS_TO_SEND --amount AMOUNT
```

## Assets
**asset-issue**: Creates a new asset.
```
> troncli asset-issue -w YOUR_WALLET --name ExampleCoin --abbr EC --supply 1000000 --conversion 1 1  --url example.com --description "example description" --start 2018/11/20 --end 2200/01/01
```

**participate-asset**: Participates in an asset, exchanging TRX for Assets.
```
> troncli participate-asset -w YOUR_WALLET --asset ExampleCoin --amount AMOUNT_OF_ASSETS
```

**asset-update**: Updates an asset. Must be the account owner of the asset.
```
> troncli asset-update -w YOUR_WALLET --url example.com --description "example description" --free-net-limit 100 --public-net-limit 100
```

**asset-unfreeze**: Unfreezes the frozen amount of an asset. Must be the account owner of the asset.
```
> troncli asset-unfreeze -w YOUR_WALLET 
```

## Energy and bandwidth

**account-resource**: Retrieves the resource information for an account (energy and bandwidth).
```
> troncli account-resource -a YOUR_ADDRESS
```

**freeze-for-energy**: Freezes balance for energy.
```
> troncli freeze-for-energy -w YOUR_WALLET --trx 5000
```

**freeze-for-bandwidth**: Freezes balance for bandwidth.
```
> troncli freeze-for-bandwidth -w YOUR_WALLET --trx 5000
```

**unfreeze-energy**: Unfreezes the balance frozen for energy.
```
> troncli unfreeze-energy -w YOUR_WALLET
```

**unfreeze-bandwidth**: Unfreezes the balance frozen for bandwidth.
```
> troncli unfreeze-bandwidth -w YOUR_WALLET --trx 5000
```

## Voting

**vote**: Votes for witnesses.
```
> troncli vote -w YOUR_WALLET --vote TC1ZCj9Ne3j5v3TLx5ZCDLD55MU9g3XqQW 1000 --vote TDGmmTC7xDgQGwH4FYRGuE7SFH2MePHYeH 500
```

## Proposals

**create-proposal**: Creates a new proposal.
```
> troncli create-proposal -w YOUR_WALLET --param 20 1
```

**approve-proposal**: Approves a proposal. Must be super representative.
```
> troncli approve-proposal -w YOUR_WALLET --num 1
```

**delete-proposal**: Deletes a proposal.
```
> troncli delete-proposal -w YOUR_WALLET --num 1
```

## Smart contracts

**deploy**: Deploys a smart contract.
```
> troncli deploy -w YOUR_WALLET --name MyNewContract --abi-file mycontract.abi.json --bytecode-file mycontract.bytecode.hex --constructor "MyContract(string,uint256)" --params "Example String" 700 --fee-limit 1000
```

**update-settings**: Updates the settings of a smart contract.
```
> troncli update-settings -w YOUR_WALLET -a CONTRACT_ADDRESS --consume-user 20
```

**contract**: Retrieves the information about a contract.
```
> troncli contract -a CONTRACT_ADDRESS
```

**contract-methods**: Retrieves the list of methods signatures of a contract.
```
> troncli contract-methods -a CONTRACT_ADDRESS
```

**contract-events**: Retrieves the list of events signatures of a contract.
```
> troncli contract-events -a CONTRACT_ADDRESS
```

**trigger**: Triggers a method of a smart contract.
```
> troncli trigger -w YOUR_WALLET -a CONTRACT_ADDRESS --method method(uint256) --params 300 --call-value 0 
```

**contract-interactive**: Interactive mode for triggering smart contracts methods.
```
> troncli contract-interactive -w YOUR_WALLET -a CONTRACT_ADDRESS
```

**contract-listen**: Listens for smart contract events.
```
> troncli contract-listen -w YOUR_WALLET -a CONTRACT_ADDRESS
```

