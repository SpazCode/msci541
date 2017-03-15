package com.stuartsullivan.ir.utils;

/**
 * Created by stuart on 3/14/17.
 */
public class BM25Scores implements Comparable<BM25Scores> {
    private int docid;
    private String docno;
    private float score;

    public BM25Scores(int _docid, String _docno, float _score) {
        this.docid = _docid;
        this.docno = _docno;
        this.score = _score;
    }

    public int getDocid() {
        return docid;
    }

    public void setDocid(int docid) {
        this.docid = docid;
    }

    public String getDocno() {
        return docno;
    }

    public void setDocno(String docno) {
        this.docno = docno;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int compareTo(BM25Scores compareScore) {

        float score = ((BM25Scores) compareScore).getScore();

        //ascending order
        return (int) (-1*((this.score - score) / Math.abs(this.score - score)));

        //descending order
        //return compareQuantity - this.quantity;
    }
}
