package pr01;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


/** Clase para crear instancias como listas de reproducci�n,
 * que permite almacenar listas de ficheros con posici�n de �ndice
 * (al estilo de un array / arraylist)
 * con marcas de error en los ficheros y con m�todos para cambiar la posici�n
 * de los elementos en la lista, borrar elementos y a�adir nuevos.
 */
public class ListaDeReproduccion implements ListModel<String> {
	
	private static Logger logger = Logger.getLogger(ListaDeReproduccion.class.getName());
	private static final boolean ANYADIR_A_FIC_LOG = false; // poner true para no sobreescribir
	static {
		try {
			logger.addHandler( new FileHandler(
					ListaDeReproduccion.class.getName()+".log.xml", ANYADIR_A_FIC_LOG ));
		} catch (SecurityException | IOException e) {
			logger.log( Level.SEVERE, "Error en creaci�n fichero log" );
		}
	}
	
	ArrayList<File> ficherosLista;     // ficheros de la lista de reproducci�n
	int ficheroEnCurso = -1;           // Fichero seleccionado (-1 si no hay ninguno seleccionado)
	
	/** Constructor vac�o que devuelve la lista de reproducci�n vac�a
	 * @return Devuelve un objeto ListaDeReproduccion con una lista vac�a
	 */
	public ListaDeReproduccion() {
		ficherosLista = new ArrayList<>();
	}
	
	/** Intercambia dos elementos de la lista de ficheros, si alguno de los 
	 * par�metros es err�neo no hace nada
	 * @param posi1 Posici�n de uno de los dos elementos a intercambiar ( 0 <= posi1 < size() )
	 * @param posi2 Posici�n de uno de los dos elementos a intercambiar ( 0 <= posi2 < size() )
	 */
	public void intercambia( int posi1, int posi2 ) {
		if (posi1 >= 0 && posi1 < ficherosLista.size() &&
				posi2 >= 0 && posi2 < ficherosLista.size()) {
			File tmp = getFic(posi1);
			ficherosLista.set(posi1, getFic(posi2));
			ficherosLista.set(posi2, tmp);
		}
	}
	
	/** Devuelve el n�mero de elementos de la lista de ficheros
	 * @return Devuelve el n�mero de elementos de la lista
	 */
	public int size() {
		return ficherosLista.size();
	}
	
	/** A�ade un nuevo fichero al final de la lista
	 * @param f Fichero que se quiere a�adir (de tipo File)
	 * */
	public void add( File f) {
		ficherosLista.add(f);
	}
	
	/**Elimina el elemento de una posici�n dada de la lista de ficheros
	 * @param posi Posici�n del elemento que se quiere eliminar
	 * */
	public void removeFic( int posi ) {
		if (posi >= 0 && posi < size()) {
			ficherosLista.remove(posi);
		}
	}
	
	/** Borra todos los elementos de la lista
	 */
	public void clear() {
		ficherosLista.clear();
	}
	
	/** Devuelve uno de los ficheros de la lista
	 * @param posi	Posici�n del fichero en la lista (de 0 a size()-1)
	 * @return	Devuelve el fichero en esa posici�n
	 * @throws IndexOutOfBoundsException	Si el �ndice no es v�lido
	 */
	public File getFic( int posi ) throws IndexOutOfBoundsException {
		return ficherosLista.get( posi );
	}	

	/** A�ade a la lista de reproducci�n todos los ficheros que haya en la 
	 * carpeta indicada, que cumplan el filtro indicado.
	 * Si hay cualquier error, la lista de reproducci�n queda solo con los ficheros
	 * que hayan podido ser cargados de forma correcta.
	 * @param carpetaFicheros	Path de la carpeta donde buscar los ficheros
	 * @param filtroFicheros	Filtro del formato que tienen que tener los nombres de
	 * 							los ficheros para ser cargados.
	 * 							String con cualquier letra o d�gito. Si tiene un asterisco
	 * 							hace referencia a cualquier conjunto de letras o d�gitos.
	 * 							Por ejemplo p*.* hace referencia a cualquier fichero de nombre
	 * 							que empiece por p y tenga cualquier extensi�n.
	 * @return	N�mero de ficheros que han sido a�adidos a la lista
	 */
	public int add(String carpetaFicheros, String filtroFicheros) {
		// TODO: Codificar este m�todo de acuerdo a la pr�ctica (pasos 3 y sucesivos)
		logger.log( Level.INFO, "A�adiendo ficheros con filtro " + filtroFicheros ); 
		filtroFicheros = filtrar(filtroFicheros);
		logger.log( Level.INFO, "A�adiendo ficheros con filtro " + filtroFicheros ); 
		File fInic = new File(carpetaFicheros);
		if (fInic.isDirectory()) {
			Pattern pattern = Pattern.compile(filtroFicheros);
			int ficherosAnyadidos = 0;
			for( File f : fInic.listFiles() ) {
				logger.log( Level.FINE, "Procesando fichero " + f.getName() );
				// TODO: Comprobar que f.getName() cumple el patr�n y a�adirlo a la lista
				Matcher matcher = pattern.matcher(f.getName());
				if (matcher.matches()) {
					ficherosLista.add(f);
					ficherosAnyadidos++;
				}
			}
			return ficherosAnyadidos;
		} else {
			return 0;
		}
	} 
	
