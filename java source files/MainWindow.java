import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainWindow extends JFrame implements WindowListener, ActionListener {
	public MainWindow() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {

		super("Music Library Organiser");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));// i tried many layouts and this was the
																				// best one to display components in a
																				// logical and intuitive way to the user
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		addWindowListener(this);

		// This part creates the JTextArea containing the tutorial on how to use the
		// program
		JTextArea introduction = new JTextArea(
				"\n	This easy to use program is for every music lover who likes to keep their music library clean and tidy. "
						+ "\n\n	It will rename and move the audio files according this standard path :"
						+ "\n			chosen output folder/artist name/album name/#-name of the song"
						+ "\n\n	In addition the \"start sorting\" button will delete duplicates of exact same songs."
						+ "\n\n	The software will sort out all the content of the input folder(s) placing "
						+ "\n	the following type of files into different folders : "
						+ "\n	- the sorted audio files " + "\n	- the full album files with their .cue files "
						+ "\n	- the audio files that can't be sorted will be moved to the following path :"
						+ "\n			chosen output folder/Unknown Artist/Unknown Album "
						+ "\n\n	You can remove folders from chosen input folders by selecting them and pressing the 'del' key."
						+ "\n\n	Once you sorted your folders you can use the clean button which does the following :"
						+ "\n	- deletes empty folders" + "\n	- deletes corrupt/incomplete audio files"
						+ "\n	- moves non-audio files to \"other files\" folder"
						+ "\n\n	If you don't know what is .cue file and think you have any,"
						+ "\n	it doesn't matter you can still use the program."
						+ "\n\n\n	Thanks for using my program ! let me know what you think and report bugs at :"
						+ "\n					matthieulormeau@hotmail.com" + "\n");
		introduction.setEditable(false);

		/*
		 * this part creates the inputButton and the corresponding JList that displays
		 * the chosen input folders. You can select multiple folders at the same time in
		 * the JFileChooser and you can remove folders from the input by selecting them
		 * and pressing the del key
		 */

		DefaultListModel<String> foldersPaths = new DefaultListModel<>();
		JList inputFolders = new JList(foldersPaths);
		inputFolders.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_DELETE) {
					List<String> selected = inputFolders.getSelectedValuesList();
					for (String i : selected) {
						foldersPaths.removeElement(i);
						MainClass.getInputDirectories().remove(i);
					}
					inputFolders.revalidate();// refreshes the displayed list
					inputFolders.repaint();
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		JScrollPane scroll = new JScrollPane(inputFolders);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		JButton inputButton = new JButton("Choose 1 or multiple input folders");
		inputButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		inputButton.setToolTipText(
				"Folders containing your music : it doesn't matter if you have some other files in them because they will be moved into \"non audio files\" folder");
		inputButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser input = new JFileChooser();
				input.setCurrentDirectory(new File("C:\\Users\\DidouPower\\Music"));
				input.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				input.setMultiSelectionEnabled(true);
				input.setAcceptAllFileFilterUsed(false);
				if (input.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File[] files = input.getSelectedFiles();
					for (File f : files) {
						if (!MainClass.getInputDirectories().contains(f.getAbsolutePath()))
							MainClass.addInputDirectory(f.getAbsolutePath());
					}
					for (String i : MainClass.getInputDirectories()) {
						if (!foldersPaths.contains(i))
							foldersPaths.addElement(i);
						;
					}

				}
			}
		});

		JButton outputButton = new JButton("Choose Output Folder");
		outputButton.setToolTipText("Folder where will be saved your sorted library");

		JTextArea outputFolderText = new JTextArea(MainClass.getOutputDirectory());
		outputFolderText.setEditable(false);

		outputButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser output = new JFileChooser();
				output.setCurrentDirectory(new File("C:\\Users\\DidouPower\\Music"));
				output.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				output.setAcceptAllFileFilterUsed(false);
				if (output.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					MainClass.setOutputDirectory(output.getSelectedFile().getAbsolutePath());
					outputFolderText.setText(MainClass.getOutputDirectory());
				} else {
					outputFolderText.setText(MainClass.getOutputDirectory());
				}
			}
		});

		JButton cueButton = new JButton("Choose a folder for full album files with their .cue");
		cueButton.setToolTipText("Folder where will be put the unsplit albums with their associated .cue files");

		JTextArea cueFolderText = new JTextArea(MainClass.getCueFolder());
		cueFolderText.setEditable(false);

		cueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser cue = new JFileChooser();
				cue.setCurrentDirectory(new File("C:\\Users\\DidouPower\\Music"));
				cue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				cue.setAcceptAllFileFilterUsed(false);
				if (cue.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					MainClass.setCueFolder(cue.getSelectedFile().getAbsolutePath());
					cueFolderText.setText(MainClass.getCueFolder());
				} else {
					cueFolderText.setText(MainClass.getCueFolder());
				}
			}
		});

		JButton otherFilesButton = new JButton("Choose a folder for NON-audio folders and files");
		otherFilesButton.setToolTipText("Files that aren't audio files or cue files will be put in this folder");

		JTextArea otherFilesFolderText = new JTextArea(MainClass.getOtherFiles());
		otherFilesFolderText.setEditable(false);

		otherFilesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser otherFilesChooser = new JFileChooser();
				otherFilesChooser.setCurrentDirectory(new File("C:\\Users\\DidouPower\\Music"));
				otherFilesChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				otherFilesChooser.setAcceptAllFileFilterUsed(false);
				if (otherFilesChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					MainClass.setOtherFiles(otherFilesChooser.getSelectedFile().getAbsolutePath());
					otherFilesFolderText.setText(MainClass.getOtherFiles());
				} else {
					otherFilesFolderText.setText(MainClass.getOtherFiles());
				}
			}
		});

		/*
		 * the launch button and clean button when pressed both trigger the display of a
		 * progress bar preventing user actions while the process is being done.
		 */
		JButton launchButton = new JButton("Start Sorting");
		launchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (MainClass.getInputDirectories().size() != 0) {// checks if the user selected input directories.
																	// Otherwise warns him to pick some.

					JDialog dlgProgress = new JDialog();
					dlgProgress.setModal(true);// prevents main window from being used while data is processed
					JLabel lblStatus = new JLabel("Working...");

					JProgressBar pbProgress = new JProgressBar(0, 100);
					pbProgress.setIndeterminate(true);

					dlgProgress.add(BorderLayout.NORTH, lblStatus);
					dlgProgress.add(BorderLayout.CENTER, pbProgress);
					dlgProgress.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
					dlgProgress.setSize(300, 90);

					SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							for (String i : MainClass.getInputDirectories()) {
								MainClass.launch(i);
							}
							return null;
						}

						@Override
						protected void done() {
							dlgProgress.dispose();
						}
					};

					sw.execute();
					dlgProgress.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(null,
							"Pick an input folder (you can also modify the other default folders");
				}
			}
		});

		launchButton.setToolTipText("Starts the sorting process");

		JButton cleanButton = new JButton("Clean folders");
		cleanButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (MainClass.getInputDirectories().size() != 0) {// checks if the user selected input directories.
																	// Otherwise warns him to pick some.
					JDialog dlgProgress = new JDialog();
					dlgProgress.setModal(true);// prevents main window from being used while data is processed
					JLabel lblStatus = new JLabel("Working...");

					JProgressBar pbProgress = new JProgressBar(0, 100);
					pbProgress.setIndeterminate(true);

					dlgProgress.add(BorderLayout.NORTH, lblStatus);
					dlgProgress.add(BorderLayout.CENTER, pbProgress);
					dlgProgress.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
					dlgProgress.setSize(300, 90);

					SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							for (String i : MainClass.getInputDirectories()) {
								MainClass.cleanFolders(i);

							}
							MainClass.cleanFolders(MainClass.getOtherFiles());
							MainClass.cleanFolders(MainClass.getCueFolder());
							return null;
						}

						@Override
						protected void done() {
							dlgProgress.dispose();
						}
					};

					sw.execute();
					dlgProgress.setVisible(true);

				} else {
					JOptionPane.showMessageDialog(null,
							"Pick an input folder (you can also modify the other default folders");
				}

			}
		});
		cleanButton.setToolTipText(
				"Deletes empty folders, deletes corrupt/incomplete audio files and moves other files than audio and .cue to the \"other files\" folder");

		JPanel pan1 = new JPanel();
		pan1.add(introduction);

		JPanel pan2 = new JPanel();
		pan2.setLayout(new GridLayout(8, 1));
		pan2.add(outputButton);
		pan2.add(outputFolderText);
		pan2.add(cueButton);
		pan2.add(cueFolderText);
		pan2.add(otherFilesButton);
		pan2.add(otherFilesFolderText);
		pan2.add(launchButton);
		pan2.add(cleanButton);

		JPanel panA = new JPanel();
		panA.setLayout(new GridLayout(2, 1));
		panA.add(scroll);
		panA.add(pan2);

		this.add(introduction);
		this.add(inputButton);
		this.add(panA);
		this.setLocation(0, 0);
		this.setSize(new Dimension(980, 960));
		this.setMinimumSize(new Dimension(980, 960));
		this.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

}
