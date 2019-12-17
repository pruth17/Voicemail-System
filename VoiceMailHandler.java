/**
 * An agent class which simply retrieves and leaves messages for the user. 
 * 
 * @author Pruthvi Punwar
 */
public class VoiceMailHandler {
	
	/**
	 * This function adds the message to the mailbox
	 * 
	 * @param mailbox where the message is supposed to be added
	 * @param message the message that is suppposed to be added
	 */
	public void leaveMessage(Mailbox mailbox, Message message) {
		mailbox.add(message);
	}
	/**
	 * This function retrieves the message from the mailbox
	 * 
	 * @param userType the type of user
	 * @param mailbox the mailboxes
	 * @return the message stored in the mailbox
	 */
	public String retrieveMessage(int userType, Mailbox mailbox) {
		return mailbox.get();
	}
}