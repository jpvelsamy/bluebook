package hackerrank;

import java.util.Scanner;

public class MatrixSolution {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        Matrix m = new Matrix(n);
        for(int a_i=0; a_i < n; a_i++){
            for(int a_j=0; a_j < n; a_j++)
            {
                int value = in.nextInt();
                Element elem = new Element(a_i, a_j, value);
                m.fillElement(elem);
            }
        }
        m.findPDiagonal();
        //System.out.println(Arrays.toString(m.primaryDiagonal));
        m.findSDiagonal();
        int diff =m.findAbsDiff();
        System.out.println(diff);
        in.close();
	}
}

class Matrix
{
	Element [][]matrixMembers;
	int size;
	int []primaryDiagonal;
	int [] secondaryDiagonal;
	
	Matrix(int matrixSize)
	{
		matrixMembers = new Element[matrixSize][matrixSize];
		size=matrixSize;
		primaryDiagonal = new int[matrixSize];
		secondaryDiagonal = new int[matrixSize];
	}
	void findPDiagonal()
	{
		for(int i=0;i<size;i++)
		{
			for(int j=0;j<size;j++)
			{
				if(i==j)
				{
					primaryDiagonal[i]=matrixMembers[i][j].value;
					
				}
			}
		}
	}
	void findSDiagonal()
	{
		int yCounter=0;
		int index=0;
		for(int i=size-1;i>=0;i--)
		{
			secondaryDiagonal[index]=matrixMembers[i][yCounter].value;
			yCounter++;
			index++;
		}
	}
	
	int findAbsDiff()
	{
		int pSum=0;
		for(int i=0;i<size;i++)
		{
			pSum +=primaryDiagonal[i]; 
		}
		
		int sSum=0;
		for(int i=0;i<size;i++)
		{
			sSum +=secondaryDiagonal[i]; 
		}
		//System.out.println("p="+pSum+",s="+sSum);
		int diff = pSum-sSum;
		return Math.abs(diff);
	}
	
	void fillElement(Element elem)
	{
		matrixMembers[elem.x][elem.y] = elem;
	}
	
}

class Element
{
	int x;
	int y;
	int value;
	public Element(int x, int y, int value) {
		super();
		this.x = x;
		this.y = y;
		this.value = value;
	}
	
	
	
}
