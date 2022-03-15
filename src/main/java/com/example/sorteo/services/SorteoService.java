package com.example.sorteo.services;

import java.io.BufferedReader;
import java.io.File;
//import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.sorteo.entities.Empleado;
import com.example.sorteo.entities.NumerosAsignados;
import com.example.sorteo.entities.NumerosDelSorteo;
import com.example.sorteo.entities.Sorteo;
import com.example.sorteo.repositories.ISorteoRepository;

@Service("sorteoService")
public class SorteoService {
	@Autowired
	@Qualifier("sorteoRepository")
	private ISorteoRepository sorteoRepository;

	@Autowired
	@Qualifier("empleadoService")
	private EmpleadoService empleadoService;

	@Autowired
	@Qualifier("numerosAsignadosService")
	private NumerosAsignadosService numerosAsignadosService;

	@Autowired
	@Qualifier("numerosDelSorteoService")
	private NumerosDelSorteoService numerosDelSorteoService;

	private Logger logg = LoggerFactory.getLogger(SorteoService.class);
	private String nombreArchivo = "";
	private Sorteo sorteoActual = new Sorteo();
	String folder = "cargas//";

	public List<Sorteo> getAll() {
		return sorteoRepository.findAll();
	}

	public void save(Sorteo sorteo) {
		sorteoRepository.save(sorteo);
	}

	public Sorteo buscar(long id) {
		return sorteoRepository.findById(id).orElse(null);
	}

	public void eliminar(long id) {
		sorteoRepository.deleteById(id);
	}

	public Sorteo traerPorNombre(String nombre) {
		return sorteoRepository.traerPorNombre(nombre);
	}

	public List<Sorteo> listar(String tipo) {
		return sorteoRepository.listar(tipo);
	}

	public int obtenerUltimoId() {
		return sorteoRepository.obtenerUltimoId();
	}

	public List<Sorteo> traerTodosLosSorteosOrdenados() {
		List<Sorteo> listaOrdenada = getAll();
		Collections.sort(listaOrdenada);
		return listaOrdenada;
	}

	public List<Empleado> traerGanadores(Sorteo sorteo) {
		List<Empleado> ganadores = new ArrayList<Empleado>();
		for (Empleado em : sorteo.getParticipantes()) {
			if (em.getPosicionSorteo() != 0)
				ganadores.add(em);
		}
		Collections.sort(ganadores);
		return ganadores;
	}

	public List<Empleado> traerParticipantes(Sorteo sorteo) {
		List<Empleado> participantes = new ArrayList<Empleado>(sorteo.getParticipantes());
		for (Empleado em : participantes) {
			em.setPosicionSorteo(0);
		}
		Collections.sort(participantes);
		return participantes;
	}

	public String save(MultipartFile file) {
		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();
				Path path = Paths.get(folder + file.getOriginalFilename());
				Files.write(path, bytes);
				logg.info("Archivo guardado");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		nombreArchivo = file.getOriginalFilename();
		return nombreArchivo;
	}

