package utils.poirot;

import java.util.Hashtable;
import java.util.ArrayList;

/**
 * Counter: count the number of instances of strings in an ArrayList
 */

public class Counter
{
	Counter()
	{

	}

	/**
	 * This function records the number of instances of strings in a list of strings
	 * @param[in] arrayList original list of which to count instances of strings
	 * @return a hashtable of unique strings with the count of their instances
	 */
	public static Hashtable runCounter(ArrayList arrayList)
	{
		Hashtable returnHash = new Hashtable();

		for(int j = 0; j < arrayList.size(); j++)
		{
			String s = (String)arrayList.get(j);

			if(!returnHash.containsKey(s)){
				Integer i = new Integer(1);
				returnHash.put(s, i);
			}
			else
			{
				Integer i = (Integer)returnHash.get(s);
				int x = 1+i.intValue();
				returnHash.put(s, new Integer(x));
			}
		}
		return returnHash;
	}
}
