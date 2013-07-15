package fr.labri.tima;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;

import org.jdom2.JDOMException;


public class TestViewer {

	public static void main(String[] args) throws JDOMException, IOException {
		
		SimpleNodeFactory<String> factory = new SimpleNodeFactory<String>();
		TimedAutomata<String> auto = new TimedAutomataFactory<String>(factory).loadXML(TestViewer.class.getResourceAsStream("/fr/labri/starnet/policies/commons/Random.xml"));
		
		Executor<String> exec = new Executor<>(new ITimedAutomata.ContextProvider<String>() {
			@Override
			public String getContext() {
				return "";
			}
		});
		exec.start(auto, "master");
		exec.start(auto, "slave");
		
		JFrame frame = new JFrame("test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.getContentPane().add(exec.getViewer(), BorderLayout.EAST);
		frame.getContentPane().add(new AutomataViewer<>(auto).asPanel(), BorderLayout.CENTER);

		frame.pack();
		frame.setVisible(true);
	}
}