	public List<Empleado> leerArchivoParaCargarPotencialesGanadores(Sorteo sorteo) {
		String PATH = "C:\\Users\\rricciardulli\\Desktop\\Spring Proyects\\sorteo\\cargas\\";
		File documento = new File(PATH + nombreArchivo);
		System.out.println(documento);
		List<Empleado> participantes = new ArrayList<Empleado>();
		List<Empleado> participantesRepetidos = new ArrayList<Empleado>();
		Set<Empleado> empleados = new HashSet<>();
		boolean encontrado = false;
		try (BufferedReader reader = new BufferedReader(new FileReader(documento))) {
			String linea;
			while ((linea = reader.readLine()) != null) {
				String[] newStr = linea.split("\\s+");
				Empleado empleado = new Empleado();
				empleado.setNombre(newStr[0]);
				empleado.setApellido(newStr[1]);
				empleado.setDocumento(Long.parseLong(newStr[2]));
				empleado.setSorteo(sorteo);
				Iterator<Empleado> iterador = participantes.iterator();
				encontrado = false;
				while ((iterador.hasNext()) && (!encontrado)) {
					Empleado empleado2 = iterador.next();
					if (empleado2.getDocumento() == empleado.getDocumento()
							|| empleadoService.traerXDocumento(empleado.getDocumento()) != null) {
						encontrado = true;
						participantesRepetidos.add(empleado);
					}
				}
				if (!encontrado && empleadoService.traerXDocumento(empleado.getDocumento()) == null) {
					participantes.add(empleado);
					empleados.add(empleado);
				}
				if (participantes.isEmpty()) {
					participantesRepetidos.add(empleado);
				}
			}
			if (!participantesRepetidos.isEmpty()) {
				System.out.println("\nEl nombre y apellido de los siguientes participantes se encuentra repetido: ");
				System.out.println("\n" + participantesRepetidos + "\n");
				System.out.println(
						"Dichas repeticiones de los participantes no se guardan ya que se han creado antes.\n");
			}
		} catch (Exception e) {
			System.out.println("Error al leer archivo");
		}
		sorteo.setParticipantes(empleados);
		save(sorteo);
		numerosDelSorteoService.save(new NumerosDelSorteo(0, 0, 0, 0, 0, 0, sorteo));
		for (Empleado empleado : participantes) {
			empleadoService.save(empleado);
			numerosAsignadosService.save(new NumerosAsignados(0, 0, 0, empleado));
		}
		sorteoActual = buscar(obtenerUltimoId());
		return participantesRepetidos;
	}

	public boolean cantidadParticipantes(int cantidadGanadores) {
		boolean flag = false;
		int cantidad = sorteoActual.getParticipantes().size();
		if (cantidadGanadores > cantidad)
			flag = true;
		return flag;
	}

	public Sorteo sortear(int cantidad) {
		List<Empleado> participantes = new ArrayList<Empleado>(sorteoActual.getParticipantes());
		List<Empleado> ganadores = new ArrayList<Empleado>();
		int maximoAleatorio = participantes.size() - 1;
		int i = 0;
		int posicionSorteo = 0;
		while (i < cantidad) {
			boolean existe = false;
			int numeroAleatorio = (int) (Math.random() * maximoAleatorio) + 1;
			Empleado elegido = participantes.get(numeroAleatorio);
			if (!ganadores.isEmpty()) {

				Iterator<Empleado> iterador = ganadores.iterator();
				while ((iterador.hasNext()) && (!existe)) {
					Empleado empleado1 = iterador.next();
					if (empleado1.getIdEmpleado() == elegido.getIdEmpleado()) {
						existe = true;
						cantidad++;
					}
				}
			}
			if (!existe) {
				posicionSorteo++;
				elegido.setPosicionSorteo(posicionSorteo);
				empleadoService.save(elegido);
				ganadores.add(elegido);
			}
			i++;
		}
		Set<Empleado> setParticipantes = new HashSet<>();
		for (Empleado emp : participantes) {
			setParticipantes.add(emp);
		}
		Set<Empleado> setGanadores = new HashSet<>();
		for (Empleado emp : ganadores) {
			setGanadores.add(emp);
		}
		return sorteoActual;
	}

	public Sorteo generarNumerosGanadores() {
		int[] numerosGanadores = new int[3];
		for (int i = 0; i < 3; i++) {
			numerosGanadores[i] = (int) Math.floor(Math.random() * (10 - 1 + 1)) + 1;
		}
		NumerosDelSorteo numeroDelSorteoActual = numerosDelSorteoService
				.buscar(numerosDelSorteoService.obtenerUltimoId());
		numeroDelSorteoActual.setPrimero(numerosGanadores[0]);
		numeroDelSorteoActual.setSegundo(numerosGanadores[1]);
		numeroDelSorteoActual.setTercero(numerosGanadores[2]);
		numerosDelSorteoService.save(numeroDelSorteoActual);
		return generarNumerosAsignadosAleatorios(numerosGanadores);
	}

