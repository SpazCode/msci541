package com.stuartsullivan.ir.snippit;

import com.stuartsullivan.ir.models.Document;
import com.stuartsullivan.ir.models.DocumentIndex;
import com.stuartsullivan.ir.models.Vocabulary;
import com.stuartsullivan.ir.ranking.Scores;
import com.stuartsullivan.ir.utils.Lexiconer;
import com.stuartsullivan.ir.utils.SimpleListInt;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by stuart on 3/26/17.
 */
public class SnippetEngine {
    public static Scores[] Snippets(Scores[] scores, DocumentIndex index, Vocabulary vocab, SimpleListInt tokens, boolean stem, int start, int end) {
        Document curDoc;
        for(int i = start; i < end + 1; i++) {
            curDoc = index.LoadDocument(scores[i].getDocid());
            if(scores[i].getDocHeadline() != null && scores[i].getDocSnippet() != null) continue;
            if(curDoc.getHeadline().length() > 0)
                scores[i].setDocHeadline(curDoc.getHeadline());
            else
                scores[i].setDocHeadline(curDoc.getText().substring(0, 50) + "...");
            scores[i].setDocSnippet(createSnippet(curDoc, tokens, stem, vocab));
        }
        return scores;
    }

    private static String createSnippet(Document doc, SimpleListInt tokens, boolean stem, Vocabulary vocab) {
        ArrayList<String> sentences = new ArrayList<String>();
        HashMap<Integer, Integer> sentenceCounts;
        String[] split = doc.getText().split("[.!?]");
        int count = 0;
        int maxScore = -1;
        int maxIndex = -1;
        for(int i = 0; i < split.length; i++) {
            count = 0;
            sentenceCounts = Lexiconer.CountTokens(Lexiconer.TokenIds(Lexiconer.Tokenize(split[i], stem), vocab));
            for(int y = 0; i < tokens.getLength(); i++) {
                if(sentenceCounts.containsKey(tokens.get(y)))
                    count += sentenceCounts.get(tokens.get(y));
            }
            if (count > maxScore) {
                maxScore = count;
                maxIndex = i;
            }
        }
        StringBuilder builder = new StringBuilder();
        int start = 0;
        int end = 0;
        if(maxIndex == 0) {
            start = maxIndex;
            end = maxIndex + 3;
        } else if (maxIndex == split.length -1) {
            start = maxIndex - 2;
            end = maxIndex + 1;
        } else {
            start = maxIndex - 1;
            end = maxIndex + 2;
        }
        for(int i = start; i < end; i++) {
            builder.append(split[i]);
            builder.append(".");
        }
        return builder.toString();
    }
}
