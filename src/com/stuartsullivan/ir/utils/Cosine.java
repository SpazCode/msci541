package com.stuartsullivan.ir.utils;

import com.stuartsullivan.ir.models.Document;
import com.stuartsullivan.ir.models.DocumentIndex;
import com.stuartsullivan.ir.models.PostingList;
import com.stuartsullivan.ir.models.Vocabulary;


import java.util.HashMap;

/**
 * Created by stuart on 3/16/17.
 */
public class Cosine {
    public Cosine() {

    }

    public float score(SimpleListInt tokens, HashMap<Integer, Integer> counts, int N, Document doc, Vocabulary vocab, PostingList postings, DocumentIndex index) {
        float fi, ft, res = 0;
        double W = 0;
        int termId;
        // Calculate the summation
        for(int i : counts.keySet()) {
            fi = (float) (doc.getTermCount(i)); // / (float) (doc.getWordcount());
            ft = Lexiconer.TermFrequencyInCollection(i, postings);
            res += calc(fi, N, ft, 2);
        }
        // Calculate the document weight
        for(Object i : doc.getTerms()) {
            termId = Integer.parseInt(String.valueOf(i));
            fi = (float) (doc.getTermCount(termId));
            ft = Lexiconer.TermFrequencyInCollection(termId, postings);
            W += Math.pow(calc(fi, N, ft, 1), 2);
        }
        // Put it all together
        res = (float) ((1/Math.sqrt(W)) * res);
        return res;
    }

    // Calculate the fdt * log (N/ft) ^ i
    private float calc(float fi, int N, float ft, int pow) {
        float val;
        val = (N / ft);
        val = (float) Math.log(val);
        val = (float) Math.pow(val, pow);
        return fi * val;

    }
}
