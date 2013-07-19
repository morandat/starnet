package fr.labri.starnet.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import fr.labri.starnet.Simulation;
import fr.labri.starnet.Simulation.Observer;
import fr.labri.starnet.Simulation.State;
import fr.labri.starnet.World;

public class SimpleUI {
	
	static public JFrame createDefaultLayout(final Simulation simulation) {
		JFrame frame = new JFrame("StarNet Simulator");
		frame.setPreferredSize(new Dimension(1024, 800));
		frame.setResizable(true);
		frame.setLayout(new BorderLayout());
		
		frame.addWindowListener(new WindowAdapter() {
			    public void windowClosing(WindowEvent e) {
			    	simulation.stop();
			    	e.getWindow().dispose();
			    }
			});
		final GraphicView gv = createGraphicView(simulation);
		final InfoPane info = createInfoPane(simulation);
		final SimpleControler controler = createSimulationControler(simulation, gv);
		JScrollPane pane = new JScrollPane(gv, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		frame.add(pane, BorderLayout.CENTER);
		frame.add(info, BorderLayout.EAST);
		frame.add(controler, BorderLayout.SOUTH);
		frame.pack();
		
		simulation.addObserver(new Observer() {
			public void simulationStateChanged(State oldState, State newState) {
				info.simulationStateChanged(oldState, newState);
				controler.simulationStateChanged(oldState, newState);
			}
			
			public void newtick(long time) {
				info.newtick(time);				
				gv.newtick(time);				
			}

			public void initWorld(World _world) {
				gv.initWorld(_world);
				info.initWorld(_world); // TODO count sent/recv msg
			}
		});
		return frame;
	}
	
	static public GraphicView createGraphicView(Simulation simulation){
		return new GraphicView(simulation);
	}
	
	static public InfoPane createInfoPane(Simulation simulation) {
		return new InfoPane(simulation);
	}
	
	static public SimpleControler createSimulationControler(Simulation simulation, GraphicView gv) {
		return new SimpleControler(simulation, gv);
	}
}
