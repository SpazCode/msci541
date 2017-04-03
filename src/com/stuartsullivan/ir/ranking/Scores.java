package com.stuartsullivan.ir.ranking;

/**
 * Created by stuart on 3/14/17.
 */
public class Scores implements Comparable<Scores> {
    private int docid;
    private String docno;
    private float score;
    private String docHeadline;
    private String docSnippet;

    public Scores(int _docid, String _docno, float _score) {
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

    public int compareTo(Scores compareScore) {

        float score = ((Scores) compareScore).getScore();

        //ascending order
        return (int) (-1*((this.score - score) / Math.abs(this.score - score)));

        //descending order
        //return compareQuantity - this.quantity;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.docno);
        builder.append('\t');
        builder.append(this.score);
        return builder.toString();
    }

    public String getDocHeadline() {
        return docHeadline;
    }

    public void setDocHeadline(String docHeadline) {
        this.docHeadline = docHeadline;
    }

    public String getDocSnippet() {
        return docSnippet;
    }

    public void setDocSnippet(String docSnippet) {
        this.docSnippet = docSnippet;
    }
}
