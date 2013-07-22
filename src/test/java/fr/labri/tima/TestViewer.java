package fr.labri.tima;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jdom2.JDOMException;


public class TestViewer {

	public static void main(String[] args) throws JDOMException, IOException {
		
		SimpleNodeFactory<String> factory = new SimpleNodeFactory<String>();
		List<TimedAutomata<String>> autos = new TimedAutomataFactory<String>(factory).loadXML(TestViewer.class.getResourceAsStream("/fr/labri/starnet/policies/push/GossipPush.xml"));
		
		JFrame frame = new JFrame("test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		//frame.getContentPane().add(exec.getViewer(), BorderLayout.EAST);
		frame.getContentPane().add(new JScrollPane(AutomataViewer.createPanel(autos)), BorderLayout.CENTER);

		frame.pack();
		frame.setVisible(true);
	}
}
