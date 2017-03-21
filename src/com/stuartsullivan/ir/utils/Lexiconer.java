package com.stuartsullivan.ir.utils;

import com.stuartsullivan.ir.models.PostingList;
import com.stuartsullivan.ir.models.Vocabulary;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by stuart on 1/21/17.
 */
public class Lexiconer {
    public static ArrayList<String> Tokenize(String sentence, boolean stem) {
        sentence = sentence.toLowerCase();
        ArrayList<String> tokens = new ArrayList<String>();
        int start = 0;
        int end = 0;
        for(char c: sentence.toCharArray()) {
            if (start != end && !Character.isDigit(c) && !Character.isLetter(c)) {
                String token = sentence.substring(start, end).trim();
                if (stem) {
                    tokens.add(Stemmer.stem(token));
                } else tokens.add(token);
                start = end;
            }
            end++;
            if (!Character.isDigit(c) && !Character.isLetter(c)) {
                start = end;
            }
        }
        if(start != end) {
            tokens.add(sentence.substring(start, end).trim());
        }
        return tokens;
    }

    public static HashMap<Integer, Integer> CountTokens(SimpleListInt tokenIds) {
        HashMap<Integer, Integer> termCounts = new HashMap<Integer, Integer>();
        int i;
        for(i = 0; i < tokenIds.getLength(); i++) {
            if(termCounts.containsKey(tokenIds.get(i))) {
                int val = termCounts.get(tokenIds.get(i));
                val += 1;
                termCounts.put(tokenIds.get(i), val);
            } else {
                termCounts.put(tokenIds.get(i), 1);
            }
        }
        return termCounts;
    }

    public static SimpleListInt TokenIds(ArrayList<String> tokens, Vocabulary vocabulary) {
        SimpleListInt tokenIds = new SimpleListInt();
        int id;
        for(String token: tokens) {
            id = vocabulary.getTokenId(token);
            if (id > -1) tokenIds.add(id);
        }
        return tokenIds;
    }

    public static int TermFrequencyInCollection(int termId, PostingList postings) {
        return postings.get(termId).getLength() / 2;
    }

}
