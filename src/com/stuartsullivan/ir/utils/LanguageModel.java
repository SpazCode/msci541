package com.stuartsullivan.ir.utils;

import com.stuartsullivan.ir.models.Document;
import com.stuartsullivan.ir.models.DocumentIndex;
import com.stuartsullivan.ir.models.PostingList;
import com.stuartsullivan.ir.models.Vocabulary;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by stuart on 3/19/17.
 */
public class LanguageModel {
    private float lambda, m = 1000;
    private SimpleListInt tokenIds;
    private HashMap<Integer, Integer> counts;

    public LanguageModel(float l) {
        this.lambda = l;
    }

    public LanguageModel(float l, float m) {
        this.lambda = l;
        this.m = m;
    }

    public float score_laplace(SimpleListInt tokenIds, HashMap<Integer, Integer> counts, Document doc, PostingList posting, Vocabulary vocab, DocumentIndex index) {
        float total, res = 0;
        this.lambda = index.getDocCount() / (index.getDocCount() + this.m);
        for(int i : counts.keySet()) {
            total = doc.getTermCount(i) + 1;
            total = (float) Math.log(total);
            total = counts.get(i) * total;
            res += total;
        }
        return res;
    }

    // JM Scoring
    public float score(SimpleListInt tokenIds, HashMap<Integer, Integer> counts, Document doc, PostingList posting, Vocabulary vocab, DocumentIndex index) {
        float total, wnD, wnC, res = 0;
        int token;
        this.lambda = 1 - (index.getDocCount() / (index.getDocCount() + this.m));
        HashSet<Integer> intersect = intersetSets(counts.keySet(), doc.getTerms());
        for(int i : intersect) {
            wnD = (float) doc.getTermCount(i) / (float) doc.getWordcount();
            wnC = (float) (posting.get(i).getLength() / 2) / (float) index.getDocCount();
            total = (1 - this.lambda) * wnD;
            total += this.lambda * wnC;
            total = total / (this.lambda * wnC);
            total = (float) (counts.get(i) * Math.log(total));
            res += total;
        }

//        for(int i = 0; i < tokenIds.getLength(); i++) {
//            token = tokenIds.get(i);
//            total = (float) (counts.get(token) * Math.log(this.lambda));
//            res += total;
//        }
//
//        for(int i = 0; i < tokenIds.getLength(); i++) {
//            token = tokenIds.get(i);
//            wnC = (float) (posting.get(i).getLength() / 2) / (float) index.getDocCount();
//            total = (float) (counts.get(token) * Math.log(wnC));
//            res += total;
//        }
        return res;
    }

    // Getting the intersection between sets
    private HashSet<Integer> intersetSets(Set q, Set d) {
        HashSet<Integer> intersect = new HashSet<Integer>();
        for (Object i : q) {
            if(d.contains(String.valueOf(i))) {
                intersect.add(Integer.parseInt(String.valueOf(i)));
            }
        }
        return intersect;
    }
}
