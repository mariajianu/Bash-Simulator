import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
/**
 * @author Maria Jianu
 * 
 * Clasa PWD contine metoda pwd() care va printa in fisierul de output
 * path-ul directorului curent
 */
public class PWD {

	public void pwd(FileSystem fs, String out, String err) {
		PrintWriter writerOut = null;
		PrintWriter writerErr = null;
		CommandFactory cf = CommandFactory.getInstance();
		try {
			writerOut = new PrintWriter(new FileWriter(out, true));
			writerErr = new PrintWriter(new FileWriter(err, true));
			if (fs.getCurrentDir().equals("/")) {
				//directorul curent e root
				writerOut.println(cf.getCommandNr());
				writerOut.println("/");
				writerErr.println(cf.getCommandNr());
			} else {
				//nu e root
				writerOut.println(cf.getCommandNr());
				writerErr.println(cf.getCommandNr());
				String[] path = new String[30];
				String aux = fs.getCurrentDir();
				String dirName = fs.getCurrentDir();
				int i = 0;
				while (aux.equals("/") == false) {
					// o metoda recursiva care intoarce o referinta la director-ul curent
					// ca sa pot "urca" pana la root
					Directory dir = new Directory();
					dir = dir.findRightPath(fs.getRoot(), dirName, dir);
					//in vectorul path pun numele directoarelor ca sa formez path-ul
					path[i] = dir.getDirName();
					dir = dir.getParentDir();
					aux = dir.getDirName();
					dirName = aux;
					i++;
				}
				i = i - 1;
				while (i != -1) {
					writerOut.print("/" + path[i]);
					i--;
					if (i == -1)
						writerOut.println();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		writerOut.close();
		writerErr.close();
	}
}
