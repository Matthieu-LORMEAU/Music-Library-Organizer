import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

public class CueList {

	private ArrayList<String> listOfCues = new ArrayList<String>(); // list containing all the paths of the .cue files
																	// that are linked to a Full Album file
	private ArrayList<String> listOfFullAlbum = new ArrayList<String>(); // list containing all the paths of the full
																			// album files that are linked to a .cue
																			// file

	public CueList(String inputDirectory)
			throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		setListOfCues(inputDirectory);
	}

	// this recursive method fills in both of the lists by iterating over the files
	// of the input folder that the user chose.
	public void setListOfCues(String inputDirectory)
			throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		File directory = new File(inputDirectory);
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				if (Extension.get(file.getAbsolutePath()).matches("cue")) {
					Cue cue = new Cue(file.getAbsolutePath());
					if (cue.getCuePath() != null) {
						listOfCues.add(cue.getCuePath());
						for (Song i : cue.getFullAlbumSongs()) {
							listOfFullAlbum.add(i.getSongPath());
						}
					}
				}
			} else {
				setListOfCues(file.getAbsolutePath());
			}
		}
	}

	public ArrayList<String> getListOfFullAlbums() {
		return listOfFullAlbum;
	}

	public ArrayList<String> getListOfCues() {
		return listOfCues;
	}

}
