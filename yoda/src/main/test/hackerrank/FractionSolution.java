package hackerrank;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Scanner;



public class FractionSolution {

	public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int arr[] = new int[n];
        int positives=0;
        int negatives=0;
        int zeroes=0;
        
        for(int arr_i=0; arr_i < n; arr_i++)
        {
        	arr[arr_i] = in.nextInt();
        	if(arr[arr_i]>0)
        		positives++;
        	else if(arr[arr_i]<0)
        		negatives++;
        	else
        		zeroes++;
        }
        
        MathContext mc  = new MathContext(6);
        
        BigDecimal total = new BigDecimal(n,mc);
        BigDecimal positiveFraction = new BigDecimal(positives,mc);        
        BigDecimal negativeFraction = new BigDecimal(negatives,mc);
        BigDecimal zeroFraction = new BigDecimal(zeroes,mc);
        System.out.println(positiveFraction.divide(total,mc));
        System.out.println(negativeFraction.divide(total,mc));
        System.out.println(zeroFraction.divide(total,mc));
        in.close();
    }
}
