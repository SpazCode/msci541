package com.stuartsullivan.ir;

import java.io.File;

/**
 * Created by stuart on 1/14/17.
 * 
 * File: Lookup.java
 * 
 * Lookup Program
 */
public class Lookup {
    public static void main(String[] args) {
        try {
            // Verify the arguments
            if(args.length < 3) {
                // TODO Add Help Message
                System.out.println("Not enough Arguments given for the Lookup");
                System.out.println("3 Arguments Required:");
                System.out.println(" -> Path: location indexed data");
                System.out.println(" -> Type: [id or docno]");
                System.out.println(" -> Query: the thing we are searching");
                System.exit(1);
            }

            // Check the Path
            File f = new File(args[0]);
            if(!f.exists() || !f.isDirectory()) {
                System.out.println("Invalid Path: Please verify that the directory is correct");
            }

            // Ensure that the type is correct
            if(!args[1].equals("id") && !args[1].equals("docno")) {
                System.out.println("Invalid Type: Only [id] and [docno] is valid");
            }

            DocumentLookup.Lookup(args[0], args[1], args[2]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
