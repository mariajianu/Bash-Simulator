import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Maria Jianu
 * 
 * Clasa LS contine metoda ls() care va printa in fisierul de output
 * lista cu fisiere si directoare
 */
public class LS {
	private int recursive;

	/**
	 * Metoda care listeaza fisierele si directoarele 
	 * 
	 * @param fs Instanta sistemului de fisierele
 	 * @param path Path-ul directorului
	 * @param out Fisierul de output
	 * @param errors Fisierul de erori
	 */
	public void ls(FileSystem fs, String path, String out, String errors) {
		// daca path e null -> ls fara argumente
		PrintWriter writerOut = null;
		int error = 0;
		PrintWriter writerErr = null;
		CommandFactory cf = CommandFactory.getInstance();
		int absolutePath = 0;
		String[] directory = null;
		if (path != null)
			directory = path.split("/");
		try {
			writerOut = new PrintWriter(new FileWriter(out, true));
			writerErr = new PrintWriter(new FileWriter(errors, true));
			if (path == null) {
				// avem ls sau ls -R
				if (recursive == 0) {
					// ls simplu
					if (fs.getCurrentDir().equals("/") == false) {
						// daca nu e root
						String[] dirPath = new String[30];
						String aux = fs.getCurrentDir();
						String dirName = fs.getCurrentDir();
						int i = 0;
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						while (aux.equals("/") == false) {
							// caut path ul directorului curent
							Directory dir = new Directory("name");
							dir = dir.findRightPath(fs.getRoot(), dirName, dir);
							dirPath[i] = dir.getDirName();
							dir = dir.getParentDir();
							aux = dir.getDirName();
							dirName = aux;
							i++;
						}
						i = i - 1;
						while (i != -1) {
							writerOut.print("/" + dirPath[i]);
							i--;
							if (i == -1)
								writerOut.println(":");
						}
						// am scris path-ul acum scriem fisierele si folderele
						Directory curDir = new Directory();
						curDir = curDir.findRightPath(fs.getRoot(), fs.getCurrentDir(), curDir);
						if (curDir.getDirectories().isEmpty() == true) {
							writerOut.println();
							writerOut.println();
						} else {
							ArrayList<String> dirNames = new ArrayList<String>(30);
							for (Directory d : curDir.getDirectories()) {
								dirNames.add(d.getDirName());
							}
							for (Files f : curDir.getFiles()) {
								dirNames.add(f.getFileName());
							}
							Collections.sort(dirNames);
							int nr = dirNames.size();
							for (String s : dirNames) {
								nr--;
								if (nr > 0)
									writerOut.print("/" + s + " ");
								else
									writerOut.println("/" + s);
							}
							writerOut.println();
						}
					} else {
						//folderul era root
						writerOut.println(cf.getCommandNr());
						writerOut.println("/:");
						writerErr.println(cf.getCommandNr());
						// am scris path-ul acum scriem fisierele si folderele
						if (fs.getRoot().getDirectories().isEmpty() == true) {
							writerOut.println();
							writerOut.println();
						} else {
							ArrayList<String> dirNames = new ArrayList<String>(30);
							for (Directory d : fs.getRoot().getDirectories()) {
								dirNames.add(d.getDirName());
							}
							for (Files f : fs.getRoot().getFiles()) {
								dirNames.add(f.getFileName());
							}
							Collections.sort(dirNames);
							int nr = dirNames.size();
							for (String s : dirNames) {
								nr--;
								if (nr > 0)
									writerOut.print("/" + s + " ");
								else
									writerOut.println("/" + s);
							}
							writerOut.println();
						}
					}
				} else {
					// ls -R cu path null
					recursive = 0;
					try {
						writerOut = new PrintWriter(new FileWriter(out, true));
						writerErr = new PrintWriter(new FileWriter(errors, true));
						writerOut.println(cf.getCommandNr());
						writerErr.println(cf.getCommandNr());
						writerOut.close();
						writerErr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					Directory dir = new Directory();
					//ls recursiv incepand cu directorul curent
					dir = fs.getRoot().findRightPath(fs.getRoot(), fs.getCurrentDir(), dir);
					dir.listDirectories(dir, out, errors, "");
					recursive = 0;
				}
			} else {
				// ls path sau ls -R path
				for (int i = 0; i < directory.length; i++) {
					if (directory[i].equals(".") || directory[i].equals(".."))
						absolutePath = 1;
				}
				if (absolutePath == 0) {
					if (recursive == 0) {
						// ls path, iar path nu contine . sau ..
						if (path.equals("/") || path.equals(".")) {
							writerOut.println(cf.getCommandNr());
							writerOut.println("/:");
							writerErr.println(cf.getCommandNr());
							// am scris path-ul acum scriem fisierele si folderele
							if (fs.getRoot().getDirectories().isEmpty() && fs.getRoot().getFiles().isEmpty()) {
								writerOut.println();
								writerOut.println();
							} else {
								ArrayList<String> dirNames = new ArrayList<String>(30);
								for (Directory d : fs.getRoot().getDirectories()) {
									dirNames.add(d.getDirName());
								}
								for (Files f : fs.getRoot().getFiles()) {
									dirNames.add(f.getFileName());
								}
								Collections.sort(dirNames);
								int nr = dirNames.size();
								for (String s : dirNames) {
									nr--;
									if (nr > 0)
										writerOut.print("/" + s + " ");
									else
										writerOut.println("/" + s);
								}
								writerOut.println();
							}
						} else if (directory.length > 1) {
							int nr = fs.getRoot().getDirectories().size(), i = 1, ok = 0, j = 0;
							// j < i cu 1, j e pt directoare si i pt vectorul de nume
							while (nr != 0) {
								//verific daca directorul e valid
								if (fs.getRoot().getDirectories().get(j).getDirName().equals(directory[i])) {
									ok = 1;
									break;
								}
								j++;
								nr--;
							}
							if (ok == 1) {
								Directory pathDir = new Directory(directory[directory.length - 1]);
								pathDir = pathDir.findRightPath(fs.getRoot(), directory[directory.length - 1], pathDir);
								writerOut.println(cf.getCommandNr());
								writerOut.println(path + ":");
								writerErr.println(cf.getCommandNr());
								ArrayList<String> dirNames = new ArrayList<String>(30);
								for (Directory d : pathDir.getDirectories()) {
									dirNames.add(d.getDirName());
								}
								for (Files f : pathDir.getFiles()) {
									dirNames.add(f.getFileName());
								}
								Collections.sort(dirNames);
								int nrN = dirNames.size();
								for (String s : dirNames) {
									nrN--;
									if (nrN > 0)
										writerOut.print(path + "/" + s + " ");
									else
										writerOut.println(path + "/" + s);
								}
								writerOut.println();
							} else {
								//path nu exista
								writerOut.println(cf.getCommandNr());
								writerErr.println(cf.getCommandNr());
								writerErr.println("ls: " + path + ": No such directory");
								ok = 0;
							}
						} else {
							// path e doar numele directorului
							Directory dirr = new Directory();
							Directory auxDir = new Directory();
							int found = 0;
							dirr = dirr.findRightPath(fs.getRoot(), fs.getCurrentDir(), dirr);
							for (Directory d : dirr.getDirectories()) {
								if (d.getDirName().equals(path)) {
									found = 1;
									auxDir = d;
								}
							}
							if (found == 1) {
								//daca directorul exista
								String[] dirPath = new String[30];
								String aux = path;
								String dirName = path;
								String newPath = "";
								int i = 0;
								writerOut.println(cf.getCommandNr());
								writerErr.println(cf.getCommandNr());
								while (aux.equals("/") == false) {
									// caut path ul directorului curent
									Directory dir = new Directory("name");
									dir = dir.findRightPath(fs.getRoot(), dirName, dir);
									dirPath[i] = dir.getDirName();
									dir = dir.getParentDir();
									aux = dir.getDirName();
									dirName = aux;
									i++;
								}
								i = i - 1;
								while (i != -1) {
									writerOut.print("/" + dirPath[i]);
									newPath = newPath + "/" + dirPath[i];
									i--;
									if (i == -1)
										writerOut.println(":");
								}
								// am scris path-ul acum scriem fisierele si folderele
								Directory curDir = new Directory();
								curDir = curDir.findRightPath(fs.getRoot(), path, curDir);
								if (curDir.getDirectories().isEmpty() && curDir.getFiles().isEmpty()) {
									writerOut.println();
									writerOut.println();
								} else {
									ArrayList<String> dirNames = new ArrayList<String>(30);
									for (Directory d : curDir.getDirectories()) {
										dirNames.add(d.getDirName());
									}
									for (Files f : curDir.getFiles()) {
										dirNames.add(f.getFileName());
									}
									Collections.sort(dirNames);
									int nr = dirNames.size();
									for (String s : dirNames) {
										nr--;
										if (nr > 0)
											writerOut.print(newPath + "/" + s + " ");
										else
											writerOut.println(newPath + "/" + s);
									}
									writerOut.println();
								}
							} else {
								// path-ul nu exista
								writerOut.println(cf.getCommandNr());
								writerErr.println(cf.getCommandNr());
								writerErr.println("ls: " + path + ": No such directory");
							}
						}
					} else {
						// ls -R cu path care nu e null
						recursive = 0;
						if (path.equals("/") || path.equals(".")) {
							try {
								writerOut = new PrintWriter(new FileWriter(out, true));
								writerErr = new PrintWriter(new FileWriter(errors, true));
								writerOut.println(cf.getCommandNr());
								writerErr.println(cf.getCommandNr());
								writerOut.close();
								writerErr.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							Directory dir = new Directory();
							//ls -R cand directorul curent e root
							dir = dir.findRightPath(fs.getRoot(), "/", dir);
							dir = fs.getRoot().findRightPath(fs.getRoot(), "/", dir);
							dir.listDirectories(dir, out, errors, "");
							recursive = 0;
						} else if (directory.length > 1) {
							/*directorul curent nu e root
							 * asa ca il caut
							 */
							int nr = fs.getRoot().getDirectories().size(), i = 1, ok = 0, j = 0;
							// j < i cu 1, j e pt directoare si i pt vectorul de nume
							while (nr != 0) {
								//verific daca directorul gasit e valid
								if (fs.getRoot().getDirectories().get(j).getDirName().equals(directory[i])) {
									ok = 1;
									break;
								}
								j++;
								nr--;
							}
							if (ok == 1) {
								try {
									writerOut = new PrintWriter(new FileWriter(out, true));
									writerErr = new PrintWriter(new FileWriter(errors, true));
									writerOut.println(cf.getCommandNr());
									writerErr.println(cf.getCommandNr());
									writerOut.close();
									writerErr.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
								Directory dir = new Directory();
								//ls -R pe directorul gasit
								dir = dir.findRightPath(fs.getRoot(), directory[directory.length - 1], dir);
								dir = fs.getRoot().findRightPath(fs.getRoot(), directory[directory.length - 1], dir);
								dir.listDirectories(dir, out, errors, "");
								recursive = 0;
							} else {
								// no such path
								writerOut.println(cf.getCommandNr());
								writerErr.println(cf.getCommandNr());
								writerErr.println("ls: " + path + ": No such directory");
								ok = 0;
							}
						} else {
							// path ul e doar numele directorului
							// ex: ls -R dirName
							try {
								writerOut = new PrintWriter(new FileWriter(out, true));
								writerErr = new PrintWriter(new FileWriter(errors, true));
								writerOut.println(cf.getCommandNr());
								writerErr.println(cf.getCommandNr());
								writerOut.close();
								writerErr.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							Directory dir = new Directory();
							Directory auxDir = new Directory();
							int found = 0;
							dir = dir.findRightPath(fs.getRoot(), fs.getCurrentDir(), dir);
							for (Directory d : dir.getDirectories()) {
								if (d.getDirName().equals(path)) {
									found = 1;
									auxDir = d;
								}
							}
							if (found == 1) {
								dir.listDirectories(auxDir, out, errors, "");
							} else {
								// path nu exista
								writerOut.println(cf.getCommandNr());
								writerErr.println(cf.getCommandNr());
								writerErr.println("ls: " + path + ": No such directory");
							}
						}
					}
				} else {
					// path contine . sau .. -> ls path sau ls -R path
					// ls path
					String destDir = fs.getCurrentDir();
					Directory auxDir = new Directory();
					Directory parentDir = new Directory();
					int invalidPath = 0;
					// pe pozitia directory.length - 1 se afla numele noului fisier
					for (int i = 0; i < directory.length; i++) {
						//caut directorul destinatie in functie de . sau ..
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
						//verific daca directorul destDir e valid
						if (destDir.equals("/") && fs.getCurrentDir().equals("/"))
							valid = 1; // path is ok
						valid = auxDir.findDirectory(fs.getRoot(), destDir, valid);
					}
					if (recursive == 0) {
						if (invalidPath == 0 && valid == 1) {
							// ls pe directorul destDir
							if (destDir.equals("/") || destDir.equals(".")) {
								writerOut.println(cf.getCommandNr());
								writerOut.println("/:");
								writerErr.println(cf.getCommandNr());
								// am scris path-ul acum scriem fisierele si folderele
								if (fs.getRoot().getDirectories().isEmpty() == true) {
									writerOut.println();
									writerOut.println();
								} else {
									ArrayList<String> dirNames = new ArrayList<String>(30);
									for (Directory d : fs.getRoot().getDirectories()) {
										dirNames.add(d.getDirName());
									}
									for (Files f : fs.getRoot().getFiles()) {
										dirNames.add(f.getFileName());
									}
									Collections.sort(dirNames);
									int nr = dirNames.size();
									for (String s : dirNames) {
										nr--;
										if (nr > 0)
											writerOut.print("/" + s + " ");
										else
											writerOut.println("/" + s);
									}
									writerOut.println();
								}
							} else {
								// ls pe un dir care nu e root
								String[] dirPath = new String[30];
								String aux = destDir;
								String dirName = destDir;
								String newPath = "";
								int i = 0;
								writerOut.println(cf.getCommandNr());
								writerErr.println(cf.getCommandNr());
								while (aux.equals("/") == false) {
									// caut path ul directorului curent
									Directory dir = new Directory();
									dir = dir.findRightPath(fs.getRoot(), dirName, dir);
									dirPath[i] = dir.getDirName();
									dir = dir.getParentDir();
									aux = dir.getDirName();
									dirName = aux;
									i++;
								}
								i = i - 1;
								while (i != -1) {
									writerOut.print("/" + dirPath[i]);
									newPath = newPath + "/" + dirPath[i];
									i--;
									if (i == -1)
										writerOut.println(":");
								}
								// am scris path-ul acum scriem fisierele si folderele
								Directory curDir = new Directory();
								curDir = curDir.findRightPath(fs.getRoot(), destDir, curDir);
								if (curDir.getDirectories().isEmpty() && curDir.getFiles().isEmpty()) {
									writerOut.println();
									writerOut.println();
								} else {
									ArrayList<String> dirNames = new ArrayList<String>(30);
									for (Directory d : curDir.getDirectories()) {
										dirNames.add(d.getDirName());
									}
									for (Files f : curDir.getFiles()) {
										dirNames.add(f.getFileName());
									}
									Collections.sort(dirNames);
									int nr = dirNames.size();
									for (String s : dirNames) {
										nr--;
										if (nr > 0)
											writerOut.print(newPath + "/" + s + " ");
										else
											writerOut.println(newPath + "/" + s);
									}
									writerOut.println();
								}

							}
						} else {
							// path invalid
							writerOut.println(cf.getCommandNr());
							writerErr.println(cf.getCommandNr());
							writerErr.println("ls: " + path + ": No such directory");
						}
					} else {
						// ls -R path
						recursive = 0;
						if (invalidPath == 0 && valid == 1) {
							if (destDir.equals("/") || destDir.equals(".")) {
								try {
									writerOut = new PrintWriter(new FileWriter(out, true));
									writerErr = new PrintWriter(new FileWriter(errors, true));
									writerOut.println(cf.getCommandNr());
									writerErr.println(cf.getCommandNr());
									writerOut.close();
									writerErr.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
								Directory dir = new Directory();
								dir = dir.findRightPath(fs.getRoot(), "/", dir);
								dir = fs.getRoot().findRightPath(fs.getRoot(), "/", dir);
								dir.listDirectories(dir, out, errors, "");
							} else {
								// path-ul nu e root
								try {
									writerOut = new PrintWriter(new FileWriter(out, true));
									writerErr = new PrintWriter(new FileWriter(errors, true));
									writerOut.println(cf.getCommandNr());
									writerErr.println(cf.getCommandNr());
									writerOut.close();
									writerErr.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
								Directory dir = new Directory();
								dir = dir.findRightPath(fs.getRoot(), destDir, dir);
								dir = fs.getRoot().findRightPath(fs.getRoot(), destDir, dir);
								dir.listDirectories(dir, out, errors, "");
							}
						} else {
							// path invalid
							writerOut.println(cf.getCommandNr());
							writerErr.println(cf.getCommandNr());
							writerErr.println("ls: " + path + ": No such directory");
						}
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		writerOut.close();
		writerErr.close();
	}

	public int getRecursive() {
		return recursive;
	}

	public void setRecursive(int recursive) {
		this.recursive = recursive;
	}

}
