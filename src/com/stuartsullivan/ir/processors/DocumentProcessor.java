package com.stuartsullivan.ir.processors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stuartsullivan.ir.models.*;
import com.stuartsullivan.ir.utils.Lexiconer;
import com.stuartsullivan.ir.utils.SimpleListInt;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * Created by stuart on 1/13/17.
 *
 * File: DocumentProcessor.java
 *
 * Fuctions:
 * > Process for reading the entire compressed corpus
 * > Extracting and structuring important data
 * > Saving the data in the specified file structure
 * > Updating the internal id index of Documents
 */
public class DocumentProcessor {
    // Regex Explained - https://www.tutorialspoint.com/java/java_regular_expressions.htm
    // Moved here to help performance
    private final Pattern docnoTag = Pattern.compile("<DOCNO>(.*)</DOCNO>");
    private final Pattern textTag = Pattern.compile("<TEXT>(.*)</TEXT>");
    private final Pattern headlineTag = Pattern.compile("<HEADLINE>(.*)</HEADLINE>");
    private final Pattern byLineTag = Pattern.compile("<BYLINE>(.*)</BYLINE>");
    private final Pattern graphicTag = Pattern.compile("<GRAPHIC>(.*)</GRAPHIC>");

    public void extractCorpus(String source, String output, Vocabulary vocabulary, PostingList postings, CollectionData about, boolean stem) {
        try {
            resetIndex();
            // Load the corpus archive
            // http://www.concretepage.com/java/example_inflaterinputstream_java
            String encoding = "UTF-8";
            File file = new File(source);
            FileInputStream fis = new FileInputStream(file);
            GZIPInputStream gzis = new GZIPInputStream(fis);
            Reader reader = new InputStreamReader(gzis, encoding);
            BufferedReader br = new BufferedReader(reader);
            // Keep track of documents count to be the ID
            int id =  1;
            String line = "";
            String segment = "";
            // Read through each line
            while ((line = br.readLine()) != null) {
                segment += line + "\n";
                if (line.contains("</DOC>")) {
                    // Send document segment to processor once end tag is found
                    // TODO Make Processor Multithreaded Maybe?!?
                    processDocument(segment, output, id, vocabulary, postings, about, stem);
                    segment = "";
                    // Incrament ID
                    id++;
                }
            }
            // Close the streams
            fis.close();
            gzis.close();
            reader.close();
        } catch (Exception e) {
            System.out.println("Error Parsing the data file, ensure that the file is a valid gzip file");
            e.printStackTrace();
        }
    }

    private void processDocument(String doc, String output, int id, Vocabulary vocabulary, PostingList postings, CollectionData about, boolean stem) {
        try {
            // Create new Document
            Document docObj = new Document();
            // Populate Document
            docObj.setRaw(doc);

            // Extract important tags
            doc = doc.replace("\n", "");
            Matcher docnoMatch = docnoTag.matcher(doc);
            Matcher textMatch = textTag.matcher(doc);
            Matcher headlineMatch = headlineTag.matcher(doc);
            Matcher byLineMatch = byLineTag.matcher(doc);
            Matcher graphicMatch = graphicTag.matcher(doc);

            // Load the rest of the doc data
            if(docnoMatch.find()) docObj.setDocno(docnoMatch.group(1));
            docObj.setDocid(id);
            docObj.setDate(buildDate(docObj.getDocno()));
            if(graphicMatch.find()) docObj.setGraphic(graphicMatch.group(1));
            if(headlineMatch.find()) docObj.setHeadline(headlineMatch.group(1));
            if(textMatch.find()) docObj.setText(textMatch.group(1));
            if(byLineMatch.find()) docObj.setByLine(byLineMatch.group(1));

            // Tokenize the text
            int count = constructPostingList(postings, vocabulary, id,
                    new String[] {docObj.getText(), docObj.getGraphic(), docObj.getHeadline()}, stem);
            docObj.setWordcount(count);
            // Save the Document
            // http://www.mkyong.com/java/how-to-enable-pretty-print-json-output-gson/
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(docObj);
            String path = DocumentIndex.createPath(docObj.getDocno());
            File f = new File(output + "/" + path);
            // http://stackoverflow.com/questions/2833853/create-whole-path-automatically-when-writing-to-a-new-file
            // Build/Ensure directory structure
            f.getParentFile().mkdirs();
            // Output the JSON file
            FileWriter fwr = new FileWriter(f);
            fwr.write(jsonString);
            fwr.close();
            about.updateAverageWordCount(docObj.getWordcount());
            updateIndex(docObj, output);
            // Output the Internal ID 
            System.out.print(".");
        } catch (IOException e) {
            System.out.println("Error parsing document");
            e.printStackTrace();
        }
    }

