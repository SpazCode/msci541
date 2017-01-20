# MSCI 541 Assignment #1
 ##### Stuart Sullivan ~ 20473056
 ## The Project
 Included in this project is both the Document Indexer and the Lookup programs for Assignment #1. Precompiled artifacts are stored in /out/artifacts.
 ```
─ lib
    ─ gson-2.3.1.jar
─ out
    ─ Indexer_jar
        ─ Indexer.jar
    ─ Lookup_jar
        ─ Lookup.jar
─ src
    ─ com.stuartsullivan.ir
        ─ Document.java
        ─ DoucmentLookup.java
        ─ DocumentProcessor.java
        ─ Indexer.java
        ─ Lookup.java
        ─ indexer.METE-INF
            ─ MANIFEST.MF
        ─ lookup.METE-INF
            ─ MANIFEST.MF
 ```
 ## How to Run
 ### Running the Indexer
 ```
 java -jar Indexer.jar [latimes-path] [index-output]
 ```
 ### Running the Lookup
  ```
  java -jar Lookup.jar [index-output] [query-type] [query]
  ```
 ## How to Build
 The project was coded in Intellij IDE. The IDE was used to create the artifacts.
 #### For the Indexer
 The indexer artifact requires Indexer.java and DocumentProcessor.java and gson.jar in the lib folder
 #### For the Lookup
 The indexer artifacts requires Lookup.java and DocumentLookup.java and gson.jar in the lib folder
 