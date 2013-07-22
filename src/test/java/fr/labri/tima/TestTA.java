package fr.labri.tima;

import java.io.IOException;

import org.jdom2.JDOMException;

import fr.labri.starnet.INode;

public class TestTA {

	public static void main(String[] args) throws JDOMException, IOException {
		
		TimaTester tester = new TimaTester("fr.labri.starnet.policies.commons");
		
		for(int i = 0; i < args.length ; i += 2) {
			if((i + 1) < args.length)
				tester.test(args[i], args[i + 1], INode.class);
			else
				tester.test(args[i], args[i].substring(0, args[i].lastIndexOf(".")), INode.class);
		}
	}

}
