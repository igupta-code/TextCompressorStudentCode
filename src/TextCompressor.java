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

/**
 *  The {@code TextCompressor} class provides static methods for compressing
 *  and expanding natural language through textfile input.
 *
 *  @author Zach Blick, Isha Gupta
 */

public class TextCompressor {

    private static void compress() {
        // Initialize and set up TST with letter and delete code
        TST seen = new TST();
        // TODO: Does this work??
        int code = 0x00;
        for(int i = 0; i < 127; i++){
            seen.insert("" + ((char)i),code++);
        }
        // Add in the EOF string using the next code
        // TODO: Print out "EOF as code?????
        seen.insert("EOF", code++);



        String text = BinaryStdIn.readString();
        int index = 0;
        String prefix;

        while(index < text.length()){
            // Find the longest string in the TST that matches
            prefix = seen.getLongestPrefix(text, index);
            // Convert the string into a code and print out
            BinaryStdOut.write(seen.lookup(prefix));
            if(index+1 < text.length()){
                prefix += text.charAt(index+1);
                // TODO: how to check when codes are full
                seen.insert(prefix, code++);
            }
            index += prefix.length();
        }
        BinaryStdOut.write("EOF");


        BinaryStdOut.close();
    }

    private static void expand() {


        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
