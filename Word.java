import java.util.*;

public class Word {
	
    private String word;
    private int count;
    
	public Word(String word, int count) {
		this.word = word;
		this.count = count;
	}
	
	public String getWord() {
        return word;
	}
	
	public void setWord(String word) {
		this.word = word;
	}
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
    //Comparator for sorting the list by count
    public static Comparator<Word> WordCount = new Comparator<Word>() {
		public int compare(Word w1, Word w2) {
	
		   int count1 = w1.getCount();
		   int count2 = w2.getCount();
	
		   /*For ascending order*/
		   //return count1-count2;
	
		   /*For descending order*/
		   return count2-count1;
		}
	};
	
    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Word)) {
            return false;
        }
        Word words = (Word) o;
        return Objects.equals(word, words.word) &&
        		count == words.count;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(word, count);
    }

    @Override
    public String toString() {
    	return word;
        //return count + " " + word;
    }
}


