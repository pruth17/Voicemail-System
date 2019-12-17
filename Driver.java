/**
 * An initializer class that starts the VoiceMailSystem and Telephone classes. Once started, the Voice Mail System is "called" and users may
 * begin interacting with the system. 
 * 
 * @author Pruthvi Punwar
 */
public class Driver {

	public static void main(String[] args) {
		VoiceMailSystem system = new VoiceMailSystem();
		Telephone telephone = new Telephone(system);
	}
}

