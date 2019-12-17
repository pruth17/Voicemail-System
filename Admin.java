import java.util.*;

/**
 * The Admin class contains two methods which are only executable by an Admin object; createAMailbox and changePassword.
 * 
 * @author Pruthvi Punwar
 */
public class Admin {
	private final int password;
	private int savedExt;
	
	/**
	 * Constructor of the class
	 * @param password assigns to this password
	 */
	public Admin(int password) {
		this.password = password;
	}
	
	
	/**
	 * Changes the password for an Owner's mailbox.
	 * @param mailboxes
	 * @param newPassword
	 */
	public void changePassword(ArrayList<Mailbox> mailboxes, int newPassword) {
		mailboxes.get(savedExt).setPassword(newPassword);
	}
	
	/**
	 * Saves the extension that will have its password changed.
	 * @param extension
	 * @precondition extension >= 0 && extension < mailboxes.size();
	 */
	public void saveExtension(int ext) {
		this.savedExt = ext;
	}
	
	/**
	 * returns password
	 * @return password
	 */
	
	public int getPassword() {
		return this.password;
	}
	/**
	 * Creates a new Mailbox for an Owner. 
	 * @param mailboxes
	 * @param password
	 */
	public void createAMailbox(ArrayList<Mailbox> mailboxes, int password) {
		int mailboxNum = mailboxes.size();
		mailboxes.add(mailboxNum, new Mailbox(mailboxNum, password));
	}
	
}