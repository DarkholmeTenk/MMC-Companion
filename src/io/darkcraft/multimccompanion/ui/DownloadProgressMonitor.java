package io.darkcraft.multimccompanion.ui;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

public class DownloadProgressMonitor extends ProgressMonitor implements PropertyChangeListener
{
	private final String v;

	public DownloadProgressMonitor(SwingWorker sw, Component parentComponent, Object message, String note, int min, int max, String _v)
	{
		super(parentComponent, message, note, min, max);
		v = _v;
		sw.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
		if((v == null) || v.equals(e.getPropertyName()))
		{
			setProgress((int) e.getNewValue());
		}
	}

}
