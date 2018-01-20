import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

public class SongList {

	private ArrayList<String> listOfSongsPaths = new ArrayList<String>(); /*
																			 * this is a list containing the paths of
																			 * all the songs within an input directory,
																			 * except the songs contained in the
																			 * "list of full albums" of a CueList
																			 * instance.
																			 */

	public SongList(String inputFolder)
			throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		setListOfSongs(inputFolder);
	}

	// The following recursive method iterates through all the folders of the
	// directory and for each audio file (except the ones linked to a .cue)
	// it add their path to the listOfSongsPaths list.
	public void setListOfSongs(String inputDirectory)
			throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {

		CueList tempCueList = new CueList(inputDirectory);
		File directory = new File(inputDirectory);
		File[] fList = directory.listFiles();

		for (File file : fList) {
			if (!file.isDirectory()) {
				if (Extension.get(file.getAbsolutePath())
						.matches("flac|mp3|wav|m4a|aa|aac|aiff|ape|ogg|oga|mogg|wma|webm|3gp|dsf")
						&& !tempCueList.getListOfFullAlbums().contains(file.getAbsolutePath())) {
					listOfSongsPaths.add(file.getAbsolutePath());
				}

				else if (Extension.get(file.getAbsolutePath())
						.matches("flac|mp3|wav|m4a|aa|aac|aiff|ape|ogg|oga|mogg|wma|webm|3gp|dsf")
						&& tempCueList.getListOfFullAlbums().contains(file.getAbsolutePath())) {
				}
			} else {
				setListOfSongs(file.getAbsolutePath());
			}
		}
	}

	public ArrayList<String> getListOfSongs() {
		return listOfSongsPaths;
	}
}