/*
 *  implements k-means, cosine, euclidean, groups clusters according to distances 
 */

import java.util.*;

public class Similarity {
	public static final int ITERATIONS = 20;
	public static int num_words = 0;
	public static int num_docs = 0;
	public static int num_clusters = 0; 
	public static int[][] predicted_clusters; 
	
////////////////////////////////////////////////////////////////////	
	public Similarity() {}
	
////////////////////////////////////////////////////////////////////
	//initialize clusters
	public static double[][] init_clusters(double[][] tfidf_matrix) {	
		double[][] init_means = new double[num_clusters][num_words];
		//pick 3 unique random documents from 0-23

		ArrayList<Integer> mylist = new ArrayList<Integer>();
        for (int i = 0; i <= num_docs; i++) {
            mylist.add(new Integer(i));
        }
        Collections.shuffle(mylist);
		
        for (int i = 0; i < num_clusters; i++) {
        	init_means[i] = tfidf_matrix[mylist.get(i)];
//        	System.out.println("MY RAND: " + mylist.get(i));
        }
	
		return init_means;
	}	
	
////////////////////////////////////////////////////////////////////	
	public static double[][] init_kpp_clusters(double[][] tfidf_matrix) {	
		double[][] init_means = new double[num_clusters][num_words];

		ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < num_docs; i++) {
            list.add(new Integer(i));
        }
        Collections.shuffle(list);
		
        for(int i=0; i<num_clusters; i++) {
//	       	System.out.println("MY RAND: " + list.get(0));
//	       	System.out.println("init_means["+i+"]: tfidf_matrix["+ list.get(0)+"]");
	        init_means[i] = tfidf_matrix[list.get(0)];											
	        list.remove(list.get(0));
	        
	        
	        double[] similarity = new double[num_docs];
	        for (int j=0; j<num_docs; j++) {
	    		similarity[j] = cosineSimilarity(init_means[i], tfidf_matrix[j]);   
	        	
	    		//ORRRRR! Choose similarity that is smallest next, FIND MIN SIMILARITY AND CHOOSE IT!
	    		if (similarity[j] > 0.05) {
	        		list.remove(new Integer(j));
	        	}
	        }     
        }   
		return init_means;
	}
	
////////////////////////////////////////////////////////////////////
	//compute Euclidean distance between two vectors
	private static double euclidianDistance(double[] vectorA, double[] vectorB) {
		double diff = 0;
		double sum = 0;
		
		for (int i=0; i<vectorA.length; i++){ //vectorA.length is num words = 96
			diff = vectorA[i]-vectorB[i];
			sum += Math.pow(diff, 2); 
		}
		return Math.sqrt(sum); 
	}

////////////////////////////////////////////////////////////////////
	//compute cosine similarity between two vectors
	public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
	    double dotProduct = 0;
	    double normA = 0;
	    double normB = 0;
	    
	    for (int i = 0; i < vectorA.length; i++) {
	        dotProduct += vectorA[i] * vectorB[i];
	        normA += Math.pow(vectorA[i], 2);
	        normB += Math.pow(vectorB[i], 2);
	    }   
	    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

////////////////////////////////////////////////////////////////////	
	public static int[][] compute_cosine(double[][] input_matrix, double[][] cluster_means) {
		//find cosine similarity between means and documents
		double[][] distance = new double[num_docs][num_clusters]; 
		for (int i=0; i<num_docs; i++) {
			for (int j=0; j<num_clusters; j++) {
				distance[i][j] = cosineSimilarity(input_matrix[i], cluster_means[j]); //c_m = k x words
			}
		}
		
		int[][] my_clusters = new int[num_clusters][num_docs];
		predicted_clusters = new int[num_clusters][num_docs]; 
		
		for (int i=0; i<num_docs; i++) { //look for max because number has to be closest to 1
			double max = 0;
			int cluster = -1;
			
			for (int j=0; j<num_clusters; j++) {
				if(distance[i][j] >= max) {
					max = distance[i][j];
					cluster = j;
				}
				
			}
//			System.out.println(i +" CLUSTER J = " + cluster);
			my_clusters[cluster][i] = 1;//cluster;//distance[i][cluster]; 
		}
		
		for (int i=0; i<num_clusters; i++) {
			for (int j=0; j<num_docs; j++) {
				if (my_clusters[i][j] > 0) {
					predicted_clusters[i][j] = (int)my_clusters[i][j];
				}
			}
		}
		
		//print out what articles are in which cluster
/*		for (int i=0; i<num_clusters; i++) {
			System.out.println("cluster " + i);
			for (int j=0; j<num_docs; j++) {
				System.out.print(my_clusters[i][j] + " ");
			}
			System.out.println();
		}
*/
		return my_clusters;
	}
