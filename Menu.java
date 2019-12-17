public class Menu {
	
	public String giveInitialGreeting() {
		 final String s = "Welcome to the Voicemail System!\nEnter 1 to leave a message as a Caller\nEnter 2 to access your Mailbox as an Owner\nEnter 3 to access Administrator mode"
				+ "\nWhen entering a choice, please enter \"#\" when finished to send your choice!";
		 return s;
	}
	
	public String goodbye() {
		final String s = "Thank you for using the Voice Mail System! Goodbye!";
		return s;
	}
	
	public String askForExtension() {
		final String s = "Please enter the extension of the mailbox you wish to access";
		return s;
	}
	
	public String askForPassword() {
		final String s =  "Please enter your password";
		return s;
	}
	
	public String promtInvalidExtension() {
		final String s =  "The given extension does not exist";
		return s;
	}
	
	public String promptIncorrectPassword() {
		final String s =  "The given password is invalid";
		return s;
	}
	
	public String askToSaveOrDelete() {
		final String s = "Would you like to save or delete this message";
		return s;
	}
	
	public String askToLeaveMessage() {
		final String s = "Please leave a message: ";
		return s;
	}
	
	public String askToViewSavedMessages() {
		final String s = "Would you like to view your saved messages?";
		return s;
	}
}