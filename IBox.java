/**
 * An interface which defines commonly-used methods such as add, delete, get, size, and isFull. 
 * 
 * @author Pruthvi Punwar
 */
public interface IBox{
	
	void add(Message message);
	
	void delete();
	
	String get();
	
	boolean isFull();
	
	int size();
}