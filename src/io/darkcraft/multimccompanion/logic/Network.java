package io.darkcraft.multimccompanion.logic;

import io.darkcraft.multimccompanion.Main;
import io.darkcraft.multimccompanion.workers.DownloadSwingWorker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Network
{
	public volatile static long length = 0;
	public volatile static long done = 0;

	private static String[] addArg(String arg, String... old)
	{
		if ((old == null) || (old.length == 0))
			return new String[] { arg };
		String[] newArray = new String[old.length + 1];
		newArray[0] = arg;
		for (int i = 0; i < old.length; i++)
		{
			newArray[i + 1] = old[i];
		}
		return newArray;
	}

	public static String buildOptions(String... options)
	{
		if ((options == null) || (options.length == 0))
			return "";
		StringBuilder built = new StringBuilder("?").append(options[0]);
		for (int i = 1; i < options.length; i++)
			built.append("&").append(options[i]);
		return built.toString();
	}

	private static String retreiveData(String... options)
	{
		HttpURLConnection conn;
		BufferedReader reader = null;
		StringBuilder result = new StringBuilder();
		try
		{
			URL url = new URL(Main.url + buildOptions(options));
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while((line = reader.readLine()) != null)
				result.append(line).append("\n");
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return result.toString();
	}

	public static String getData(String function, String... options)
	{
		return retreiveData(addArg("d=" + function, options));
	}

	public static String getDataWithAccessToken(String function, String user, String... options)
	{
		return getData(function, addArg("a=" + user, options));
	}

	public static File getFile(URL url)
	{
		if(!Main.cacheLocation.isDirectory())
			Main.cacheLocation.mkdir();
		String[] data = url.getPath().split("/");
		String name = data[data.length-1];
		System.out.println("Looking for " + name + " from " + url.toString());
		File currentFile = new File(Main.cacheLocation,name);
		if(currentFile.exists())
			return currentFile;
		try
		{
			System.out.println("Not found: " + name + " initialising download");
			DownloadSwingWorker dsw = new DownloadSwingWorker(currentFile,url,name);
			dsw.execute();
			dsw.get();
			System.out.println("Download finished");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return currentFile;
	}
}
