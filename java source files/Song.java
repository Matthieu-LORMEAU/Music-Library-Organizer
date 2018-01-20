import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

public class Song {

	// this class defines all the variables we need for a song in order to exploit
	// its metadata. It uses the JAudioTagger library of JThink
	// (http://www.jthink.net/jaudiotagger/)

	private String songPath;
	private File songFile;
	private AudioFile songAF;
	private Tag tag;
	private AudioHeader AudioHeader;

	public Song(String path) throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		songPath = path;
		songFile = new File(path);
		try {
			songAF = AudioFileIO.read(songFile);
			if (songAF != null) {
				tag = songAF.getTagOrCreateAndSetDefault();
			}
			AudioHeader = songAF.getAudioHeader();
		} catch (org.jaudiotagger.audio.exceptions.CannotReadException e) {
		} catch (java.io.IOException e) {
		}
	}

	public String getArtist() {
		try {
			if (tag.getFirst(FieldKey.ARTIST) != "") {
				return tag.getFirst(FieldKey.ARTIST);
			} else {
				return "Unknown Artist";
			}
		} catch (java.lang.NullPointerException e) {
			return "Unknown Artist";
		}
	}

	public void setSongPath(String songPath) {
		this.songPath = songPath;
	}

	public File getSongFile() {
		return songFile;
	}

	public AudioHeader getAudioHeader() {
		return AudioHeader;
	}

	public String getAlbumArtist() {
		try {
			return tag.getFirst(FieldKey.ALBUM_ARTIST);
		} catch (java.lang.NullPointerException e) {
			return "";
		}

	}

	public String getAlbum() {
		try {
			if (tag.getFirst(FieldKey.ALBUM) != "") {
				return tag.getFirst(FieldKey.ALBUM);
			} else {
				return "Unknown Album";
			}
		} catch (java.lang.NullPointerException e) {
			return "Unkwnown Album";
		}
	}

	public String getTitle() {
		try {
			if (tag.getFirst(FieldKey.TITLE) != "") {
				return tag.getFirst(FieldKey.TITLE);
			} else {
				return songFile.getName();
			}
		} catch (java.lang.NullPointerException e) {
			return songFile.getName();
		}
	}

	public String getTrackNumber() {
		try {
			return tag.getFirst(FieldKey.TRACK);
		} catch (java.lang.NullPointerException e) {
			return null;
		}
	}

	public String getSongPath() {
		return songPath;
	}

}
