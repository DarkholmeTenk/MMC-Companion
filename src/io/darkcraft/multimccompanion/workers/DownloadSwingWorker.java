package io.darkcraft.multimccompanion.workers;

import io.darkcraft.multimccompanion.ui.MainWindow;
import io.darkcraft.multimccompanion.ui.ProgressDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class DownloadSwingWorker extends SwingWorker<Boolean,Object>
{
	private final File currentFile;
	private final URL url;
	public volatile int length = -1;
	public volatile int done = 0;
	public boolean downloaded = false;
	private final URLConnection urlConn;
	public String err;
	private ProgressDialog pd;

	public DownloadSwingWorker(File f, URL _url, String n) throws IOException
	{
		currentFile = f;
		url = _url;
		urlConn = url.openConnection();// connect
		length = (int) urlConn.getContentLengthLong();
		System.out.println("Downloading from URL: " + url.toString());
		System.out.println("Expected content length: " + length);
		pd = new ProgressDialog(this, MainWindow.i,"Downloading " + n, 0, length, "progress");
	}

	@Override
	public Boolean doInBackground() throws Exception
	{
		System.out.println("Download starting");
		try(FileOutputStream fos = new FileOutputStream(currentFile); InputStream is = urlConn.getInputStream())
		{
			currentFile.createNewFile();
			done = 0;

			byte[] buffer = new byte[4096]; // declare 4KB buffer
			int len;
			long pDone = 0;

			// while we have availble data, continue downloading and storing to local file
			while ((len = is.read(buffer)) >= 0)
			{
				fos.write(buffer, 0, len);
				pDone = done;
				done += len;
				//if((done - pDone) >= 10000)
				{

					firePropertyChange("progress",pDone,done);
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
			try
			{
				if(currentFile.exists())
					currentFile.delete();
			}
			catch(SecurityException e2)
			{
				e2.printStackTrace();
			}
			done = 0;
			length = -1;
			err = "Failed to download " + url.toString() + " " + e.getMessage();
			System.err.println(err);
			JOptionPane.showMessageDialog(pd,err);
			return false;
		}
		System.out.println("Downloaded: " + url.toString() + " - " + done + "/" + length + " bytes");
		downloaded = true;
		return true;
	}

	@Override
	public void done()
	{
		pd.dispose();
	}

}
