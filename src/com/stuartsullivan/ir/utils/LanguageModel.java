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

    // Constructor
    public LanguageModel(float m) {
        this.m = m;
    }

    public LanguageModel(float l, float m) {
        this.lambda = l;
        this.m = m;
    }

    // JM Scoring Function
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
