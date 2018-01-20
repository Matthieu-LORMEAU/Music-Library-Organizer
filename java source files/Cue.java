import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

public class Cue {

	private String cuePath;
	private File cueFile;
	private ArrayList<String> allLines; // each element of this arrayList corresponds to a line of the .cue file (a .cue
										// file is a text file basically)
	private ArrayList<Song> fullAlbumSongs = new ArrayList<Song>(); // this arrayList contains all the song instances of
																	// each path in the "listOfFullAlbum" of a CueList

	public Cue(String path) throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		cuePath = path;
		cueFile = new File(cuePath);
		setAllLines(); // fills in the allLines arrayList with each line of the .cue file

		/*
		 * The following part checks if the .cue corresponds to a full album single file
		 * or to already splitted songs. In order to do this we count the number of
		 * occurences of the keyword FILE in the cue file : if FILE appears more than
		 * three times we consider that it's a cue file corresponding to already
		 * splitted songs. Why max 3 appearances allowed and not only 1 ? because
		 * sometimes Best of albums or Vinyl albums contain multiple big files (each
		 * with different songs) on it corresponding to different CDs or both faces of
		 * the vinyl. A cue file is only useful if an album isn't splitted : -if the
		 * album is already splitted the cue file will be deleted and corresponding
		 * songs list set to null -if the album isn't splitted the .cue file will remain
		 * and will be added to the "listOfCues" when creating a CueList instance.
		 */
		int count1 = 0;
		for (String i : allLines) {
			if (i.contains("FILE")) {
				count1++;
			}
		}
		if (count1 < 4) {
			for (String i : allLines) {
				if (i.contains("FILE")) {
					int début = i.indexOf("\"");
					int fin = i.lastIndexOf("\"");
					fullAlbumSongs.add(new Song(cueFile.getParent() + "\\" + i.substring(début + 1, fin)));
				}
			}
		} else {
			cuePath = null;
			cueFile.delete();
			fullAlbumSongs = null;
		}

		// the following checks if the albums linked to the cue are at least 20 MBytes
		try {
			for (Song i : fullAlbumSongs) {
				if (i.getSongFile().length() < 20000000) {
					cuePath = null;
					cueFile.delete();
					i.setSongPath(null);
				}
			}
		} catch (java.lang.NullPointerException e) {
		}

	}

	// this method reads the cue file and add each line as a string to the
	// ArrayList<String> allLines.
	public void setAllLines() throws IOException {
		allLines = new ArrayList<String>();
		for (String line : Files.readAllLines(Paths.get(cuePath), Charset.forName("ISO-8859-1"))) {
			allLines.add(line);
		}
	}

	public String getCuePath() {
		return cuePath;
	}

	public ArrayList<String> getAllLines() {
		return allLines;
	}

	public ArrayList<Song> getFullAlbumSongs() {
		return fullAlbumSongs;
	}
}
