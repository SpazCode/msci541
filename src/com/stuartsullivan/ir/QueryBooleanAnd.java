package com.stuartsullivan.ir;

import com.stuartsullivan.ir.models.DocumentIndex;
import com.stuartsullivan.ir.models.PostingList;
import com.stuartsullivan.ir.models.Vocabulary;
import com.stuartsullivan.ir.utils.SimpleListInt;

import java.io.*;

/**
 * Created by stuart on 1/29/17.
 */
public class QueryBooleanAnd {
    public static void main(String[] args) {
        try {
            // Ensure enough arguments are entered
            if(args.length < 3) {
                // TODO Add Help Message
                System.out.println("Not enough Arguments given for the Indexer");
                System.out.println("3 Arguments Required:");
                System.out.println(" -> Path: location indexed data");
                System.out.println(" -> Query Text: location to the query text");
                System.out.println(" -> Output: location to save the results of the query");
                System.exit(1);
                return;
            }

            // Ensure that the path to the index is valid
            String indexPath = args[0];
            File f = new File(indexPath);
            if(!f.exists() || !f.isDirectory()) {
                System.out.println("Invalid Path: Please verify that the directory is correct");
            }

            // Ensure that the path to the query is valid
            f = new File(args[1]);
            if(!f.exists() || !f.isFile()) {
                System.out.println("Invalid Query Text: Please verify that the directory is correct");
            }

            String outputPath = args[2];
            BufferedWriter bfw = new BufferedWriter(new FileWriter(outputPath));
            BufferedReader bfr = new BufferedReader(new FileReader(f));
            PostingList postings = new PostingList(indexPath);
            Vocabulary vocabulary = new Vocabulary(indexPath);
            DocumentIndex docIndex = new DocumentIndex(indexPath);
            String line = "";
            while ((line = bfr.readLine()) != null) {
                String topicId = line.split(":")[0];
                String topicText = line.split(":")[1];
                SimpleListInt res = DocumentLookup.QueryAnd(indexPath, topicText, postings, vocabulary);
                int count = 0;
                int maxScore = res.getLength();
                for(count = 0; count < maxScore; count++) {
                    bfw.write(topicId +"\t"+ docIndex.get(res.get(count)) +"\t"+ (count+1) +"\t"+
                            (maxScore-count) +"\t"+ "spasulliAND\n");
                }
            }
            bfr.close();
            bfw.close();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