	private String filtrar(String filtroFicheros) {
		String f = new String(filtroFicheros);
		f = f.replaceAll( "\\.", "\\\\." );  // Pone el s�mbolo de la expresi�n regular \. donde figure un .
		f = f.replaceAll("\\*", "\\.*");
		return f;
	}
	
	
	//
	// M�todos de selecci�n
	//
	
	/** Seleciona el primer fichero de la lista de reproducci�n
	 * @return	true si la selecci�n es correcta, false si hay error y no se puede seleccionar
	 */
	public boolean irAPrimero() {
		ficheroEnCurso = 0;  // Inicia
		if (ficheroEnCurso>=ficherosLista.size()) {
			ficheroEnCurso = -1;  // Si no se encuentra, no hay selecci�n
			return false;  // Y devuelve error
		}
		return true;
	}
	
	/** Seleciona el �ltimo fichero de la lista de reproducci�n
	 * @return	true si la selecci�n es correcta, false si hay error y no se puede seleccionar
	 */
	public boolean irAUltimo() {
		ficheroEnCurso = ficherosLista.size()-1;  // Inicia al final
		if (ficheroEnCurso==-1) {  // Si no se encuentra, no hay selecci�n
			return false;  // Y devuelve error
		}
		return true;
	}

	/** Seleciona el anterior fichero de la lista de reproducci�n
	 * @return	true si la selecci�n es correcta, false si hay error y no se puede seleccionar
	 */
	public boolean irAAnterior() {
		if (ficheroEnCurso>=0) ficheroEnCurso--;
		if (ficheroEnCurso==-1) {  // Si no se encuentra, no hay selecci�n
			return false;  // Y devuelve error
		}
		return true;
	}

	/** Seleciona el siguiente fichero de la lista de reproducci�n
	 * @return	true si la selecci�n es correcta, false si hay error y no se puede seleccionar
	 */
	public boolean irASiguiente() {
		ficheroEnCurso++;
		if (ficheroEnCurso>=ficherosLista.size()) {
			ficheroEnCurso = -1;  // Si no se encuentra, no hay selecci�n
			return false;  // Y devuelve error
		}
		return true;
	}
	
	/** Selecciona un fichero aleatorio de la lista de reproducci�n.
	 * @return true si la selecci�n es correcta, false si hay error y no se puede seleccionar
	 */
	 public boolean irARandom() {
		 if (ficherosLista.isEmpty()) return false;
		 ficheroEnCurso = (int) (Math.random()*ficherosLista.size());
		 return true;
	 }
	 
	 public boolean irA(int p) {
		 if(ficherosLista.isEmpty() || p < 0 || p >= ficherosLista.size()) return false;
		 ficheroEnCurso = p;
		 return true;
	 }

	/** Devuelve el fichero seleccionado de la lista
	 * @return	Posici�n del fichero seleccionado en la lista de reproducci�n (0 a n-1), -1 si no lo hay
	 */
	public int getFicSeleccionado() {
		return ficheroEnCurso;
	}

	//
	// M�todos de DefaultListModel
	//
	
	@Override
	public int getSize() {
		return ficherosLista.size();
	}

	@Override
	public String getElementAt(int index) {
		return ficherosLista.get(index).getName();
	}

		// Escuchadores de datos de la lista
		ArrayList<ListDataListener> misEscuchadores = new ArrayList<>();
	@Override
	public void addListDataListener(ListDataListener l) {
		misEscuchadores.add( l );
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		misEscuchadores.remove( l );
	}
	
	// Llamar a este m�todo cuando se a�ada un elemento a la lista
	// (Utilizado para avisar a los escuchadores de cambio de datos de la lista)
	private void avisarAnyadido( int posi ) {
		for (ListDataListener ldl : misEscuchadores) {
			ldl.intervalAdded( new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, posi, posi ));
		}
	}
}