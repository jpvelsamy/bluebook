package nlp;

public class JaccardDistance {

	static int findDuplicates(char[] s, boolean[] sDuplicates)
	{
		int nDup=0;
		for (int i = 0; i < s.length; i++) {
			if(sDuplicates[i])
			{
				nDup++;
			}
			else
			{
				for(int j=i+1;j<s.length;j++)
				{
					if(!sDuplicates[j])
					{
						sDuplicates[j]=s[i]==s[j];
					}
				}
			}
		}
		return nDup;
	}

	public static float jaccard(char[] s, char[]t)
	{
		float coefficient = 0;
		int intersection = 0;
		int union = s.length+t.length;
		boolean [] sDuplicates = new boolean[s.length];
		union = union - findDuplicates(s, sDuplicates);
		boolean [] tDuplicates =  new boolean[t.length];
		union = union - findDuplicates(t, tDuplicates);
		for (int i = 0; i < s.length; i++) {
			if(!sDuplicates[i])
			{
				for (int j = 0; j < t.length; j++) {
					if(!tDuplicates[j])
					{
						if(s[i]==t[j])
						{
							intersection++;
							break;
						}
								
					}
				}
			}
			
		}
		union=union-intersection;
		coefficient = (float) intersection/union;
		return coefficient;
	}

}
