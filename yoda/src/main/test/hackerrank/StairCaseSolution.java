package hackerrank;

import java.util.Scanner;

public class StairCaseSolution {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int stairCaseBlock = in.nextInt();
		if (stairCaseBlock == 0)
			System.out.print("#");
		else {
			for (int i = 1; i <= stairCaseBlock; i++) 
			{
				int emptySpaces=stairCaseBlock-i;
				for (int j = 0; j < emptySpaces; j++) 
				{
					System.out.print(" ");
				}
				for (int j = 0; j < i; j++) 
				{
					System.out.print("#");
				}
				System.out.print("\n");
			}
		}
		in.close();
	}
}