	public Sorteo generarNumerosAsignadosAleatorios(int[] numerosGanadores) {
		int[] numerosAleatoriosAsignados = new int[3];
		for (Empleado em : sorteoActual.getParticipantes()) {
			for (int i = 0; i < 3; i++) {
				numerosAleatoriosAsignados[i] = (int) Math.floor(Math.random() * (10 - 1 + 1)) + 1;
			}
			NumerosAsignados numerosAsignadosAux = numerosAsignadosService.traerXEmpleado(em);
			numerosAsignadosAux.setPrimero(numerosAleatoriosAsignados[0]);
			numerosAsignadosAux.setSegundo(numerosAleatoriosAsignados[1]);
			numerosAsignadosAux.setTercero(numerosAleatoriosAsignados[2]);
			numerosAsignadosService.save(numerosAsignadosAux);
			comprobarSiGano(em, numerosGanadores);
		}
		return sorteoActual;
	}

	public boolean comprobarSiGano(Empleado empleado, int[] numerosGanadores) {
		NumerosAsignados numerosAsignados = numerosAsignadosService.traerXEmpleado(empleado);
		boolean flag = false;
		if (numerosAsignados.getPrimero() == numerosGanadores[0] && numerosAsignados.getSegundo() == numerosGanadores[1]
				&& numerosAsignados.getTercero() == numerosGanadores[2]) {
			empleado.setPosicionSorteo(1);
			empleadoService.save(empleado);
			flag = true;
		} else if (numerosAsignados.getPrimero() == numerosGanadores[0]
				&& numerosAsignados.getSegundo() == numerosGanadores[1]
				|| numerosAsignados.getPrimero() == numerosGanadores[0]
						&& numerosAsignados.getTercero() == numerosGanadores[2]
				|| numerosAsignados.getSegundo() == numerosGanadores[1]
						&& numerosAsignados.getTercero() == numerosGanadores[2]) {
			empleado.setPosicionSorteo(2);
			empleadoService.save(empleado);
			flag = true;
		} else if (numerosAsignados.getPrimero() == numerosGanadores[0]
				|| numerosAsignados.getSegundo() == numerosGanadores[1]
				|| numerosAsignados.getTercero() == numerosGanadores[2]) {
			empleado.setPosicionSorteo(3);
			empleadoService.save(empleado);
			flag = true;
		}
		return flag;
	}

	public void cargarNuevoSorteoDeCincoNumeros(Sorteo sorteo) {
		save(sorteo);
		ArrayList<Integer> numeros = generarCincoNumerosAleatorios();
		numerosDelSorteoService.save(new NumerosDelSorteo(numeros.get(0), numeros.get(1), numeros.get(2),
				numeros.get(3), numeros.get(4), sorteo));
	}

	public ArrayList<Integer> generarCincoNumerosAleatorios() {
		ArrayList<Integer> numeros = new ArrayList<Integer>();
		int[] numerosGanadores = new int[5];
		for (int i = 0; i < 5; i++) {
			numerosGanadores[i] = (int) Math.floor(Math.random() * (100 - 1 + 1)) + 1;
			if (numeros.contains(numerosGanadores[i]))
				i--;
			else
				numeros.add(numerosGanadores[i]);
		}
		return numeros;
	}

