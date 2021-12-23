
public class AnalisisLogTrack {
	public final static double RADIO_TIERRA_KM = 6371;
	
	
	/**
	 * Obtiene las estadísticas básicas de la actividad realizada
	 * 
	 * @param pInfo
	 *            información de la actividad (track) del atleta
	 * @return las estadísticas basicas de duracion, velocidad media y distancia total de la actividad
	 */
	public static EstadisticasBasicas obtEstadisticasBasicas(InfoLogTrack pInfo) {
		//LEXICO
		
		EstadisticasBasicas miEstadisticasBasicas = new EstadisticasBasicas();
				
		//ALGORITMO PRINCIPAL
		
		miEstadisticasBasicas.duracion = pInfo.tiempo[pInfo.tiempo.length -1];
		miEstadisticasBasicas.fCMedia =  obtFrecCardiaca(pInfo);
		miEstadisticasBasicas.distancia = distanciaRecorrida(pInfo);
		miEstadisticasBasicas.velocidad = CalcularVelocidadMedia(pInfo); 
	return miEstadisticasBasicas;
		
	}
	public static double obtFrecCardiaca (InfoLogTrack pInfo) {
		double frecCardiacaTotal;
		int i;
		double numRegistros;
		numRegistros = pInfo.tiempo.length;
			
		frecCardiacaTotal = 0;
		for(i=0; i<numRegistros -1;i++) {
			frecCardiacaTotal = frecCardiacaTotal  + pInfo.frecCardiaca[i];
		}
		frecCardiacaTotal = frecCardiacaTotal / numRegistros;
	return frecCardiacaTotal ;
	}

	// Calcula la distancia total recorrida
	public static double distanciaRecorrida(InfoLogTrack pInfo) {
		int i,numRegistros;
		double dist, distanciaTotal, lat1, lat2, long1, long2;
		numRegistros = pInfo.tiempo.length;
		distanciaTotal = 0;
		
			for(i=0; i<numRegistros -2;i++) {
				lat1 = Math.toRadians(pInfo.latitud[i]);
				lat2 = Math.toRadians(pInfo.latitud[i +1]);
				long1 = Math.toRadians(pInfo.longitud[i]);
				long2 = Math.toRadians(pInfo.longitud[i +1]);
				dist = obtenerDistancia(lat1, lat2, long1, long2);
				distanciaTotal = distanciaTotal + dist;		
			}
	return distanciaTotal;	
	}
	
	//Calcula la distancia entre dos puntos
	public static double obtenerDistancia(double pLat1, double pLat2, double pLong1, double pLong2) {
		double diLat, diLong, a, x ;
		
		diLat = pLat2 - pLat1;
		diLong = pLong2 - pLong1;
		a = Math.pow(Math.sin(diLat/2), 2) + (Math.cos(pLat1) * Math.cos(pLat2)) * Math.pow(Math.sin(diLong/2), 2);
        x = 2 * RADIO_TIERRA_KM * Math.atan2(Math.pow(a,0.5), Math.pow(1-a, 0.5));
        return x;
	}
	
	//Calcula la velocidad media
	public static double CalcularVelocidadMedia (InfoLogTrack pInfo) {
		double lat1, lat2, long1, long2, dist, x, tiempoTotal;
		int i, numRegistros, tiempo1, tiempo2;
		numRegistros = pInfo.tiempo.length;
		x = 0;
		for(i=0; i<numRegistros -2;i++) {
			lat1 = Math.toRadians(pInfo.latitud[i]);
			lat2 = Math.toRadians(pInfo.latitud[i +1]);
			long1 = Math.toRadians(pInfo.longitud[i]);
			long2 = Math.toRadians(pInfo.longitud[i +1]);
			dist = obtenerDistancia(lat1, lat2, long1, long2);
			tiempo1= pInfo.tiempo[i];
			tiempo2= pInfo.tiempo[i+1];
			tiempoTotal = tiempo2 - tiempo1;
			tiempoTotal = tiempoTotal / 3600;
			x = x + (dist / tiempoTotal);
		}
		return x / numRegistros;
	}

