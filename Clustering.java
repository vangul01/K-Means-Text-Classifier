/*
 * initializes true clusters and predicted clusters based on similarity class
 * finds the topics for the predicted cluster groups
 * figures out confusion matrix based on true and predicted clusters
 * finds precision, recall and f1 for each group and total groups
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public class Clustering {

	public static final int ACTUAL_DOCS = 8; 
	public static int[][] defined_clusters;
	public static int num_clusters;
	public static int num_docs; 
	
	///Topics
	public static String[] my_topics;
	
	public Clustering() {}
	 
	public static int[][] init_actual() {
		int[][] actual_value = new int[num_clusters][ACTUAL_DOCS];
		int index = 0;

		for (int i=0; i<num_clusters; i++) {
			for (int j=0; j<ACTUAL_DOCS; j++) {
				actual_value[i][j] = index;
				index++;
			}
		}
		return actual_value;
	}
	
	public static int[][] init_predicted(int[][] cluster_results) {
		int[][] pred_value = new int[num_clusters][num_docs];
		int index = 0;

		for (int i=0; i<num_clusters; i++) {
			for (int j=0; j<num_docs; j++) {
				pred_value[i][j] = -1; //init all values to be -1
			}
		}
		
		for (int i=0; i<num_clusters; i++) {
			index = 0;
			for (int j=0; j<num_docs; j++) {
				if (cluster_results[i][j] > 0) {
					pred_value[i][index] = j;
				}
				index++;
			}
		}
		return pred_value;
	}
		
	//compare all numbers in cluster and actual grouping to determine 
	//and accurately label cluster with most matches																					
	public static int assign_clusters(int[][] true_clusters, int[] cluster) {
		int cluster_group = -1, max=0, temp=0;
		
		for (int i=0; i<num_clusters; i++) {
			int match=0;
			
			for (int j=0; j<ACTUAL_DOCS; j++) {
				for (int n=0; n<num_docs; n++) {
					if (true_clusters[i][j] == cluster[n]) {	//num_clust x actual_docs
						match++;
					}
				}
			}
			temp = match;
			
			if (temp >= max) {
				max = temp;
				cluster_group = i;
			}
		}
		return cluster_group;
	}

	public static int gen_cmatrix(int[] true_clusters, int[] cluster) {
		int count = 0;
		
		for (int i=0; i<num_docs; i++) {
			for (int j=0; j<ACTUAL_DOCS; j++) {
				if (cluster[i] == true_clusters[j]) {
					count++;	
				}
			}
		}
		return count;
	}
	
	public static void print_cmatrix(int[][] conf_Matrix) {
		System.out.print("\nCONFUSION MATRIX\n\n");
		System.out.println("     Actual: 1 2 3");
		for(int i=0; i<num_clusters;i++) {
			System.out.print("Predicted "+(i+1)+": ");
			int sum = 0;
			for(int j=0; j<num_clusters;j++) {
				sum += conf_Matrix[i][j];
				System.out.print(conf_Matrix[i][j] + " ");
			}
			System.out.print( "|" + sum);
			System.out.println();
		}
		System.out.println("\t     " + ACTUAL_DOCS + " " + ACTUAL_DOCS +  " " + ACTUAL_DOCS);
		
		System.out.println("TOPICS KEY:");
		System.out.println("1: Folder C1 Topic " + my_topics[0]);
		System.out.println("2: Folder C4 Topic " + my_topics[1]);
		System.out.println("3: Folder C7 Topic " + my_topics[2]);

	}

	//precision, recall, F1 
	public static void computations(int[][] conf_Matrix) {
		double[] my_precision = precision(conf_Matrix);
		double[] my_recall = recall(conf_Matrix);
		double[] my_fmeasure = f_measure(my_precision, my_recall);
		
		double psum=0, rsum=0, fm=0;

		System.out.println("\nPRECISION");
		for (int i=0; i<num_clusters; i++ ) {
			System.out.print(my_topics[i] + "= " + my_precision[i] + "  ");
			psum += my_precision[i];
		}
		
		System.out.println("\n\nRECALL");
		for (int i=0; i<num_clusters; i++ ) {
			System.out.print(my_topics[i]  + "= " + my_recall[i] + "  ");
			rsum += my_recall[i];
		}
		System.out.println("\n\nF MEASURE");
		for (int i=0; i<num_clusters; i++ ) {
			System.out.print(my_topics[i]  + "= " + my_fmeasure[i] + "  ");
		}
		
		fm = 2 * ((double)(psum*rsum) / (double)(psum+rsum));
		
		System.out.println("\n\nTOTAL PRECISION = " + psum/num_clusters + 
				"\nTOTAL RECALL = " + rsum/num_clusters + 
				"\nTOTAL F-MEASURE = " + fm/num_clusters);
	}
	
	public static double[] precision(int[][] conf_Matrix) {
		double[] prec = new double[num_clusters];
		
		for(int i=0; i<num_clusters;i++) {
			int sum = 0;
			int true_val = conf_Matrix[i][i];
			
			for(int j=0; j<num_clusters;j++) {
				sum += conf_Matrix[i][j];
			}
			prec[i] = (double)true_val/(double)sum;
		}
		return prec;
	}
	
	public static double[] recall(int[][] conf_Matrix) {
		double[] rec = new double[num_clusters];
		int true_val = 0;
		
		for(int i=0; i<num_clusters;i++) {
			true_val = conf_Matrix[i][i];
			rec[i] = (double)true_val/(double)ACTUAL_DOCS;
		}
		return rec;
	}
	
	public static double[] f_measure(double[] precision, double[] recall) {
		double[] fprec = new double[num_clusters];
		for (int i=0; i<num_clusters; i++) {
			fprec[i] = 2 * ((double)(precision[i] * recall[i]) / (double)(precision[i] + recall[i]));
		}
		return fprec;
	}
	
	public static int find_max(double[] row) {
        double max = 0;
        int max_index = -1;
          
		for (int i=0; i<row.length; i++) {
        	if (row[i] >= max) {
        		max_index = i;
        		max = row[i];
			}
		}
		return max_index;
	}

	
	public static int topics(double[][] tfidfmatrix, int[] group) {  //group is row of defined group
		int num_words = tfidfmatrix[0].length;
		int keyword_index = -1;
		
		double[][] tfidf_group = new double[group.length][num_words];

		//collect all tfidf doc rows together
		for (int i=0; i<group.length; i++) {  //num_docs = 24
			if (group[i] > -1) {
				tfidf_group[i] = tfidfmatrix[i];
			}
		}
		
		//add up all rows to see the top
		double[] tfidf_sums = new double[num_words];
		Arrays.fill(tfidf_sums, 0);
		
		for (int i=0; i<group.length; i++) { 
			for (int j=0; j<num_words; j++) {
				tfidf_sums[j] += tfidf_group[i][j];
			}
		}

		//find the keyword index of the maximum sum tfidf for cluster label
		keyword_index = find_max(tfidf_sums);	
		return keyword_index;
	}
	
	public static void print_all(int[][] conf_Matrix) {
		try {
	        PrintStream o = new PrintStream(new File("computations.txt")); 
	        PrintStream console = System.out; 
	        System.setOut(o); 
	        //////////////////////////
			//my printouts
	        print_cmatrix(conf_Matrix);
			computations(conf_Matrix);
	        //////////////////////////
	        System.setOut(console); 
	        print_cmatrix(conf_Matrix);
			computations(conf_Matrix);
			
		} catch (FileNotFoundException e) {
		    System.err.println(e.getMessage());
		    System.exit(-1);
		}
	}
	
	//row is prediction and col are actual
	public static void confusion_matrix(double[][] tfidfmatrix, String[] topWords) {

		int[][] assigned_docs = Similarity.predicted_clusters.clone();
		if (assigned_docs != null) {
			num_docs = assigned_docs[0].length;
			num_clusters = assigned_docs.length;
		}
	
		int[][] actual_clusters = init_actual();
		int[][] pred_clusters = init_predicted(assigned_docs);
		
		defined_clusters = new int[num_clusters][num_docs];
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		for (int i=0; i<num_clusters; i++) {
			int cnum = assign_clusters(actual_clusters, pred_clusters[i]);						
			
			//makes sure all clusters are represented in case 2 random clusters 
			//are part of same cluster and have same cluster values
			if (!list.contains(cnum)) {
				defined_clusters[cnum] = pred_clusters[i].clone();									
				list.add(new Integer(cnum));
			} else {
				for (int j=0; j<num_clusters; j++) {
					if (!list.contains(j)) {
						cnum = j;
						defined_clusters[cnum] = pred_clusters[j].clone();	
						list.add(new Integer(cnum));
					}
				}
			}
		}	
			
		//generate topics
		int[] mytopicindex = new int[num_clusters];
		my_topics = new String[num_clusters];
		
		for (int i=0; i<num_clusters; i++) {
			mytopicindex[i] = topics(tfidfmatrix, defined_clusters[i]);
			String topic = topWords[mytopicindex[i]];
			my_topics[i] = topic;
		}
		
		// generate the confusion matrix
		int[][] confMatrix = new int[num_clusters][num_clusters];
		for (int i=0; i<num_clusters; i++) {
			for (int j=0; j<num_clusters; j++) {
				confMatrix[i][j] = gen_cmatrix(actual_clusters[j], defined_clusters[i]);
			}
		}
		
		//uncomment to print out confusion matrix and performance measures
		//print_all(confMatrix);
	}
}

//This can go in line 280 if you want to see the clusters and/or tfidf matrix values
/*
System.out.println("\nMy Actual groups:");
for (int i=0; i<num_clusters; i++) {
	for (int j=0; j<ACTUAL_DOCS; j++) {
		System.out.print(actual_clusters[i][j] + " ");
	}
	System.out.println();
}

System.out.println("\nMy predicted groups:");
for (int i=0; i<num_clusters; i++) {
	for (int j=0; j<num_docs; j++) {
		System.out.print(pred_clusters[i][j] + " ");
	}
	System.out.println();
}

System.out.println("My defined groups:");
for (int i=0; i<num_clusters; i++) {
	for (int j=0; j<num_docs; j++) {
		System.out.print(defined_clusters[i][j] + " ");
	}
	System.out.println();
}
*/
/*
System.out.println("MY TFIDF MATRIX!!!");
for (int i=0; i<tfidfmatrix.length; i++) {
	for (int j=0; j<tfidfmatrix[0].length; j++) {
		System.out.print(tfidfmatrix[i][j] + " ");
	}
	System.out.println();
}
*/	