    private ArrayList<String> tokenizeText(String[] body, boolean stem) {
        String text = "";
        for (String section : body) {
            text = text.concat(" ").concat(section);
        }
        return Lexiconer.Tokenize(text, stem);
    }

    private SimpleListInt tokenIds(ArrayList<String> tokens, Vocabulary vocabulary) {
        SimpleListInt tokenIds = new SimpleListInt();
        for(String token: tokens) {
            tokenIds.add(vocabulary.getId(token));
        }
        return tokenIds;
    }

    private int constructPostingList(PostingList postings, Vocabulary vocabulary, int docId, String[] content, boolean stem) {
        ArrayList<String> tokens = tokenizeText(content, stem);
        SimpleListInt tokenIds = tokenIds(tokens, vocabulary);
        HashMap<Integer, Integer> tokenCounts = Lexiconer.CountTokens(tokenIds.getValues());
        try {
            for (int tokenId : tokenCounts.keySet()) {
                postings.add(tokenId, docId, tokenCounts.get(tokenId));
            }
        } catch (Exception e) {
            System.out.println("" + postings.getLength());
            e.printStackTrace();
        }
        return tokens.size();
    }

    private void updateIndex(Document doc, String output) {
        try {
            // Load the index file
            String INDEXPATH = output + "/index/index.csv";
            File f = new File(INDEXPATH);
            f.getParentFile().mkdirs();
            // Appending : http://stackoverflow.com/questions/1625234/how-to-append-text-to-an-existing-file-in-java
            // Append the index entry to the end of the file
            // TODO Opening the file each time to update the index is expensive, could improve performace
            FileWriter fwr = new FileWriter(f, true);
            String line = doc.getDocid() + "," + doc.getDocno() + "\n";
            fwr.append(line);
            fwr.close();
        } catch (Exception e) {
            System.out.println("Error Updating the Index File");
            e.printStackTrace();
        }
    }

    // Function for updating the indexed data
    private void resetIndex() {
        try {
            String INDEXPATH = "index/index.csv";
            File f = new File(INDEXPATH);
            FileWriter fwr = new FileWriter(f);
            fwr.write("");
            fwr.close();
        } catch (Exception e) {
            System.out.println("No index to reset");
            // e.printStackTrace();
        }
    }

    private String buildDate(String docno) {
        // Extract Date peices from DOCNO
        String[] docSegments = docno.split("-");
        int yy = 1900 + Integer.parseInt(docSegments[0].substring(6, 8));
        int mm = Integer.parseInt(docSegments[0].substring(2, 4)) - 1;
        int dd = Integer.parseInt(docSegments[0].substring(4, 6));
        // http://stackoverflow.com/questions/5677470/java-why-is-the-date-constructor-deprecated-and-what-do-i-use-instead
        // Convert to Calaendar object to get date object
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, yy);
        cal.set(Calendar.MONTH, mm);
        cal.set(Calendar.DATE, dd);
        // http://stackoverflow.com/questions/12575990/calendar-date-to-yyyy-mm-dd-format-in-java
        // Format date and return the string
        SimpleDateFormat sdf = new SimpleDateFormat("MMMMM dd, yyyy");
        return sdf.format(cal.getTime());
    }
}
