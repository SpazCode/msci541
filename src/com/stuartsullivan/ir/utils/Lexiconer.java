package com.stuartsullivan.ir.utils;

import com.stuartsullivan.ir.models.PostingList;
import com.stuartsullivan.ir.models.Vocabulary;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by stuart on 1/21/17.
 */
public class Lexiconer {
    private static PorterStemer stemer = new PorterStemer();
    public static ArrayList<String> Tokenize(String sentence, boolean stem) {
        sentence = sentence.toLowerCase();
        ArrayList<String> tokens = new ArrayList<String>();
        int start = 0;
        int end = 0;
        for(char c: sentence.toCharArray()) {
            if (start != end && !Character.isDigit(c) && !Character.isLetter(c)) {
                String token = sentence.substring(start, end).trim();
                if (stem || token.length() > 1) {
                    stemer.set(token);
                    stemer.stem();
                    tokens.add(stemer.toString());
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

    public static HashMap<Integer, Integer> CountTokens(int[] tokenIds) {
        HashMap<Integer, Integer> termCounts = new HashMap<Integer, Integer>();
        int i;
        for(i = 0; i < tokenIds.length; i++) {
            if(termCounts.containsKey(tokenIds[i])) {
                int val = termCounts.get(tokenIds[i]);
                val += 1;
                termCounts.put(tokenIds[i], val);
            } else {
                termCounts.put(tokenIds[i], 1);
            }
        }
        return termCounts;
    }

    public static SimpleListInt TokenIds(ArrayList<String> tokens, Vocabulary vocabulary) {
        SimpleListInt tokenIds = new SimpleListInt();
        for(String token: tokens) {
            tokenIds.add(vocabulary.getId(token));
        }
        return tokenIds;
    }

    public static int TermFrequencyInDoc(int termId, int docId, PostingList postings) {
        SimpleListInt list = postings.get(termId);
        for (int i = 0; i < list.getLength(); i+=2) {
            if(list.get(i) == docId) {
                return list.get(i+1);
            }
        }
        return 0;
    }

}
