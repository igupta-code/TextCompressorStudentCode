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
    public static final int EOF = 256;
    public static final int MAX_NUM_CODES = 4096;


    private static void compress() {
        // Initialize and set up TST with letter and delete code
        TST seen = new TST();
        for(int i = 0; i < EOF; i++){
            seen.insert("" + (char)i, i);
        }
        // Add in the EOF string using the next code
        int code = EOF + 1;
        // seen.print();

        String text = BinaryStdIn.readString();
        int index = 0;

        while(index < text.length()){
            // Find the longest string in the TST that matches
            String prefix = seen.getLongestPrefix(text, index);
            // Convert the string into a code and print out
            BinaryStdOut.write(seen.lookup(prefix), 12);
            if(index+prefix.length() < text.length()){
                if(code < MAX_NUM_CODES) {
                    seen.insert(prefix + text.charAt(index+prefix.length()), code++);
                }
            }
            index += prefix.length();
        }
        BinaryStdOut.write(EOF, 12);
        BinaryStdOut.close();
    }

    private static void expand() {
        // Fills in ascii values into seen array
        String[] seen = new String[MAX_NUM_CODES];
        for(int i = 0; i < EOF; i++){
            seen[i] = "" + (char)i;
        }
        int nextEmptyCode = EOF + 1;

        String nextStr;
        int nextCode;

        // Read in the first string
        int code = BinaryStdIn.readInt(12);
        String strCode = seen[code];

       while(code != EOF){
//           System.out.println(curCode);
           // Print the current code
           BinaryStdOut.write(strCode);

           // Lookahead to next string to add to seen array
           nextCode = BinaryStdIn.readInt(12);
           if(nextCode == EOF){
               break;
           }
           nextStr = seen[nextCode];

           // If you have free codes, add in a new code
           if(nextEmptyCode < MAX_NUM_CODES) {
               // Fixes edge case bug
               if(nextEmptyCode == nextCode){
                   seen[nextEmptyCode++] = strCode + strCode.charAt(0);
                   nextStr = strCode + strCode.charAt(0);
               }
               else{
                   seen[nextEmptyCode++] = strCode + nextStr.charAt(0);
               }
           }
           // For next loop, update the current Code
           strCode = nextStr;
           code = nextCode;
       }
       BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
