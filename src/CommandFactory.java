import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Clasa CommandFactory implementeaza Design Pattern - urile Factory si
 * Singleton
 * 
 * Aici citesc comenzile din fisierul de input si instantiez comenzile
 * corespunzator
 * 
 * @author Maria Jianu
 *
 */
public class CommandFactory {
//Factory + Singleton design pattern
	private static CommandFactory uniqueInstance;
	private int commandNr = 0;

	private CommandFactory() {

	}

	public static CommandFactory getInstance() {
		// pentru Singleton
		if (uniqueInstance == null)
			uniqueInstance = new CommandFactory();
		return uniqueInstance;
	}

	/**
	 * Citesc comenzile din fisier si le instantiez conform Factory Design Pattern
	 * 
	 * @param inFile numele fisierului de input
	 */
	public void createCommand(String inFile) {
		// metoda factory pentru Factory design pattern
		Command commandInstance = null;
		Scanner reader = null; // reader pt fisier
		String command, path = null, dest;
		int recursive = 0; // indicator pentru ls -R
		String line = null;
		try {
			reader = new Scanner(new File(inFile));
			while (reader.hasNext() == true) {
				path = null;
				command = reader.next();
				commandNr++;
				if (command.equals("ls")) {
					/*
					 * comanda ls este de 5 feluri contorul recursive = 1 daca era -R path e null
					 * daca comanda nu avea path
					 */
					if (reader.hasNextLine())
						line = reader.nextLine();
					if (line != null && line.isEmpty() == false && !line.equals(" ")) {
						String[] parts = line.split("\\s+");
						// parts va avea 2 sau 3 elemente
						if (parts[1].equals("-R")) {
							recursive = 1;
						} else
							path = parts[1];
						if (parts.length > 2 && parts[2].equals("-R")) {
							recursive = 1;
						} else if (parts.length > 2)
							path = parts[2];
						LS ls = new LS();
						if (recursive == 0)
							ls.setRecursive(0);
						else
							ls.setRecursive(1);
						commandInstance = new LSCommand(ls, path);
						commandInstance.execute();
						recursive = 0;
						// apelez ls cu path != null
					} else {
						// instantiez un obiect LS
						LS ls = new LS(); // ls fara argumente
						path = null;
						commandInstance = new LSCommand(ls, path);
						// executa comanda ls
						commandInstance.execute();
					}
					path = null;
				} else if (command.equals("pwd")) {
					// instantiez un obiect PWD
					PWD pwd = new PWD();
					commandInstance = new PWDCommand(pwd);
					// executa pwd
					commandInstance.execute();

				} else if (command.equals("cd")) {
					CD cd = new CD();
					path = reader.next();
					commandInstance = new CDCommand(cd, path);
					commandInstance.execute();
				} else if (command.equals("cp")) {
					CP cp = new CP();
					path = reader.next();// source dir
					dest = reader.next();// dest dir
					commandInstance = new CPCommand(cp, path, dest);
					commandInstance.execute();

				} else if (command.equals("mv")) {
					MV mv = new MV();
					path = reader.next();// source dir
					dest = reader.next();// dest dir
					commandInstance = new MVCommand(mv, path, dest);
					commandInstance.execute();

				} else if (command.equals("rm")) {
					RM rm = new RM();
					path = reader.next();
					commandInstance = new RMCommand(rm, path);
					commandInstance.execute();

				} else if (command.equals("touch")) {
					TOUCH touch = new TOUCH();
					path = reader.next();
					commandInstance = new TOUCHCommand(touch, path);
					commandInstance.execute();

				} else if (command.equals("mkdir")) {
					MKDIR mkdir = new MKDIR();
					path = reader.next();
					commandInstance = new MKDIRCommand(mkdir, path);
					commandInstance.execute();
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (reader != null)
				reader.close();
		}

	}

	public int getCommandNr() {
		return commandNr;
	}

	public void setCommandNr(int commandNr) {
		this.commandNr = commandNr;
	}

}
