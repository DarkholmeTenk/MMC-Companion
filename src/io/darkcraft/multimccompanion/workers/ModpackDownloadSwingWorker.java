package io.darkcraft.multimccompanion.workers;

import io.darkcraft.multimccompanion.logic.DarkcraftInstance;
import io.darkcraft.multimccompanion.logic.Modpack;
import io.darkcraft.multimccompanion.logic.Network;
import io.darkcraft.multimccompanion.logic.ZipHandler;
import io.darkcraft.multimccompanion.ui.MainWindow;

import java.io.File;
import java.net.URL;

import javax.swing.SwingWorker;

public class ModpackDownloadSwingWorker extends SwingWorker<Boolean, Object>
{
	private final URL url;
	private final File installLocation;
	private final Modpack m;

	public ModpackDownloadSwingWorker(URL _url, File _installLocation, Modpack _m)
	{
		url = _url;
		installLocation = _installLocation;
		m = _m;
	}

	@Override
	protected Boolean doInBackground() throws Exception
	{
		MainWindow.i.setEnabled(false);
		File installableZip = Network.getFile(url);
		ZipHandler.unzip(installableZip, installLocation);
		new DarkcraftInstance(installLocation, m);
		MainWindow.i.init();
		return true;
	}

	@Override
	public void done()
	{
		MainWindow.i.setEnabled(true);
	}

}
