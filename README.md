# MSCI 541 Assignment
 ##### Stuart Sullivan ~ 20473056
 
 ## How to Run
 ### Running the Indexer
 ```
 java -jar Indexer.jar [latimes-path] [index-output]
 ```
### Running the Lookup
```
java -jar Lookup.jar [index-output] [query-type] [query]
``` 
### Running the Interactive CLI
```
java -jar CLI.jar [index-path]
``` 
Within the CLI you will be asked to input a query. Once the query has run you are able to see the results in the terminal. 
The user can the result number to see the doc. A new query can be started by entering "N", you can see the results again by
entering "R" and you can exit the system by entering "Q".
  
 ## How to Build
 The project was coded in Intellij IDE. The IDE was used to create the artifacts.
 #### For the Indexer
 The indexer artifact requires Indexer.java and DocumentProcessor.java and gson.jar in the lib folder
 #### For the Lookup
 The indexer artifacts requires Lookup.java and DocumentLookup.java and gson.jar in the lib folder
 
 
 ## Assignment 4 Ranking Algorithms
 Each ranking alogrithm implemented has a JAR in the out folder. The ranking engines require the new version off indexes
 to run. Please run the indexer again before using one of the new ranking algorithms.
 To run an algorithm:

 ```
java -jar <Algo>.jar [index-location] [query-file] [results-file]
```