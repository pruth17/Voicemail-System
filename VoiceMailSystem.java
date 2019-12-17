import java.util.*;

/**
 * The VoiceMailSystem class runs like a state machine; it keeps an internal
 * state and depending on the input the process/function calls change.
 * 
 * @author Pruthvi Punwar
 *
 */
public class VoiceMailSystem {
	private ArrayList<Mailbox> mailboxes;
	private int state;
	private int userType;
	private int mailboxNum;
	private int greetingChoice = -1;
	private int savedMessagePos = -1;
	private String speakerText;
	private Message message;
	private static final String[] userTypes = { "", "Caller", "Owner", "Admin" };
	private final VoiceMailHandler handler;
	private final Admin admin = new Admin(6789);
	private final Menu menu = new Menu();

	private final static int RESET_SESSION = 99;
	private final static int SET_USER_TYPE = -1;
	private final static int NEW_USER = -1;
	private final static int CALLER = 1;
	private final static int OWNER = 2;
	private final static int ADMIN = 3;

	// Caller states
	private final static int RECIEVE_EXTENSION = 0;
	private final static int LEAVE_MESSAGE = 1;

	// Owner states
	private final static int CHECK_PASSWORD = 1;
	private final static int OWNER_CHOOSE_OPERATION = 2;
	private final static int SHOW_NEW_MESSAGES = 3;
	private final static int LOOP_NEW_MESSAGES = 4;
	private final static int SHOW_SAVED_MESSAGES = 5;
	private final static int LOOP_SAVED_MESSAGES = 6;
	private final static int LIST_ALL_MESSAGES = 12;
	private final static int DELETE_MESSAGE = 13;
	private final static int DELETE_ALL_MESSAGES = 14;

	// Admin states
	private final static int CHECK_ADMIN_PASSWORD = 0;
	private final static int ADMIN_CHOOSE_OPERATION = 1;
	private final static int CREATE_NEW_MAILBOX_PASSWORD = 2;
	private final static int EXECUTE_CREATE_NEW_MAILBOX = 3;
	private final static int CHANGE_MAILBOX_PASSWORD = 4;
	private final static int ASK_FOR_NEW_PASSWORD = 5;
	private final static int EXECUTE_CHANGE_MAILBOX_PASSWORD = 6;

	public VoiceMailSystem() {
		// State -1 and userType -1 are used to denote that the session has just
		// started.
		state = -1;
		userType = -1;
		mailboxes = new ArrayList<Mailbox>();
		handler = new VoiceMailHandler();

		mailboxes.add(0, new Mailbox(0, 123));
		handler.leaveMessage(mailboxes.get(0), new Message("Yo!"));
		handler.leaveMessage(mailboxes.get(0), new Message("Hey!"));

		mailboxes.add(1, new Mailbox(1, 123));
		handler.leaveMessage(mailboxes.get(1), new Message("Answer my call."));
		handler.leaveMessage(mailboxes.get(1), new Message("Hey, how is it going."));

		mailboxes.add(2, new Mailbox(2, 123));
		handler.leaveMessage(mailboxes.get(2), new Message("sup?"));
		handler.leaveMessage(mailboxes.get(2), new Message("Call me"));
	}

	/**
	 * This function is the main driving force of the System. The steps are split
	 * into Caller, Owner, Admin interactions.
	 * 
	 * @param userInput : The String the user has given to the System. Converted to
	 *                  an int when possible.
	 */
	public void nextState(String userInput) {
		int input = userInputAsInt(userInput);

		if (state == RESET_SESSION) {
			if (input == 1) {
				speakerText = menu.giveInitialGreeting();
				this.resetSession();
			}
		} else if (state == SET_USER_TYPE && userType == NEW_USER) {
			this.setUserType(input);
			if (userType == 1 || userType == 2)
				speakerText = "You have chosen to be a: " + userTypes[input] + " Please enter an extension: ";
			else if (userType == 3)
				speakerText = "Administrator mode activated, please enter the administrator password";
		} else if (userType == CALLER) { // Caller
			callerMenu(userInput);
		} else if (userType == OWNER) { // Owner
			ownerMenu(userInput);
		} else if (userType == ADMIN) { // Admin
			adminMenu(userInput);
		}
	}

