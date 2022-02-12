package com.coding.imdb.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Properties;

@Slf4j
public class CommonUtil {

    public static SparkSession createSparkSessionLocal() {

        var spark = SparkSession.builder()
                .master("local[4]")
                .appName("CodingExerciseBGC")
                .config("spark.driver.memory", "20g")
                .config("spark.driver.cores", "4")
                .config("spark.executor.memory", "20g")
                .config("spark.sql.crossJoin.enabled", "true")
                .getOrCreate();
        return spark;

    }

    public static SparkSession createSparkSession() {

        var spark = SparkSession.builder()
                .master("yarn")
                .appName("CodingExerciseBGC")
                .config("spark.sql.crossJoin.enabled", "true")
                .getOrCreate();
        return spark;

    }

    public static Dataset<Row> readTextFile(SparkSession spark, Properties prop, String propKey) {

        var fileName = prop.getProperty(propKey);
        var df = spark.emptyDataFrame();
        ArrayList<StructField> schema = new ArrayList<>();

        if (Files.exists(Path.of(fileName)))
            df = spark.read().option("delimiter", "\t").option("header", "true").csv(fileName);
        else
            throw new RuntimeException(fileName + " Doesnt Exist ");

        log.debug("prop key: {}", propKey);
        //df.show(5);
        //df.printSchema();
        return df;
    }

    public static Properties getProperties(String propFilePath) {
        Properties prop = new Properties();


        try {
            InputStream input = new FileInputStream(propFilePath);
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return prop;
    }

    public static void writeCsvFile(Dataset<Row> df, String outputFileName) {

        df.repartition(10).write().option("header", "true").option("delimiter", ",").mode(SaveMode.Overwrite).csv(outputFileName)
        ;
        log.info("{} file is created ", outputFileName);
    }

}
