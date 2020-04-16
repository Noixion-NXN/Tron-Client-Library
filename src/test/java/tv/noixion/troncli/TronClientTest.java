package tv.noixion.troncli;

import tv.noixion.troncli.TronClient;
import tv.noixion.troncli.exceptions.GRPCException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tv.noixion.troncli.models.*;

import java.util.List;


public class TronClientTest {
    private TronClient client;
    private int blockNumber = 0;
    private String hashFirstBlock = "00000000000000001ebf88508a03865c71d452e25f4d51194196a1d22b6653dc ";
    private String hashTransaction = "4e214c2cfc823d06d8816062b4550d3be84e65f1be606a212569dea98e498072";
    private String address = "TFA1qpUkQ1yBDw4pgZKx25wEZAqkjGoZo1";
    private String accountName = "JustinSunTron";
    private int freeBandWidth = 5000;
    private int sRepresentativeNumber = 27;


    @Before
    public void setUp() {
        client = new TronClient(new TronNode("grpc.trongrid.io:50051"), new TronNode("grpc.trongrid.io:50052"));
    }

    @After
    public void tearDown(){
        client.shutdown();
    }

    @Test
    public void getListWitnessesTest() {
        try {
            List<TronWitness> tronWitnesses = client.listWitnesses();
            Assert.assertTrue(sRepresentativeNumber <= tronWitnesses.size());
        } catch (GRPCException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void listNodesTest() {
        try {
            List<TronNode> tronNodes = client.listNodes();
            Assert.assertTrue(sRepresentativeNumber <= tronNodes.size());
        } catch (GRPCException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getProposalByIdTest() {
        try {
            TronProposal proposal = client.getProposalById(1);
            Assert.assertEquals(TronProposal.ProposalStatus.APPROVED, proposal.getStatus());
            proposal = client.getProposalById(2);
            Assert.assertEquals(TronProposal.ProposalStatus.DISAPPROVED, proposal.getStatus());

        } catch (GRPCException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void listProposalsTest() {
        try {
            List<TronProposal> tronProposals = client.listProposals();
            Assert.assertTrue(tronProposals.size() >= 14);
            Assert.assertEquals(TronProposal.ProposalStatus.APPROVED, tronProposals.get(1).getStatus());
            Assert.assertEquals(TronProposal.ProposalStatus.DISAPPROVED, tronProposals.get(2).getStatus());
            client.shutdown();
        } catch (GRPCException e) {
            e.printStackTrace();
        }
    }


}