	public boolean comprobarSiGanoConCincoNumeros(Empleado empleado, ArrayList<Integer> numerosGanadores) {
		NumerosAsignados numerosAsignados = numerosAsignadosService.traerXEmpleado(empleado);
		boolean flag = false;
		int aciertos = 0;
		if (numerosAsignados.getPrimero() == numerosGanadores.get(0)
				|| numerosAsignados.getPrimero() == numerosGanadores.get(1)
				|| numerosAsignados.getPrimero() == numerosGanadores.get(2)
				|| numerosAsignados.getPrimero() == numerosGanadores.get(3)
				|| numerosAsignados.getPrimero() == numerosGanadores.get(4)) {
			aciertos++;
			flag = true;
		}
		if (numerosAsignados.getSegundo() == numerosGanadores.get(0)
				|| numerosAsignados.getSegundo() == numerosGanadores.get(1)
				|| numerosAsignados.getSegundo() == numerosGanadores.get(2)
				|| numerosAsignados.getSegundo() == numerosGanadores.get(3)
				|| numerosAsignados.getSegundo() == numerosGanadores.get(4)) {
			aciertos++;
			flag = true;
		}
		if (numerosAsignados.getTercero() == numerosGanadores.get(0)
				|| numerosAsignados.getTercero() == numerosGanadores.get(1)
				|| numerosAsignados.getTercero() == numerosGanadores.get(2)
				|| numerosAsignados.getTercero() == numerosGanadores.get(3)
				|| numerosAsignados.getTercero() == numerosGanadores.get(4)) {
			aciertos++;
			flag = true;
		}
		if (numerosAsignados.getCuarto() == numerosGanadores.get(0)
				|| numerosAsignados.getCuarto() == numerosGanadores.get(1)
				|| numerosAsignados.getCuarto() == numerosGanadores.get(2)
				|| numerosAsignados.getCuarto() == numerosGanadores.get(3)
				|| numerosAsignados.getCuarto() == numerosGanadores.get(4)) {
			aciertos++;
			flag = true;
		}
		if (numerosAsignados.getQuinto() == numerosGanadores.get(0)
				|| numerosAsignados.getQuinto() == numerosGanadores.get(1)
				|| numerosAsignados.getQuinto() == numerosGanadores.get(2)
				|| numerosAsignados.getQuinto() == numerosGanadores.get(3)
				|| numerosAsignados.getQuinto() == numerosGanadores.get(4)) {
			aciertos++;
			flag = true;
		}
		switch (aciertos) {
		case 1:
			empleado.setPosicionSorteo(5);
			empleadoService.save(empleado);
			break;
		case 2:
			empleado.setPosicionSorteo(4);
			empleadoService.save(empleado);
			break;
		case 3:
			empleado.setPosicionSorteo(3);
			empleadoService.save(empleado);
			break;
		case 4:
			empleado.setPosicionSorteo(2);
			empleadoService.save(empleado);
			break;
		case 5:
			empleado.setPosicionSorteo(1);
			empleadoService.save(empleado);
			break;
		}
		return flag;
	}

	public void cargarNuevoSorteoQuini(Sorteo sorteo) {
		ArrayList<Integer> numeros = generarSeisNumerosAleatorios();
		save(sorteo);
		numerosDelSorteoService.save(new NumerosDelSorteo(numeros.get(0), numeros.get(1), numeros.get(2), numeros.get(3),
				numeros.get(4), numeros.get(5), sorteo));
	}

	public ArrayList<Integer> generarSeisNumerosAleatorios() {
		ArrayList<Integer> numeros = new ArrayList<Integer>();
		int[] numerosGanadores = new int[6];
		for (int i = 0; i < 6; i++) {
			numerosGanadores[i] = (int) Math.floor(Math.random() * (46 - 1 + 1)) + 1;
			if (numeros.contains(numerosGanadores[i]))
				i--;
			else
				numeros.add(numerosGanadores[i]);
		}
		return numeros;
	}

