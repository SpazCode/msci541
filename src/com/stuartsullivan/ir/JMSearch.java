package com.stuartsullivan.ir;

import com.stuartsullivan.ir.models.CollectionData;
import com.stuartsullivan.ir.models.DocumentIndex;
import com.stuartsullivan.ir.models.PostingList;
import com.stuartsullivan.ir.models.Vocabulary;
import com.stuartsullivan.ir.processors.DocumentLookup;
import com.stuartsullivan.ir.utils.LanguageModel;
import com.stuartsullivan.ir.utils.Scores;

import java.io.*;

/**
 * Created by stuart on 3/19/17.
 */
public class JMSearch {
    public static void main(String[] args) {
        try {
            final long startTime = System.currentTimeMillis();
            // Ensure enough arguments are entered
            if(args.length < 3) {
                // TODO Add Help Message
                System.out.println("Not enough Arguments given for the BM25 Search");
                System.out.println("3 Arguments Required:");
                System.out.println(" -> Path: location indexed data");
                System.out.println(" -> Query Text: location to the query text");
                System.out.println(" -> Output: location to save the results of the query");
                System.exit(1);
                return;
            }

            // Ensure that the path to the index is valid
            String indexPath = args[0].trim();
            File f = new File(indexPath);
            if(!f.exists() || !f.isDirectory()) {
                System.out.println("Invalid Path: Please verify that the directory is correct");
            }

            // Ensure that the path to the query is valid
            f = new File(args[1].trim());
            if(!f.exists() || !f.isFile()) {
                System.out.println("Invalid Query Text: Please verify that the directory is correct");
            }

            String outputPath = args[2].trim();
            File output = new File(outputPath);
            BufferedWriter bfw = new BufferedWriter(new FileWriter(output));
            BufferedReader bfr = new BufferedReader(new FileReader(f));
            PostingList postings = new PostingList(indexPath);
            Vocabulary vocabulary = new Vocabulary(indexPath);
            CollectionData about = CollectionData.LoadCollectionData(indexPath);
            LanguageModel lm = new LanguageModel(0, 1000);
            DocumentIndex docIndex = new DocumentIndex(indexPath);
            String line = "";
            StringBuilder builder = new StringBuilder();
            while ((line = bfr.readLine()) != null) {
                builder.setLength(0);
                String topicId = line.split(":")[0];
                String topicText = line.split(":")[1];
                System.out.println(topicId);
                Scores[] res = DocumentLookup.JMSearch(topicText, about.isStemmedSet(), vocabulary, postings, docIndex, lm);
                int rank = 1;
                for(rank = 0; rank < Math.min(1000, res.length); rank++) {
                    builder.setLength(0);
                    builder.append(topicId);
                    builder.append("\tq0\t");
                    builder.append(res[rank].getDocno());
                    builder.append("\t");
                    builder.append(rank+1);
                    builder.append("\t");
                    builder.append(res[rank].getScore());
                    builder.append("\tspasulliAND\n");
                    bfw.write(builder.toString());
                }
            }
            bfr.close();
            bfw.close();
            final long endTime = System.currentTimeMillis();
            System.out.print((endTime - startTime)/1000 + " secs\n");
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
