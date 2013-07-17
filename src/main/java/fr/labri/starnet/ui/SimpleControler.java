package fr.labri.starnet.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.labri.starnet.Simulation;
import fr.labri.starnet.Simulation.State;
import fr.labri.starnet.Simulation.Observer;
import fr.labri.starnet.World;

public class SimpleControler extends JPanel implements Observer {
	private static final long serialVersionUID = 1L;
	
	public static final String PLAY = "\u25B8";
	public static final String STOP = "\u25FC";
	public static final String PAUSE = "\u2759\u2759";
	public static final String SKIP = "\u2759\u25B8";

	final JSpinner _timeout = new JSpinner(new SpinnerNumberModel(10, 0, Integer.MAX_VALUE, 10));
	final JButton _stopbtn = new JButton(STOP);
	final JButton _playPause = new JButton(PLAY);
	final JButton _skipbtn = new JButton(SKIP);
	
	final Simulation _simulation;

	public SimpleControler(final Simulation simulation) {
		_simulation = simulation;
		setLayout(new FlowLayout());
		
		_timeout.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				simulation.setTimeout((Integer)_timeout.getValue());
			}
		});
		add(_timeout);
		
		_stopbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simulation.stop();
				_playPause.setEnabled(false);
				_stopbtn.setEnabled(false);
			}
		});
		add(_stopbtn);

		_skipbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simulation.setRunning(false);
				simulation.interrupt();
			}
		});
		add(_skipbtn);

		_playPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean running = simulation.isRunning();
				simulation.setRunning(!running);
			}
		});
		add(_playPause);
	}
	
	public void simulationStateChanged(State oldState, State newState) {
		_playPause.setText(playPauseText(_simulation.isRunning()));
	}
	
	private String playPauseText(boolean running) {
		return running ? PAUSE : PLAY;
	}

	public void newtick(long time) {
	}

	public void initWorld(World _world) {
	}
}
