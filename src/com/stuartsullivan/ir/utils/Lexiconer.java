package com.stuartsullivan.ir.utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by stuart on 1/21/17.
 */
public class Lexiconer {
    public static ArrayList<String> Tokenize(String sentence) {
        sentence = sentence.toLowerCase();
        ArrayList<String> tokens = new ArrayList<String>();
        int start = 0;
        int end = 0;
        for(char c: sentence.toCharArray()) {
            if (start != end && !Character.isDigit(c) && !Character.isLetter(c)) {
                tokens.add(sentence.substring(start, end).trim());
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
        for(int id: tokenIds) {
            if(termCounts.containsKey(id)) {
                int val = termCounts.get(id);
                val += 1;
                termCounts.put(id, val);
            } else {
                termCounts.put(id, 1);
            }
        }
        return termCounts;
    }
}
