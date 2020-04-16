package tv.noixion.troncli.utils;

import tv.noixion.troncli.utils.TronUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TronUtilsTest {
    private String address = "THph9K2M2nLvkianrMGswRhz5hjSA9fuH7";
    private String etherAddress = "0x7f5Cf3d954D3Ee108D54D4Bb0f987E80A4b5eb0f";
    private byte[] base58Address = {65, 86, 36, -63, 46, 48, -117, 3, -95, -90, -78, 29, -101, -122, -29, -108, 47, -84, 26, -71, 43};
    private BigInteger intNumber = new BigInteger( "314159" );
    private double doubleNumber = 3.14159;
    private double incorrectDoubleNumber = 3.14158;
    private int decimals = 5;
    private String testHash = "9c22ff5f21f0b81b113e63f7db6da94fedef11b2119b4088b89664fb9a3cb658";
    private String test = "test";
    private char[] testChar = {'t', 'e', 's', 't'};

    @Test
    public void testDecodeFromBase58 () {
        Assert.assertEquals( Arrays.toString( TronUtils.decodeFromBase58( address ) ), Arrays.toString( base58Address ) );
    }

    @Test
    public void testEncodeToBase58Check () {
        Assert.assertEquals( TronUtils.encodeToBase58Check( base58Address ), address );
    }

    @Test
    public void testValidateAddress () {
        Assert.assertEquals( TronUtils.validateAddress( address ), true );
        Assert.assertNotEquals( TronUtils.validateAddress( address ), etherAddress );
    }

    @Test
    public void testDoubleToInt () {
        Assert.assertEquals( TronUtils.doubleToInt( doubleNumber, decimals ), intNumber );
        Assert.assertNotEquals( TronUtils.doubleToInt( doubleNumber, decimals + 1 ), intNumber );

    }

    @Test
    public void testIntToDouble () {
        Assert.assertEquals( TronUtils.intToDouble( intNumber, decimals ), doubleNumber, 0 );
        Assert.assertNotEquals( TronUtils.intToDouble( intNumber, decimals ), incorrectDoubleNumber, 0 );

    }

    @Test
    public void testKeccack256 () {
        Assert.assertEquals( TronUtils.valueToString( TronUtils.keccak256( test.getBytes() ) ), testHash );
    }

    @Test
    public void testValueToString () {
        Assert.assertEquals( TronUtils.valueToString( TronUtils.keccak256( test.getBytes() ) ), testHash );
    }

    @Test
    public void testAsStringList () {
        char[] cArray = test.toCharArray();
        List<String> list = new ArrayList<>( cArray.length );
        for (char c : cArray) {
            list.add( String.valueOf( c ) );
        }
        Assert.assertEquals( TronUtils.asStringsList( testChar ), list );
    }


}
