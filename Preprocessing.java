/*
* reading the input text data, cleaning it, remove stop words
*/

import java.util.*;
import java.io.*;
import edu.stanford.nlp.simple.*;

public class Preprocessing {
	
	///// My Global Vars
	public static String[] stopWords = null;
	public static String allText = "";
	public static int numFiles = 0;
	////////////////////
	
	Preprocessing() { }
	
	//messes with capitalization...
	public static String lemmatization(String text) {
		  Sentence sentence = new Sentence(text);
		  List<String> lemmas = sentence.lemmas();
		  String myLemmas = String.join(", ", lemmas); 
		  myLemmas = myLemmas.replaceAll("\\,", ""); 
		  return myLemmas;
	}	
	
	public static String[] tokenizer(String data) {
		String[] tokenizeData = data.split(" ");
		return tokenizeData;
	}

	//read stopwords from an input textfile
    public static String[] readStopWords(String stopWordsFilename) {
    	String filename = stopWordsFilename;

        try {
        	File stopWordsFile = new File(filename); 
    		Scanner countScan = new Scanner(stopWordsFile); 
    		Scanner stopWordsScan = new Scanner(stopWordsFile);
        	
        	int numStopWords = 0;
        	while (countScan.hasNextLine()) {
        		numStopWords++;
        		countScan.nextLine();
  			}

            stopWords = new String[numStopWords];
            for (int i = 0; i < numStopWords; i++) {
                stopWords[i] = stopWordsScan.nextLine();
            }

            countScan.close();
            stopWordsScan.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        return stopWords;
     }

    //remove stopwords from a string
    public static String removeStopWords(String article) {    
    	if (stopWords != null) {
			for (int i=0; i < stopWords.length; i++) {
				String stopword = "(?i)\\b"+ stopWords[i] +"\\b"; //(?i) case insensitive
	
				//compare with a lower case version of the stop words
				String lowered = article;
				lowered = lowered.toLowerCase();
				
				if (lowered.contains(stopWords[i])) {
					article = article.replaceAll(stopword, "");
				}
			}
    	}
		return article;
    }

    //remove punctuation from a string
	public static String cleanText(String text) {
		String cleaned = text.trim().replace("[\\.] +", " ");
		cleaned = cleaned.trim().replace(",", "");
		cleaned = cleaned.trim().replaceAll("[ \n\r\t\"\\/\\_\\-\\;\\:\\?\\!\\[\\]\\(\\)]+", " ");//"[\n\r\t\\/\\_\\-]+", " "); //"[ \n\r\t\"\\/\\_\\-\\,\\.\\;\\:\\?\\!\\[\\]\\(\\)]+", " "

		//remove periods at the end
		String[] cleanArray = tokenizer(cleaned);
		for (int i=0; i<cleanArray.length; i++) {
			if(cleanArray[i] != null && cleanArray[i].length() > 0 && cleanArray[i].charAt(cleanArray[i].length() - 1) == '.') {
				cleanArray[i] = cleanArray[i].substring(0, cleanArray[i].length() - 1);
			}			
		}

		String squeakyClean = "";
		for (int i=0; i<cleanArray.length; i++) {
			squeakyClean += cleanArray[i] + " "; 
		}
		
		squeakyClean = removeStopWords(squeakyClean);
		squeakyClean = squeakyClean.trim().replaceAll("[\\']+", "");
		squeakyClean = squeakyClean.trim().replaceAll("[ \\%\\$]+", " "); // "[ \\%\\$]+", " "
		
		return squeakyClean;
	}
	
	//read in all textfiles into one string
	public static String readText(String datafilename) { 
		String filename = datafilename;
		
		try {
			File data = new File(filename);
			Scanner sc = new Scanner(data);

			//read in in the paths of the articles
			while (sc.hasNextLine()) {
				String nextfile = System.getProperty("user.dir") + sc.nextLine();
				Scanner textFile = new Scanner(new File(nextfile));
				numFiles++;

				//actually reading the articles
				while (textFile.hasNextLine()) {
					allText += " " + textFile.nextLine();
				}
				textFile.close();
			}
			sc.close();		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return allText;
	}
	
	//perform preprocessing steps to return clean string of all textfiles
	public static String preprocess() {
        stopWords = readStopWords(Bds.stopwordsfilename);
		String myData = readText(Bds.datafilename);
		myData = cleanText(myData);
        String cleanedText = lemmatization(myData);
        cleanedText = cleanedText.toLowerCase();
        return cleanedText;
	}
}


 