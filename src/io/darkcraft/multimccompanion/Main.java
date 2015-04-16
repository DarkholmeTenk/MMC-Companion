package io.darkcraft.multimccompanion;

import io.darkcraft.multimccompanion.ui.MainWindow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author DarkholmeTenk
 *
 */
public class Main
{
	public static String url = "http://darkcraft.io/launcher.php";
	public static File cacheLocation = new File("cache");
	public static File instanceLocation;

	public static void main(String[] args)
	{
		readConfig();
		MainWindow window = new MainWindow();
	}
	
	private static void readConfig()
	{
		File config = new File("config.dat");
		try
		{
			if(config.exists())
			{
				BufferedReader reader = new BufferedReader(new FileReader(config));
				String line = reader.readLine();
				if(line != null)
				{
					String[] data = line.split(",");
					instanceLocation = new File(data[0].trim());
				}
			}
			else
			{
				instanceLocation = new File("instances/");
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void saveConfig()
	{
		File config = new File("config.dat");
		try
		{
			if(!config.exists())
				config.createNewFile();
			else
			{
				config.delete();
				config.createNewFile();
			}
			PrintWriter writer = new PrintWriter(config);
			writer.write(instanceLocation.toString());
			writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

}
