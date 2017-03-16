package com.stuartsullivan.ir.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

/**
 * Created by stuart on 3/14/17.
 */
public class CollectionData {
    private int DocumentCount = 0;
    private int AverageWordCount = 0;
    private boolean StemmedSet = false;

    public int getDocumentCount() {
        return DocumentCount;
    }

    public void setDocumentCount(int documentCount) {
        DocumentCount = documentCount;
    }

    public int getAverageWordCount() {
        return AverageWordCount;
    }

    public void updateAverageWordCount(int x) {
        this.DocumentCount++;
        this.AverageWordCount = this.AverageWordCount + ((x - this.AverageWordCount)/this.DocumentCount);
    }

    public static CollectionData LoadCollectionData(String path) {
        try {
            // Build the path to the document based on DOCNO
            File f = new File(path + "/about.json");
            FileReader fr = new FileReader(f);
            // Load the JSON file into the CollectionData Object
            // Source: https://sites.google.com/site/gson/gson-user-guide
            Gson gson = new Gson();
            CollectionData data = gson.fromJson(fr, CollectionData.class);
            return data;
        } catch (FileNotFoundException e) {
            // Return Null if no file found
            e.printStackTrace();
            return null;
        }
    }

    public static void SaveCollectionData(String path, CollectionData data) {
        // Create the Json Builder
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(data);
            File f = new File(path + "/about.json");
            FileWriter fwr = new FileWriter(f);
            fwr.write(jsonString);
            fwr.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isStemmedSet() {
        return StemmedSet;
    }

    public void setStemmedSet(boolean stemmedSet) {
        StemmedSet = stemmedSet;
    }
}
