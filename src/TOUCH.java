import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

/**
 * @author Maria Jianu
 * 
 *         Clasa TOUCH contine metoda touch() care va face un fisier in zona
 *         indicata de path
 */
public class TOUCH {
	/**
	 * @param path    Path-ul fisierului
	 * @param outFile Fisierul de output
	 * @param errFile Fisierul de erori
	 */
	public void touch(String path, String outFile, String errFile) {
		PrintWriter writerOut = null;
		int error = 0;
		PrintWriter writerErr = null;
		CommandFactory cf = CommandFactory.getInstance();
		FileSystem fs = Main.getFileSys();
		int absolutePath = 0;
		try {
			writerOut = new PrintWriter(new FileWriter(outFile, true));
			writerErr = new PrintWriter(new FileWriter(errFile, true));
			String[] directory = path.split("/"); // un vector de nume de directoare
			for (int i = 0; i < directory.length; i++) {
				if (directory[i].equals(".") || directory[i].equals(".."))
					absolutePath = 1;
			}
			if (absolutePath == 0) {
				if (directory.length == 1) {
					// path-ul e doar numele directorului deci il facem in directorul curent
					Directory dir = new Directory();
					dir = dir.findRightPath(fs.getRoot(), fs.getCurrentDir(), dir);
					int duplicate = dir.findDuplicates(dir, directory[0]);
					if (duplicate == 0) {
						Files newFile = new Files(directory[0]);
						dir.getFiles().add(newFile);
						Collections.sort(dir.getFiles(), new CompareFileNames());
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
					} else {
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.print("touch: cannot create file ");
						for (int i = 0; i < directory.length; i++) {
							writerErr.print("/" + directory[i]);
						}
						writerErr.println(": Node exists");
					}

				} else if (directory.length == 2 && path.charAt(0) == '/') {
					Files newFile = new Files(directory[1]);
					fs.getRoot().getFiles().add(newFile);
					Collections.sort(fs.getRoot().getFiles(), new CompareFileNames());
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
				} else if (directory.length >= 2 || path.charAt(0) == '/') {
					// pe poz directory - 1 e numele noului fisier
					Directory dir = fs.getRoot().findRightPath(fs.getRoot(), directory[directory.length - 2],
							fs.getRoot());
					// verfic daca fisierul exista deja, sau daca e un folder cu acelasi nume
					int valid = 0;
					valid = dir.findDirectory(fs.getRoot(), directory[directory.length - 2], 0);
					for (Directory d : dir.getDirectories()) {
						if (d.getDirName().equals(directory[directory.length - 1]))
							error = 1;
					}
					for (Files f : dir.getFiles()) {
						if (f.getFileName().equals(directory[directory.length - 1]))
							error = 1;
					}
					if (valid == 0) {
						// nu era un path valid
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.print("touch: ");
						for (int i = 0; i <= directory.length - 2; i++) {
							writerErr.print(directory[i]);
							if (i != directory.length - 2)
								writerErr.print("/");

						}

						writerErr.println(": No such directory");
					} else if (error == 1) {
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.print("touch: cannot create file ");
						for (int i = 0; i <= directory.length - 1; i++) {
							if (!directory[i].equals("") && i == 0)
								writerErr.print("/");
							writerErr.print(directory[i]);
							if (i != directory.length - 1)
								writerErr.print("/");
						}
						writerErr.println(": Node exists");
					} else {
						// facem fisierul in directorul de la directory - 1
						Files newFile = new Files(directory[directory.length - 1]);
						dir.getFiles().add(newFile);
						Collections.sort(dir.getFiles(), new CompareFileNames());
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerOut.close();
						writerErr.close();
						return;
					}
				} else {
					// path-ul e /nume deci face fisierul in root
					Files newFile = new Files(directory[1]);
					fs.getRoot().getFiles().add(newFile);
					Collections.sort(fs.getRoot().getFiles(), new CompareFileNames());
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
				}
			} else {
				// path contine . sau ..
				String destDir = fs.getCurrentDir();
				Directory auxDir = new Directory();
				Directory parentDir = new Directory();
				int invalidPath = 0;
				// pe pozitia directory.length - 1 se afla numele noului fisier
				for (int i = 0; i < directory.length - 1; i++) {
					if (directory[i].equals("."))
						destDir = destDir;
					else if (directory[i].equals("..")) {
						if (destDir.equals("/")) {
							invalidPath = 1;
							destDir = "invalid";
							break;
						}
						auxDir = fs.getRoot().findRightPath(fs.getRoot(), destDir, auxDir);
						if (auxDir.getParentDir() == null) {
							invalidPath = 1;
							destDir = "invalid";
							break;
						} else {
							auxDir = auxDir.getParentDir();
							destDir = auxDir.getDirName();
						}
					} else {
						destDir = directory[i];
						// verific daca directory[i] e valid
						int valid = 0;
						valid = auxDir.findDirectory(fs.getRoot(), destDir, valid);
						if (valid == 0) {
							invalidPath = 1;
							destDir = "invalid";
							break;
						}
					}
				}
				int valid = 0;
				if (invalidPath == 0) {
					if (destDir.equals("/") && fs.getCurrentDir().equals("/"))
						valid = 1; // path is ok
					valid = auxDir.findDirectory(fs.getRoot(), destDir, valid);
				}
				int duplicate = 0;
				if (destDir.equals("/") && valid == 1 && invalidPath == 0) {
					for (Directory d : fs.getRoot().getDirectories())
						if (d.getDirName().equals(directory[directory.length - 1]))
							duplicate = 1;
					if (duplicate == 0) {
						for (Files f : fs.getRoot().getFiles())
							if (f.getFileName().equals(directory[directory.length - 1]))
								duplicate = 1;
					}

				} else if (invalidPath == 0) {
					auxDir = auxDir.findRightPath(fs.getRoot(), destDir, auxDir);
					for (Directory d : auxDir.getDirectories())
						if (d.getDirName().equals(directory[directory.length - 1]))
							duplicate = 1;
					if (duplicate == 0) {
						for (Files f : auxDir.getFiles())
							if (f.getFileName().equals(directory[directory.length - 1]))
								duplicate = 1;
					}
				}
				if (valid == 1 && invalidPath == 0 && duplicate == 0) {
					if (destDir.equals("/")) {
						// adaugam fisierul in root
						Files newFile = new Files(directory[directory.length - 1]);
						fs.getRoot().getFiles().add(newFile);
						Collections.sort(fs.getRoot().getFiles(), new CompareFileNames());
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());

					} else {
						// adaugam fisierul intr-un folder care nu e root
						auxDir = auxDir.findRightPath(fs.getRoot(), destDir, auxDir);
						Files newFile = new Files(directory[directory.length - 1]);
						auxDir.getFiles().add(newFile);
						Collections.sort(auxDir.getFiles(), new CompareFileNames());
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
					}
				} else {
					if (duplicate == 1) {
						// eroarea 2
						System.out.println("eroarea2");
					} else {
						// eroarea 1
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.print("touch: ");
						for (int i = 0; i < directory.length - 1; i++) {
							if (path.charAt(0) != '/' && i == 0)
								writerErr.print(directory[i]);
							else if (i == 0)
								writerErr.print(directory[i]);
							else
								writerErr.print("/" + directory[i]);
						}
						writerErr.println(": No such directory");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		writerOut.close();
		writerErr.close();
	}
}
