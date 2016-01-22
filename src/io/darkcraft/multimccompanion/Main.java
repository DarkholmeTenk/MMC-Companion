package io.darkcraft.multimccompanion;

import io.darkcraft.multimccompanion.ui.MainWindow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

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
		initStreamRedir();
		readConfig();
		MainWindow window = new MainWindow();
	}

	private static void initStreamRedir()
	{
		File f = new File("mmcc-err.txt");
		try
		{
			if(!f.exists())
				f.createNewFile();
			PrintStream fos = new PrintStream(new FileOutputStream(f));
			System.setErr(fos);
			System.setOut(fos);
		}
		catch(IOException e){}
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

	public static void printException(Exception e)
	{
		String message = "";
		StackTraceElement[] elements = e.getStackTrace();
		for(StackTraceElement el : elements)
			message += el.toString() + "\n";
		JOptionPane.showMessageDialog(MainWindow.i, message, e.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
	}

}
