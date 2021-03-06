
package com.stuartsullivan.ir;

import com.google.gson.Gson;

import java.io.*;

/**
 * Created by stuart on 1/13/17.
 *
 * File: DocumentLookup.java
 *
 * Logic to Search for documents in the data index
 */
public class DocumentLookup {
    public static void Lookup(String path, String type, String query) {
        try {
            // Find the DOCNO based on the ID if the query is by ID
            if (type.equals("id")) {
                query = SearchIndex(path, query);
            }
            // If no DOCNO was found then print an error Message
            if(query == null) {
                System.out.println("Error: No Document with that ID found");
                return;
            }
            // Load the Document Store
            Document doc = LoadDocument(path, query);
            // If no document was loaded return an error
            if(doc == null) {
                System.out.println("No Document Found with DOCNO: " + query);
                return;
            }
            // Output data about the doucment
            System.out.println("docno : " + doc.getDocno());
            System.out.println("internal id : " + doc.getDocid());
            System.out.println("date : " + doc.getDate());
            System.out.println("headline : " + doc.getHeadline());
            System.out.println("raw : \n" + doc.getRaw());
        } catch (Exception e) {
            // Spit out an error message about finding the file
            System.out.print("There was an error receiving your document");
            e.printStackTrace();
            return;
        }
    }

    private static String SearchIndex(String path, String id) throws IOException {
        // Load the index file
        File f = new File(path + "/index/index.csv");
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line = "";
        // Read the index until we find a matching ID
        while((line = br.readLine()) != null) {
            String[] index = line.split(",");
            if(index.length == 2) {
                if(index[0].equals(id)) {
                    return index[1];
                }
            }
        }
        // Return null if no matching ID was found
        return null;
    }

    private static Document LoadDocument(String path, String docno) {
        try {
            // Build the path to the document based on DOCNO
            String ext = createPath(docno);
            // Load the file
            File f = new File(path + "/" + ext);
            FileReader fr = new FileReader(f);
            // Load the JSON file into the Document Object
            // Source: https://sites.google.com/site/gson/gson-user-guide
            Gson gson = new Gson();
            Document doc = gson.fromJson(fr, Document.class);
            return doc;
        } catch (FileNotFoundException e) {
            // Return Null if no file found
            e.printStackTrace();
            return null;
        }
    }

    private static String createPath(String docno) {
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
