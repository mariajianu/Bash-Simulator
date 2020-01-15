import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
/**
 * @author Maria Jianu
 * 
 *         Clasa RM contine metoda rm() care va sterge directorul sau fisierul
 *         indicat de path
 */
public class RM {
	/**
	 * @param path   Path-ul directorului/fisierului
	 * @param out    Fisierul de output
	 * @param errors Fisierul de erori
	 */
	public void rm(String path, String outFile, String errFile) {
		PrintWriter writerOut = null;
		PrintWriter writerErr = null;
		CommandFactory cf = CommandFactory.getInstance();
		FileSystem fs = Main.getFileSys();
		int absolutePath = 0, isFile = 0, isDirectory = 0;
		Directory sourceDir = new Directory();
		Files sourceFile = new Files();
		Directory fileDir = new Directory();
		int invalidPath = 0;
		try {
			writerOut = new PrintWriter(new FileWriter(outFile, true));
			writerErr = new PrintWriter(new FileWriter(errFile, true));
			String[] directory = path.split("/"); // un vector de nume de directoare
			for (int i = 0; i < directory.length; i++) {
				if (directory[i].equals(".") || directory[i].equals(".."))
					absolutePath = 1;
			}
			if(absolutePath == 0) {
				if (directory.length > 2) {
					// pe poz dir - 1 e numele dir/fis si pe poz dir - 2 e numele dir parinte
					Directory auxDir = new Directory();
					int valid = 0;
					for (int i = 1; i < directory.length - 1; i++) {
						valid = auxDir.findDirectory(fs.getRoot(), directory[i], 0);
						if (valid == 0)
							break;
					}
					if (valid == 1) {
						// verifici daca pe ultima pozitie era un fisier sau un director
						auxDir = auxDir.findRightPath(fs.getRoot(), directory[directory.length - 2], auxDir);
						for (Directory d : auxDir.getDirectories()) {
							if (d.getDirName().equals(directory[directory.length - 1])) {
								sourceDir = d;
								isDirectory = 1;
								break;
							}
						}
						for (Files f : auxDir.getFiles()) {
							if (f.getFileName().equals(directory[directory.length - 1])) {
								sourceFile = f;
								isFile = 1;
								break;
							}
						}
						if(isFile == 1)
							auxDir.getFiles().remove(sourceFile);
						if (isFile == 0 && isDirectory == 0) {
							// fisierul/folderul nu exista
							invalidPath = 1;
						}
					} else {
						invalidPath = 1;
					}
				}
				else if(directory.length == 2 && path.charAt(0) == '/') {
					//path e /dir
					int ok = 0;
					Directory rmDir = new Directory();
					Files rmFile = new Files();
					for(Directory d : fs.getRoot().getDirectories()) {
						if(d.getDirName().equals(directory[1])) {
							rmDir = d;
							ok = 1;
							isDirectory = 1;
							break;
						}
					}
					for(Files f : fs.getRoot().getFiles()){
						if(f.getFileName().equals(directory[1])){
							rmFile = f;
							ok = 1;
							isFile = 1;
							break;
						}
					}
					if(ok == 1 && isFile == 1){
						fs.getRoot().getFiles().remove(rmFile);
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.close();
						writerOut.close();
						return;

					}
					if(fs.getCurrentDir().equals(rmDir.getDirName())){
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.close();
						writerOut.close();
						return;
					}
					if(ok == 1 && !fs.getCurrentDir().equals(rmDir.getDirName()) && isDirectory == 1) {
						//il sterg
						Directory parentDir = rmDir.getParentDir();
						parentDir.getDirectories().remove(rmDir);
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.close();
						writerOut.close();
						return;
					}
					else if(ok == 0) {
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.println("rm: cannot remove " + path + ": No such file or directory");
						writerErr.close();
						writerOut.close();
						return;
					}
						
				}
				else if (directory.length == 1) {
					// path-ul e doar numele directorului/fisierului
					Directory auxDir = new Directory();
					auxDir = auxDir.findRightPath(fs.getRoot(), fs.getCurrentDir(), auxDir);
					for (Directory d : auxDir.getDirectories()) {
						if (d.getDirName().equals(directory[directory.length - 1])) {
							sourceDir = d;
							isDirectory = 1;
							break;
						}
					}
					for (Files f : auxDir.getFiles()) {
						if (f.getFileName().equals(directory[directory.length - 1])) {
							sourceFile = f;
							isFile = 1;
							break;
						}
					}
					if(isFile == 1)
						auxDir.getFiles().remove(sourceFile);
					if (isFile == 0 && isDirectory == 0) {
						// fisierul/folderul nu exista
						invalidPath = 1;
					}
				} else {
					// path-ul e /dir
					Directory auxDir = new Directory();
					auxDir = auxDir.findRightPath(fs.getRoot(), "/", auxDir);
					for (Directory d : auxDir.getDirectories()) {
						if (d.getDirName().equals(directory[directory.length - 1])) {
							sourceDir = d;
							isDirectory = 1;
							break;
						}
					}
					for (Files f : auxDir.getFiles()) {
						if (f.getFileName().equals(directory[directory.length - 1])) {
							sourceFile = f;
							isFile = 1;
							break;
						}
					}
					if(isFile == 1)
						auxDir.getFiles().remove(sourceFile);
					if (isFile == 0 && isDirectory == 0) {
						// fisierul/folderul nu exista
						invalidPath = 1;
					}

				}
				if (invalidPath == 1) {
					// nu exista dir/fisier sursa
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
					writerErr.println("rm: cannot remove " + path + ": No such file or directory");
					writerErr.close();
					writerOut.close();
					return;
				}
			}
			else {
				//path contine . sau ..
				String destDir = fs.getCurrentDir();
				Directory auxDir = new Directory();
				String parentDir = "";
				int root = 0, parent = 0, current = 0;
				if(destDir.equals("/"))
					root = 1;
				else {
					Directory parDir = fs.getRoot().findRightPath(fs.getRoot(), destDir, fs.getRoot());
					parentDir = parDir.getDirName();
				}
				if(directory.length == 1 && directory[0].equals("..")){
					destDir = parentDir;
					parent = 1;
				}
				if(directory.length == 1 && directory[0].equals(".") && parent == 0){
					destDir = fs.getCurrentDir();
					current = 1;
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
					writerErr.close();
					writerOut.close();
					return;
				}
				if(parent == 0 && current == 0){
				for (int i = 0; i < directory.length - 1; i++) {
					if (directory[i].equals("."))
						parentDir = destDir;
					else if (directory[i].equals("..")) {
						if (parentDir.equals("/")) {
							invalidPath = 1;
							parentDir = "invalid";
							break;
						}
						auxDir = fs.getRoot().findRightPath(fs.getRoot(), parentDir, auxDir);
						if (auxDir.getParentDir() == null) {
							invalidPath = 1;
							parentDir = "invalid";
							break;
						} else {
							auxDir = auxDir.getParentDir();
							parentDir = auxDir.getDirName();
						}
					} else
						parentDir = directory[i];
				}
			}
				invalidPath = 0;
				if(current == 0){
				for (int i = 0; i < directory.length; i++) {
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
					} else
						destDir = directory[i];
				}
			}
				// destDir e numele fisierului/dir care trebuie sters
				if (invalidPath == 0) {
					Files file = new Files(destDir);
					Directory dir = new Directory(destDir);
					isFile = dir.findFile(fs.getRoot(), destDir, 0);
					isDirectory = dir.findDirectory(fs.getRoot(), destDir, 0);
					if (isFile == 0 && isDirectory == 0) {
						// nu exista dir/fisier sursa
						invalidPath = 1;
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerErr.println("rm: cannot remove " + path + ": No such file or directory");
						writerErr.close();
						writerOut.close();
						return;
					} else if (isFile == 1) {
						sourceFile = file;
						Directory parentDirr = new Directory();
						parentDirr = parentDirr.findRightPath(fs.getRoot(), parentDir, fs.getRoot());
						parentDirr.getFiles().remove(sourceFile);
					} else {
						// era director
						Directory parentDirr = new Directory();
						parentDirr = parentDirr.findRightPath(fs.getRoot(), parentDir, fs.getRoot());
						for(Directory d : parentDirr.getDirectories()) {
							if(d.getDirName().equals(destDir)) {
								sourceDir = d;
								break;
							}
						}
					}
				} else {
					// nu exista sursa
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
					writerErr.println("rm: cannot remove " + path + ": No such file or directory");
					writerErr.close();
					writerOut.close();
					return;
				}
			}
			if(invalidPath == 0) {
				//facem stergerea
				if(isDirectory == 1) {
					Directory auxDir = new Directory();
					auxDir = sourceDir.getParentDir();
					auxDir.getDirectories().remove(sourceDir);
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
				}
				else {
					writerOut.println(cf.getCommandNr());
					writerErr.println(cf.getCommandNr());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		writerOut.close();
		writerErr.close();
	}
}
