package com.stuartsullivan.ir.utils;

import com.stuartsullivan.ir.models.Document;
import com.stuartsullivan.ir.models.PostingList;
import com.stuartsullivan.ir.models.Vocabulary;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by stuart on 3/16/17.
 */
public class Cosine {
    public Cosine() {

    }

    public float score(String query, boolean stem, int N, Document doc, Vocabulary vocab, PostingList postings) {
        float res = 0;
        double W = 0;
        int fi, ft, count;
        SimpleListInt tokens = Lexiconer.TokenIds(Lexiconer.Tokenize(query, stem), vocab);
        HashMap<Integer, Integer> counts = Lexiconer.CountTokens(tokens.getValues());
        for(int i : counts.keySet()) {
            // ft = counts.get(i);
            fi = Lexiconer.TermFrequencyInDoc(i, doc.getDocid(), postings);
            ft = Lexiconer.TermFrequencyInCollection(i, postings);
            res += calc(fi, N, ft, 2);
            W += Math.pow(calc(fi, N, ft, 1), 2);
        }
        res = (float) ((1/Math.sqrt(W)) * res);
        return res;
    }

    private float calc(float fi, int N, int ft, int pow) {
        float val;
        val = (float) (N / ft);
        val = (float) Math.log(val);
        val = fi * val;
        return (float) Math.pow(val, pow);
    }
}
