package pr01;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.windows.Win32FullScreenStrategy;

/** Ventana principal de reproductor de v�deo
 * Utiliza la librer�a VLCj que debe estar instalada y configurada
 *     (http://www.capricasoftware.co.uk/projects/vlcj/index.html)
 * @author Andoni Egu�luz Mor�n
 * Facultad de Ingenier�a - Universidad de Deusto
 */
public class VideoPlayer extends JFrame {
	private static final long serialVersionUID = 1L;
	
	// Varible de ventana principal de la clase
	private static VideoPlayer miVentana;

	// Atributo de VLCj
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	// Atributos manipulables de swing
	private JList<String> lCanciones = null;  // Lista vertical de v�deos del player
	private JProgressBar pbVideo = null;      // Barra de progreso del v�deo en curso
	private JCheckBox cbAleatorio = null;     // Checkbox de reproducci�n aleatoria
	private JLabel lMensaje = null;           // Label para mensaje de reproducci�n
	// Datos asociados a la ventana
	private ListaDeReproduccion listaRepVideos;  // Modelo para la lista de v�deos

	public VideoPlayer() {
		// Creaci�n de datos asociados a la ventana (lista de reproducci�n)
		listaRepVideos = new ListaDeReproduccion();
		
		// Creaci�n de componentes/contenedores de swing
		lCanciones = new JList<String>( listaRepVideos );
		pbVideo = new JProgressBar( 0, 10000 );
		cbAleatorio = new JCheckBox("Rep. aleatoria");
		lMensaje = new JLabel( "" );
		JPanel pBotonera = new JPanel();
		JButton bAnyadir = new JButton( new ImageIcon( VideoPlayer.class.getResource("img/Button Add.png")) );
		JButton bAtras = new JButton( new ImageIcon( VideoPlayer.class.getResource("img/Button Rewind.png")) );
		JButton bPausaPlay = new JButton( new ImageIcon( VideoPlayer.class.getResource("img/Button Play Pause.png")) );
		JButton bAdelante = new JButton( new ImageIcon( VideoPlayer.class.getResource("img/Button Fast Forward.png")) );
		JButton bMaximizar = new JButton( new ImageIcon( VideoPlayer.class.getResource("img/Button Maximize.png")) );
		
		// Componente de VCLj
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent() {
			private static final long serialVersionUID = 1L;
			@Override
            protected FullScreenStrategy onGetFullScreenStrategy() {
                return new Win32FullScreenStrategy(VideoPlayer.this);
            }
        };

		// Configuraci�n de componentes/contenedores
		setTitle("Video Player - Deusto Ingenier�a");
		setLocationRelativeTo( null );  // Centra la ventana en la pantalla
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		setSize( 800, 600 );
		lCanciones.setPreferredSize( new Dimension( 200,  500 ) );
		pBotonera.setLayout( new FlowLayout( FlowLayout.LEFT ));
		
		// Enlace de componentes y contenedores
		pBotonera.add( bAnyadir );
		pBotonera.add( bAtras );
		pBotonera.add( bPausaPlay );
		pBotonera.add( bAdelante );
		pBotonera.add( bMaximizar );
		pBotonera.add( cbAleatorio );
		pBotonera.add( lMensaje );
		getContentPane().add( mediaPlayerComponent, BorderLayout.CENTER );
		getContentPane().add( pBotonera, BorderLayout.NORTH );
		getContentPane().add( pbVideo, BorderLayout.SOUTH );
		getContentPane().add( new JScrollPane( lCanciones ), BorderLayout.WEST );
		
		// Escuchadores
		bAnyadir.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File fPath = pedirCarpeta();
				if (fPath==null) return;
				path = fPath.getAbsolutePath();
				ficheros = JOptionPane.showInputDialog(null, "Indica el filtro", "Selecci�n de archivos", JOptionPane.INFORMATION_MESSAGE);
				listaRepVideos.add( path, ficheros );
				lCanciones.setModel(new DefaultListModel<>());
				lCanciones.setModel(listaRepVideos);
				lCanciones.repaint();
			}
		});
		bAtras.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				paraVideo();
				if(cbAleatorio.isSelected()) {
					listaRepVideos.irARandom();
				} else {
					listaRepVideos.irAAnterior();
				}
				lanzaVideo();
			}
		});
		bAdelante.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				paraVideo();
				if(cbAleatorio.isSelected()) {
					listaRepVideos.irARandom();
				} else {
					listaRepVideos.irASiguiente();
				}
				lanzaVideo();
			}
		});
		bPausaPlay.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (mediaPlayerComponent.getMediaPlayer().isPlayable()) {
					if (mediaPlayerComponent.getMediaPlayer().isPlaying()) {
						// TODO: hacer pausa
						mediaPlayerComponent.getMediaPlayer().pause();
					} else {
						// TODO: hacer play
						mediaPlayerComponent.getMediaPlayer().play();
					}
				} else {
					lanzaVideo();
				}
			}
		});
		bMaximizar.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (mediaPlayerComponent.getMediaPlayer().isFullScreen())
			        mediaPlayerComponent.getMediaPlayer().setFullScreen(false);
				else
					mediaPlayerComponent.getMediaPlayer().setFullScreen(true);
			}
		});
		addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				mediaPlayerComponent.getMediaPlayer().stop();
				mediaPlayerComponent.getMediaPlayer().release();
			}
		});
		mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener( 
			new MediaPlayerEventAdapter() {
				@Override
				public void finished(MediaPlayer mediaPlayer) {
					if(cbAleatorio.isSelected()) {
						listaRepVideos.irARandom();
					} else {
						listaRepVideos.irASiguiente();						
					}
					lanzaVideo();
				}
				@Override
				public void error(MediaPlayer mediaPlayer) {
					if(cbAleatorio.isSelected()) {
						listaRepVideos.irARandom();
					} else {
						listaRepVideos.irASiguiente();						
					}
					lanzaVideo();
					lCanciones.repaint();
				}
			    @Override
			    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
					pbVideo.setValue( (int) (10000.0 * 
							mediaPlayerComponent.getMediaPlayer().getTime() /
							mediaPlayerComponent.getMediaPlayer().getLength()) );
					pbVideo.repaint();
			    }
		});
		lCanciones.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) { // doble click
					int indice = lCanciones.locationToIndex(e.getPoint());
					listaRepVideos.irA(indice);
					lanzaVideo();
				}
			}
		});
		pbVideo.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int ancho =  pbVideo.getWidth();
				double porcent = (double) x/ancho;
				long segundo = (long) (porcent*mediaPlayerComponent.getMediaPlayer().getLength());
				mediaPlayerComponent.getMediaPlayer().setTime(segundo);
			}
		});
	}

	//
	// M�todos sobre el player de v�deo
	//
	
	// Para la reproducci�n del v�deo en curso
	private void paraVideo() {
		if (mediaPlayerComponent.getMediaPlayer()!=null)
			mediaPlayerComponent.getMediaPlayer().stop();
	}

	// Empieza a reproducir el v�deo en curso de la lista de reproducci�n
	private void lanzaVideo() {
		if (mediaPlayerComponent.getMediaPlayer()!=null &&
			listaRepVideos.getFicSeleccionado()!=-1) {
			File ficVideo = listaRepVideos.getFic(listaRepVideos.getFicSeleccionado());
			mediaPlayerComponent.getMediaPlayer().playMedia( 
				ficVideo.getAbsolutePath() );
			lCanciones.setSelectedIndex( listaRepVideos.getFicSeleccionado() );
			
			// Mostrar la fecha del v�deo
			long f = listaRepVideos.getFic(listaRepVideos.getFicSeleccionado()).lastModified();
			Date fecha = new Date(f);
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
			String formateado = df.format(fecha);
			lMensaje.setText("Fecha fichero: "+formateado);
		} else {
			lCanciones.setSelectedIndices( new int[] {} );
		}
	}
	
	// Pide interactivamente una carpeta para coger v�deos
	// (null si no se selecciona)
	private static File pedirCarpeta() {
		JFileChooser jfc = new JFileChooser();
		jfc.setMultiSelectionEnabled(false); // Solo se podr� elegir una carpeta a cada vez
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		
		int returnValue = jfc.showOpenDialog(null);
		if(returnValue == JFileChooser.APPROVE_OPTION) {
			return jfc.getSelectedFile();
		} else { 
			return null;
		}
	}

		private static String ficheros;
		private static String path;
	/** Ejecuta una ventana de VideoPlayer.
	 * El path de VLC debe estar en la variable de entorno "vlc".
	 * Comprobar que la versi�n de 32/64 bits de Java y de VLC es compatible.
	 * @param args	Un array de dos strings. El primero es el nombre (con comodines) de los ficheros,
	 * 				el segundo el path donde encontrarlos.  Si no se suministran, se piden de forma interactiva. 
	 */
	public static void main(String[] args) {
		// Para probar carga interactiva descomentar o comentar la l�nea siguiente:
		args = new String[] { "*Pentatonix*.mp4", "test/res/" };
		if (args.length < 2) {
			// No hay argumentos: selecci�n manual
			File fPath = pedirCarpeta();
			if (fPath==null) return;
			path = fPath.getAbsolutePath();
			
			String filtro = JOptionPane.showInputDialog(null, "Indica el filtro", "Selecci�n de archivos", JOptionPane.INFORMATION_MESSAGE);
			if(filtro != null) {
				ficheros = filtro;
			}
		} else {
			ficheros = args[0];
			path = args[1];
		}
		
		// Inicializar VLC.
		// Probar con el buscador nativo...
		boolean found = new NativeDiscovery().discover();
    	// System.out.println( LibVlc.INSTANCE.libvlc_get_version() );  // Visualiza versi�n de VLC encontrada
    	// Si no se encuentra probar otras opciones:
    	if (!found) {
			// Buscar vlc como variable de entorno
			String vlcPath = System.getenv().get( "vlc" );
			if (vlcPath==null) {  // Poner VLC a mano
	        	System.setProperty("jna.library.path", "c:\\Program Files\\videoLan\\VLC");
			} else {  // Poner VLC desde la variable de entorno
				System.setProperty( "jna.library.path", vlcPath );
			}
		}
    	
    	// Lanzar ventana
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				miVentana = new VideoPlayer();
				// Descomentar estas dos l�neas para ver un v�deo de ejemplo
				// miVentana.listaRepVideos.ficherosLista = new ArrayList<File>();
				// miVentana.listaRepVideos.ficherosLista.add( new File("test/res/[Official Video] Daft Punk - Pentatonix.mp4") );				
				miVentana.setVisible( true );
				miVentana.listaRepVideos.add( path, ficheros );
				miVentana.listaRepVideos.irAPrimero();
				miVentana.lanzaVideo();
			}
		});
	}
	
}