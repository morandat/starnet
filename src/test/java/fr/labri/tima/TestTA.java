package fr.labri.tima;

import java.io.IOException;


import org.jdom2.JDOMException;

import fr.labri.AutoQualifiedClassLoader;
import fr.labri.DotViewer;
import fr.labri.tima.TimedAutomata;
import fr.labri.tima.ITimedAutomata.Action;
import fr.labri.tima.ITimedAutomata.NodeFactory;
import fr.labri.tima.ITimedAutomata.Predicate;
import fr.labri.tima.ITimedAutomata.Spawner;

public class TestTA {

	final private AutoQualifiedClassLoader _classLoader;
	final public boolean RENDER = Boolean.parseBoolean(System.getProperty("test.render", "true"));
	final public boolean COMPILED = Boolean.parseBoolean(System.getProperty("test.compile", "false"));
	
	public TestTA(String namespace) {
		_classLoader = new AutoQualifiedClassLoader(namespace);
	}

	public static void main(String[] args) throws JDOMException, IOException {
		
		TestTA tester = new TestTA("fr.labri.starnet.policies.commons");
		
		for(int i = 0; i < args.length ; i += 2) {
			if((i + 1) < args.length)
				tester.test(args[i], args[i + 1]);
			else
				tester.test(args[i], args[i].substring(0, args[i].lastIndexOf(".")));
		}
	}
	
	void test(String name, String namespace) throws JDOMException, IOException {
		String fname = "/" + name.replaceAll("\\.", "/") + ".xml";
		TimedAutomata<Object> b = new TimedAutomataFactory<>(getSimpleNodeBuilder(namespace)).loadXML(
				getClass().getResourceAsStream(fname)
		);
		
		ITimedAutomata<Object> auto = COMPILED ? b.compile() : b;
		String dot = new DotRenderer<>(auto).toDot(name.substring(name.lastIndexOf(".") + 1));
		if(RENDER)
			DotViewer.view(dot);
		else
			System.out.println(dot);
	}
	
	<C> NodeFactory<C> getSimpleNodeBuilder(final String namespace) {
		final NodeFactory<Object> factory = TimedAutomataFactory.getReflectNodeBuilder(new AutoQualifiedClassLoader(namespace, _classLoader), Object.class);
		return new SimpleNodeFactory<C>() {
			public Predicate<C> newPredicate(String name, String attr) {
				if(factory.newPredicate(name, attr) == null) error(name);
				return super.newPredicate(name, attr);
			}

			@Override
			public Action<C> newAction(String type, String attr) {
				if(factory.newAction(type, attr) == null) error(type);
				return super.newAction(type, attr);
			}

			@Override
			public Spawner<C> newSpawner(String type, String attr) {
				if(factory.newSpawner(type, attr) == null) error(type);
				return newSpawner(type, attr);
			}

			private void error(String name) {
				System.err.printf("Class %s not found in %s\n", name, namespace);
			}
		};
	}
}
