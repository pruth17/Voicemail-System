import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This Telephone class has the simply duty 
 * of taking in user input and passing it into The VoiceMailSystem.
 * 
 * @author Pruthvi Punwar
 */
public class Telephone {
	
	public Telephone(VoiceMailSystem system) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(700, 500));
        frame.setTitle("Voicemail System");

        frame.setLayout(new BorderLayout());
        
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(new JLabel("Speaker: "));
        JTextArea speaker = new JTextArea(10, 10);
        JTextArea microphone = new JTextArea(10, 10);
        speaker.setText("Welcome to the Voicemail System!\nEnter 1 to leave a message as a Caller\nEnter 2 to access your Mailbox as an Owner\nEnter 3 to access Administrator mode"
        				+ "\nWhen entering a choice, please enter \"#\" when finished to send your choice!");
        northPanel.add(speaker);
        frame.add(northPanel, BorderLayout.NORTH);
        
        // Here we create buttons from 1 - 9, then the * and 0 buttons. Each on will simply add to the user input.
        JPanel centerPanel = new JPanel(new GridLayout(4, 3));
        for (int i = 1; i < 10; i++) {
        	final String key = i + "";
        	JButton button = new JButton(key);
        	centerPanel.add(button);
        	// The action performed here simply adds to user input.
        	button.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent event) {
            		microphone.setText(microphone.getText() + key);
            	}
            });
        }
        
        JButton star = new JButton("*");
    	centerPanel.add(star);
    	// The action performed here simply adds to user input.
    	star.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		microphone.setText(microphone.getText() + "*");
        	}
        });
    	
    	JButton zero = new JButton("0");
     	centerPanel.add(zero);
     	// The action performed here simply adds to user input.
     	zero.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent event) {
         		microphone.setText(microphone.getText() + "0");
         	}
         });
        
        // Here # will be used to enter the user input to the VoiceMailSystem class, then retrieve the text that will
        // be played by the "speaker".
        JButton pound = new JButton("#");
        centerPanel.add(pound);
        pound.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		system.nextState(microphone.getText());
        		microphone.setText("");
        		speaker.setText(system.sendToSpeaker());
        	}
        });
        frame.add(centerPanel, BorderLayout.CENTER);

        // South Panel layout
        JPanel southPanel = new JPanel(new BorderLayout());
        JLabel microphoneLabel = new JLabel("Microphone (Enter option or message here):");
        southPanel.add(microphoneLabel, BorderLayout.NORTH);
        southPanel.add(microphone, BorderLayout.CENTER);
        JButton hangup = new JButton("Hangup");
        southPanel.add(hangup, BorderLayout.SOUTH);
        hangup.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent event) {
         		system.hangup();
        		microphone.setText("");
        		speaker.setText(system.sendToSpeaker());
         	}
         });
        frame.add(southPanel, BorderLayout.SOUTH);
        
        frame.setVisible(true);
	}
}