	/**
	 * This is a helper function, which processes the caller's function.
	 * 
	 * @param userInput
	 */
	private void callerMenu(String userInput) {
		int input = userInputAsInt(userInput);
		if (state == RECIEVE_EXTENSION) {
			if (input >= mailboxes.size()) {
				speakerText = "This extension does not exist.\nPlease enter 1 to go back to the Main Menu!";
				state = RESET_SESSION;
			} else {
				mailboxNum = input;
				speakerText = "Extension recieved, please enter the message afer their greeting.\nPlease press \"#\" when finished: \n"
						+ mailboxes.get(mailboxNum).playGreeting();
				state = LEAVE_MESSAGE;
			}
		} else if (state == LEAVE_MESSAGE) {
			// If the mailbox has reached max capacity (40 new + saved messages), you cannot
			// leave a message.
			if (mailboxes.get(mailboxNum).size() == 40) {
				speakerText = "This Owner's mailbox is full, they cannot recieve any more messages.\nPlease enter 1 to go back to the Main Menu!";
				state = RESET_SESSION;
			}
			message = new Message(userInput);
			handler.leaveMessage(mailboxes.get(mailboxNum), message);
			speakerText = "Message left, please enter 1 to go back to the main menu!";
			state = RESET_SESSION;
		}
	}

	/**
	 * This is a helper function, which processes the owner's function.
	 * 
	 * @param userInput
	 */
	private void ownerMenu(String userInput) {
		int input = userInputAsInt(userInput);
		if (state == RECIEVE_EXTENSION || state == CHECK_PASSWORD) {
			this.ownerLogin(input);
		} else if (state == OWNER_CHOOSE_OPERATION) {
			this.ownerOperationChoice(input);
		} else if (state == SHOW_NEW_MESSAGES) {
			this.initialNewMessage(input);
		} else if (state == LOOP_NEW_MESSAGES) {
			this.playNewMessages(input);
		} else if (state == SHOW_SAVED_MESSAGES || state == LOOP_SAVED_MESSAGES) {
			this.playSavedMessages(input);
		} else if (state >= 7 && state <= 11) {
			this.manageGreetings(input, userInput);
		} else if (state >= LIST_ALL_MESSAGES) {
			this.listMessages(input);
		}
	}

	/**
	 * This helper defines the admin's menu and its functions.
	 * 
	 * @param userInput
	 */
	private void adminMenu(String userInput) {
		int input = userInputAsInt(userInput);
		if (state == CHECK_ADMIN_PASSWORD) {
			if (input == admin.getPassword()) {
				speakerText = "Administrator access granted.\nEnter 1 to create a new Mailbox\nEnter 2 to change a Mailbox password";
				state = ADMIN_CHOOSE_OPERATION;
				this.nextState("");
			} else {
				speakerText = "Incorrect Administrator password!\nPress 1 to return to Main Menu!";
			}
		} else if (state == ADMIN_CHOOSE_OPERATION) {
			speakerText = "Administrator access granted.\nEnter 1 to create a new Mailbox\nEnter 2 to change a Mailbox password";
			if (input == 1) {
				state = CREATE_NEW_MAILBOX_PASSWORD;
				this.nextState("");
			} else if (input == 2) {
				state = CHANGE_MAILBOX_PASSWORD;
				this.nextState("");
			}
		} else if (state >= CREATE_NEW_MAILBOX_PASSWORD && state <= EXECUTE_CHANGE_MAILBOX_PASSWORD) {
			this.executeAdminOperations(input);
		}
	}

