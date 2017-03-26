package com.stuartsullivan.ir.ranking;

import com.stuartsullivan.ir.models.Document;
import com.stuartsullivan.ir.models.DocumentIndex;
import com.stuartsullivan.ir.models.PostingList;
import com.stuartsullivan.ir.models.Vocabulary;
import com.stuartsullivan.ir.utils.SimpleListInt;

import java.util.HashMap;

/**
 * Created by stuart on 3/23/17.
 */
public interface Ranker {
    public float score(SimpleListInt tokenIds, HashMap<Integer, Integer> counts, Document doc, PostingList postings, Vocabulary vocab, DocumentIndex index);
}
