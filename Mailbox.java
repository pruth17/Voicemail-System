import java.util.*;

/**
 * The Mailbox class has the responsibility of holding the owner's messages and
 * greetings. The Mailbox has 2 types of message queues, new and saved. The
 * methods defined below are implemented from the IBox class interface, which
 * defines basic methods such as add, delete, get, and size.
 * 
 * @author Pruthvi Punwar
 */
public class Mailbox implements IBox {

	private final int extension;
	private int password;
	private ArrayList<Message> newMessages;
	private ArrayList<Message> savedMessages;
	private String[] greetings;
	private String greeting;

	/**
	 * Constructor of the class
	 * 
	 * @param extension extension of the mailbox
	 * @param password  password of the mailbox
	 */
	public Mailbox(int extension, int password) {
		this.extension = extension;
		this.password = password;
		this.newMessages = new ArrayList<Message>();
		this.savedMessages = new ArrayList<Message>();
		greeting = "Hello! This is Mailbox #" + extension + " , please leave a message!";
		greetings = new String[3];
		greetings[0] = greeting;
	}

	/**
	 * Deletes a saved message at a given position.
	 * 
	 * @param position
	 * @precondition position >= 0 && position < savedMessagesSize();
	 */
	public void deleteSavedMessage(int position) {
		assert position >= 0 && position < savedMessagesSize() : "Invalid position given";
		savedMessages.remove(position);
	}

	/**
	 * Deletes all messages in the Mailbox system.
	 */
	public void deleteAll() {
		newMessages.clear();
		savedMessages.clear();
	}

	/**
	 * Returns the newest Message's content from newMessages.
	 * 
	 * @precondition !noNewMessages()
	 */
	@Override
	public String get() {
		assert !noNewMessages() : "There are no messages to retrieve";
		return newMessages.get(newMessages.size() - 1).getContent();
	}

	/**
	 * Returns the new message at a given position
	 * 
	 * @precondition position >= 0 && position < newMessagesSize()
	 */
	public String get(int position) {
		assert position >= 0 && position < newMessagesSize() : "Invalid positionition given";
		return newMessages.get(position).getContent();
	}

	/**
	 * Returns the newest saved message
	 * 
	 * @precondition !noSavedMessages()
	 */
	public String getSavedMessage() {
		assert !noSavedMessages() : "There are no saved messages to retrieve";
		return savedMessages.get(savedMessages.size() - 1).getContent();
	}

	/**
	 * Returns the saved message at a given position.
	 * 
	 * @param position
	 * @precondition position >= && position <= savedMessagesSize()
	 */
	public String getSavedMessage(int position) {
		assert position >= 0 && position < savedMessagesSize() : "Invalid positionition given";
		return savedMessages.get(position).getContent();
	}

	/**
	 * Returns true if the Mailbox has reached max capacity
	 */
	@Override
	public boolean isFull() {
		if (this.size() == 40) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if there are no new messages.
	 */
	public boolean noNewMessages() {
		return newMessages.size() == 0;
	}

	/**
	 * Returns true is there are no saved messages.
	 */
	public boolean noSavedMessages() {
		return savedMessages.size() == 0;
	}

	/**
	 * Returns the password to see if a given password matches when an Owner tries
	 * to log into their Mailbox.
	 */
	public int getPassword() {
		return password;
	}

	/**
	 * Plays the Owner's greeting. The greeting is set between a choice of three
	 * greetings.
	 */
	public String playGreeting() {
		return greeting;
	}

	/**
	 * Returns the Owner's greeting choices.
	 * 
	 * @param number
	 * @precondition number >= 0 && number < 3
	 */
	public String getGreetings(int numberber) {
		assert numberber >= 0 && numberber < 3 : "Greetings are a value between 0 and 2";
		return greetings[numberber - 1];
	}

	/**
	 * Sets the Owner's greeting to the given greeting number.
	 * 
	 * @param number
	 * @precondition number >= 0 && number < 3
	 */
	public void setGreeting(int number) {
		assert number >= 0 && number < 3 : "Greetings are a value between 0 and 2";
		greeting = greetings[number - 1];
	}

	/**
	 * Inserts a new greeting into the list of greetings. If the number given is an
	 * occupied greeting space, it is overwritten.
	 * 
	 * @param number
	 * @param greeting
	 * @precondition number >= 0 && number < 3
	 */
	public void insertGreeting(int number, String greeting) {
		assert number >= 0 && number < 3 : "Greetings are a value between 0 and 2";
		greetings[number - 1] = greeting;
	}

	/**
	 * Returns the size of newMessages.
	 */
	public int newMessagesSize() {
		return newMessages.size();
	}

	/**
	 * Returns the total size of the Mailbox's lists.
	 */
	@Override
	public int size() {
		return newMessages.size() + savedMessages.size();
	}

	/**
	 * Saves the given message from newMessages into savedMessages
	 */
	public void save() {
		Message message = newMessages.get(newMessages.size() - 1);
		newMessages.remove(newMessages.size() - 1);
		savedMessages.add(message);
	}

	/**
	 * Sets the password to a new password. Only accessed by an Admin class.
	 * 
	 * @param password
	 */
	public void setPassword(int password) {
		this.password = password;
	}

	/**
	 * Returns the size of savedMessages.
	 */
	public int savedMessagesSize() {
		return savedMessages.size();
	}

	/**
	 * The method adds a new message to the end of the newMessage list. The first
	 * index is the oldest message.
	 * 
	 * @precondition this.size() < 40
	 */
	@Override
	public void add(Message message) {
		assert this.size() < 40 : "Mailbox capacity is full";
		newMessages.add(message);
	}

	/**
	 * The method deletes the first message on the queue.
	 * 
	 * @precondition !noNewMessages();
	 */
	@Override
	public void delete() {
		assert !noNewMessages() : "No messages in the queue.";
		newMessages.remove(newMessages.size() - 1);
	}

	/**
	 * Deletes a new message at a given position.
	 * 
	 * @param position
	 * @precondition position >= 0 && position < newMessagesSize();
	 */
	public void delete(int position) {
		assert position >= 0 && position < newMessagesSize() : "Invalid position given";
		// Deletes the message at the given position by shifting all the messages ahead
		// of it into the position behind them.
		newMessages.remove(position);
	}

	/**
	 * Deletes the newest messages in the savedMessage list.
	 * 
	 * @precondition !noSavedMessages()
	 */
	public void deleteSavedMessage() {
		assert !noSavedMessages() : "There are no saved messages in the list";
		savedMessages.remove(savedMessages.size() - 1);
	}
}