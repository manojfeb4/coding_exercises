#!/bin/ksh
export imdbPropertiesFile=/home/mp29022/imdb/properties/imdb.properties
export HADOOP_CONF_DIR=/mnt/c/hadoop_common/etc/hadoop/
spark-submit \
	--master yarn \
	--deploy-mode cluster \
	--conf "spark.sql.shuffle.partitions=20000" \
	--conf "spark.executor.memoryOverhead=5244" \
	--conf "spark.memory.fraction=0.8" \
	--conf "spark.memory.storageFraction=0.2" \
	--conf "spark.serializer=org.apache.spark.serializer.KryoSerializer" \
	--conf "spark.sql.files.maxPartitionBytes=168435456" \
	--conf "spark.dynamicAllocation.minExecutors=1" \
	--conf "spark.dynamicAllocation.maxExecutors=200" \
	--conf "spark.dynamicAllocation.enabled=true" \
	--conf "spark.executor.extraJavaOptions=-XX:+PrintGCDetails -XX:+PrintGCTimeStamps" \ 
	--jars /home/mp29022/imdb/jars/external/apache-logging-log4j.jar \
	/home/mp29022/imdb/jars/ImdbMainApp.jar --class com.coding.imdb.ImdbMainApp
