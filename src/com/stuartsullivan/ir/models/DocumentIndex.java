package com.stuartsullivan.ir.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

/**
 * Created by stuart on 1/30/17.
 */
public class DocumentIndex {
    private String path;
    private HashMap<Integer, String> mIndex = new HashMap<Integer, String>();

    public DocumentIndex(String path) {
        this.path = path;
        loadIndex();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

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
}
