package io.darkcraft.multimccompanion.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

public class DarkcraftInstance
{
	private final File		location;
	private final Modpack	modpack;
	private int				version;

	public DarkcraftInstance(File instanceFolder) throws InstantiationException
	{
		if (instanceFolder.exists())
		{
			location = instanceFolder;
			File darkcraftFile = new File(location, "dc.dat");
			if (darkcraftFile.exists())
			{
				BufferedReader reader = null;
				try
				{
					reader = new BufferedReader(new FileReader(darkcraftFile));
					String line = reader.readLine();
					if (line != null)
					{
						String[] data = line.split(",");
						if (data.length == 2)
						{
							modpack = Modpack.create(Integer.parseInt(data[0]));
							version = Integer.parseInt(data[1]);
						}
						else
							throw new InstantiationException("Invalid data file");
					}
					else
						throw new InstantiationException("Couldn't read data file");
				}
				catch (InstantiationException e)
				{
					throw e;
				}
				catch (Exception e)
				{
					throw (InstantiationException) new InstantiationException().initCause(e);
				}
				finally
				{
					try
					{
						if(reader != null)
							reader.close();
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			}
			else
				throw new InstantiationException("Instance is not a darkcraft instance");
		}
		else
			throw new InstantiationException("Instance folder does not exist");
	}
	
	public DarkcraftInstance(File installLocation, Modpack installModpack)
	{
		location = installLocation;
		modpack = installModpack;
		version = installModpack.getMostRecentVersion();
		write();
	}
	
	public int getVersion()
	{
		return version;
	}
	
	public Modpack getModpack()
	{
		return modpack;
	}
	
	public void update()
	{
		if(version == modpack.getMostRecentVersion())
			return;
		String data = Network.getData("mpc", "i="+modpack.modpackID, "o="+version);
		String[] dataArray = data.split(",");
		try
		{
			URL url = new URL(dataArray[0].trim().replaceAll(" ", "%20"));
			ZipHandler.unzip(Network.getFile(url), location);
			for(int i = 3; i < dataArray.length; i++)
			{
				File toDelete = new File(location,dataArray[i]);
				if(toDelete.exists())
					toDelete.delete();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		version = modpack.getMostRecentVersion();
		write();
	}
	
	private void write()
	{
		File darkcraftFile = new File(location, "dc.dat");
		try
		{
			if(!location.exists())
				location.mkdirs();
			if(!darkcraftFile.exists())
				darkcraftFile.createNewFile();
			PrintWriter writer = new PrintWriter(darkcraftFile);
			writer.println(modpack.modpackID + "," + version);
			writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public String folderName()
	{
		return location.toString().replaceAll(".*/", "");
	}
	
	public boolean ood()
	{
		return version < modpack.getMostRecentVersion();
	}
	
	private void remove(File f)
	{
		try
		{
			if(f.isDirectory())
			{
				File[] children = f.listFiles();
				for(File child : children)
					remove(child);
				f.delete();
			}
			else
				f.delete();
		}
		catch(SecurityException e)
		{
			e.printStackTrace();
		}
	}

	public void remove()
	{
		remove(location);
	}
}
