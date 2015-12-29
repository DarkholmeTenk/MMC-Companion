package io.darkcraft.multimccompanion.workers;

import io.darkcraft.multimccompanion.logic.DarkcraftInstance;
import io.darkcraft.multimccompanion.logic.Network;
import io.darkcraft.multimccompanion.logic.ZipHandler;
import io.darkcraft.multimccompanion.ui.MainWindow;

import java.io.File;
import java.net.URL;

import javax.swing.SwingWorker;

public class ModpackInstanceUpdateSwingWorker extends SwingWorker<Boolean, Object>
{
	private final File location;
	private final String[] dataArray;
	private final DarkcraftInstance di;

	public ModpackInstanceUpdateSwingWorker(DarkcraftInstance darkcraftInstance, File _location, String[] _data)
	{
		location = _location;
		dataArray = _data;
		di = darkcraftInstance;
	}

	@Override
	protected Boolean doInBackground() throws Exception
	{
		MainWindow.i.setEnabled(false);
		try
		{
			URL url = new URL(dataArray[0].trim().replaceAll(" ", "%20"));
			File f = Network.getFile(url);
			ZipHandler.unzip(f, location);
			for(int i = 3; i < dataArray.length; i++)
			{
				File toDelete = new File(location,dataArray[i]);
				if(toDelete.exists())
					toDelete.delete();
			}
			di.finishUpdating();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void done()
	{
		MainWindow.i.setEnabled(true);
		MainWindow.i.toFront();
		MainWindow.i.requestFocus();
	}

}
