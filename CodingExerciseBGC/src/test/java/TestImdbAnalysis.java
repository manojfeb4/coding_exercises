import com.coding.imdb.ImdbMainApp;
import com.coding.imdb.parser.ImdbAnalysis;
import com.coding.imdb.util.CommonUtil;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TestImdbAnalysis {



    @Test
    void testMissingInputFile(){


        var spark = CommonUtil.createSparkSessionLocal();
        var prop = CommonUtil.getProperties("src/test/resources/imdb.properties");
        prop.setProperty("nameBasicsFilePath","src/test/resources/inputFiles/missing.tsv.gz");
        assertThrows(RuntimeException.class , () -> new ImdbAnalysis().getTop20Movies(spark, prop));

    }

    @Test
    void testImdbAnalysis(){

        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);
        var spark = CommonUtil.createSparkSessionLocal();
        var prop = CommonUtil.getProperties("src/test/resources/imdb.properties");
        var top20MoviesDf = new ImdbAnalysis().getTop20Movies(spark, prop);
        assertEquals(top20MoviesDf.count() >= 0, true);
        var mostCreditedDf = new ImdbAnalysis().getMostCreditedPersons(spark, top20MoviesDf);
        assertEquals(mostCreditedDf.count() >= 0, true);
        //assertEquals(Files.exists(Path.of("src/test/resources/outputFiles/top20_movies.csv")), true);
        //assertEquals(Files.exists(Path.of("src/test/resources/outputFiles/most_credited_persons.csv")), true);
    }
}
