package tv.noixion.troncli.utils;

import tv.noixion.troncli.utils.ConsoleUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;


public class ConsoleUtilsTest {
    private String textFile ="Lorem ipsum dolor sit amet\n";


    @Test
    public void readTextFile(){
        File file = new File("src\\test\\resources\\text.txt");
        try {
            String textInFile = ConsoleUtils.readTextFile(file);
            Assert.assertEquals(textFile, textInFile);
        }catch (Exception ex){
            System.err.println(ex);
        }
    }

    @Test
    public void listToStringTest(){
        List <String> listStrings = Arrays.asList("Lorem", "ipsum", "dolor", "sit", "amet");
        String strList = ConsoleUtils.listToString(listStrings);
        Assert.assertEquals(textFile.replace("\n", "").replace(" ", ", "), strList);
    }


}
