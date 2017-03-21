
package com.stuartsullivan.ir.processors;

import com.stuartsullivan.ir.models.*;
import com.stuartsullivan.ir.utils.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by stuart on 1/13/17.
 *
 * File: DocumentLookup.java
 *
 * Logic to Search for documents in the data index
 */
public class DocumentLookup {
    public static void Lookup(String path, String type, String query) {
        try {
            // Find the DOCNO based on the ID if the query is by ID
            if (type.equals("id")) {
                query = SearchIndex(path, query);
            }
            // If no DOCNO was found then print an error Message
            if(query == null) {
                System.out.println("Error: No Document with that ID found");
                return;
            }
            // Load the Document Store
            Document doc = DocumentIndex.LoadDocument(path, query);
            // If no document was loaded return an error
            if(doc == null) {
                System.out.println("No Document Found with DOCNO: " + query);
                return;
            }
            // Output data about the doucment
            System.out.println("docno : " + doc.getDocno());
            System.out.println("internal id : " + doc.getDocid());
            System.out.println("date : " + doc.getDate());
            System.out.println("headline : " + doc.getHeadline());
            System.out.println("raw : \n" + doc.getRaw());
        } catch (Exception e) {
            // Spit out an error message about finding the file
            System.out.print("There was an error receiving your document");
            e.printStackTrace();
            return;
        }
    }

    public static SimpleListInt QueryAnd(String path, String query, PostingList postings, Vocabulary vocabulary) {
        ArrayList<String> tokens = Lexiconer.Tokenize(query, false);
        SimpleListInt tokenIds = new SimpleListInt();
        for(String token: tokens) {
            if(vocabulary.get(token) >= 0)
                tokenIds.add(vocabulary.get(token));
        }
        SimpleListInt[] foundPostings = new SimpleListInt[tokenIds.getLength()];
        int count = 0;
        for(int i = 0; i < tokenIds.getLength(); i++) {
            foundPostings[count] = postings.get(tokenIds.get(i));
            count++;
        }
        Arrays.sort(foundPostings);
        SimpleListInt results = new SimpleListInt(2);
        SimpleListInt check = new SimpleListInt(foundPostings[0].getLength()/2);
        for(int i = 0; i < foundPostings[0].getLength(); i+=2) {
            check.add(foundPostings[0].get(i));
        }
        if (foundPostings.length <= 1) {
            return check;
        } else {
            for (count = 1; count < tokenIds.getLength(); count++) {
                results = new SimpleListInt(2);
                int i = 0;
                int j = 0;
                while (i < check.getLength() && j < foundPostings[count].getLength()) {
                    if (check.get(i) == foundPostings[count].get(j)) {
                        results.add(check.get(i));
                        i += 1;
                        j += 2;
                    } else if (check.get(i) > foundPostings[count].get(j)) {
                        j += 2;
                    } else {
                        i += 1;
                    }
                }
                check = results;
            }
        }
        return results;
    }

    public static Scores[] BM25Search(String query, boolean stem, Vocabulary vocab, PostingList postings, DocumentIndex index, BM25 bm25) {
        SimpleListInt tokenIds = Lexiconer.TokenIds(Lexiconer.Tokenize(query, stem), vocab);
        HashMap<Integer, Integer> counts = Lexiconer.CountTokens(tokenIds);
        HashMap<Integer, Scores> docScores = new HashMap<Integer, Scores>();
        int token, i, j, docid ;
        float score;
        Scores scoreObj;
        for(i = 0; i < tokenIds.getLength(); i++) {
            token = tokenIds.get(i);
            for(j = 0; j < postings.get(token).getLength(); j+=2) {
                docid = postings.get(token).get(j);
                if (docScores.containsKey(docid)) continue;
                score = bm25.score(postings, vocab, index.getDocCount(), index.LoadDocument(docid), tokenIds, counts);
                scoreObj = new Scores(docid, index.get(docid), score);
                docScores.put(docid, scoreObj);
            }
        }
        Scores[] scores = docScores.values().toArray(new Scores[docScores.values().size()]);
        Arrays.sort(scores);
        return scores;
    }

    public static Scores[] CosineSearch(String query, boolean stem, Vocabulary vocab, PostingList postings, DocumentIndex index, Cosine cosine) {
        SimpleListInt tokenIds = Lexiconer.TokenIds(Lexiconer.Tokenize(query, stem), vocab);
        HashMap<Integer, Integer> counts = Lexiconer.CountTokens(tokenIds);
        HashMap<Integer, Scores> docScores = new HashMap<Integer, Scores>();
        int token, i, j, docid ;
        float score;
        Scores scoreObj;
        for(i = 0; i < tokenIds.getLength(); i++) {
            token = tokenIds.get(i);
            for(j = 0; j < postings.get(token).getLength(); j+=2) {
                docid = postings.get(token).get(j);
                if (docScores.containsKey(docid)) continue;
                score = cosine.score(tokenIds, counts, index.getDocCount(), index.LoadDocument(docid), vocab, postings, index);
                scoreObj = new Scores(docid, index.get(docid), score);
                docScores.put(docid, scoreObj);
            }
        }
        Scores[] scores = docScores.values().toArray(new Scores[docScores.values().size()]);
        Arrays.sort(scores);
        return scores;
    }

    public static Scores[] JMSearch(String query, boolean stem, Vocabulary vocab, PostingList postings, DocumentIndex index, LanguageModel lm) {
        SimpleListInt tokenIds = Lexiconer.TokenIds(Lexiconer.Tokenize(query, stem), vocab);
        HashMap<Integer, Integer> counts = Lexiconer.CountTokens(tokenIds);
        HashMap<Integer, Scores> docScores = new HashMap<Integer, Scores>();
        int token, i, j, docid ;
        float score;
        Scores scoreObj;
        for(i = 0; i < tokenIds.getLength(); i++) {
            token = tokenIds.get(i);
            for(j = 0; j < postings.get(token).getLength(); j+=2) {
                docid = postings.get(token).get(j);
                if (docScores.containsKey(docid)) continue;
                score = lm.score(tokenIds, counts, index.LoadDocument(docid), postings, vocab, index);
                scoreObj = new Scores(docid, index.get(docid), score);
                docScores.put(docid, scoreObj);
            }
        }
        Scores[] scores = docScores.values().toArray(new Scores[docScores.values().size()]);
        Arrays.sort(scores);
        return scores;
    }

    private static String SearchIndex(String path, String id) throws IOException {
        // Load the index file
        File f = new File(path + "/index/index.csv");
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line = "";
        // Read the index until we find a matching ID
        while((line = br.readLine()) != null) {
            String[] index = line.split(",");
            if(index.length == 2) {
                if(index[0].equals(id)) {
                    return index[1];
                }
            }
        }
        // Return null if no matching ID was found
        return null;
    }
}