    /**
     * Genera y guarda el gráfico que muestra el perfil de la actividad junto con 
     * la evolución de la frecuencia cardíaca.
     * 
     * @param pInfo
     * 			Información de la actividad (track) del atleta
     * @param pFichero
     * 			Nombre (path) del fichero donde se guardará el gráfico generado
     */
    public static void graficarPerfil(InfoLogTrack pInfo, String pFichero) {
    	int i, numRegistros; //LEXICO
    	double lat1, lat2, long1, long2;
    	double[] dist, FC, elev;
    	
    	//inicializacion
    	numRegistros = pInfo.tiempo.length;
    	dist= new double[numRegistros];
    	FC = new double[numRegistros];
    	elev = new double[numRegistros];
    	
    	for(i=0; i<numRegistros -1;i++) {
    		elev[i] = pInfo.altitud[i];
    	}
    	for(i=0; i<numRegistros -1;i++) {
    		FC[i] = pInfo.frecCardiaca[i];
    	}
    	
    	elev[numRegistros-1] = elev[numRegistros-2];
        FC[numRegistros-1] = FC[numRegistros-2];
    	
		dist[0] = 0;
        for(i=0; i<numRegistros-1;i++) {
            lat1 = Math.toRadians(pInfo.latitud[i]);
            lat2 = Math.toRadians(pInfo.latitud[i+1]);
            long1 = Math.toRadians(pInfo.longitud[i]);
            long2 = Math.toRadians(pInfo.longitud[i+1]);
            dist[i+1] = dist[i] + obtenerDistancia(lat1, lat2, long1, long2);
            
        }
    	
    	FuncionalidadAuxiliar.generarTrackPlot(dist, FC, elev, pFichero, true);
    }
	
    /**
     * Devuelve la distribucion por zonas de la frecuencia cardiaca
     * 
     * @param pInfo
     *         Información de la actividad (track) del atleta
     * @return lista de enteros indicando en cada posicion la distribucion de la FR
     */
    public static double[] obtDistribucionRC(InfoLogTrack pInfo) {
    	//lexico
    	double total, numRegistros,resistencia, moderado, ritmo, umbral, anaeborico, x;
    	double[] resultado;
    	int i;
    	
    	
    	//inicializacion
    	numRegistros = pInfo.tiempo.length;
    	resultado = new double[5];
    	resistencia = 0;
    	moderado = 0;
    	ritmo = 0;
    	umbral = 0;
    	anaeborico = 0;
    	
    	//ALGORITMO
    	for(i=0; i<numRegistros -1;i++) {
    		x = pInfo.frecCardiaca[i];
    		if(x < 123) {
    			resistencia = resistencia + 1;
    		}
    		else if(x>=123 && x <153) {
    			moderado = moderado + 1;
    		}
    		else if(x>=153 && x <169) {
    			ritmo = ritmo + 1;
    		}
    		else if(x>=169 && x <184) {
    			umbral = umbral + 1;
    		}
    		if(x > 184) {
    			anaeborico = anaeborico + 1;
    		}
    	}
    	total = resistencia + moderado + ritmo + umbral + anaeborico;
    	resistencia = (resistencia*100) / total;
    	moderado = (moderado*100) / total;
    	ritmo = (ritmo*100) / total;
    	umbral = (umbral*100) / total;
    	anaeborico = (anaeborico*100) / total;
    	
    	resultado[0] = resistencia;
    	resultado[1] = moderado;
    	resultado[2] = ritmo;
    	resultado[3] = umbral;
    	resultado[4] = anaeborico;
    return resultado;
    	
    }

    
    /**
     * Calcula el consumo de calorías del ciclista durante su actividad basandose 
     * en el método Metabolic Equivalent Task (MET).
     * 
     * @param pInfo
     * 		Información de la actividad (track) del atleta.
     * @param pKg
     * 		Peso corporal del atletam en kilogramos.
     * @return Las calorías consumidas en la actividad. 
     */
    public static double estimarConsumoCalorias(InfoLogTrack pInfo, double pKg) {
    	int i;
    	double dist, x, diTiempo, tiempo1, tiempo2, calorias, MET, numRegistros, lat1, lat2, long1, long2;
    	numRegistros = pInfo.tiempo.length;
    	
    	calorias=0;
    	for(i=0; i<numRegistros-2;i++) {
    		lat1 = Math.toRadians(pInfo.latitud[i]);
			lat2 = Math.toRadians(pInfo.latitud[i +1]);
			long1 = Math.toRadians(pInfo.longitud[i]);
			long2 = Math.toRadians(pInfo.longitud[i +1]);
			dist = obtenerDistancia(lat1, lat2, long1, long2);
			tiempo1 = pInfo.tiempo[i];
			tiempo2 = pInfo.tiempo[i+1];
			diTiempo = tiempo2 - tiempo1;
			diTiempo = diTiempo / 3600;
			x = dist / diTiempo;
			MET = -1.52 + 0.510 * x;
			diTiempo = diTiempo * 60;
			calorias = calorias + diTiempo*(3.5 * MET * pKg)/200;
    	}
    return calorias;
    }
    
	
	
