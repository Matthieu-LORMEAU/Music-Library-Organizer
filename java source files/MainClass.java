import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import java.util.ArrayList;

import javax.swing.UnsupportedLookAndFeelException;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

public class MainClass {

	private static String newSongPath = "";

	private static ArrayList<String> inputDirectories = new ArrayList<String>(); /*
																					 * input folders that have to be
																					 * selected by user
																					 */
	private static String outputDirectory = "C:/Users/" + System.getProperty("user.name")
			+ "/Music/"; /*
							 * output folder that has a default value but can be modified
							 */

	private static String cueFolder = "C:/Users/" + System.getProperty("user.name")
			+ "/Music/Full album audio files with associated cue files"; /*
																			 * some albums have one big file holding all
																			 * the songs and require the user to split
																			 * the audio file thanks to a .cue file :
																			 * the cue files and associated audio files
																			 * will be placed in this folder so that the
																			 * user can later split the audio into the
																			 * different tracks.
																			 */
	private static String otherFiles = "C:/Users/" + System.getProperty("user.name")
			+ "/Documents/NON audio folders and files"; /*
														 * non audio files folder : has a default value but can be
														 * modified
														 */

	private static CueList cueList = null;

	public static void main(String[] args)
			throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException, CannotReadException,
			ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

