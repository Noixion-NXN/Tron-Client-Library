package tv.noixion.troncli.models;

import com.google.gson.JsonObject;
import tv.noixion.troncli.TronClient;
import tv.noixion.troncli.models.contracts.TriggerSmartContractContract;
import tv.noixion.troncli.models.contracts.TronContractFactory;
import org.spongycastle.util.encoders.Hex;
import org.tron.common.crypto.Sha256Hash;
import org.tron.protos.Protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a transaction on the Tron network.
 */
public class TronTransaction {
    /**
     * Transaction result code.
     */
    public enum Code {
        SUCCESS,
        FAILED,
        UNRECOGNIZED
    }

    /**
     * Transaction result.
     */
    public class Result {
        private final Code code;
        private final long fee;
        private final TronCurrency unfreezeAmount;
        private final TronCurrency withdrawAmount;

        public Result(Protocol.Transaction.Result res) {
            switch (res.getRet()) {
                case SUCESS:
                    this.code = Code.SUCCESS;
                    break;
                case FAILED:
                    this.code = Code.FAILED;
                    break;
                default:
                    this.code = Code.UNRECOGNIZED;
            }
            this.fee = res.getFee();
            this.unfreezeAmount = TronCurrency.sun(res.getUnfreezeAmount());
            this.withdrawAmount = TronCurrency.sun(res.getWithdrawAmount());
        }

        /**
         * @return The result code
         */
        public Code getCode() {
            return code;
        }

        /**
         * @return The result fee.
         */
        public long getFee() {
            return fee;
        }

        /**
         * @return The result unfreeze amount.
         */
        public TronCurrency getUnfreezeAmount() {
            return unfreezeAmount;
        }

        /**
         * @return the result withdraw amount.
         */
        public TronCurrency getWithdrawAmount() {
            return withdrawAmount;
        }
    }

    /**
     * Transaction autentication.
     */
    public class Auth {
        private final TronAddress accountAddress;
        private final String accountName;
        private final String permissionName;

        public Auth(Protocol.authority auth) {
            if (auth.hasAccount()) {
                accountAddress = new TronAddress(auth.getAccount().getAddress().toByteArray());
                accountName = new String(auth.getAccount().getName().toByteArray());
            } else {
                accountAddress = null;
                accountName = "";
            }

            this.permissionName = new String(auth.getPermissionName().toByteArray());
        }

        /**
         * @return The account address
         */
        public TronAddress getAccountAddress() {
            return accountAddress;
        }

        /**
         * @return The account name
         */
        public String getAccountName() {
            return accountName;
        }

        /**
         * @return The permission name
         */
        public String getPermissionName() {
            return permissionName;
        }
    }

    private final HashIdentifier id;
    private final Date date;
    private final List<TronContract> contracts;
    private final List<Auth> auths;
    private final Date expiration;
    private final TronCurrency feeLimit;
    private final byte[] refBlockBytes;
    private final HashIdentifier refBlockHash;
    private final long refBlockNum;
    private final List<Result> results;
    private final List<byte[]> signatures;

    public TronTransaction(Protocol.Transaction t) {
        this.id = new HashIdentifier(Sha256Hash.hash(t.getRawData().toByteArray()));
        this.date = new Date(t.getRawData().getTimestamp());
        this.contracts = new ArrayList<>();
        for (Protocol.Transaction.Contract c : t.getRawData().getContractList()) {
            this.contracts.add(TronContractFactory.makeContract(c));
        }
        this.auths = new ArrayList<>();
        //for (Protocol.authority a : t.getRawData().getAuthsList()) {
        //    this.auths.add(new Auth(a));
        //}
        this.expiration = new Date(t.getRawData().getExpiration());
        this.feeLimit = TronCurrency.sun(t.getRawData().getFeeLimit());
        this.refBlockBytes = t.getRawData().getRefBlockBytes().toByteArray();
        this.refBlockHash = new HashIdentifier(t.getRawData().getRefBlockHash().toByteArray());
        this.refBlockNum = t.getRawData().getRefBlockNum();
        this.results = new ArrayList<>();
        //for (Protocol.Transaction.Result res : t.getRetList()) {
        //    this.results.add(new Result(res));
        //}
        this.signatures = new ArrayList<>();
        //for (ByteString bs : t.getSignatureList()) {
        //    this.signatures.add(bs.toByteArray());
        //}
    }

