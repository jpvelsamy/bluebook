package hackerrank;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class RightCRotation {

	private static final int queryThreshold = 500;
	private static final int threshold = 100000;

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
        int totalElements = Math.abs(in.nextInt());
        int totalRotations = Math.abs(in.nextInt());
        int totalQueries = Math.abs(in.nextInt());
        totalElements = totalElements<threshold?totalElements:threshold;
        totalRotations = totalRotations<threshold?totalRotations:threshold;
        totalQueries=totalQueries<queryThreshold?totalQueries:queryThreshold;
        Deque<Integer> numberQueue = new ArrayDeque<Integer>();
        int []queryArr = new int[totalQueries];
        
        for(int i=0;i<totalElements;i++)
        {
        	int value = in.nextInt();
        	numberQueue.addLast(value);
        }
        for(int i=0;i<totalQueries;i++)
        {
        	int value = in.nextInt();
        	value=Math.abs(value);
        	if(value>totalElements-1)
        		value=totalElements-1;
        	queryArr[i]=value;
        }
        for(int i=0;i<totalRotations;i++)
        {
        	rotate(numberQueue);        	
        }
        Integer [] numberArray=new Integer[numberQueue.size()];
        numberQueue.toArray(numberArray);
        
        for(int i=0;i<queryArr.length;i++)
        {
        	int index = queryArr[i];
        	int valueAtIndex = numberArray[index];
        	System.out.println(valueAtIndex);
        }
        in.close();
	}
	static void rotate(Deque<Integer> numberQueue)
	{
		int lastVal = numberQueue.peekLast();
		numberQueue.removeLast();
		numberQueue.addFirst(lastVal);
		
	}
}
