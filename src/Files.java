import java.util.ArrayList;

/**
 * Clasa Files reprezinta un fisier din sistemul de fisiere
 * 
 * @author Maria Jianu
 *
 */
public class Files implements Node {
	// componenta Leaf
	private String fileName;

	public Files() {

	}

	public Files(String name) {
		fileName = name;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void showPath() {
		// TODO Auto-generated method stub
		PWD pwd = new PWD();
		FileSystem fs = null;
		fs = Main.getFileSys();
		String outFile = Main.getOutFile();
		String errFile = Main.getErrFile();
		pwd.pwd(fs, outFile, errFile);
	}

	@Override
	public void showContent() {
		// TODO Auto-generated method stub
		FileSystem fs = null;
		LS ls = new LS();
		fs = Main.getFileSys();
		String path = null;
		String errFile = Main.getErrFile();
		String outFile = Main.getOutFile();
		ls.ls(fs, path, outFile, errFile);
	}

}
