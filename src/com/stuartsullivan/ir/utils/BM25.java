package com.stuartsullivan.ir.utils;

import com.stuartsullivan.ir.models.Document;
import com.stuartsullivan.ir.models.DocumentIndex;
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
    private float k2 = 1000f;
    // Average Doc Length
    private float avgdl = 0;

    // Constructors
    public BM25(float _b, float _k1, float _k2, float _avgdl) {
        this.b = _b;
        this.k1 = _k1;
        this.k2 = _k2;
        this.avgdl = _avgdl;
    }

    public float score(PostingList postings, Vocabulary vocab, int docCount, Document doc, String query) {
        float score = 0.0f;
        SimpleListInt tokens = Lexiconer.TokenIds(Lexiconer.Tokenize(query), vocab);
        HashMap<Integer, Integer> counts = Lexiconer.CountTokens(tokens.getValues());

        for(int i : counts.keySet()) {
            int count = counts.get(i);
            int docsWithTerm = postings.get(i).getLength()/2;
            float fi = Lexiconer.TermFrequencyInDoc(i, doc.getDocid(), postings);
            float tfindoc = ((this.k1 + 1) * fi)/(K(doc) + fi);
            float tfinque = ((this.k2 + 1) * count * fi)/(this.k2 + (count*fi));
            float idf = (float) Math.log10((docCount-docsWithTerm+0.5)/docsWithTerm+0.5);
            score += tfindoc * tfinque * idf;
        }
        return score;
    }

    private float K(Document doc) {
        return this.k1 * ((1 + this.b) + this.b * (doc.getWordcount()/this.avgdl));
    }
}
