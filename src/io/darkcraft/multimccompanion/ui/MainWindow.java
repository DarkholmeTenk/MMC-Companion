package io.darkcraft.multimccompanion.ui;

import io.darkcraft.multimccompanion.Main;
import io.darkcraft.multimccompanion.logic.DarkcraftInstance;
import io.darkcraft.multimccompanion.logic.Modpack;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Insets;
import java.io.File;
import java.util.HashMap;
import java.util.Set;

public class MainWindow extends JFrame implements ActionListener, ListSelectionListener
{
	private static final long	serialVersionUID	= 1561840731281002320L;

	JButton						setButton;
	JButton						newButton;
	JButton						updButton;
	JButton						remButton;
	JList						list;

	DarkcraftInstance			inst				= null;

	public MainWindow()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		setButton = new JButton("Set Instance Folder");
		setButton.addActionListener(this);
		GridBagConstraints setButtonConstraints = new GridBagConstraints();
		setButtonConstraints.insets = new Insets(0, 0, 5, 5);
		setButtonConstraints.gridx = 0;
		setButtonConstraints.gridy = 0;
		getContentPane().add(setButton, setButtonConstraints);

		newButton = new JButton("Install New");
		newButton.addActionListener(this);
		GridBagConstraints newButtonConstraints = new GridBagConstraints();
		newButtonConstraints.insets = new Insets(0, 0, 5, 0);
		newButtonConstraints.gridx = 1;
		newButtonConstraints.gridy = 0;
		getContentPane().add(newButton, newButtonConstraints);

		list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.addListSelectionListener(this);
		list.setPreferredSize(new Dimension(200, 300));
		GridBagConstraints listConstraints = new GridBagConstraints();
		listConstraints.gridwidth = 3;
		listConstraints.insets = new Insets(0, 0, 5, 0);
		listConstraints.gridheight = 2;
		listConstraints.fill = GridBagConstraints.BOTH;
		listConstraints.gridx = 0;
		listConstraints.gridy = 1;
		getContentPane().add(list, listConstraints);

		updButton = new JButton("Update");
		updButton.addActionListener(this);
		GridBagConstraints updButtonConstraints = new GridBagConstraints();
		updButtonConstraints.insets = new Insets(0, 0, 0, 5);
		updButtonConstraints.gridx = 0;
		updButtonConstraints.gridy = 3;
		getContentPane().add(updButton, updButtonConstraints);
		
		remButton = new JButton("Delete");
		remButton.addActionListener(this);
		GridBagConstraints remButtonConstraints = new GridBagConstraints();
		remButtonConstraints.insets = new Insets(0, 0, 0, 5);
		remButtonConstraints.gridx = 1;
		remButtonConstraints.gridy = 3;
		getContentPane().add(remButton, remButtonConstraints);
		init();
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	private HashMap<String, DarkcraftInstance>	instanceMap	= new HashMap();

	public void init()
	{
		updButton.setEnabled(false);
		remButton.setEnabled(false);
		instanceMap.clear();
		DefaultListModel listModel = new DefaultListModel();
		File[] children = Main.instanceLocation.listFiles();
		if (children == null)
			return;
		for (File file : children)
		{
			if (file.isDirectory())
			{
				try
				{
					DarkcraftInstance i = new DarkcraftInstance(file);
					String name = (i.ood() ? "OOD - " : "") + i.folderName() + " - " + i.getModpack().getName();
					listModel.addElement(name);
					instanceMap.put(name, i);
				}
				catch (InstantiationException e)
				{

				}
			}
		}
		list.setModel(listModel);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object o = e.getSource();
		if (o == setButton)
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = chooser.showOpenDialog(this);
			if (result == chooser.APPROVE_OPTION)
			{
				Main.instanceLocation = chooser.getSelectedFile();
				Main.saveConfig();
				init();
			}
		}
		else if (o == updButton)
		{
			if (inst != null && inst.ood())
			{
				inst.update();
				init();
			}
		}
		else if (o == newButton)
		{
			Set<Modpack> possiblePacks = Modpack.getModpacks();
			Object[] possibilities = new Object[possiblePacks.size()];
			int i = 0;
			for (Modpack mp : possiblePacks)
				possibilities[i++] = mp;
			Modpack pack = (Modpack) JOptionPane.showInputDialog(this, "What modpack would you like to install?",
					"Modpack selection", JOptionPane.PLAIN_MESSAGE, null, possibilities, null);
			if(pack == null)
				return;
			File installFolder = null;
			while (installFolder == null || installFolder.exists())
			{
				String installFolderStr = (String) JOptionPane.showInputDialog(this,
						"Where would you like to install the modpack?", "Modpack folder", JOptionPane.PLAIN_MESSAGE, null,
						null, null);
				if(installFolderStr == null)
					return;
				installFolder = new File(Main.instanceLocation, installFolderStr);
			}
			pack.install(installFolder);
			init();
		}
		else if (o == remButton)
		{
			int n = JOptionPane.showConfirmDialog(
				    this,
				    "Are you sure you want to delete this instance?",
				    "Are you sure?",
				    JOptionPane.YES_NO_OPTION);
			if(n == 0)
			{
				if(inst != null)
				{
					inst.remove();
					init();
				}
			}
				
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		Object o = list.getSelectedValue();
		if (o instanceof String)
		{
			String val = (String) o;
			inst = instanceMap.get(val);
			if (inst != null)
			{
				remButton.setEnabled(true);
				if (inst.ood())
					updButton.setEnabled(true);
				else
					updButton.setEnabled(false);
			}
			else
			{
				remButton.setEnabled(false);
				updButton.setEnabled(false);
			}
		}
	}
}
