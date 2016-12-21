package utils.poirot;


import java.lang.Character;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.regex.*;

/**
 * Splitter: divide strings into component parts
 */

public class Splitter
{
	private static String delimiter;
	private static String splitRegex;

	public Splitter()
	{
		delimiter = " .,:;/?'\"[]{})(-_=+~!@#$%^&*<>\n\t\r1234567890";
		splitRegex = "[A-Z][a-z]+|[a-z]+|[A-Z]+";

	}
	Splitter(String d, String s)
	{
		/*kdrew: I might need a string copy here*/
		delimiter = d;
		splitRegex = s;
	}

	public static ArrayList runSplitter(String s)
	{
		StringTokenizer st = new StringTokenizer(s, delimiter);
		ArrayList list = new ArrayList();
		//System.out.println("running Splitter");


		while(st.hasMoreTokens())
		{
			String tok = st.nextToken();

			Pattern p = Pattern.compile(splitRegex);
			Matcher m = p.matcher(tok);
			boolean found = m.find();

			//System.out.print(tok + ":");
			while(found)
			{
				String subStringFound = m.group();
				//System.out.print(" " + subStringFound);
				subStringFound = subStringFound.toLowerCase();

				/*kdrew: substring must be larger than length one*/
				if(1 < subStringFound.length())
				{
					list.add(subStringFound);
				}
				found = m.find();
			}

			//System.out.println();
		}

		return list;
	}
}


