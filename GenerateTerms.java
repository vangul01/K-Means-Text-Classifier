/*
 * Generate list of most common words among all documents
 * NOTE: words are ordered by frequency of occurrence
 */

import java.util.*;
import java.io.*;
import edu.stanford.nlp.simple.*;

public class GenerateTerms {

	public static ArrayList<Word> myWords = new ArrayList<Word>();
	public static String myArticle;
	
	public GenerateTerms() {}
	
	//CASE SENSITIVE!!! no lower-case before calling this method
	// if the word is a number connect it to the next word
	// if the word is in caps and the next word is in caps, connect them 
	public static String[] ner(String text) {	
		Sentence sent = new Sentence(text);//"lucy is in the Sky with diamonds.");
		List<String> nerTags = sent.nerTags();  // [PERSON, O, O, O, O, O, O, O]
		
		String[] myNers = text.split(" ");
		//if x at index of nerTags is not 0 and its the same as its next tag, combine the words at those indexes in the string[]
		String freshNers = String.join(",", nerTags); //suspicious...may lead to combining words
		freshNers = freshNers.replaceAll("\\,", ""); 
		String[] nerArray = freshNers.split(" ");

		for (int i=0; i< (nerArray.length) - 1; i++) {
			if (nerArray[i] != "0" && nerArray[i] == nerArray[i+1]) {
				myNers[i] = myNers[i] + "_" + myNers[i+1];
			}
		}
		return myNers;
	}	

	public static String[] ngrams(String text, int n) {
		String lowerArticle = text.toLowerCase();
		String[] article = lowerArticle.split(" ");

		int ngramLen = (article.length - n + 1);
		String [] ngram = new String[ngramLen];
		
		String temp = "";
		for (int i=0; i<=(article.length - n); i++) {
			temp = "";
			
			if ((i+n) <= article.length) {
				for (int j=0; j<n; j++) { //from 0->n words to collect grams
					temp += article[i+j] + "_";	
				}
			} 	
			ngram[i] = temp;
		}
		return ngram;
	}
	
	//remove the duplicates of words in text string, caps are accounted for
	public static String[] deDup(String[] articles) {
		Set<String> set = new HashSet<String>(Arrays.asList(articles));
		String[] result = new String[set.size()];
		result = set.toArray(result);
		return result;
	}
	
	//counts a strings words occurences within itself
	public static void wordCount(String[] text, int minimum) {
		String compare[] = deDup(text);
		
		if (compare != null) {
			for (int i=0; i<compare.length; i++) {
				String keyword = compare[i];
				int wordcount = 0;

				for (int j=0; j<text.length;j++) {
					String keyword2 = text[j];
					if (keyword.equals(keyword2)) {
						wordcount++;
					}
				} 

				//outputs the words that are >= length 2 and frequency >= min
				if (compare[i].length()>2 && wordcount >= minimum) {
					//remove _ before comparing words to remove
					keyword = keyword.trim().replaceAll("[\\_]+", " ");
					myArticle = myArticle.replaceAll(keyword, " ");
					
					//add word to topWords
					keyword = keyword.trim().replaceAll(" ", "_");
					myWords.add(new Word(keyword, wordcount));
				}
			}
		}
	}		
		
	public static String[] sortTopWords () {
		Collections.sort(myWords, Word.WordCount);
		String[] wordsArray = new String[myWords.size()]; 
		int index = 0;
		   for(Word str: myWords){
				wordsArray[index] = String.valueOf(str);
				index++;
		   }
	   
		return wordsArray;		   
	}
	
	public static void printTopWords (String[] myTops) {
		//print to console
		System.out.println("---------Top Words (listed by highest frequency across all documents)--------");
		for (int i=0; i<myTops.length; i++) {
			if (myTops[i] != null) {
				System.out.println((i+1) + ". " + myTops[i]);
			}
		}
		//print to file
		try {
			//Creating a File object that represents the disk file. 
	        PrintStream o = new PrintStream(new File("topics.txt")); 
	        //Store current System.out before assigning a new value 
	        PrintStream console = System.out; 
	        //Assign o to output stream 
	        System.setOut(o); 
	        System.out.println("---------Top Words (listed by highest frequency across all documents)--------");
			for (int i=0; i<myTops.length; i++) {
				if (myTops[i] != null) {
					System.out.println((i+1) + ". " + myTops[i]);
				}
			}
	        //reset output stream
	        System.setOut(console); 
		} catch (FileNotFoundException e) {
		    System.err.println(e.getMessage());
		    System.exit(-1);
		}
	}
	
	public static String[] topWords(String cleanedText) {

		cleanedText = cleanedText.toLowerCase();
		myArticle = cleanedText;
		//ner(myArticle); //too slow, n-grams does the job 
		
		//generate 4-grams-----------------------------------------
		String[] my4Grams = ngrams(myArticle, 4);//# of grams
		wordCount(my4Grams, 4); //minimum # of counts 4
		
		//generate 3-grams-----------------------------------------
		String[] my3Grams = ngrams(myArticle,3);
		wordCount(my3Grams, 5); //5

		//generate 2-grams-----------------------------------------
		String[] my2Grams = ngrams(myArticle,2);
		wordCount(my2Grams, 8); //8
		
		//choose top words from updated text-----------------------
		String[] fullText = myArticle.split(" ");
		wordCount(fullText, 10); //10

	   //LAST STEP: put in a list and sort by frequency
	    String[] result = sortTopWords();
	    //printTopWords(result);
	    
	    return result;
	}
}

