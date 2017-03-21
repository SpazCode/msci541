package com.stuartsullivan.ir.utils;

import com.stuartsullivan.ir.models.Document;
import com.stuartsullivan.ir.models.PostingList;
import com.stuartsullivan.ir.models.Vocabulary;

import java.util.HashMap;

/**
 * Created by stuart on 3/12/17.
 */
public class BM25 {
    // b calibration
    private float b = 0.75f;
    // k1 calibration
    private float k1 = 1.2f;
    // ks calibration
    private float k2 = 7f;
    // Average Doc Length
    private float avgdl = 502;

    // Constructors
    public BM25() {}
    // Tunable Constructor
    public BM25(float _b, float _k1, float _k2, float _avgdl) {
        this.b = _b;
        this.k1 = _k1;
        this.k2 = _k2;
        this.avgdl = _avgdl;
    }

    // Scoring Function
    public float score(PostingList postings, Vocabulary vocab, int docCount, Document doc, SimpleListInt tokens, HashMap<Integer, Integer> counts) {
        float score = 0.0f;
        int docsWithTerm, count;
        float fi, tfindoc, tfinque, idf;
        for(int i = 0; i < tokens.getLength(); i++) {
            count = counts.get(tokens.get(i));
            docsWithTerm = postings.get(tokens.get(i)).getLength()/2;
            // Get fi
            fi = doc.getTermCount(tokens.get(i));
            // Get tfid
            tfindoc = ((this.k1 + 1) * fi) / (K(doc) + fi);
            // Get tfiq
            tfinque = ((this.k2 + 1) * count)/(this.k2 + (count));
            // Get idf
            idf = (float) Math.log((docCount-docsWithTerm+0.5)/(docsWithTerm+0.5));
            // Sum up their products
            score += (tfindoc * tfinque * idf);
        }
        return score;
    }

    // Calculate K
    private float K(Document doc) {
        float res = doc.getWordcount()/this.avgdl;
        res = res * this.b;
        res = (1 - this.b) + res;
        return this.k1 * res;
    }
}
