package pr01;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListaDeReproduccionTest {

	private ListaDeReproduccion lr1;
	private ListaDeReproduccion lr2;
	private final File FIC_TEST1 = new File( "test/res/No del grupo.mp4" ); 
	private final File F1 = new File( "test/res/fichero1.mp4" );
	
	@BeforeEach
	void setUp() throws Exception {
		lr1 = new ListaDeReproduccion();
		lr2 = new ListaDeReproduccion();
		lr2.add( FIC_TEST1 ); 
	}

	@AfterEach
	void tearDown() throws Exception {
		lr2.clear();
	}

	// Chequeo de error por getFic(índice) por encima de final
	@Test//(expected = IndexOutOfBoundsException.class)
	public void testGet_Exc1() {
		try {
			lr1.getFic(0); // Debe dar error porque aún no existe la posición 0
			fail("");
		} catch ( IndexOutOfBoundsException e) {}
	}

	// Chequeo de error por get(índice) por debajo de 0
	@Test//(expected = IndexOutOfBoundsException.class)
	public void testGet_Exc2() {
		try {
			lr2.getFic(-1); // Debe dar error porque aún no existe la posición -1
			fail("");
		} catch( IndexOutOfBoundsException e ) {}
	}

	// Chequeo de funcionamiento correcto de get(índice)
	@Test public void testGet() {
		assertEquals( FIC_TEST1, lr2.getFic(0) ); // El único dato es el fic-test1
	}
	
	// Chequeo de funcionamiento correcto del método intercambia()
	@Test
	public void testIntercambio() {
		lr1.add(FIC_TEST1);
		lr1.add(F1);
		lr1.intercambia(0, 1);
		assertEquals(lr1.getFic(0), F1);
		assertEquals(lr1.getFic(1), FIC_TEST1);
	}
	
	// Chequeo de funcionamiento correcto de los métodos add() y remove()
	@Test
	public void testAddRemove() {
		lr1.add( F1 );
		assertEquals(lr1.getFic(0), F1);
		lr1.removeFic(0);
		assertEquals(lr1.ficherosLista, new ArrayList<File>());
	}
	
	// Chequeo de funcionamiento correcto del método size()
	@Test
	public void testSize() {
		assertEquals( lr2.size(), 1);
		lr2.clear();
		assertEquals( lr2.size(), 0);
	}
	
	@Test 
	public void addCarpeta() {
		 String carpetaTest = "test/res/";
		 String filtroTest = "*Pentatonix*.mp4"; 
		 ListaDeReproduccion lr = new ListaDeReproduccion();
		 assertEquals(lr.add( carpetaTest, filtroTest ), 3);
		 carpetaTest = "test/res/Fichero Pentatonix no video.txt";
		 assertEquals(lr.add(carpetaTest, filtroTest),0);
	} 
	
	@Test
	public void testIrARandom() {
		String carpetaTest = "test/res/";
		 String filtroTest = "*Pentatonix*.mp4"; 
		 ListaDeReproduccion lr = new ListaDeReproduccion();
		 lr.add( carpetaTest, filtroTest );
		 assertTrue(lr.irARandom());
	}

}
