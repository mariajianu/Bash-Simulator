/**
 * Clasa care implementeaza comanda PWD
 * 
 * In metoda executa se va apela metoda pwd() 
 * a obiectului PWD instantiat in CommandFactory
 * 
 * @author Maria Jianu
 *
 */
public class PWDCommand extends Command {
	
	PWD command;
	
	public PWDCommand(PWD pwd) {
		command = pwd;
	}
	
	public void execute() {
		FileSystem fs = null; 
		fs = Main.getFileSys();
		String outFile = Main.getOutFile();
		String errFile = Main.getErrFile();
		//metoda care printeaza path-ul directorului curent
		command.pwd(fs, outFile, errFile);
	}
}
