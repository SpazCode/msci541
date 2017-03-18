package com.stuartsullivan.ir.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;

import javax.print.Doc;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

/**
 * Created by stuart on 1/30/17.
 */
public class DocumentIndex {
    private String path;
    private HashMap<Integer, String> mIndex = new HashMap<Integer, String>();
    private final ObjectMapper mapper = new ObjectMapper();

    public DocumentIndex(String path) {
        this.path = path;
        loadIndex();
    }

    // Getters and setters for the index path
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public int getDocCount() { return mIndex.size(); }

    // Getting the docno based on doc Id
    public String get(int index) {
        return this.mIndex.get(index);
    }

    private void loadIndex() {
        try {
            // Load the index file
            File f = new File(path + "/index/index.csv");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            this.mIndex = new HashMap<Integer, String>();
            String line = "";
            // Read the index until we find a matching ID
            while ((line = br.readLine()) != null) {
                String[] index = line.split(",");
                if (index.length == 2) {
                   this.mIndex.put(Integer.parseInt(index[0]), index[1]);
                }
            }
        } catch (Exception e) {
            System.out.print("Error Loading Document Index");
            e.printStackTrace();
        }
    }

    public static Document LoadDocument(String path, String docno) {
        try {
            // Build the path to the document based on DOCNO
            String ext = createPath(docno);
            // Load the file
            File f = new File(path + "/" + ext);
            // FileReader fr = new FileReader(f);
            // Load the JSON file into the Document Object
            // Source: https://sites.google.com/site/gson/gson-user-guide
            // Gson gson = new Gson();
            // Document doc = gson.fromJson(fr, Document.class);
            ObjectMapper mapper = new ObjectMapper();
            Document doc = mapper.readValue(f, Document.class);
            return doc;
        } catch (Exception e) {
            // Return Null if no file found
            e.printStackTrace();
            return null;
        }
    }

    public Document LoadDocument(int docid) {
        try {
            // Build the path to the document based on DOCNO
            String ext = createPath(this.get(docid));
            // Load the file
            File f = new File(this.path + "/" + ext);
            // FileReader fr = new FileReader(f);
            // Load the JSON file into the Document Object
            // Source: https://sites.google.com/site/gson/gson-user-guide
            // Gson gson = new Gson();
            // Document doc = gson.fromJson(fr, Document.class);
            Document doc = mapper.readValue(f, Document.class);
            return doc;
        } catch (Exception e) {
            // Return Null if no file found
            e.printStackTrace();
            return null;
        }
    }

    public static void SaveDocument(String path, Document doc) {
        try {
            ObjectMapper m = new ObjectMapper();
            String ext = createPath(doc.getDocno());
            File f = new File(path + "/" + ext);
            // http://stackoverflow.com/questions/2833853/create-whole-path-automatically-when-writing-to-a-new-file
            // Build/Ensure directory structure
            f.getParentFile().mkdirs();
            m.enable(SerializationFeature.INDENT_OUTPUT);
            // Output the JSON file
            m.writeValue(f, doc);
        } catch (Exception e) {
            // Print Stacktrace
            e.printStackTrace();
        }
    }

    public static String createPath(String docno) {
        // Build the path to the file using substings of the DOCNO
        // /LA/YY/MM/DD/FILENO.json
        String[] docSegments = docno.split("-");
        String path = "data/";
        path = path + docSegments[0].substring(0, 2) + "/" + docSegments[0].substring(6, 8) + "/" +
                docSegments[0].substring(2, 4) + "/" + docSegments[0].substring(4, 6)  + "/" +
                docSegments[1] + ".json";
        return path;
    }
}
