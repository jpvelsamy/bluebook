package hackerrank;

import java.util.Scanner;

public class DicksGame {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int tests = Math.abs(in.nextInt());
		tests = tests <= 10 ? tests : 10;
		long valueSet[] = new long[tests];
		for (int i = 0; i < valueSet.length; i++) {
			valueSet[i] = in.nextLong();
			valueSet[i] = valueSet[i] <= Long.MAX_VALUE ? valueSet[i]
					: Long.MAX_VALUE;
		}
		for (int i = 0; i < valueSet.length; i++) {
			long value = valueSet[i];
			
			boolean counter = true;// Louise
			counter = runGame(valueSet, i, value, counter);
			if(!counter)
				System.out.println("Richard");
			else
				System.out.println("Louise");
		}
		in.close();
	}

	private static boolean runGame(long[] valueSet, int i, long value,
			 boolean counter) {
		boolean process = true;
		while (process) {
			if (valueSet[i] == 1) {
				return !counter;
			} else {
				boolean isValidNum = isPowerOf2(value);
				if (!isValidNum) {
					long nextValue = findNextPowerOf2Num(value);
					long residue = value - nextValue;
					value = residue;
				} else {
					long nextValue = value / 2;
					value = value - nextValue;
				}
				if (value == 1)
				{
					process = false;
				}
				else
					counter=!counter;
			}
		}
		return counter;
	}

	static boolean isPowerOf2(long value) {
		return (value & -value) == value;
	}

	static long findNextPowerOf2Num(long value) {
		while (!isPowerOf2(value)) {
			value = value - 1;
		}
		return value;
	}
}
