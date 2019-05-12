/* Take K as input to find k nearest documents to unknown document and 
 * then label unknown according to the majority of its neighbors
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class KNN {

	public static int k = 0;
	public static int num_docs = 0;
	//UPDATE THESE if you want to use more documents
	public static int[][] true_clusters = {
			{0,1,2,3,-1,-1,-1,-1,-1,9},
			{-1,-1,-1,-1,4,5,-1,-1,8,-1},
			{-1,-1,-1,-1,-1,-1,6,7,-1,-1}};
	public static final int G1 = 5;
	public static final int G2 = 3;
	public static final int G3 = 2;
	public static double[] doc_total = {G1, G2, G3};
	
	
	public KNN() {}

	
    public static int neighbors(int k, int[] my_top_docs, int doc_num) { 
	    //print out the topics for all docs in top_k
	    int[][] clusters = Clustering.defined_clusters.clone();
	    int index = 0;
	    
	    //match the tops to the clusters they are in
	    int[] top_clusters = new int[k]; 
	    double[] majority; //3 clusters
	    int mygroup = -1;
	    
	    System.out.println("Unknown Document " + (doc_num+1) + "'s Closest K neighbors:");
	    if (clusters != null) {
	    	majority = new double[clusters.length]; //3 clusters
	    	while (index < k) {
		        for (int i=0; i<clusters.length;i++) {	//3
		        	
		        	for (int j=0; j<clusters[0].length; j++) {	//24
		        		if ((index < k) && clusters[i][j] == my_top_docs[index]) {
		        			top_clusters[index] = i;
		        			majority[i]++;
		        			System.out.println("Document " + clusters[i][j] + " in " + Clustering.my_topics[i]);
		        			index++;
		        		}
		        	}
		        }
			}

	        //assign unknown to a topic based on top_k
			double group = sim(majority);
			mygroup = (int)group;
			System.out.println("Unknown Document " + (doc_num+1) + " most likely belongs to Topic " + Clustering.my_topics[mygroup] + "\n"); //(int)group
	    }
	    
		return mygroup;
    } 
	
    
	//read each article into an array
	public static String readArticle(String filename) {
		
		String article = "";
		try {	
			File data = new File(filename);
			Scanner sc = new Scanner(data);
			//read in in the paths of the articles
			while (sc.hasNextLine()) {
				article += " " + sc.nextLine();
			}
			sc.close();		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return article;
	}
	
	//find the most similar document
	public static int sim(double[] similarities) {
        double max = 0;
        int doc_num = -1;
          
		for (int i=0; i<similarities.length; i++) {
        	if (similarities[i] >= max) {
        		doc_num = i;
        		max = similarities[i];
			}
		}
		return doc_num;
	}
	
	public static int gen_cmatrix(int[] true_clusters, int[] cluster, int num_docs) {
		int count = 0;
		
		for (int i=0; i<num_docs; i++) {
			if ((cluster[i] > -1) && cluster[i] == true_clusters[i]) {
				count++;	
			}
		}
		return count;
	}
	
	public static void print_cmatrix(int[][] conf_Matrix) {
		System.out.print("\nCONFUSION MATRIX\n\n");
		System.out.println("     Actual: 1 2 3");
		for(int i=0; i<Bds.K; i++) {
			System.out.print("Predicted "+(i+1)+": ");
			int sum = 0;
			for(int j=0; j<Bds.K; j++) {
				sum += conf_Matrix[i][j];
				System.out.print(conf_Matrix[i][j] + " ");
			}
			System.out.print( "|" + sum);
			System.out.println();
		}
		System.out.println("\t     " + G1 + " " + G2 +  " " + G3);
		
		System.out.println("TOPICS KEY:");
		System.out.println("1:Topic " + Clustering.my_topics[0]);
		System.out.println("2:Topic " + Clustering.my_topics[1]);
		System.out.println("3:Topic " + Clustering.my_topics[2]);
	}
	
	public static void computations(int[][] conf_Matrix) {
		double[] my_precision = precision(conf_Matrix);
		double[] my_recall = recall(conf_Matrix);
		double[] my_fmeasure = f_measure(my_precision, my_recall);
		
		double psum=0, rsum=0, fm=0;

		System.out.println("\nPRECISION");
		for (int i=0; i<Bds.K; i++ ) {
			System.out.print(Clustering.my_topics[i] + "= " + my_precision[i] + "  ");
			psum += my_precision[i];
		}
		
		System.out.println("\n\nRECALL");
		for (int i=0; i<Bds.K; i++ ) {
			System.out.print(Clustering.my_topics[i] + "= " + my_recall[i] + "  ");
			rsum += my_recall[i];
		}
		System.out.println("\n\nF MEASURE");
		for (int i=0; i<Bds.K; i++ ) {
			System.out.print(Clustering.my_topics[i] + "= " + my_fmeasure[i] + "  ");
		}
		
		fm = 2 * ((double)(psum*rsum) / (double)(psum+rsum));
		
		System.out.println("\n\nTOTAL PRECISION = " + psum/Bds.K + 
				"\nTOTAL RECALL = " + rsum/Bds.K + 
				"\nTOTAL F-MEASURE = " + fm/Bds.K);
	}
	
	public static double[] precision(int[][] conf_Matrix) {
		double[] prec = new double[Bds.K];
		
		for(int i=0; i<Bds.K;i++) {
			int sum = 0;
			int true_val = conf_Matrix[i][i];
			
			for(int j=0; j<Bds.K;j++) {
				sum += conf_Matrix[i][j];
			}
			prec[i] = (double)true_val/(double)sum;
		}
		return prec;
	}
	
	public static double[] recall(int[][] conf_Matrix) {
		double[] rec = new double[Bds.K];
		int true_val = 0;
		
		for(int i=0; i<Bds.K;i++) {
			true_val = conf_Matrix[i][i];
			rec[i] = (double)true_val/(double)doc_total[i];
		}
		return rec;
	}
	
	public static double[] f_measure(double[] precision, double[] recall) {
		double[] fprec = new double[Bds.K];
		for (int i=0; i<Bds.K; i++) {
			fprec[i] = 2 * ((double)(precision[i] * recall[i]) / (double)(precision[i] + recall[i]));
		}
		return fprec;
	}
	
	
	public static void run_knn(String dir, String[] keywords, double[][] input_matrix, int num_neighbors) {
		
		k = num_neighbors;
		////////// put paths of all docs in string
        File folder = new File(dir);
        String[] files = folder.list();
        
        if (files != null) {
        	Arrays.sort(files);
        } else {
			System.out.println("Error with file path!");
	    	System.exit(1);
        }
        
		/////////// read each file
		String[] unknowns = new String[files.length];
		num_docs = unknowns.length;
		for (int i=0; i<unknowns.length; i++) {
			unknowns[i] = readArticle(dir + files[i]); 
		} 

		////////// count occurrences of keywords in each doc
		String[] sortedKeys = keywords.clone();
		Arrays.sort(sortedKeys, Comparator.comparingInt(String::length).reversed());	
		
		
		String[] clean_unknowns = new String[unknowns.length];
		int[][] unk_matrix = new int[unknowns.length][keywords.length];
		for (int i=0; i<unknowns.length; i++) {
			clean_unknowns[i] = DocMatrix.clean(unknowns[i]);
			clean_unknowns[i] = DocMatrix.replaceWords(sortedKeys, clean_unknowns[i]); //now word article array has _'s
			unk_matrix[i] = DocMatrix.countWords(keywords, clean_unknowns[i]); //number of keywords occurrences
		}

		//////////// find tfidf for matrix
		double[][] tfidfMatrix = new double[unknowns.length][keywords.length];
		
		//tf
		for (int i=0; i<unknowns.length; i++) {tfidfMatrix[i] = DocMatrix.calcTF(unknowns[i], unk_matrix[i]);}
		//idf (using idf from original matrix)
		double[] idf= DocMatrix.calcIDF(keywords, DocMatrix.myArticles);  
		//tfidf
		for(int i=0; i<unknowns.length; i++) {	
			for (int j=0; j<keywords.length; j++) { 
				tfidfMatrix[i][j] *= idf[j];
			}
		}
		
		/////////// find k closest neighbors and print out their labels
		double[][] similarity = new double[unknowns.length][Bds.NUM_ARTICLES]; // 10 unknowns, 24 bds docs
        for (int i=0; i<unknowns.length; i++) {
			for (int j=0; j<Bds.NUM_ARTICLES; j++) {
	    		similarity[i][j] = Similarity.cosineSimilarity(tfidfMatrix[i], input_matrix[j]);   
	        } 
        }
        
        int[][] top_k = new int[unknowns.length][k];
        for (int i=0; i<unknowns.length;i++) {
            for(int j=0; j<k; j++) {
            	int top_doc = sim(similarity[i]);
            	top_k[i][j] = top_doc;
            	similarity[i][top_doc] = 0;
            }
        }
        
        //initialize clustering of unknowns
        int[][] unk_clusters = new int[Bds.K][unknowns.length]; //assign unknowns to one of 3 clusters
        for (int i=0; i<Bds.K; i++) { //for 3 clusters
        	for (int j=0; j<unknowns.length; j++) {
        		unk_clusters[i][j] = -1;
        	}
        }
        
        //find k nearest neighbors
        for (int i=0; i<unknowns.length; i++) {
        	int mygroup = neighbors(k, top_k[i], i);
        	unk_clusters[mygroup][i] = i;	//for group x, in document spot y, put y
        }
        
        //make confusion matrix
		int[][] confMatrix = new int[Bds.K][Bds.K];
		for (int i=0; i<Bds.K; i++) {
			for (int j=0; j<Bds.K; j++) {
				confMatrix[i][j] = gen_cmatrix(true_clusters[j], unk_clusters[i], unknowns.length);
			}
		}
		
		print_cmatrix(confMatrix);
		computations(confMatrix);
	}
}

