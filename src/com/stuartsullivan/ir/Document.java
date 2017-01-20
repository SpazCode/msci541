package com.stuartsullivan.ir;

/**
 * Created by stuart on 1/13/17.
 *
 * File: Document.java
 *
 * An Object to represent the Document files
 */
public class Document {
    // The Components of the 
    private String raw;
    private String text;
    private String graphic;
    private String docno;
    private String docid;
    private String date;
    private String headline;
    private String byLine;

    public Document() {
    }

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

    public String getDocid() {
        return docid;
    }

    public void setDocid(String _docid) {
        this.docid = _docid.replace("<P>", "").replace("</P>", "").trim();
    }
}