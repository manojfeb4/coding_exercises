package com.coding.imdb;

import com.coding.imdb.parser.ImdbAnalysis;
import com.coding.imdb.util.CommonUtil;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;






/*
    Author: Manoj Manokar
    Description:  Imdb Movie Analytis - BGC Coding Exercise
 */


public class ImdbMainApp {
    public static void main(String[] args) {
        Logger log = Logger.getLogger(ImdbMainApp.class);
        String imdbPropertiesFile = System.getenv("imdbPropertiesFile");
        log.info("imdbPropertiesFile: " + imdbPropertiesFile);
        if (imdbPropertiesFile.isEmpty() || imdbPropertiesFile.isBlank()) {
            log.info("imdb Properties File is not found");
            System.exit(2);
        }
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);

        var spark = CommonUtil.createSparkSession();
        new ImdbAnalysis().analyseImdb(imdbPropertiesFile, spark);


    }
}
