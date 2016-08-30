package hackerrank;

import java.io.*;

class StringTest {
    public static void main(String[] args) {
	String s = "abcdefgh";
	int stringLength = s.length();
	char chars[] = new char[stringLength];
        s.getChars(0, stringLength, chars, 0);
        System.out.println(chars);
        CharArrayReader reader1 = new CharArrayReader(chars);
        CharArrayReader reader2 = new CharArrayReader(chars, 1, 4);
        int i;
        int j;
        try {
            while ((i = reader1.read()) == (j = reader2.read())) 
            {
            	
                System.out.print((char) i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
