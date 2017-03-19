package com.stuartsullivan.ir.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by stuart on 1/13/17.
 *
 * File: Document.java
 *
 * An Object to represent the Document files
 */
public class Document {
    // The Components of the
    @JsonProperty
    private int docid = 0;
    @JsonProperty
    private int wordcount = 0;
    @JsonProperty
    private String raw = "";
    @JsonProperty
    private String date = "";
    @JsonProperty
    private String text = "";
    @JsonProperty
    private String docno = "";
    @JsonProperty
    private String byLine = "";
    @JsonProperty
    private String graphic = "";
    @JsonProperty
    private String headline = "";
    @JsonIgnore
    private HashMap termcounts = new HashMap();

    public Document() {}

    public String getRaw() {
        return raw;
    }

    public void setRaw(String _raw) {
        this.raw = _raw;
    }

    public String getText() {
        return text;
    }

    public void setText(String _text) {
        this.text = _text.replace("<P>", "").replace("</P>", "").trim();
    }

    public String getGraphic() {
        return graphic;
    }

    public void setGraphic(String _graphic) {
        this.graphic = _graphic.replace("<P>", "").replace("</P>", "").trim();
    }

    public String getDocno() {
        return docno;
    }

    public void setDocno(String _docno) {
        this.docno = _docno.replace("<P>", "").replace("</P>", "").trim();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String _date) {
        this.date = _date.replace("<P>", "").replace("</P>", "").trim();
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String _headline) {
        this.headline = _headline.replace("<P>", "").replace("</P>", "").trim();
    }

    public String getByLine() {
        return byLine;
    }

    public void setByLine(String _byLine) {
        this.byLine = _byLine.replace("<P>", "").replace("</P>", "").trim();
    }

    public int getDocid() {
        return docid;
    }

    public void setDocid(int _docid) {
        this.docid = _docid;
    }

    public int getWordcount() { return wordcount; }

    public void setWordcount(int wordcount) {
        this.wordcount = wordcount;
    }

    @JsonIgnore
    public Set getTerms() { return termcounts.keySet(); }

    public int getTermCount(Object i) {
        String k = String.valueOf(i);
        return Integer.parseInt(String.valueOf(termcounts.get(k)));
    }

    public HashMap getTermCounts() { return termcounts; }

    public void setTermCounts(HashMap<Integer, Integer> termcounts) { this.termcounts = termcounts; }
}