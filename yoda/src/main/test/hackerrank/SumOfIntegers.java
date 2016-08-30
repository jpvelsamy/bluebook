package hackerrank;

import java.util.Scanner;

public class SumOfIntegers {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        //System.out.println("Enter how many numbers to read");
        int n = in.nextInt();
        int sum=0;
        int counter=0;
        //System.out.println("Enter the numbers now");
        while(in.hasNextInt())        
        {
        	int curVal = in.nextInt();
        	sum=sum+curVal;
        	counter++;
        	if(counter==n)
        		break;
        }
        System.out.println(sum);
        /*int output = sumUp(arrayStr);
        System.out.println(output);*/
        
        
    }

	private static int sumUp(String arrayStr) {		
		int sum=0;
		if(arrayStr.trim().length()>0)
        {
        	String []data = arrayStr.split(" ");
            for (int i = 0; i < data.length; i++) {
				String element= data[i];
				Integer value = Integer.parseUnsignedInt(element);
				sum = sum+value;		
			}            
        }
        else
            System.out.println(0);
		return sum;
	}
	
}