	/**
	 * This helper function help for the owner to login.
	 * 
	 * @param input
	 */
	private void ownerLogin(int input) {
		if (state == RECIEVE_EXTENSION) {
			if (input >= mailboxes.size()) {
				speakerText = "This extension does not exist.\nPlease enter 1 to go back to the Main Menu!";
				state = RESET_SESSION;
			} else {
				mailboxNum = input;
				speakerText = "Extension recieved, please enter your password: ";
				state = CHECK_PASSWORD;
			}
		} else if (state == CHECK_PASSWORD) {
			int pin = input;
			if (pin == mailboxes.get(mailboxNum).getPassword()) {
				speakerText = "Welcome to your mailbox!\nEnter 1 to play your new messages\nEnter 2 to play your saved messages\nEnter 3 to view all messages\nEnter 4 to manage all your greetings";
				;
				state = OWNER_CHOOSE_OPERATION;
			} else {
				speakerText = "Incorrect password, press 1 to return to the Main Menu";
				state = RESET_SESSION;
			}
		}
	}

	/**
	 * This helper function changes the function based on the actions performed.
	 * 
	 * @param input
	 */
	private void ownerOperationChoice(int input) {
		if (input == 1) {
			state = SHOW_NEW_MESSAGES;
			this.nextState("");
		} else if (input == 2) {
			state = SHOW_SAVED_MESSAGES;
			this.nextState("");
		} else if (input == 3) {
			state = LIST_ALL_MESSAGES;
			this.nextState("");
		} else if (input == 4) {
			state = 7;
			this.nextState("");
		}
	}

	/**
	 * This helper function helps what to do when the the owner wants to see the message.
	 * 
	 * @param input
	 */
	private void initialNewMessage(int input) {
		if (mailboxes.get(mailboxNum).noNewMessages()) {
			speakerText = "No new messages! Would you like to view your saved messages?\nPress 1 to do so.";
			if (input == 1) {
				state = SHOW_SAVED_MESSAGES;
				savedMessagePos = mailboxes.get(mailboxNum).savedMessagesSize() - 1;
				this.nextState("");
			} else {
				state = RESET_SESSION;
				this.nextState("");
			}
		} else {
			speakerText = "\nPlaying first message:\n" + mailboxes.get(mailboxNum).get()
					+ "\nPress 1 to save this message or 2 to delete:";
			state = LOOP_NEW_MESSAGES;
		}
	}

	/**
	 * This helper function handles the viewing of the new messages in the queue.
	 * @param input : User input
	 */
	private void playNewMessages(int input) {
		if (mailboxes.get(mailboxNum).noNewMessages()) {
			speakerText = "No new messages! Would you like to view your saved messages?\nPress 1 to do so.";
			if (input == 1) {
				state = SHOW_SAVED_MESSAGES;
				savedMessagePos = mailboxes.get(mailboxNum).savedMessagesSize() - 1;
				this.nextState("");
			} else {
				state = RESET_SESSION;
				this.nextState("");
			}
		} else {
			if (input == 1) {
				mailboxes.get(mailboxNum).save();
				speakerText = "Message saved!";
			} else if (input == 2) {
				mailboxes.get(mailboxNum).delete();
				speakerText = "Message deleted!";
			}

			if (mailboxes.get(mailboxNum).noNewMessages())
				speakerText += "\nNo new messages! Would you like to view your saved messages?\nPress 1 to do so.";
			else
				speakerText += "\nPlaying next message:\n" + mailboxes.get(mailboxNum).get()
						+ "\nPress 1 to save this message or 2 to delete:";
		}
	}

	/**
	 * This helper function sets the state for the owner.
	 * 
	 * @param input
	 */
	private void playSavedMessages(int input) {
		if (state == SHOW_SAVED_MESSAGES) {
			if (mailboxes.get(mailboxNum).noSavedMessages()) {
				speakerText = "\nNo saved messages! Goodbye!";
			} else {
				speakerText = "\nPlaying first saved message:\n" + mailboxes.get(mailboxNum).getSavedMessage()
						+ "\nPress 1 to save this message or 2 to delete:";
				state = LOOP_SAVED_MESSAGES;
			}
		} else if (state == LOOP_SAVED_MESSAGES) {
			if (mailboxes.get(mailboxNum).noSavedMessages() || savedMessagePos == -1) {
				speakerText = "No saved messages! \nPress 1 to go to the Main Menu!";
				if (input == 1) {
					state = RESET_SESSION;
					this.nextState("");
				} else {
					speakerText = "Goodbye!";
				}
			} else {
				if (input == 1) {
					speakerText = "Message kept!";
				} else if (input == 2) {
					mailboxes.get(mailboxNum).deleteSavedMessage(savedMessagePos);
					speakerText = "Message deleted!";
				}
				savedMessagePos--;

				if (mailboxes.get(mailboxNum).noSavedMessages() || savedMessagePos == -1) {
					speakerText += "\nNo saved messages! \nPress 1 to go to the Main Menu!";
					state = RESET_SESSION;
					this.nextState("");
				} else
					speakerText += "\nPlaying next message:\n"
							+ mailboxes.get(mailboxNum).getSavedMessage(savedMessagePos)
							+ "\nPress 1 to save this message or 2 to delete:";
			}
		}
	}

