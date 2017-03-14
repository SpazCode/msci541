package com.stuartsullivan.ir;

import com.stuartsullivan.ir.processors.DocumentLookup;

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
        final long startTime = System.currentTimeMillis();
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
                return;
            }

            // Check the Path
            File f = new File(args[0]);
            if(!f.exists() || !f.isDirectory()) {
                System.out.println("Invalid Path: Please verify that the directory is correct");
                System.exit(1);
                return;
            }

            // Ensure that the type is correct
            if(!args[1].equals("id") && !args[1].equals("docno")) {
                System.out.println("Invalid Type: Only [id] and [docno] is valid");
                System.exit(1);
                return;
            }
            // Do the Lookup
            DocumentLookup.Lookup(args[0], args[1], args[2]);
            final long endTime = System.currentTimeMillis();
            System.out.print((endTime - startTime)/1000 + " secs\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
