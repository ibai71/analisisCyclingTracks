import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.knowm.xchart.style.Styler;


public class FuncionalidadAuxiliar {
	
	/**
	 * Crea la estructura para almacenar la información de la inmersión
	 * @param pNumRegistros el número de registros que contiene
	 * @return la estructura que almacena la información completa de la inmersión
	 */
	public static InfoLogTrack createInfoLogTrack(int pNumRegistros) {
		InfoLogTrack trackLogData = new InfoLogTrack();
		trackLogData.tiempo = new int[pNumRegistros];
		trackLogData.longitud = new double[pNumRegistros];
		trackLogData.latitud = new double[pNumRegistros];
		trackLogData.altitud = new double[pNumRegistros];
		trackLogData.frecCardiaca = new int[pNumRegistros];
		return trackLogData;
	}
	
	/**
	 * Procesa un fichero que contiene el log de una actividad y devuelve una estructura
	 * con toda la información del track
	 * @param pFich el fichero que contiene el log de la actividad
	 * @return la estructura que contiene toda la información del track
	 */
	public static InfoLogTrack cargarInfoCSV(String pFich) {
		try (Reader reader = Files.newBufferedReader(Paths.get(pFich));
				CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader().withDelimiter(','))) {
			List<CSVRecord> records = csvParser.getRecords();
			int numRegistros = records.size();
			InfoLogTrack trackLogData = createInfoLogTrack(numRegistros);
			for (int i = 0; i < numRegistros; i++) {
				CSVRecord csvRecord = records.get(i);
				trackLogData.tiempo[i] = Integer.parseInt(csvRecord.get(0));
				trackLogData.longitud[i] = Float.parseFloat(csvRecord.get(1));
				trackLogData.latitud[i] = Float.parseFloat(csvRecord.get(2));
				trackLogData.altitud[i] = Float.parseFloat(csvRecord.get(3));
				trackLogData.frecCardiaca[i] = Integer.parseInt(csvRecord.get(4));
			}
			return trackLogData;

		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Devuelve el peso corporal del atleta indicado
	 * @param pId
	 * 		identificador del atleta ("e.g. Athelete2")
	 * @return el peso corporal del atleta
	 */
	public static double obtPesoAtleta(String pId) {
		switch (pId) {
			case "Athlete1":
				return 70.5;
			case "Athlete2":
				return 65.4;
			case "Athlete3":
				return 78.2;
			case "Athlete4":
				return 70.5;
			case "Athlete5":
				return 73.5;
			case "Athlete6":
				return 63.9;
			case "Athlete7":
				return 83.5;
			default:
				return 68.0;
		}
	}

	/**
	 * Genera la gráfica con los datos de la actividad especificada. 
	 * @param pDist array que contiene las distancias recorrida cada instance 
	 * @param pFC array que contiene la frecuencia cardíaca de cada instante  
	 * @param pElev array que contiene la altura de cada instante
	 * @param pPlotFileName el nombre del fichero en el que se quiere guardar la 
	 * imagen
	 * @param pShowPlot valor booleano que indica si se quiere mostrar la grafica 
	 * o no 
	 * */
	 public static void generarTrackPlot(double[] pDist, double[] pFC, double[] pElev, 
										  String pPlotFileName, boolean pShowPlot) {
		 
		// Labels
		String titulo = "Perfil del recorrido vs Frec. Cardiaca";
		String xLabel = "Distancia [km]";
		String yLabel1 = "Elevacion [m]";
		String yLabel2 = "FC";
			
		// Generar la gráfica
		XYChart chart = new XYChartBuilder()
				.title(titulo)
				.xAxisTitle(xLabel)
				.build();
		
	    chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSW);
		chart.getStyler().setYAxisGroupPosition(1, Styler.YAxisPosition.Right);
		chart.setYAxisGroupTitle(0, yLabel1);
		chart.setYAxisGroupTitle(1, yLabel2);
		
		// Plotear los datos del frecuencia cardiaca
		XYSeries pulso = chart.addSeries("Frec. Cardiaca", pDist, pFC);
		pulso.setYAxisGroup(1);
		pulso.setLineColor(Color.BLUE);
		pulso.setMarker(SeriesMarkers.NONE);
		
		// 
		// Plotear el perfil
		XYSeries perfil = chart.addSeries("Perfil del recorrido", pDist, pElev);
		perfil.setLineColor(Color.red);
		perfil.setMarker(SeriesMarkers.NONE);
		
		// Ajustar características de la gráfica
		chart.getStyler().setChartBackgroundColor(Color.WHITE);
		chart.getStyler().setAxisTickPadding(10);
		chart.getStyler().setPlotMargin(10);
		// Mostrar la gráfica si se ha indicado que se desea ver el plot
		if (pShowPlot) {
			 SwingWrapper<XYChart> sw = new SwingWrapper<XYChart>(chart);
			 sw.displayChart();
		}
		
		// Guardar el plot en el fichero indicado
		try {
			BitmapEncoder.saveBitmap(chart, pPlotFileName, BitmapFormat.PNG);
		} catch (IOException e) {
			System.err.println("No se ha podido guardar el gráfico en el fichero indicado");
		}
	}
	
	
	/**
	 * Elimina las imágenes generadas previamente de la carpeta GeneratedPlots
	 */
	public static void eliminarPlots() {
		String[] atletas = {"Athlete1", "Athlete2", "Athlete3", "Athlete4", 
				"Athlete5", "Athlete6", "Athlete7"};
		for (String atleta:atletas) {
			try {
				if (new File("GeneratedPlots/"+atleta).exists()) {
					FileUtils.cleanDirectory(new File("GeneratedPlots/"+atleta));
				}
			} catch (IOException e) {
				System.err.println("No se ha podido limpiar la carpeta GeneratedPlots");
			}
		}
	}
}