////////////////////////////////////////////////////////////////////
	
	public static int[][] compute_euclidian(double[][] input_matrix, double[][] cluster_means) {
		//find cosine similarity between means and documents
		double[][] distance = new double[num_docs][num_clusters]; 
		for (int i=0; i<num_docs; i++) {
			for (int j=0; j<num_clusters; j++) {
				distance[i][j] = euclidianDistance(input_matrix[i], cluster_means[j]); //c_m = k x words
			}
		}
		
		int[][] my_clusters = new int[num_clusters][num_docs];
		predicted_clusters = new int[num_clusters][num_docs]; 
		
		for (int i=0; i<num_docs; i++) {//look for min because number has to be closest to 0
			double min = 100;
			int cluster = -1;
			
			for (int j=0; j<num_clusters; j++) {
				if(distance[i][j] <= min) {
					min = distance[i][j];
					cluster = j;
				}
			}	
//			System.out.println(i +" CLUSTER J = " + cluster);
			my_clusters[cluster][i] = 1;
		}
	
		for (int i=0; i<num_clusters; i++) {
			for (int j=0; j<num_docs; j++) {
				if (my_clusters[i][j] > 0) {
					predicted_clusters[i][j] = (int)my_clusters[i][j];
				}
			}
		}
		
		//print out what articles are in which cluster
/*		for (int i=0; i<num_clusters; i++) {
			System.out.println("cluster " + i);
			for (int j=0; j<num_docs; j++) {
				System.out.print(my_clusters[i][j] + " ");
			}
				System.out.println();
		}	
*/
		return my_clusters;
	}		

////////////////////////////////////////////////////////////////////
	public static void kmeans(double[][] matrix, int k, boolean cosine) {
		double[][] myMatrix = matrix.clone();  //dim = #articles x #words
		int count = 0;
		
		//initialize vars	
		num_clusters = k;
		if (myMatrix != null) {
			num_words = myMatrix[0].length;
			num_docs = myMatrix.length;
		}
		
		//initialize clusters
		double[][] means = new double[num_clusters][num_words];
		means = init_kpp_clusters(myMatrix);	//k++ means
		//means = init_clusters(myMatrix);		//k-means
		
		//THE START OF MY GIANT LOOP - until the centroids do not update anymore, keep looping
		while (count < ITERATIONS) {		
			//assign docs to closest cluster
			int[][] clusters = new int[num_clusters][num_docs];
			if (cosine) {
				clusters = compute_cosine(myMatrix, means); //k x docs
			} else {
				clusters = compute_euclidian(myMatrix, means);
			}
				
			//copy myMatrix into cluster_vectors to calculate mean for each cluster		
			for (int i=0; i<num_clusters; i++) {
				double[][]cluster_vectors = new double[num_words][num_docs]; 
				double total_docs = 0;
				
				for (int j=0; j<num_docs; j++) {
					if (clusters[i][j] > 0) {			
						total_docs++;
						for (int n=0; n<num_words; n++) {
							cluster_vectors[n][j]= myMatrix[j][n];	//holds assigned myMatrix[doc_num][word]'s for each cluster
						}
					}
				}				
		
				//find the mean of all the vectors here! Before the next cluster is made
				for(int row=0; row<num_words; row++) {
					double sum =0;
					for(int col=0; col<num_docs; col++) {
						sum += cluster_vectors[row][col];
					}	
					means[i][row] = (double)sum/(double)total_docs;
				}
			}			
			count++;
		}
	}
}

	