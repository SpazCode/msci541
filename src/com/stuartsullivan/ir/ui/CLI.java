package com.stuartsullivan.ir.ui;

import com.stuartsullivan.ir.lookup.DocumentLookup;
import com.stuartsullivan.ir.models.*;
import com.stuartsullivan.ir.ranking.BM25;
import com.stuartsullivan.ir.ranking.Scores;
import com.stuartsullivan.ir.snippit.SnippetEngine;
import com.stuartsullivan.ir.utils.Lexiconer;
import com.stuartsullivan.ir.utils.SimpleListInt;
import com.sun.xml.internal.ws.util.StringUtils;

import javax.print.Doc;
import java.io.File;
import java.util.Scanner;

/**
 * Created by stuart on 3/26/17.
 * The command line interface for this search engine
 */
public class CLI {
    public static void main(String[] args) {
        // Arguments to Load
        // The path to the index
        if(args.length < 1) {
            System.out.println("Not enough Arguments");
            System.out.println("1 Arguments needed: ");
            System.out.println(" -> Index Path: The path to the processed index files");
            return;
        }

        // Check to see if the file path exists
        final String path = args[0];
        File f = new File(path);
        if(!f.exists() || !f.isDirectory()) {
            System.out.println("The index location is invalid, please input the correct index location");
            return;
        }
        // State Controlling Variables
        boolean searching = true;
        boolean showing = true;
        // Scanner to handle user input
        Scanner scanner = new Scanner(System.in);
        // Search Engine Models
        CollectionData about = CollectionData.LoadCollectionData(path);
        PostingList postings = new PostingList(path);
        DocumentIndex index = new DocumentIndex(path);
        Vocabulary vocab = new Vocabulary(path);
        // Load the Ranker
        BM25 bm25 = new BM25();
        // Place Holder variables
        String query;
        long startTime, endTime;
        Scores[] scores;
        Document doc;

        while (searching) {
            showing = true;
            System.out.println("Please enter a query: ");
            query = scanner.next();
            System.out.println("Searching");
            startTime = System.currentTimeMillis();
            SimpleListInt tokenIds = Lexiconer.TokenIds(Lexiconer.Tokenize(query, about.isStemmedSet()), vocab);
            scores = DocumentLookup.Search(tokenIds, vocab, postings, index, bm25);
            endTime = System.currentTimeMillis();
            scores = SnippetEngine.Snippets(scores, index, vocab, tokenIds, about.isStemmedSet(), 0, 10);
            while(showing) {
                for (int i = 0; i < 10; i++) {
                    System.out.print(i + 1);
                    System.out.print(")\t");
                    System.out.print(scores[i].getDocHeadline());
                    System.out.print("\t");
                    System.out.println(createDateString(scores[i].getDocno()));
                    System.out.println(scores[i].getDocSnippet());
                    System.out.print("(");
                    System.out.print(scores[i].getDocno());
                    System.out.println(")");
                    System.out.println();
                }
                System.out.println("Done In: " +  ((float) (endTime - startTime)/1000) + " secs");
                while(true) {
                    System.out.println("Enter the number to see the contents of the document\nEnter 'Q' to exit the search engine\nEnter 'N' to make another query\nEnter 'R' to See the Results again: ");
                    query = scanner.next().trim();
                    try {
                        int pos = Integer.parseInt(query);
                        if (pos > 10) {
                            System.out.println("Command was invalid");
                            continue;
                        } else {
                            doc = index.LoadDocument(scores[pos-1].getDocid());
                            printDocument(doc);
                        }
                    } catch (Exception e) {
                        int pos = checkScores(scores, query, 10);
                        if(pos >= 0) {
                            doc = index.LoadDocument(scores[pos].getDocid());
                            printDocument(doc);
                        }
                        else if (query.equals("Q")) {
                            showing = false;
                            searching = false;
                            break;
                        } else if (query.equals("N")) {
                            showing = false;
                            break;
                        } else if (query.equals("R")) {
                            break;
                        } else {
                            System.out.println("Command was invalid");
                        }
                    }
                }
            }
        }
    }

    private static String createDateString(String docNo) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        builder.append(docNo.subSequence(2, 4));
        builder.append("/");
        builder.append(docNo.subSequence(4, 6));
        builder.append("/19");
        builder.append(docNo.subSequence(6, 8));
        builder.append(")");
        return builder.toString();
    }

    private static int checkScores(Scores[] scores, String docNo, int max) {
        for(int i = 0; i < max; i++) {
            if(scores[i].getDocno().equals(docNo)) {
                return i;
            }
        }
        return -1;
    }

    private static void printDocument(Document doc) {
        System.out.println(doc.getHeadline());
        System.out.println(doc.getByLine());
        System.out.println(doc.getDate());
        System.out.println(doc.getText());
    }
}
