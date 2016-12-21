package utils.poirot;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class WordsGenerator
{
	private static Hashtable hash;
	private static String delimiter = " .,:;/?'\"[]{})(-_=+~!@#$%^&*<>\n\t\r1234567890";
	private static String splitRegex = "[A-Z][a-z]+|[a-z]+|[A-Z]+";
	//private static String stopwordsFile = "./edu/depaul/poirotplus/cleanser/stopwords.txt";
	private static String stopwordsFile = "./bin/utils/poirot/stopwords.txt";


	/**
	 * <<<**** Discard ****>>>
	 * This function is to clean a string of data.  Cleaning entails
	 * splitting into substrings, removing stopwords, stemming (Porter 
	 * Stemming Algorithm), and counting instances of substrings
	 *
	 * NOTE: The function name will probably be changed in the future
	 *
	 * @param[in] data a string of which to clean
	 * @return a hash table of clean substrings and their instance count
	 */
	public static Hashtable getWordsList(String data)
	{
		/*kdrew: initializations*/
		ArrayList arrayList;
		hash = new Hashtable();

		IgnoreWords ignoreWords = new IgnoreWords(stopwordsFile);
		Splitter splitter = new Splitter(delimiter, splitRegex);
		Stemmer stemmer = new Stemmer();
		Counter counter = new Counter();

		//System.out.println("Orig: " + data);
		if(data == null) data = "";		
		arrayList = splitter.runSplitter(data);
		//System.out.println("Split: " + arrayList);

		arrayList = ignoreWords.runIgnoreWords(arrayList);
		//System.out.println("Ignored: " + arrayList);

		arrayList = stemmer.runStemmer(arrayList);
		//System.out.println("Stemmed: " + arrayList);

		hash = counter.runCounter(arrayList);
		//System.out.println("Counter: " + hash);

		return hash;
	}
	
	//	 added by jlin8, 2006-09-06
	public static Hashtable getWordsList(String data, ArrayList stopwords)
	{

		/* initializations*/
		ArrayList arrayList;
		hash = new Hashtable();

		IgnoreWords ignoreWords = new IgnoreWords(stopwords);
		Splitter splitter = new Splitter(delimiter, splitRegex);
		Stemmer stemmer = new Stemmer();
		Counter counter = new Counter();

		//System.out.println("Orig: " + data);
		if(data == null) data = "";		
		arrayList = splitter.runSplitter(data);
		//System.out.println("Split: " + arrayList);

		arrayList = ignoreWords.runIgnoreWords(arrayList);
		//System.out.println("Ignored: " + arrayList);

		arrayList = stemmer.runStemmer(arrayList);
		//System.out.println("Stemmed: " + arrayList);

		hash = counter.runCounter(arrayList);
		//System.out.println("Counter: " + hash);

		return hash;
	}
	
	/*
	// added by jlin8, 2005-10-22
	public static Hashtable getWordsList(String data, ArrayList stopwords)
	{

		IgnoreWords word = new IgnoreWords(stopwords);
		hash = new Hashtable();
		StringTokenizer st = new StringTokenizer(data, delimiter);
		while(st.hasMoreTokens()){
			//ArrayList arrayList = Splitter.runSplitter("hello");
			String s = Stemmer.runStemmer(st.nextToken());
			
			if(!word.isIgnoreWord(s) && s.length()!=1)
			{
				if(!hash.containsKey(s)){
					Integer i = new Integer(1);
					hash.put(s, i);
				}
				else
				{
					Integer i = (Integer)hash.get(s);
					int x = 1+i.intValue();
					hash.put(s, new Integer(x));

				}
			}
		}
		return hash;
	}
	*/

}

