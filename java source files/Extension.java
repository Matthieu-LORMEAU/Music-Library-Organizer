
public class Extension {

	// this class contains one method that returns the extension of a file

	public static String get(String path) {
		String extension = "";
		int i = path.lastIndexOf('.');
		if (i > 0) {
			extension = path.substring(i + 1);
		}
		return extension;
	}
}
