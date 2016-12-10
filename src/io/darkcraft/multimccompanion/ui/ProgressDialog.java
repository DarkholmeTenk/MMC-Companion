package io.darkcraft.multimccompanion.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class ProgressDialog extends JDialog implements PropertyChangeListener
{
	private final Component parent;
	private final SwingWorker sw;
	private final int m;
	private final int M;
	private final String v;

	private final JLabel label;
	private final JProgressBar bar;

	public ProgressDialog(SwingWorker _sw, Component parentComponent, String note, int min, int max, String _v)
	{
		parent = parentComponent;
		m = min;
		M = max;
		v = _v;
		sw = _sw;
		sw.addPropertyChangeListener(this);

		setLayout(new GridBagLayout());
		label = new JLabel(note);
		GridBagConstraints labelConstraints = new GridBagConstraints();
		labelConstraints.gridx = 0;
		labelConstraints.gridy = 0;
		add(label, labelConstraints);

		if(max == -1)
		{
			bar = new JProgressBar();
			bar.setIndeterminate(true);
		}
		else
			bar = new JProgressBar(min,max);
		GridBagConstraints barConstraints = new GridBagConstraints();
		barConstraints.gridx = 0;
		barConstraints.gridy = 1;
		add(bar,barConstraints);

		pack();
		setVisible(true);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if(evt.getSource() != sw) return;
		if((v == null) || v.equals(evt.getPropertyName()))
		{
			int val = (int)evt.getNewValue();
			bar.setValue(val);
			bar.setString(val + "/"+M);
		}
	}
}
