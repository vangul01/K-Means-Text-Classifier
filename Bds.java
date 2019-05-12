/*
 *main class :)
*/

import java.io.IOException;

public class Bds {
 
	////file names, update here if needed
	public static String datafilename = "data.txt";
	public static String stopwordsfilename = "stopwords.txt"; 
	public static String unknownsfilename = "datahw/";
	public static final int NUM_ARTICLES = 24;
	public static final int K = 3;
	public static final int UNK_K = 3;    ///update this for KNN
	////////////////////////////////
	 
	Bds() {}

	public static void main(String[] args) throws ClassCastException, IOException {
//		System.out.println("Preprocessing begin---------------"); 

        //preprocess all articles to obtain a clean string of text
        String cleanedText = Preprocessing.preprocess();        
//        System.out.println("Preprocessing complete---------------"); 
        
        //generate list of keywords from the clean string of text
        String[] topWords = GenerateTerms.topWords(cleanedText);
//        System.out.println("Term generation complete---------------"); 
   
        //create document matrix
        double[][] myMatrix = DocMatrix.createMatrix(topWords);
//        System.out.println("Document matrix complete---------------");    
        
        //similarity methods: input matrix, number of clusters, true if cosine similarity false if euclidean
        Similarity.kmeans(myMatrix, K, true);	//myMatrix shape = #articles x #words 
//        System.out.println("K-means complete-----------------------");   
        
        //evaluations and topics 
        Clustering.confusion_matrix(myMatrix, topWords);	//myMatrix shape = #articles x #words 
//        System.out.println("Evaluations complete------------------");   
        
        //find k nearest neighbors for unknown documents
        KNN.run_knn(unknownsfilename, topWords, myMatrix, UNK_K);
//        System.out.println("KNN complete------------------");  
        
	}
}
