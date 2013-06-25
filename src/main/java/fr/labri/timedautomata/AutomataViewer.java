package fr.labri.timedautomata;

import javax.swing.JFrame;

public class AutomataViewer {
	public static JFrame viewAsFrame(TimedAutomata<?> automata) {
		JFrame frame = new JFrame("Simple Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(automata.asPanel()); 
		frame.pack();
		frame.setVisible(true); 
		return frame;
	}
}
