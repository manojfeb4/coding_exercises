#!/bin/ksh
export imdbPropertiesFile=/home/mp29022/imdb/properties/imdb.properties
spark-submit --master yarn --deploy-mode cluster --class com.coding.imdb.ImdbMainApp --jars /home/mp29022/imdb/jars/external/apache-logging-log4j.jar /home/mp29022/imdb/jars/ImdbMainApp.jar
