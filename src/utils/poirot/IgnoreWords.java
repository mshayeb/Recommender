package utils.poirot;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*;
public class IgnoreWords
{
	/*kdrew: this is the windows path to the stopwords.txt file
	"baysnet\\wordsgenerator\\stopwords.txt"
	*/
	private ArrayList ignorewords = new ArrayList();

	IgnoreWords(String stopwordsFile)
	{
		BufferedReader in = null;
		String line = null;

		try
		{
			//in = new BufferedReader(new FileReader(new File("stopwords.txt")));
			in = new BufferedReader(new FileReader(stopwordsFile));
			while ((line = in.readLine()) != null)
				ignorewords.add(line);
		}
	   	catch (FileNotFoundException ex)
	   	{
		  	ex.printStackTrace();
		}
		catch (IOException ex)
		{
		  	ex.printStackTrace();
		}
	    finally
	    {
	      	try
	      	{
	        	if (in!= null)
	          		in.close();
	        }
	    	catch (IOException ex)
	    	{
	    	    ex.printStackTrace();
	    	}
		}
	}

	// added by jlin8, client program provides the stopwords list
	public IgnoreWords(ArrayList stopwords){
                if (stopwords != null) { 
		    ignorewords = stopwords;
                }
	}

	public boolean isIgnoreWord(String word)
	{
		if(word != null && ignorewords.contains(word))
			return true;
		return false;
	}

	/**
	 * This function removes all instances of strings that are in the given Stopwords file
	 * @param[in] arrayList the list of strings to remove stopwords from
	 * @return the input list with stopwords removed
	 */
	public ArrayList runIgnoreWords(ArrayList arrayList)
	{
		ArrayList returnList = new ArrayList();

		for(int i=0; i<arrayList.size(); i++)
		{
			if(!isIgnoreWord((String)arrayList.get(i)))
			{
				returnList.add((String)arrayList.get(i));
			}
		}
		return returnList;
	}
}


