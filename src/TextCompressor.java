/******************************************************************************
 *  Compilation:  javac TextCompressor.java
 *  Execution:    java TextCompressor - < input.txt   (compress)
 *  Execution:    java TextCompressor + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   abra.txt
 *                jabberwocky.txt
 *                shakespeare.txt
 *                virus.txt
 *
 *  % java DumpBinary 0 < abra.txt
 *  136 bits
 *
 *  % java TextCompressor - < abra.txt | java DumpBinary 0
 *  104 bits    (when using 8-bit codes)
 *
 *  % java DumpBinary 0 < alice.txt
 *  1104064 bits
 *  % java TextCompressor - < alice.txt | java DumpBinary 0
 *  480760 bits
 *  = 43.54% compression ratio!
 ******************************************************************************/

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 *  The {@code TextCompressor} class provides static methods for compressing
 *  and expanding natural language through textfile input.
 *
 *  @author Zach Blick, Isha Gupta
 */

public class TextCompressor {
    public static final int EOF = 257;
    public static final int MAX_NUM_CODES = 4096;


    private static void compress() {
        // Initialize and set up TST with letter and delete code
        TST seen = new TST();
        int code = 0x00;
        for(int i = 0; i < EOF; i++){
            seen.insert("" + ((char)i), i);
        }
        // Add in the EOF string using the next code
        code = EOF + 1;
        // seen.print();



        String text = BinaryStdIn.readString();
        int index = 0;
        String prefix;

        while(index < text.length()){
            // Find the longest string in the TST that matches
            prefix = seen.getLongestPrefix(text, index);
            // Convert the string into a code and print out
            BinaryStdOut.write(seen.lookup(prefix), 12);
            if(index+1 < text.length()){
                prefix += text.charAt(index+1);
                // TODO: how to check when codes are full
                if(code < MAX_NUM_CODES) {
                    seen.insert(prefix, code++);
                }
            }
            index += prefix.length();
        }
        BinaryStdOut.write(EOF);


        BinaryStdOut.close();
    }

    private static void expand() {
        String[] seen = new String[MAX_NUM_CODES];
        for(int i = 0; i < EOF; i++){
            seen[i] = ("" + (char)i);
        }
        String curCode;
        int curNum = EOF + 1;
        String lookahead;


       while(BinaryStdIn.isEmpty()){
            // Read in string and print
           curCode = seen[BinaryStdIn.readInt(12)];
           BinaryStdOut.write(curCode);

           // Lookahead to next string to add to seen array
           lookahead = seen[BinaryStdIn.readInt(12)];
           seen[curNum] = curCode + lookahead;

       }



        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
