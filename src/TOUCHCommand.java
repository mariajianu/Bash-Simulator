/**
 * Clasa care implementeaza comanda TOUCH
 * 
 * In metoda executa se va apela metoda touch() 
 * a obiectului TOUCH instantiat in CommandFactory
 * 
 * @author Maria Jianu
 *
 */
public class TOUCHCommand extends Command {
	TOUCH command;
	String path;

	public TOUCHCommand(TOUCH t, String path) {
		command = t;
		this.path = path;
	}

	@Override
	public void execute() {
		String errFile = Main.getErrFile();
		String outFile = Main.getOutFile();
		//face un fisier la adresa path
		command.touch(path, outFile, errFile);
	}

}