	/**
	 * This helper function handles the sequence about the owner
	 * 
	 * @param input
	 * @param userInput
	 */
	private void manageGreetings(int input, String userInput) {
		if (state == 7) {
			speakerText = "Enter 1 to set a greeting\nEnter 2 to delete a greeting\nEnter 3 to insert or change a greeting";
			if (input == 1) {
				speakerText = "Please select a greeting:\n1: " + mailboxes.get(mailboxNum).getGreetings(1) + "\n2: "
						+ mailboxes.get(mailboxNum).getGreetings(2) + "\n3: "
						+ mailboxes.get(mailboxNum).getGreetings(3);
				state = 8;
			} else if (input == 2) {
				speakerText = "Delete a greeting:\n1: " + mailboxes.get(mailboxNum).getGreetings(1) + "\n2: "
						+ mailboxes.get(mailboxNum).getGreetings(2) + "\n3: "
						+ mailboxes.get(mailboxNum).getGreetings(3);
				state = 9;
			} else if (input == 3) {
				speakerText = "Change one of the following greetings:\n1: " + mailboxes.get(mailboxNum).getGreetings(1)
						+ "\n2: " + mailboxes.get(mailboxNum).getGreetings(2) + "\n3: "
						+ mailboxes.get(mailboxNum).getGreetings(3);
				state = 10;
			}
		} else if (state == 8) {
			mailboxes.get(mailboxNum).setGreeting(input);
			speakerText = "Greeting set to: " + mailboxes.get(mailboxNum).playGreeting()
					+ "\nEnter 1 to return to the Main Menu!";
			state = RESET_SESSION;
		} else if (state == 9) {
			mailboxes.get(mailboxNum).insertGreeting(input, null);
			speakerText = "Greeting # " + input + "deleted!\nEnter 1 to return to the Main Menu!";
			state = RESET_SESSION;
		} else if (state == 10) {
			greetingChoice = input;
			speakerText = "Please leave a greeting: ";
			state = 11;
		} else if (state == 11) {
			mailboxes.get(mailboxNum).insertGreeting(greetingChoice, userInput);
			speakerText = "New greeting inserted at choice #" + greetingChoice + ":\n"
					+ mailboxes.get(mailboxNum).getGreetings(greetingChoice)
					+ "\nEnter 1 to go to the Main Menu, or 2 to hang up!";
			state = RESET_SESSION;
		}
	}

	/**
	 * This helper function lists all the messages in the mailbox.
	 * 
	 * @param input
	 */
	private void listMessages(int input) {
		if (state == LIST_ALL_MESSAGES) {
			if (mailboxes.get(mailboxNum).size() == 0) {
				speakerText = "No more messages. Press 1 to return to the Main Menu!";
				state = RESET_SESSION;
				this.nextState("");
			} else {
				this.giveAllMessages(input);
			}
		} else if (state == DELETE_MESSAGE) {
			if (input >= mailboxes.get(mailboxNum).newMessagesSize())
				input -= mailboxes.get(mailboxNum).newMessagesSize();
			else
				input--;

			mailboxes.get(mailboxNum).delete(input);
			state = LIST_ALL_MESSAGES;
			this.nextState("");
		} else if (state == DELETE_ALL_MESSAGES) {
			mailboxes.get(mailboxNum).deleteAll();
			state = LIST_ALL_MESSAGES;
			this.nextState("");
		}
	}

