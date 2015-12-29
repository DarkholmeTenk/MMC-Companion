package io.darkcraft.multimccompanion.logic;

import io.darkcraft.multimccompanion.ui.MainWindow;
import io.darkcraft.multimccompanion.workers.ModpackDownloadSwingWorker;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Modpack
{
	public final int		modpackID;
	private final String	name;
	private final int		mostRecentVersion;
	private final String	mostRecentVersionName;
	private final String	mostRecentVersionShortName;

	private Modpack(int id) throws InstantiationException
	{
		modpackID = id;
		try
		{
			String data = Network.getData("mp", "i=" + id);
			String[] dataArray = data.split(",");
			name = dataArray[0].trim();
			mostRecentVersion = Integer.parseInt(dataArray[1].trim());
			mostRecentVersionName = dataArray[2].trim();
			mostRecentVersionShortName = dataArray[3].trim();
		}
		catch (Exception e)
		{
			InstantiationException ie = (InstantiationException) new InstantiationException().initCause(e);
			throw ie;
		}

	}

	public String getName()
	{
		return name;
	}

	public int getMostRecentVersion()
	{
		return mostRecentVersion;
	}

	public String getMostRecentVersionName()
	{
		return mostRecentVersionName;
	}

	public String getMostRecentVersionShortName()
	{
		return mostRecentVersionShortName;
	}

	private static HashMap<Integer, Modpack>	cache	= new HashMap();

	public static Modpack create(int id)
	{
		try
		{
			if (!cache.containsKey(id))
				cache.put(id, new Modpack(id));
			return cache.get(id);
		}
		catch (InstantiationException e)
		{
			return null;
		}
	}

	public void install(File installLocation)
	{
		try
		{
			String data = Network.getData("mpd", "i=" + modpackID);
			String[] dataArray = data.split(",");
			URL url = new URL(dataArray[0].trim().replaceAll(" ", "%20"));
			/*File installableZip = Network.getFile(url);
			ZipHandler.unzip(installableZip, installLocation);
			new DarkcraftInstance(installLocation, this);*/
			ModpackDownloadSwingWorker mdsw = new ModpackDownloadSwingWorker(url, installLocation, this);
			mdsw.execute();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		MainWindow.i.init();
	}

	public static Set<Integer> getModpackIDs()
	{
		HashSet<Integer> toReturn = new HashSet<Integer>();
		String data = Network.getData("modpacks");
		String[] dataArray = data.split(",");
		for (String datum : dataArray)
		{
			if (datum != null)
				datum = datum.trim();
			if ((datum == null) || (datum.length() == 0))
				continue;
			try
			{
				Integer i = Integer.parseInt(datum);
				toReturn.add(i);
			}
			catch (NumberFormatException e)
			{

			}
		}
		return toReturn;
	}

	public static Set<Modpack> getModpacks()
	{
		HashSet<Modpack> modpacks = new HashSet();
		Set<Integer> modpackIDs = getModpackIDs();
		for(int i : modpackIDs)
			modpacks.add(create(i));
		return modpacks;
	}
	@Override
	public String toString()
	{
		return getName() + " - " + getMostRecentVersionShortName();
	}
}