	/**
	 * Obtiene las estadísticas avanzadas de la actividad realizada
	 * 
	 * @param pInfo
	 * 			informacion de la actividad del atleta
	 * @param pKg
	 * 			peso corporal del atleta
	 * @return la estadisticas avanzadas que incluyen velocidad máxima, 
	 * 			km más rápido, consumo calórico y desnivel positivo
	 */
	public static EstadisticasAvanzadas obtEstadisticasAvanzadas(InfoLogTrack pInfo, double pKg) {
		
		
		EstadisticasAvanzadas miEstadisticasAvanzadas = new EstadisticasAvanzadas();
		//PRINCIPAL
		miEstadisticasAvanzadas.calorias = estimarConsumoCalorias(pInfo, pKg);
		miEstadisticasAvanzadas.velocidadMaxima = obtenerMaximo(pInfo);
		miEstadisticasAvanzadas.desnivel = ObtDesnivelPositivo(pInfo);
		miEstadisticasAvanzadas.velocidadMaxKm = ObtenerKmMasRapido(pInfo);
		return miEstadisticasAvanzadas;
				
		
		
	}
	public static double obtenerMaximo (InfoLogTrack pInfo) {
		int i, numRegistros;
		double maximo, lat1, lat2, long1, long2,vel, tiempo1, tiempo2, dist, tiempoTotal;
		numRegistros = pInfo.tiempo.length;
		maximo=0;
		vel=0;
		for(i=0; i<numRegistros-2;i++) {
			lat1 = Math.toRadians(pInfo.latitud[i]);
			lat2 = Math.toRadians(pInfo.latitud[i +1]);
			long1 = Math.toRadians(pInfo.longitud[i]);
			long2 = Math.toRadians(pInfo.longitud[i +1]);
			dist = obtenerDistancia(lat1, lat2, long1, long2);
			tiempo1= pInfo.tiempo[i];
			tiempo2= pInfo.tiempo[i+1];
			tiempoTotal = tiempo2 - tiempo1;
			tiempoTotal = tiempoTotal / 3600;
			vel = dist / tiempoTotal;
			if(vel > maximo) {
				maximo = vel;
			}
		}
	return maximo;
		
	}
        
	
	public static double ObtDesnivelPositivo(InfoLogTrack pInfo ) {
		int i, numRegistros;
		double elev1, elev2, elevTemp, desnivel;
		numRegistros = pInfo.tiempo.length;
		
		desnivel = 0;
		for(i=0;i<numRegistros-2;i++) {
			elev1 = pInfo.altitud[i];
			elev2 = pInfo.altitud[i+1];
			if(elev2>elev1) { //para calcular solo cuando sube
				elevTemp = elev2 - elev1;
				desnivel = desnivel + elevTemp;
			}
		}
		return desnivel;
	}		
	
	public static double ObtenerKmMasRapido(InfoLogTrack pInfo) {
		double x;
		int i,  numRegistros;
		double lat1, lat2, long1, long2, tiempo1,tiempo2, dist, velMaxima, distTotal;
		
		
		numRegistros = pInfo.tiempo.length;
		
		tiempo2 = 0;
        distTotal = 0;
        velMaxima = 0;
        for(i = 0;i < numRegistros-2; i++){
            lat1 = Math.toRadians(pInfo.latitud[i]);
            lat2 = Math.toRadians(pInfo.latitud[i+1]);
            long1 = Math.toRadians(pInfo.longitud[i]);
            long2 = Math.toRadians(pInfo.longitud[i+1]);
            //c�lculos 
            dist = obtenerDistancia(lat1, lat2, long1, long2);
            distTotal = distTotal + dist;
            if(distTotal >= 1) {
            	tiempo1 = pInfo.tiempo[i] - tiempo2;
                tiempo1 = tiempo1 / 3600;
                x = distTotal / tiempo1;
                if(x > velMaxima) {
                	velMaxima = x;
                }
                distTotal = 0;
                tiempo2 = pInfo.tiempo[i];
            }
				
		}
      return velMaxima;
	}
		
		
	
	
		
