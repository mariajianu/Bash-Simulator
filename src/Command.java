
/**
 * Clasa abstracta Command cu metoda execute()
 * 
 * Metoda va fi implementata diferit in clasele care
 * extind clasa Command
 * 
 * @author Maria Jianu
 *
 */
public abstract class Command {
	/*Command design pattern
	 * Metoda execute() va executa comanda
	 * ceruta de obiectul instantiat 
	 */
	public abstract void execute();
}