    /**
     * @return The transaction identifier.
     */
    public HashIdentifier getId() {
        return id;
    }

    /**
     * @return The transaction date (timestamp).
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return The list of contracts executed for this transaction.
     */
    public List<TronContract> getContracts() {
        return contracts;
    }

    /**
     * @return The list of authentications.
     */
    public List<Auth> getAuths() {
        return auths;
    }

    /**
     * @return The expiration date.
     */
    public Date getExpiration() {
        return expiration;
    }

    /**
     * @return the fee limit.
     */
    public TronCurrency getFeeLimit() {
        return feeLimit;
    }

    /**
     * @return The reference block bytes.
     */
    public byte[] getRefBlockBytes() {
        return refBlockBytes;
    }

    /**
     * @return The reference block hash.
     */
    public HashIdentifier getRefBlockHash() {
        return refBlockHash;
    }

    /**
     * @return The reference block num.
     */
    public long getRefBlockNum() {
        return refBlockNum;
    }

    /**
     * @return The list of results.
     */
    public List<Result> getResults() {
        return results;
    }

    /**
     * @return The list of signatures.
     */
    public List<byte[]> getSignatures() {
        return signatures;
    }

    /**
     * @return The object as Json.
     */
    public JsonObject toJson() {
        JsonObject result = new JsonObject();

        return result;
    }

    /**
     * Prints the object in stdout.
     * @param indent The indent to use.
     */
    public void print(String indent) {
        this.print(indent, null);
    }

    /**
     * Prints the object in stdout.
     * @param indent The indent to use.
     * @param client The client to interpret the contracts
     */
    public void print(String indent, TronClient client) {
        System.out.println(indent + "Transaction id: " + this.getId().toString());
        System.out.println(indent + "Date: " + this.getDate().toString());
        System.out.println(indent + "Expiration: " + this.getExpiration().toString());
        System.out.println(indent + "Fee Limit: " + String.format("%.0f TRX", this.getFeeLimit().getTRX()));
        System.out.println(indent + "Ref block bytes: " + Hex.toHexString(this.getRefBlockBytes()));
        System.out.println(indent + "Ref block hash: " + this.getRefBlockHash().toString());
        System.out.println(indent + "Ref block num: " + this.getRefBlockNum());

        if (!this.getAuths().isEmpty()) {
            System.out.println(indent + "Authorities: ");
            for (Auth a : this.getAuths()) {
                System.out.println(indent + "    " + a.getAccountAddress() + " / " + a.getAccountName() + " / " + a.getPermissionName());
            }
        }

        if (!this.getContracts().isEmpty()) {
            System.out.println(indent + "Contracts: ");
            for (TronContract c : this.getContracts()) {
                System.out.println(indent + "    " + c.getType().toString() + "-------------------------------");
                if (c.getType() == TronContract.Type.TRIGGER_SMART_CONTRACT && client != null) {
                    ((TriggerSmartContractContract)c).printAndInterpret(indent + "    ", client);
                } else {
                    c.print(indent + "    ");
                }

                System.out.println(indent + "    " + "----------------------------------------------");
            }
        }

        if (!this.getSignatures().isEmpty()) {
            System.out.println(indent + "Signatures: ");
            for (byte[] sig : this.getSignatures()) {
                System.out.println(indent + "    " + Hex.toHexString(sig));
            }
        }

        if (!this.getResults().isEmpty()) {
            System.out.println(indent + "Results: ");
            for (Result r : this.getResults()) {
                System.out.println(indent + "    " + r.getCode().toString() + " / Fee: "
                        + r.getUnfreezeAmount().getSUN() + " SUN / Unfroze amount: "
                        + r.getUnfreezeAmount().getSUN() + " SUN / Withdraw amount: "
                        + r.getWithdrawAmount().getSUN() + " SUN.");
            }
        }
    }
}