	/**
	 * Genera el informe completo de las actividades
	 */
	public static void generarInformesTrack() {
		 int e,i;
	        EstadisticasBasicas resultado1;
	        EstadisticasAvanzadas resultado4;
	        InfoLogTrack info;
	        String textoprim;
	        String graficaPrim;
	        double kg;
	        double[] resultado2;
	        FuncionalidadAuxiliar.eliminarPlots();
	        String graficaFin = new String();
	        String textoFin = new String();
	        for(i = 1; i<= 7; i++){
	            textoprim = "C:\\Users\\ibais\\OneDrive\\Escritorio\\analisisCyclingTracks\\TrackFiles\\Athlete"+i+"\\activity-Athlete"+i;
	            graficaPrim =  "C:\\Users\\ibais\\OneDrive\\Escritorio\\analisisCyclingTracks\\GeneratedPlots\\Athlete"+i+"\\activity-Athlete"+i;
	            for(e = 1; e <=5; e++) {
	            	System.out.println("Log: Athlete"+i+"/activity-Athlete"+i+"-0"+e+".csv");
	            	
	            	System.out.println();
	                textoFin = textoprim+"-0"+e+".csv";
	                graficaFin = graficaPrim+"-0"+e+"-perfil.png";
	                info = FuncionalidadAuxiliar.cargarInfoCSV(textoFin);
	                
	                resultado1 = AnalisisLogTrack.obtEstadisticasBasicas(info);
	                System.out.println("Frecuencia cardiaca media: "+truncar(resultado1.fCMedia,2)+"p/m");
	                System.out.println("distancia: "+truncar(resultado1.distancia,2)+"km");
	                System.out.println("velocidad media: "+truncar(resultado1.velocidad,2)+"km/h");
	                System.out.println("duracion: "+convertirHoras(resultado1.duracion));
	                kg = FuncionalidadAuxiliar.obtPesoAtleta("Athlete"+i);
	                
	                resultado4 = AnalisisLogTrack.obtEstadisticasAvanzadas(info, kg);
	                System.out.println("calorias: "+truncar(resultado4.calorias,2)+"kcal");
	                System.out.println("velocidad maxima: "+truncar(resultado4.velocidadMaxima,2)+"km/h");
	                System.out.println("desnivel: "+truncar(resultado4.desnivel,2)+"m");
	                System.out.println("km mas rapido: "+truncar(resultado4.velocidadMaxKm,2)+"km/h");
	                System.out.println("Distribuci�n de zonas FC:");
	                
	                resultado2 = AnalisisLogTrack.obtDistribucionRC(info);
	                System.out.println("Z1 (resistencia): "+truncar(resultado2[0],2)+"%");
	                System.out.println("Z2 (moderado): "+truncar(resultado2[1],2)+"%");
	                System.out.println("Z3 (ritmo): "+truncar(resultado2[2],2)+"%");
	                System.out.println("Z4 (umbral): "+truncar(resultado2[3],2)+"%");
	                System.out.println("Z5 (anaerobico): "+truncar(resultado2[4],2)+"%");
	                
	                graficarPerfil(info, graficaFin);
	                   
	                
	                System.out.println("-----------------------------------------");
	            }
	        }
	}
	
	public static String convertirHoras(int pResultado) {
		int horas, restoHoras, restoMinutos, minutos, segundos;
		
		restoHoras = pResultado%3600;
		horas = (pResultado - restoHoras) / 3600;
		restoMinutos = pResultado%60;
		minutos = (restoHoras-restoMinutos)/60;
		segundos = restoMinutos;
		
		return horas+"h : "+minutos+"m : "+segundos+"s";
	}
	
	public static double truncar( double num, int puestosDecimales ){
		
        int numEntero = (int)( num * Math.pow( 10, puestosDecimales ) );
        double resultado = (double)( numEntero / Math.pow( 10, puestosDecimales ) );
        return resultado;
    }
	
	
}
