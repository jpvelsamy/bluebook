package hackerrank;

import java.math.BigInteger;
import java.util.Scanner;

public class BigIntSum {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        BigInteger sumTotal= new BigInteger("0");        
        for(int i=0;i<n;i++)
        {
        	 String incoming= in.next();
        	 BigInteger val = new BigInteger(incoming);
        	 sumTotal=sumTotal.add(val);
        }
        System.out.println(sumTotal);
        in.close();
	}

}
