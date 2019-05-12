READ ME

*** Bds.java is my main class ***

To run program:

1. unzip file 

**** My program requires Stanford NLP that contains Sentence and Lemmatization ****
First try running with the dependencies you already have, if that doesnt work...
2. download stanford corenlp from here https://stanfordnlp.github.io/CoreNLP/download.html
	choose English	download version 3.9.2
3. the specific referenced libraries used in my program are 
	xom.jar
	protobuff.jar	
	stanfor_corenlp-3.9.2-models.jar
 	stanfor_corenlp-3.9.2.jar
These jars must all be part of the class path, a screenshot of my eclipse environment is included
4. If using eclipse, it can run as is, otherwise the jars should be exported as a runnable jar file saved as HW4/BDSHW/myjars.jar
5. From the command line make sure you are in HW4/BDSHW/ and run: 
javac -cp myjars.jar Bds.java
6. Next run: java -cp myjars.jar Bds
		
This will show output from my program, however the k cannot be updated by editing the program in sublime, it would have to be updated through eclipse 


Settings/Good to know:

1. Default KNN k is 3. To change KNN k value, manually change in Bds.java line 15
line15	public static final int UNK_K = 8;    ///update this for KNN

2. All paths to files can be found in Bds.java, none should need to be updated
line10	public static String datafilename = "data.txt";
line11	public static String stopwordsfilename = "stopwords.txt"; 
line12	public static String unknownsfilename = "datahw/";

3.  To change method of similarity comparison: Bds.java line 37
-True for cosine, False for euclidean
-default is cosine
line37        Similarity.kmeans(myMatrix, K, true);
	
4.  K++ means is implemented in Similarity as default. To change to normal kmeans, comment out line 205 in Similarity.java and uncomment line 206
line205		means = init_kpp_clusters(myMatrix);	//k++ means
line206		//means = init_clusters(myMatrix);	//k-means



Notes:
1. Word output is ordered by frequency, with most frequent at the top
	To see the wordcount too, update commented out string in Word.java line 64
2. NER was too slow to correctly implement
	n-grams takes its place in pre-processing
	NER code is still written but commented out because it takes too long
3. The confusion matrix for output clusters and precision/recall/F measure scores are in computations.txt file
4. KNN Confusion matrix and precision/recall/F measure scores are in computations.txt file


