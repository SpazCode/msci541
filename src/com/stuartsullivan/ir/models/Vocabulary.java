package com.stuartsullivan.ir.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

/**
 * Created by stuart on 1/23/17.
 */
public class Vocabulary {
    private final String path;
    private int nextId;
    private HashMap<String, Integer> dictionary;
    private char currentLetter;

    public Vocabulary(String path) {
        this.path = path;
        this.loadDictionary();
        this.nextId = getMaxKey();
    }

    // Get id for a token and create new id for new tokens
    public int getId(String token) {
        if (dictionary.containsKey(token))
            return dictionary.get(token);
        else {
            int id = nextId;
            dictionary.put(token, id);
            nextId++;
            return id;
        }
    }

    // Get id for token
    public int get(String token) {
        if (dictionary.containsKey(token))
            return dictionary.get(token);
        else return -1;
    }

    // Figure out what the highest key
    public int getMaxKey() {
        int max = 0;
        for (int value : dictionary.values()) {
            max = Math.max(value, max);
        }
        return max;
    }

    public void loadDictionary() {
        try {
            File f = new File(path + "/vocabulary/dictionary.csv");
            if(!f.exists()) {
                dictionary = new HashMap<String, Integer>();
                return;
            }
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            dictionary = new HashMap<String, Integer>();
            String line;
            while((line = br.readLine()) != null) {
                String[] cols = line.split(",");
                dictionary.put(cols[0].trim(), Integer.parseInt(cols[1].trim()));
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error Loading the dictionary");
            e.printStackTrace();
        }
    }

    public void UpdateDictionary() {
        try {
            File f = new File(path + "/vocabulary/dictionary.csv");
            f.getParentFile().mkdirs();
            FileWriter fw = new FileWriter(f);
            for(String key: dictionary.keySet()) {
                fw.write(String.format("%s, %s\n", key, dictionary.get(key)));
            }
            fw.close();
        } catch(Exception e) {
            System.out.println("Error updating the dictionary");
            e.printStackTrace();
        }
    }
}