		new MainWindow(); // we initialize our program by creating an instance of our window

	}

	public static void launch(String inputDirectory)
			throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException, CannotReadException {

		if (inputDirectory != cueFolder) {

			buildCueFolder(inputDirectory); /*
											 * first we separate the full album files and their cues from the rest by
											 * moving them in their respective folder
											 */

			SongList s = new SongList(inputDirectory);

			for (String i : s.getListOfSongs()) {

				Song currentSong = new Song(i);

				String correctedName;
				
				String title = currentSong.getTitle();

				/*
				 * following part determines the new path path for the currentSong according to
				 * the available information about it. Also we remove the characters that are
				 * not compatible with windows file names.
				 */

				if (currentSong.getTrackNumber() != null && currentSong.getTrackNumber() != "") {

					correctedName = (currentSong.getTrackNumber().replace(".", "") + " - " + title)
							.replaceAll("[/\\\\:*?\"<>|.]", "") + "." + Extension.get(i);
				} else {
					correctedName = title;
				}

				String album = currentSong.getAlbum().replaceAll("[/\\\\:*?\"<>|.]", "");
				album = album.replaceAll("\\s+$", "");

				if (currentSong.getAlbumArtist() != "") {
					String artist = currentSong.getAlbumArtist().replaceAll("[/\\\\:*?\"<>|.]", "");
					artist = artist.replaceAll("\\s+$", "");
					newSongPath = outputDirectory + "\\" + artist + "\\" + album + "\\" + correctedName;
				}

				else {
					String artist = currentSong.getArtist().replaceAll("[/\\\\:*?\"<>|.]", "");
					artist = artist.replaceAll("\\s+$", "");
					newSongPath = outputDirectory + "\\" + artist + "\\" + album + "\\" + correctedName;

				}

				File targetFile = new File(newSongPath);

				if (!targetFile.exists()) {

					File parent = targetFile.getParentFile();
					if (!parent.exists() && !parent.mkdirs()) {
						parent.mkdirs();
					}
					File a = new File(i);
					a.renameTo(targetFile);

					/*
					 * if the targetFile corresponding to the newSongPath already exists we check if
					 * the canonicalPath of the current song is the same as the targetFile canonical
					 * path : if it is, that means they are the same file and we must not delete it
					 * therefore (that would delete our entire library!). If the canonical paths are
					 * different it means our currentSong is a duplicate of the targetFile that
					 * already exists.
					 */

				} else if (targetFile.exists()) {

					if (currentSong.getSongFile().getCanonicalPath() != targetFile.getCanonicalPath()) {
						currentSong.getSongFile().delete();
					}
				}

			}
		}
	}

	public static void cleanFolders(String directoryName)
			throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {

		for (int i = 0; i < 3; i++) {// repeats the method three times to make sure it cleaned everything (i didn't
										// find a do()while way of making this so i just repeated the method 3 times)
			if (cueList == null) {
				cueList = new CueList(directoryName);
				/*
				 * this checks if cueList has already been instantiated by buildCueFolders
				 * method. Otherwise it builds it now (this happens if the user only used
				 * "clean folders" button without using "sorting" button before)
				 */
			}
			File directory = new File(directoryName);
			File[] fList = directory.listFiles();

			try {
				for (File file : fList) {

					if (!file.isDirectory()) {

						/*
						 * While troubleshooting my app i realized some empty folders were not being
						 * deleted and that was caused to hidden files or system files. So this part
						 * checks if the file is a hidden or system file and deletes it only if the
						 * containing folder has no other "regular viewable" files
						 */

						Path path = Paths.get(file.getAbsolutePath());
						DosFileAttributes dfa;

						try {
							dfa = Files.readAttributes(path, DosFileAttributes.class);
							if ((dfa.isHidden() || dfa.isSystem()) && file.getParentFile().listFiles(new FileFilter() {
								@Override
								public boolean accept(File fileb) {
									Path path = Paths.get(fileb.getAbsolutePath());
									DosFileAttributes dfa;
									try {
										dfa = Files.readAttributes(path, DosFileAttributes.class);
									} catch (IOException e) {
										return false;
									}
									return (!dfa.isHidden() && !dfa.isSystem());
								}
							}).length == 0) {
								file.delete();
								continue; // since the file has been deleted we can jump to next iteration
							}
						} catch (IOException e) {
						}

						File currentFolder = file.getParentFile();

						/*
						 * This part moves all non audio files and non important cue files to otherFiles
						 * folder.
						 * 
						 * Concerning audio files it checks if they are not corrupt and removes them if
						 * they are : if the audioheader is null or the bitrate is below 50 Kbits per
						 * second is i consider them corrupt. This is not the most accurate method but
						 * it helps removing some incomplete files. I didn't find a precise way to check
						 * how corrupt is an audio file.
						 * 
						 * If the file is a directory and the directory is empty it deletes it.
						 */
						if (!Extension.get(file.getAbsolutePath())
								.matches("flac|mp3|wav|m4a|aa|aac|aiff|ape|ogg|oga|mogg|wma|webm|3gp|dsf")
								&& !cueList.getListOfCues().contains(file.getAbsolutePath())) {
							String dirPath = otherFiles + "\\"
									+ file.getParent().substring(file.getParent().lastIndexOf("\\") + 1);
							File dir = new File(dirPath);

							if (!dir.exists() && !dir.mkdirs()) {
								dir.mkdirs();
							}

							newSongPath = dirPath + "\\" + file.getName();
							File targetFile = new File(newSongPath);
							if (!targetFile.exists()) {
								file.renameTo(targetFile);
							} else if (targetFile.exists()
									&& targetFile.getCanonicalPath() != file.getCanonicalPath()) {
								file.delete();
							}
						}

						else if (Extension.get(file.getAbsolutePath())
								.matches("flac|mp3|wav|m4a|aa|aac|aiff|ape|ogg|oga|mogg|wma|webm|3gp|dsf")) {
							Song currentSong = new Song(file.getAbsolutePath());

							if (currentSong.getAudioHeader().getBitRateAsNumber() < 50) {
								file.delete();
							} 
							
						}
						currentFolder.delete();

					} else if (file.isDirectory() && file.listFiles().length == 0) {
						file.delete();

					} else
						cleanFolders(file.getAbsolutePath());
				}
			} catch (java.lang.NullPointerException e) {
			}
		}
	}

	public static void buildCueFolder(String inputDir)
			throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {

		/*
		 * this method moves all the elements corresponding to the paths in listOfCues
		 * and ListOfFullAlbums to the default cueFolder or the one chosen by user.
		 */

		cueList = new CueList(inputDir);
		ArrayList<String> c = cueList.getListOfCues();
		ArrayList<String> f = cueList.getListOfFullAlbums();

		for (int i = 0; i < cueList.getListOfFullAlbums().size(); i++) {

			try {
				File a = new File(c.get(i));
				String name = cueFolder + "\\" + a.getName();
				File targetFile = new File(name);
				File parent = targetFile.getParentFile();

				if (!parent.exists() && !parent.mkdirs()) {
					parent.mkdirs();
				}

				if (!targetFile.exists()) {
					a.renameTo(targetFile);
				} else if (targetFile.exists() && targetFile.mkdir()
						&& a.getCanonicalPath() != targetFile.getCanonicalPath()) {
					a.delete();
				}
			} catch (java.lang.NullPointerException e) {
			}

			try {
				File b = new File(f.get(i));
				String name2 = cueFolder + "\\" + b.getName();
				File targetFile2 = new File(name2);
				File parent2 = targetFile2.getParentFile();

				if (!parent2.exists() && !parent2.mkdirs()) {
					parent2.mkdirs();
				}

				if (!targetFile2.exists()) {
					b.renameTo(targetFile2);
				} else if (targetFile2.exists() && targetFile2.mkdir()
						&& b.getCanonicalPath() != targetFile2.getCanonicalPath()) {
					b.delete();
				}
			} catch (java.lang.NullPointerException e) {
			}

		}
	}

	public static String getName() {
		return newSongPath;
	}

	public static void setName(String name) {
		MainClass.newSongPath = name;
	}

	public static ArrayList<String> getInputDirectories() {
		return inputDirectories;
	}

	public static void addInputDirectory(String inputDirectory) {
		inputDirectories.add(inputDirectory);
	}

	public static String getOutputDirectory() {
		return outputDirectory;
	}

	public static void setOutputDirectory(String outputDirectory) {
		MainClass.outputDirectory = outputDirectory;
	}

	public static String getCueFolder() {
		return cueFolder;
	}

	public static void setCueFolder(String cueFolder) {
		MainClass.cueFolder = cueFolder;
	}

	public static String getOtherFiles() {
		return otherFiles;
	}

	public static void setOtherFiles(String otherFiles) {
		MainClass.otherFiles = otherFiles;
	}

}
