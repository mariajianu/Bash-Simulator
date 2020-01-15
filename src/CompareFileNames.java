import java.util.Comparator;

/**
 * @author Maria Jianu 321CB
 *
 */
public class CompareFileNames implements Comparator<Files> {
	public int compare(Files d1, Files d2) {
		// compara doua obiecte Files dupa numele lor
		// folosesc aceasta metoda cu functia de sortare deja imprementata sort
		return d1.getFileName().compareTo(d2.getFileName());
	}
}