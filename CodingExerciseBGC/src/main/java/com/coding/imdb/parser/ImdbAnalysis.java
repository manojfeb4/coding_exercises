package com.coding.imdb.parser;

import com.coding.imdb.ImdbMainApp;
import com.coding.imdb.helper.ImdbConstants;
import com.coding.imdb.util.CommonUtil;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;

import java.util.Properties;

import static org.apache.spark.sql.functions.col;

public class ImdbAnalysis {

    Logger log = Logger.getLogger(ImdbMainApp.class);
    Dataset<Row> nameBasicsDf;
    Dataset<Row> titleAkasDf;
    Dataset<Row> titleBasicsDf;
    Dataset<Row> titleCrewDf;
    Dataset<Row> titleEpisodeDf;
    Dataset<Row> titlePrincipalsDf;
    Dataset<Row> titleRatingsDf;

    public void analyseImdb(String propFilePath, SparkSession spark) {

        var prop = CommonUtil.getProperties(propFilePath);
        createImdbDf(spark, prop);
        var dfTopMovies = getTop20Movies(spark, prop);
        var top20MoviesoutputFile = prop.getProperty(ImdbConstants.TOP20_MOVIES_OUTPUT_FILE);
        // CommonUtil.writeCsvFile(dfTopMovies, top20MoviesoutputFile);
        var dfMostCredited = getMostCreditedPersons(spark, dfTopMovies);
    }

    void createImdbDf(SparkSession spark, Properties prop) {

        nameBasicsDf = CommonUtil.readTextFile(spark, prop, ImdbConstants.NAME_BASICS_FILE_PATH);
        // titleAkasDf = CommonUtil.readTextFile(spark,prop, ImdbConstants.TITLE_AKAS_FILE_PATH);
        titleBasicsDf = CommonUtil.readTextFile(spark, prop, ImdbConstants.TITLE_BASICS_FILE_PATH);
        // titleCrewDf = CommonUtil.readTextFile(spark,prop, ImdbConstants.TITLE_CREW_FILE_PATH);
        // titleEpisodeDf = CommonUtil.readTextFile(spark,prop, ImdbConstants.TITLE_EPISODE_FILE_PATH);
        titlePrincipalsDf = CommonUtil.readTextFile(spark, prop, ImdbConstants.TITLE_PRINCIPALS_FILE_PATH);
        titleRatingsDf = CommonUtil.readTextFile(spark, prop, ImdbConstants.TITLE_RATINGS_FILE_PATH);

        log.info("nameBasics : " + nameBasicsDf.count());
        log.info("titleBasics : " + titleBasicsDf.count());
        log.info("titlePrincipals : " + titlePrincipalsDf.count());
        log.info("titleRatings : " + titleRatingsDf.count());

    }

    public Dataset<Row> getTop20Movies(SparkSession spark, Properties prop) {
        createImdbDf(spark, prop);
        var topMoviesDf = titleRatingsDf.filter(col("numVotes").$greater$eq(50));
        topMoviesDf = topMoviesDf.withColumn("numVotes", col("numVotes").cast("int"));
        topMoviesDf = topMoviesDf.withColumn("averageRating", col("averageRating").cast("double"));

        topMoviesDf.show(5);
        var avgVotes = topMoviesDf.groupBy(functions.lit("1")).avg("numVotes").collectAsList().get(0).getDouble(1);
        log.debug("avgVotes: " + avgVotes);
        topMoviesDf = topMoviesDf.withColumn("ranking", col("numVotes").$div(functions.lit(avgVotes)).multiply(col("averageRating")));
        topMoviesDf = topMoviesDf.sort(col("ranking").desc()).limit(20);
        topMoviesDf = topMoviesDf.join(titleBasicsDf, topMoviesDf.col("tconst").equalTo(titleBasicsDf.col("tconst")), "inner")
                .select(titleBasicsDf.col("primaryTitle"), topMoviesDf.col("*"));
        topMoviesDf.show(5);
        topMoviesDf.printSchema();
        return topMoviesDf;
    }

    public Dataset<Row> getMostCreditedPersons(SparkSession spark, Dataset<Row> topMoviesDf) {

        //var mostCreditedDf = spark.sqlContext().sql("select t1.tconst, t2.* from titlePrincipalsTbl t1 inner join topMoviesTbl t2 on t1.tconst = t2.tconst");
        topMoviesDf = topMoviesDf.repartition(col("tconst"));
        titlePrincipalsDf = titlePrincipalsDf.repartition(col("tconst"));


        var mostCreditedDf = titlePrincipalsDf.as("t1").join(topMoviesDf.as("t2"),
                        col("t1.tconst").equalTo(col("t2.tconst")), "inner")
                .select(col("t2.*"), col("t1.nconst")).repartition(col("tconst"));

       log.info("most credited df count: " + mostCreditedDf.count());
        mostCreditedDf.printSchema();
        titleBasicsDf = titleBasicsDf.repartition(col("tconst"));
        mostCreditedDf = titleBasicsDf.as("t1").join(functions.broadcast(mostCreditedDf.as("t2")),
                        col("t1.tconst")
                                .equalTo(col("t2.tconst")), "inner")
                .select(col("t2.tconst"), col("t2.nconst"), col("t2.primaryTitle"), col("t1.originalTitle"))
                .repartition(col("nconst"));
        mostCreditedDf.printSchema();

        nameBasicsDf = nameBasicsDf.repartition(col("nconst"));
        mostCreditedDf = nameBasicsDf.as("t1").join(mostCreditedDf.as("t2"),
                        col("t1.nconst").equalTo(col("t2.nconst")), "inner")
                .select(col("t2.primaryTitle"), col("t2.originalTitle"), col("t1.primaryName"));
        mostCreditedDf.show(5);
        mostCreditedDf.printSchema();
        return mostCreditedDf;
    }
}
