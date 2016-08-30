package hackerrank;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Ranking {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);

		int a0 = in.nextInt();
		int a1 = in.nextInt();
		int a2 = in.nextInt();

		int b0 = in.nextInt();
		int b1 = in.nextInt();
		int b2 = in.nextInt();

		PersonRank person1 = new PersonRank(a0, a1, a2);
		PersonRank person2 = new PersonRank(b0, b1, b2);

		int firsPersonScore = person1.computeTotalScore(person2);
		int secondPersonScore = person2.computeTotalScore(person1);

		System.out.print(firsPersonScore+" ");
		System.out.print(secondPersonScore);
	}

}

final class PersonRank {
	int firstScore;
	int secondScore;
	int thirdScore;

	int istFirstBetter;
	int istSecondBetter;
	int isThirdBetter;

	PersonRank(int firstScore, int secondScore, int thirdScore) {
		super();
		this.firstScore = firstScore;
		this.secondScore = secondScore;
		this.thirdScore = thirdScore;
	}

	int computeTotalScore(PersonRank anotherRank) {
		int totalScore = 0;
		if (this.firstScore > anotherRank.firstScore)
			totalScore++;
		if (this.secondScore > anotherRank.secondScore)
			totalScore++;
		if (this.thirdScore > anotherRank.thirdScore)
			totalScore++;
		return totalScore;
	}

}