	public boolean comprobarSiGanoConSeisNumeros(Empleado empleado, ArrayList<Integer> numerosGanadores) {
		NumerosAsignados numerosAsignados = numerosAsignadosService.traerXEmpleado(empleado);
		boolean flag = false;
		int aciertos = 0;
		if (numerosAsignados.getPrimero() == numerosGanadores.get(0)
				|| numerosAsignados.getPrimero() == numerosGanadores.get(1)
				|| numerosAsignados.getPrimero() == numerosGanadores.get(2)
				|| numerosAsignados.getPrimero() == numerosGanadores.get(3)
				|| numerosAsignados.getPrimero() == numerosGanadores.get(4)
				|| numerosAsignados.getPrimero() == numerosGanadores.get(5)) {
			aciertos++;
			flag = true;
		}
		if (numerosAsignados.getSegundo() == numerosGanadores.get(0)
				|| numerosAsignados.getSegundo() == numerosGanadores.get(1)
				|| numerosAsignados.getSegundo() == numerosGanadores.get(2)
				|| numerosAsignados.getSegundo() == numerosGanadores.get(3)
				|| numerosAsignados.getSegundo() == numerosGanadores.get(4)
				|| numerosAsignados.getSegundo() == numerosGanadores.get(5)) {
			aciertos++;
			flag = true;
		}
		if (numerosAsignados.getTercero() == numerosGanadores.get(0)
				|| numerosAsignados.getTercero() == numerosGanadores.get(1)
				|| numerosAsignados.getTercero() == numerosGanadores.get(2)
				|| numerosAsignados.getTercero() == numerosGanadores.get(3)
				|| numerosAsignados.getTercero() == numerosGanadores.get(4)
				|| numerosAsignados.getTercero() == numerosGanadores.get(5)) {
			aciertos++;
			flag = true;
		}
		if (numerosAsignados.getCuarto() == numerosGanadores.get(0)
				|| numerosAsignados.getCuarto() == numerosGanadores.get(1)
				|| numerosAsignados.getCuarto() == numerosGanadores.get(2)
				|| numerosAsignados.getCuarto() == numerosGanadores.get(3)
				|| numerosAsignados.getCuarto() == numerosGanadores.get(4)
				|| numerosAsignados.getCuarto() == numerosGanadores.get(5)) {
			aciertos++;
			flag = true;
		}
		if (numerosAsignados.getQuinto() == numerosGanadores.get(0)
				|| numerosAsignados.getQuinto() == numerosGanadores.get(1)
				|| numerosAsignados.getQuinto() == numerosGanadores.get(2)
				|| numerosAsignados.getQuinto() == numerosGanadores.get(3)
				|| numerosAsignados.getQuinto() == numerosGanadores.get(4)
				|| numerosAsignados.getQuinto() == numerosGanadores.get(5)) {
			aciertos++;
			flag = true;
		}
		if (numerosAsignados.getSexto() == numerosGanadores.get(0)
				|| numerosAsignados.getSexto() == numerosGanadores.get(1)
				|| numerosAsignados.getSexto() == numerosGanadores.get(2)
				|| numerosAsignados.getSexto() == numerosGanadores.get(3)
				|| numerosAsignados.getSexto() == numerosGanadores.get(4)
				|| numerosAsignados.getSexto() == numerosGanadores.get(5)) {
			aciertos++;
			flag = true;
		}
		switch (aciertos) {
		case 1:
			empleado.setPosicionSorteo(6);
			empleadoService.save(empleado);
			break;
		case 2:
			empleado.setPosicionSorteo(5);
			empleadoService.save(empleado);
			break;
		case 3:
			empleado.setPosicionSorteo(4);
			empleadoService.save(empleado);
			break;
		case 4:
			empleado.setPosicionSorteo(3);
			empleadoService.save(empleado);
			break;
		case 5:
			empleado.setPosicionSorteo(2);
			empleadoService.save(empleado);
			break;
		case 6:
			empleado.setPosicionSorteo(1);
			empleadoService.save(empleado);
			break;
		}
		return flag;
	}
	
	public boolean generarArchivo(Sorteo sorteo, String nombreSorteo) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(sorteo.getFecha());
		buffer.append(" GANADORES: ");
		for (Empleado ganador : traerGanadores(sorteo)) {
			buffer.append(ganador.toString());
		}
		buffer.append("\n");
		buffer.append(" PARTICIPANTES: ");
		for (Empleado participante : sorteo.getParticipantes()) {
			buffer.append(participante.toString());
		}
		return guardarArchivo(buffer, nombreSorteo);
	}

	private boolean guardarArchivo(StringBuffer file, String nombreSorteo) {
		String PATH = "C:\\Users\\rricciardulli\\Desktop\\Java proyects\\Roger Ricciardulli\\";
		File archivo = new File(PATH + nombreSorteo + ".txt");
		try {
			FileOutputStream archi = new FileOutputStream(archivo, true);
			archi.write(file.toString().getBytes());
			archi.flush();
			archi.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/*public ResponseEntity<Object> downloadFile(File file) throws IOException{
		InputStreamResource resource = New InputStreamResource(new FileInputStream(file));
		return ResponseEntity;
	}*/
}
