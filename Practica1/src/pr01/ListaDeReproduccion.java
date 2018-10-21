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


/** Clase para crear instancias como listas de reproducción,
 * que permite almacenar listas de ficheros con posición de índice
 * (al estilo de un array / arraylist)
 * con marcas de error en los ficheros y con métodos para cambiar la posición
 * de los elementos en la lista, borrar elementos y añadir nuevos.
 */
public class ListaDeReproduccion implements ListModel<String> {
	
	private static Logger logger = Logger.getLogger(ListaDeReproduccion.class.getName());
	private static final boolean ANYADIR_A_FIC_LOG = false; // poner true para no sobreescribir
	static {
		try {
			logger.addHandler( new FileHandler(
					ListaDeReproduccion.class.getName()+".log.xml", ANYADIR_A_FIC_LOG ));
		} catch (SecurityException | IOException e) {
			logger.log( Level.SEVERE, "Error en creación fichero log" );
		}
	}
	
	ArrayList<File> ficherosLista;     // ficheros de la lista de reproducción
	int ficheroEnCurso = -1;           // Fichero seleccionado (-1 si no hay ninguno seleccionado)
	
	/** Constructor vacío que devuelve la lista de reproducción vacía
	 * @return Devuelve un objeto ListaDeReproduccion con una lista vacía
	 */
	public ListaDeReproduccion() {
		ficherosLista = new ArrayList<>();
	}
	
	/** Intercambia dos elementos de la lista de ficheros, si alguno de los 
	 * parámetros es erróneo no hace nada
	 * @param posi1 Posición de uno de los dos elementos a intercambiar ( 0 <= posi1 < size() )
	 * @param posi2 Posición de uno de los dos elementos a intercambiar ( 0 <= posi2 < size() )
	 */
	public void intercambia( int posi1, int posi2 ) {
		if (posi1 >= 0 && posi1 < ficherosLista.size() &&
				posi2 >= 0 && posi2 < ficherosLista.size()) {
			File tmp = getFic(posi1);
			ficherosLista.set(posi1, getFic(posi2));
			ficherosLista.set(posi2, tmp);
		}
	}
	
	/** Devuelve el número de elementos de la lista de ficheros
	 * @return Devuelve el número de elementos de la lista
	 */
	public int size() {
		return ficherosLista.size();
	}
	
	/** Añade un nuevo fichero al final de la lista
	 * @param f Fichero que se quiere añadir (de tipo File)
	 * */
	public void add( File f) {
		ficherosLista.add(f);
	}
	
	/**Elimina el elemento de una posición dada de la lista de ficheros
	 * @param posi Posición del elemento que se quiere eliminar
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
	 * @param posi	Posición del fichero en la lista (de 0 a size()-1)
	 * @return	Devuelve el fichero en esa posición
	 * @throws IndexOutOfBoundsException	Si el índice no es válido
	 */
	public File getFic( int posi ) throws IndexOutOfBoundsException {
		return ficherosLista.get( posi );
	}	

	/** Añade a la lista de reproducción todos los ficheros que haya en la 
	 * carpeta indicada, que cumplan el filtro indicado.
	 * Si hay cualquier error, la lista de reproducción queda solo con los ficheros
	 * que hayan podido ser cargados de forma correcta.
	 * @param carpetaFicheros	Path de la carpeta donde buscar los ficheros
	 * @param filtroFicheros	Filtro del formato que tienen que tener los nombres de
	 * 							los ficheros para ser cargados.
	 * 							String con cualquier letra o dígito. Si tiene un asterisco
	 * 							hace referencia a cualquier conjunto de letras o dígitos.
	 * 							Por ejemplo p*.* hace referencia a cualquier fichero de nombre
	 * 							que empiece por p y tenga cualquier extensión.
	 * @return	Número de ficheros que han sido añadidos a la lista
	 */
	public int add(String carpetaFicheros, String filtroFicheros) {
		// TODO: Codificar este método de acuerdo a la práctica (pasos 3 y sucesivos)
		logger.log( Level.INFO, "Añadiendo ficheros con filtro " + filtroFicheros ); 
		filtroFicheros = filtrar(filtroFicheros);
		logger.log( Level.INFO, "Añadiendo ficheros con filtro " + filtroFicheros ); 
		File fInic = new File(carpetaFicheros);
		if (fInic.isDirectory()) {
			Pattern pattern = Pattern.compile(filtroFicheros);
			int ficherosAnyadidos = 0;
			for( File f : fInic.listFiles() ) {
				logger.log( Level.FINE, "Procesando fichero " + f.getName() );
				// TODO: Comprobar que f.getName() cumple el patrón y añadirlo a la lista
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
		f = f.replaceAll( "\\.", "\\\\." );  // Pone el símbolo de la expresión regular \. donde figure un .
		f = f.replaceAll("\\*", "\\.*");
		return f;
	}
	
	
	//
	// Métodos de selección
	//
	
	/** Seleciona el primer fichero de la lista de reproducción
	 * @return	true si la selección es correcta, false si hay error y no se puede seleccionar
	 */
	public boolean irAPrimero() {
		ficheroEnCurso = 0;  // Inicia
		if (ficheroEnCurso>=ficherosLista.size()) {
			ficheroEnCurso = -1;  // Si no se encuentra, no hay selección
			return false;  // Y devuelve error
		}
		return true;
	}
	
	/** Seleciona el último fichero de la lista de reproducción
	 * @return	true si la selección es correcta, false si hay error y no se puede seleccionar
	 */
	public boolean irAUltimo() {
		ficheroEnCurso = ficherosLista.size()-1;  // Inicia al final
		if (ficheroEnCurso==-1) {  // Si no se encuentra, no hay selección
			return false;  // Y devuelve error
		}
		return true;
	}

	/** Seleciona el anterior fichero de la lista de reproducción
	 * @return	true si la selección es correcta, false si hay error y no se puede seleccionar
	 */
	public boolean irAAnterior() {
		if (ficheroEnCurso>=0) ficheroEnCurso--;
		if (ficheroEnCurso==-1) {  // Si no se encuentra, no hay selección
			return false;  // Y devuelve error
		}
		return true;
	}

	/** Seleciona el siguiente fichero de la lista de reproducción
	 * @return	true si la selección es correcta, false si hay error y no se puede seleccionar
	 */
	public boolean irASiguiente() {
		ficheroEnCurso++;
		if (ficheroEnCurso>=ficherosLista.size()) {
			ficheroEnCurso = -1;  // Si no se encuentra, no hay selección
			return false;  // Y devuelve error
		}
		return true;
	}
	
	/** Selecciona un fichero aleatorio de la lista de reproducción.
	 * @return true si la selección es correcta, false si hay error y no se puede seleccionar
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
	 * @return	Posición del fichero seleccionado en la lista de reproducción (0 a n-1), -1 si no lo hay
	 */
	public int getFicSeleccionado() {
		return ficheroEnCurso;
	}

	//
	// Métodos de DefaultListModel
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
	
	// Llamar a este método cuando se añada un elemento a la lista
	// (Utilizado para avisar a los escuchadores de cambio de datos de la lista)
	private void avisarAnyadido( int posi ) {
		for (ListDataListener ldl : misEscuchadores) {
			ldl.intervalAdded( new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, posi, posi ));
		}
	}
}