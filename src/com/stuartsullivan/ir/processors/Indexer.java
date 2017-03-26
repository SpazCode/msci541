package com.stuartsullivan.ir.processors;

import com.stuartsullivan.ir.models.CollectionData;
import com.stuartsullivan.ir.models.PostingList;
import com.stuartsullivan.ir.models.Vocabulary;
import com.stuartsullivan.ir.processors.DocumentProcessor;

import java.io.File;

/**
 * Created by stuart on 1/14/17.
 *
 * File: Indexer.java
 *
 * Indexer Program
 */
public class Indexer {
    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();
        try {
            // Ensure enough arguments are entered
            if(args.length < 2) {
                // TODO Add Help Message
                System.out.println("Not enough Arguments given for the Indexer");
                System.out.println("2 Arguments Required:");
                System.out.println(" -> Path: location of the latimes.gz file, the file should be included in the path");
                System.out.println(" -> Output: location to save the indexed data");
                System.exit(1);
                return;
            }

            // Ensure users are inputting a file
            File f = new File(args[0]);
            if(!f.exists() || !f.isFile()) {
                System.out.println("Invalid Path: Please verify that the file location is correct");
                System.exit(1);
                return;
            }

            // Enusre that the destination is new
            f = new File(args[1]);
            if(f.exists() || !f.getParentFile().isDirectory()) {
                System.out.println("Invalid Output: The output must be a valid directory path but must be a new path");
                System.exit(1);
                return;
            }

            // Create Vocabulary and PostingLists Objects
            PostingList postings = new PostingList(args[1]);
            Vocabulary vocabulary = new Vocabulary(args[1]);
            // Create the CollectionData Object
            CollectionData about = new CollectionData();
            about.setStemmedSet(true);
            // Process the Documents
            DocumentProcessor processor = new DocumentProcessor();
            processor.extractCorpus(args[0], args[1], vocabulary, postings, about, about.isStemmedSet());
            // Update the Vocabulary and PostingList
            vocabulary.UpdateDictionary();
            postings.updatePostingList();
            CollectionData.SaveCollectionData(args[1], about);
            final long endTime = System.currentTimeMillis();
            System.out.print((endTime - startTime)/1000 + " secs\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}