	/**
	 * This helper function handles of showing the message.
	 * 
	 * @param input
	 */
	private void giveAllMessages(int input) {
		speakerText = "Here is a list of your New and Saved messages. \nEnter a number to delete that message, or enter 99 to delete all messages.\nNew Messages:";
		for (int i = 0; i < mailboxes.get(mailboxNum).newMessagesSize(); i++) {
			speakerText += "\n [" + (i + 1) + "] :" + mailboxes.get(mailboxNum).get(i);
		}
		speakerText += "\nSaved Messages: ";
		for (int i = 0; i < mailboxes.get(mailboxNum).savedMessagesSize(); i++) {
			speakerText += "\n [" + (i + mailboxes.get(mailboxNum).newMessagesSize() + 1) + "] :"
					+ mailboxes.get(mailboxNum).getSavedMessage(i);
		}

		if (input >= 1 && input != 99) {
			state = DELETE_MESSAGE;
			this.nextState(Integer.toString(input));
		} else if (input == 99) {
			state = DELETE_ALL_MESSAGES;
			mailboxes.get(mailboxNum).deleteAll();
			this.nextState("");
		}
	}

	/**
	 * This helper function helps with the admin functions.
	 * 
	 * @param input
	 */
	private void executeAdminOperations(int input) {
		if (state == CREATE_NEW_MAILBOX_PASSWORD) {
			speakerText = "Please enter a password for the new mailbox: ";
			state = EXECUTE_CREATE_NEW_MAILBOX;
		} else if (state == EXECUTE_CREATE_NEW_MAILBOX) {
			admin.createAMailbox(mailboxes, input);
			speakerText = "New mailbox created. Extension is: " + (mailboxes.size() - 1) + " with a password of: "
					+ input + "\nPress 1 to return to the Main Menu.";
			state = RESET_SESSION;
		} else if (state == CHANGE_MAILBOX_PASSWORD) {
			speakerText = "Please enter a Mailbox extension to change its password: ";
			state = ASK_FOR_NEW_PASSWORD;
		} else if (state == ASK_FOR_NEW_PASSWORD) {
			admin.saveExtension(input);
			speakerText = "Please enter a new password: ";
			state = EXECUTE_CHANGE_MAILBOX_PASSWORD;
		} else if (state == EXECUTE_CHANGE_MAILBOX_PASSWORD) {
			admin.changePassword(mailboxes, input);
			speakerText = "Password changed! Press 1 to return to the Main Menu";
			state = RESET_SESSION;
		}
	}

	/**
	 * Uses a regular expression to convert the user's input as a digit, if
	 * possible.
	 * 
	 * @param userInput
	 * @return -1 if the userInput is a string or the integer representation of the
	 *         string.
	 */
	private int userInputAsInt(String userInput) {
		if (userInput.matches("\\d+")) {
			return Integer.parseInt(userInput);
		} else
			return -1;
	}

	/**
	 * Resets all session variables, including the mailboxNum, message, and
	 * userTypes.
	 */
	private void resetSession() {
		state = -1;
		userType = -1;
		message = null;
		greetingChoice = -1;
		mailboxNum = -1;
	}

	/**
	 * Sets the user type when initially prompted. Sets the state to 0 as well to
	 * jump-start the system to the next stage.
	 * 
	 * @param type
	 */
	private void setUserType(int type) {
		this.userType = type;
		state = 0;
	}

	/**
	 * Ends the current Voice Mail System session
	 */
	public void hangup() {
		speakerText = menu.goodbye();
		state = -99;
	}

	/**
	 * Sends the text to the speaker
	 * 
	 * @return the message to be sent to the user.
	 */
	public String sendToSpeaker() {
		return speakerText;
	}

	/**
	 * Saves the mailbox number being used by the user for repeated access to that
	 * mailbox number.
	 * 
	 * @param num
	 */
	public void saveMailboxNum(int num) {
		this.mailboxNum = num;
	}

	/**
	 * Saves a copy of the user's message locally to minimize the amount of states
	 * needed.
	 * 
	 * @param message
	 */
	public void saveMessage(String message) {
		this.message = new Message(message);
	}

}