# coding_exercises

I. This program calculates two metrics from imdb repository
  a. Retrieval of top 20 movies based on Ranking logic
  b. Retreival of most credited persons of those 20 movies
  

a. Top 20 movies:

1. create the dataframe from title.ratings.tsv.gz (tconst, averageRating, numVotes)
2. filter the records which has atleast of minimum of 50 votes ( numVotes >= 50)
3. calculate the average number of votes
4. calculate the ranking = (numVotes/averageNumberOfVotes) * averageRating
5. sort the records based on ranking and fetch top 20 ranked movies
6. create the datafram from title.basics.tsv.gz 
7. join (5) and (6) on tconst to derive primaryTitle of the movie
8. final df is written to csv file

b. Most credited persons of 20 movies

9. create the dataframe from title.principals.tsv.gz 
10. join (8) and (9) on tconst to get nconst
11. create the dataframe from name.basics.tsv.gz
12. join (10) and (11) on nconst to fetch the most credited person (primaryName)
13. display the output with originalTitle and primaryTitle 


II. Steps to productionize the code :

1. export imdbPropertiesFile=/users/manoj/imdb/imdb.properties
2. place the jar file ImdbMainApp.jar in /users/manoj/imdb/jars/
3. spark-submit --master yarn --name /users/manoj/imdb/jars/ImdbMainApp.jar --class com.coding.imdb.ImdbMainApp --driver-memory 20g --executor-memory 20g --executor-cores 4 --num-executors 10


III. Build commands:
1. cd /mnt/c/users/manoj/IdeaProjects/demo/CodingExerciseBGC/
2. mvn clean install
3. cp /mnt/c/users/manoj/IdeaProjects/demo/CodingExerciseBGC/target/ImdbMainApp.jar /home/mp29022/imdb/jars/
4. chmod 755 /home/mp29022/imdb/jars/ImdbMainApp.jar
5. cp /mnt/c/users/manoj/IdeaProjects/demo/CodingExerciseBGC/src/main/resources/imdb.properties  /home/mp29022/imdb/properties/
6. ksh -x /home/mp29022/imdb/bin/imdbAnalysis.ksh


IV. Area of code improvements:
1.  Facing some performance issue in the join while fetching most credited person.  Need to look at it.
2.  Unable to write the output df to the file as there is some issue in hadoop common libraries, could not fix it in the given window
  
