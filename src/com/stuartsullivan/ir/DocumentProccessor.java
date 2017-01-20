package com.stuartsullivan.ir;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
public class DocumentProccessor {
    public static void extractCorpus(String source, String output) {
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
                    processDocument(segment, output, id);
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

    private static void processDocument(String doc, String output, int id) {
        try {
            // Create new Document
            Document docObj = new Document();
            // Populate Document
            docObj.setRaw(doc);

            // Regex Explained - https://www.tutorialspoint.com/java/java_regular_expressions.htm
            Pattern docnoTag = Pattern.compile("<DOCNO>(.*)</DOCNO>");
            Pattern textTag = Pattern.compile("<TEXT>(.*)</TEXT>");
            Pattern headlineTag = Pattern.compile("<HEADLINE>(.*)</HEADLINE>");
            Pattern byLineTag = Pattern.compile("<BYLINE>(.*)</BYLINE>");
            Pattern graphicTag = Pattern.compile("<GRAPHIC>(.*)</GRAPHIC>");

            // Extract important tags
            doc = doc.replace("\n", "");
            Matcher docnoMatch = docnoTag.matcher(doc);
            Matcher textMatch = textTag.matcher(doc);
            Matcher headlineMatch = headlineTag.matcher(doc);
            Matcher byLineMatch = byLineTag.matcher(doc);
            Matcher graphicMatch = graphicTag.matcher(doc);

            // Load the rest of the doc data
            if(docnoMatch.find()) docObj.setDocno(docnoMatch.group(1));
            docObj.setDocid("" + id);
            docObj.setDate(buildDate(docObj.getDocno()));
            if(graphicMatch.find()) docObj.setGraphic(graphicMatch.group(1));
            if(headlineMatch.find()) docObj.setHeadline(headlineMatch.group(1));
            if(textMatch.find()) docObj.setText(textMatch.group(1));
            if(byLineMatch.find()) docObj.setByLine(byLineMatch.group(1));
            // Save the Document
            // http://www.mkyong.com/java/how-to-enable-pretty-print-json-output-gson/
            Gson gson =  new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(docObj);
            String path = createPath(docObj.getDocno());
            File f = new File(output + "/" + path);
            // http://stackoverflow.com/questions/2833853/create-whole-path-automatically-when-writing-to-a-new-file
            // Build/Ensure directory structure
            f.getParentFile().mkdirs();
            // Output the JSON file
            FileWriter fwr = new FileWriter(f);
            fwr.write(jsonString);
            fwr.close();
            updateIndex(docObj, output);
            // Output the Internal ID 
            System.out.print(docObj.getDocid() + "\n");
        } catch (IOException e) {
            System.out.println("Error parsing document");
            e.printStackTrace();
        }
    }

    private static void updateIndex(Document doc, String output) {
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
    private static void resetIndex() {
        try {
            String INDEXPATH = "index/index.csv";
            File f = new File(INDEXPATH);
            FileWriter fwr = new FileWriter(f);
            fwr.write("");
            fwr.close();
        } catch (Exception e) {
            System.out.print("No index to reset");
            // e.printStackTrace();
        }
    }

    private static String createPath(String docno) {
        String[] docSegments = docno.split("-");
        String path = "data/";
        path = path + docSegments[0].substring(0, 2) + "/" + docSegments[0].substring(6, 8) + "/" + docSegments[0].substring(2, 4) + "/" + docSegments[0].substring(4, 6)  + "/" + docSegments[1] + ".json";
        return path;
    }

    private static String buildDate(String docno) {
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
