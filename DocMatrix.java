/*
 * Creates a document matrix, one thats non-transformed
 * and one thats TF-IDF transformed
 */

import java.io.*;
import java.util.*;


public class DocMatrix {

	public static ArrayList<String> articles = new ArrayList<String>();
	public static String[] myArticles;
	public static int num_articles = 0;
	
	//TF(t) = (Number of times term t appears in a document) / (Total number of terms in the document).
	public static double[] calcTF(String article, int[] matrixRow) { 
		double[] result =  new double[matrixRow.length];
		
		//FOR WORD COUNT: fresh array with stop words included and merged "-"
		article = article.trim().replaceAll("[ \n\r\t\\_]+", " ");
		article = article.trim().replaceAll("[\\-]+", "");
		String[] tokenizeArticle = article.split(" "); 
		int totalWords = tokenizeArticle.length;
		
		for (int i=0; i<matrixRow.length; i++) {
			result[i] = ((double)matrixRow[i]/(double)totalWords);
		}
		return result;
	}
	
	//IDF(t) = log_e(Total number of documents / Number of documents with term t in it). 
	public static double[] calcIDF(String[] keywords, String[] allArticles) { 
		
		double[] result = new double[keywords.length];

		//for 0->98, the length of a row
		for (int i=0; i<keywords.length; i++) {
			int count = 0;
			String keyword = keywords[i];
			String phrase = keyword.trim();
			phrase = " " + phrase + " ";
			//for 0->24, the number of articles
			for (int j=0; j<allArticles.length; j++) {
				if (allArticles[j].contains(phrase)) {
					count++;
				}
			}
			result[i] =  Math.log((double)num_articles/(double)count);
		}
		return result;
	}
	
	public static String replaceWords(String[] words, String text) {

		for (int i=0; i<words.length;i++) {
			if (words[i] != null) {
				String keyword = words[i];
				String phrase = keyword.trim().replaceAll("[\\_]+", " "); //compare with no _
				
				//adds _'s to the word
				if (text.contains(phrase)) {
					text = text.replaceAll(phrase, keyword);  
				}
			}
		}
		return text;
	}

	public static int[] countWords(String[] topWords, String myArticle) { 
		int[] myCounts = new int[topWords.length];
		String[] articleArray = myArticle.split(" "); //article is turned into an array
		
		if (topWords != null) {
			for (int i=0; i<topWords.length; i++) {
				String keyword1 = topWords[i];
				int wordcount = 0;
				
				//populates matrix!
				for (int j=0; j<articleArray.length; j++) {
					String keyword2 = articleArray[j];
					if (keyword1.equals(keyword2)) {
						wordcount++; 
					}
				}
				myCounts[i] = wordcount;
			}
		}		
		return myCounts;
	}
	
	
	//putting lowercase before lemmas matters
	public static String clean(String article) {
		article = Preprocessing.cleanText(article);
        article = Preprocessing.removeStopWords(article); 
        
		article = article.trim().replaceAll("[\\']+", "");
		article = article.trim().replaceAll("[ \\%\\$]+", " "); 

		article = Preprocessing.lemmatization(article);
        article = article.toLowerCase();
        return article;
	}
	
	//make an array of each document to modify out of my global ArrayList<String> articles
	public static String[] arrayify () {
		String[] myArticles = new String[articles.size()]; 
		int index = 0;
		   for(String str: articles){
				myArticles[index] = String.valueOf(str);
				index++;
		   }
		return myArticles;		   
	}
	
	//read each article into an array
	public static String[] readArticle(String filename) {		
		try {
			File data = new File(filename);
			Scanner sc = new Scanner(data);

			//read in in the paths of the articles
			while (sc.hasNextLine()) {
				String nextfile = System.getProperty("user.dir") + sc.nextLine();
				Scanner textFile = new Scanner(new File(nextfile));
				
				String article = "";				
				while (textFile.hasNextLine()) {
					article += " " + textFile.nextLine();
				}
				articles.add(article);
				num_articles++;	
				textFile.close();
			}
			sc.close();		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String[] myArticles = arrayify();
		return myArticles;
	}

	public static double[][] createMatrix(String[] keywords) {
		
		//populate myArticles[]
		myArticles = readArticle(Bds.datafilename);

		//order my keywords by length so I can replace them properly
		String[] sortedKeys = keywords.clone();
		Arrays.sort(sortedKeys, Comparator.comparingInt(String::length).reversed());	
			 
		//replace keywords in myArticles[]
		for (int i=0; i<myArticles.length; i++) {
			myArticles[i] = clean(myArticles[i]);
			myArticles[i] = replaceWords(sortedKeys, myArticles[i]); //now word article array has _'s
		}

		//make a matrix of the proper size
		int[][] matrix = new int[num_articles][keywords.length];
		
		
		//populate matrix row per article counts
		if (myArticles != null) {
			for (int i=0; i<num_articles; i++) {
				matrix[i]= countWords(keywords, myArticles[i]);
			}
		}
		
		double[][] tfidfMatrix = new double[num_articles][keywords.length];
		String[] fullArticles = arrayify(); //fresh string array with stop words
	
		//TF(t) = (Number of times term t appears in a document) / (Total number of terms in the document).
		for (int i=0; i<num_articles; i++) {
			tfidfMatrix[i] = calcTF(fullArticles[i], matrix[i]);
		}
		
		//IDF(t) = log_e(Total number of documents / Number of documents with term t in it). 
		double[] temp2 = calcIDF(keywords, myArticles);

		//transform matrix with TF-IDF
		for (int i=0; i<num_articles; i++) {
			for (int j=0; j<keywords.length; j++) { 
				tfidfMatrix[i][j] *= temp2[j];
			}
		}
				
		return tfidfMatrix;
	}
}


