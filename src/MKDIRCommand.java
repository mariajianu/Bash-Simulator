/**
 * Clasa care implementeaza comanda MKDIR
 * 
 * In metoda executa se va apela metoda mkdir() 
 * a obiectului MKDIR instantiat in CommandFactory
 * 
 * @author Maria Jianu
 *
 */
public class MKDIRCommand extends Command{
	MKDIR command;
	String path;

	public MKDIRCommand(MKDIR t, String path) {
		command = t;
		this.path = path;
	}

	@Override
	public void execute() {
		String errFile = Main.getErrFile();
		String outFile = Main.getOutFile();
		//metoda care face un nou director la path
		command.mkdir(path, outFile, errFile);
	}
}
