import java.util.Comparator;

/**
 * A simple Message class which holds a String of content.
 * 
 * @author Pruthvi Punwar
 */
public class Message {
	private final String content;

	/**
	 * 
	 * @param content
	 */
	public Message(String content) {
		this.content = content;
	}
	/**
	 * 
	 * @return
	 */
	public String getContent() {
		return this.content;
	}
	/**
	 * 
	 * @return
	 */
	public int getSize() {
		return content.length();
	}

	/**
	 * Comparator for sorting by message size.
	 * 
	 * @return
	 */

	public static Comparator<Message> compareBattery = new Comparator<Message>() {
		public int compare(Message m1, Message m2) {
			return Integer.compare(m1.getSize(), m2.getSize());
		}
	};

}