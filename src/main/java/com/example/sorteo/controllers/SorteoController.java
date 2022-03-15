package com.example.sorteo.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.sorteo.entities.Empleado;
import com.example.sorteo.entities.Sorteo;
import com.example.sorteo.services.EmpleadoService;
import com.example.sorteo.services.NumerosDelSorteoService;
import com.example.sorteo.services.SorteoService;

@Controller
@RequestMapping("/sorteo")
public class SorteoController {
	@Autowired
	@Qualifier("sorteoService")
	private SorteoService sorteoService;

	@Autowired
	@Qualifier("empleadoService")
	private EmpleadoService empleadoService;

	@Autowired
	@Qualifier("numerosDelSorteoService")
	private NumerosDelSorteoService numerosDelSorteoService;

	@GetMapping("/lista")
	public String listarSorteos(Model model) {
		List<Sorteo> listado = sorteoService.traerTodosLosSorteosOrdenados();
		model.addAttribute("titulo", "Lista de sorteos");
		model.addAttribute("lista", listado);
		return "sorteo/lista";
	}

	@GetMapping("/lista/traerParticipantes/{id}")
	public String mostrarParticipantes(@PathVariable("id") long id, Model model) throws Exception {
		Sorteo sorteo = sorteoService.buscar(id);
		List<Empleado> participantes = sorteoService.traerParticipantes(sorteo);
		model.addAttribute("titulo", "Participantes");
		model.addAttribute("lista", participantes);
		return "sorteo/participantes";
	}

	@GetMapping("/lista/traerGanadores/{id}")
	public String mostrarGanadores(@PathVariable("id") long id, Model model) throws Exception {
		Sorteo sorteo = sorteoService.buscar(id);
		List<Empleado> ganadores = sorteoService.traerGanadores(sorteo);
		model.addAttribute("titulo", "Ganadores");
		model.addAttribute("lista", ganadores);
		return "sorteo/ganadores";
	}

	// ---------------------"Hilo de ejecucion" de Sorteo Normal-----------------

	@GetMapping("/subirArchivo")
	public String subirArchivo() {
		return "sorteo/subirArchivo";
	}

	@PostMapping("/guardarArchivo")
	public String comprobarArchivo(@RequestParam("archivo") MultipartFile file, Model model,
			RedirectAttributes attribute) {
		if (file.isEmpty()) {
			attribute.addFlashAttribute("success", "No se ha subido un archivo .txt correctamente");
			return "redirect:/sorteo/subirArchivo";
		}
		sorteoService.save(file);
		Sorteo sorteo = new Sorteo();
		model.addAttribute("titulo", "Formulario: Sorteo Normal");
		model.addAttribute("sorteo", sorteo);

		return "sorteo/crearSorteoNormal";
	}

	@PostMapping("/guardarSorteoNormal")
	public String guardarSorteoNormal(@Valid @ModelAttribute Sorteo sorteo, BindingResult result, Model model,
			RedirectAttributes attribute) {
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario: Sorteo Normal");
			model.addAttribute("sorteo", sorteo);
			System.out.println("Hubo errores en la creacion del formulario!");
			return "sorteo/crearSorteoNormal";
		}
		System.out.println("Sorteo guardado con exito!");
		attribute.addFlashAttribute("success", "Sorteo agregado con exito");
		List<Empleado> participantesRepetidos = sorteoService.leerArchivoParaCargarPotencialesGanadores(sorteo);
		if (!participantesRepetidos.isEmpty()) {
			model.addAttribute("titulo",
					"Los siguientes participantes tienen documentos repetidos, por lo que solo se guardaron la primera vez:");
			model.addAttribute("lista", participantesRepetidos);
			System.out.println("Hay participantes repetidos!");
			return "sorteo/mostrarParticipantesRepetidos";
		}

		return "sorteo/cantidadGanadores";
	}

	@GetMapping("/cantidadGanadores")
	public String cantidadGanadores(Model model) {
		return "sorteo/cantidadGanadores";
	}

	@PostMapping("/guardarCantidadGanadores")
	public String guardarCantidadGanadores(@RequestParam("cantidad") String number, Model model,
			RedirectAttributes attribute) throws Exception {
		if(number.isEmpty()) {
			attribute.addFlashAttribute("success",
					"Error: La cantidad de ganadores debe ser mayor a 0.");
			return "redirect:/sorteo/cantidadGanadores";
		}
		int numero = Integer.parseInt(number);
		if (sorteoService.cantidadParticipantes(numero)) {
			attribute.addFlashAttribute("success",
					"Error: La cantidad de ganadores debe ser menor a la cantidad de participantes ("
							+ sorteoService.buscar(sorteoService.obtenerUltimoId()).getParticipantes().size() + ")");
			return "redirect:/sorteo/cantidadGanadores";
		}
		Sorteo sorteo = sorteoService.sortear(numero);
		List<Sorteo> listado = new ArrayList<Sorteo>();
		listado.add(sorteo);
		model.addAttribute("titulo", "Sorteo:");
		model.addAttribute("lista", listado);
		return "sorteo/lista";
	}

	// ---------------------"Hilo de ejecucion" de Sorteo 3-----------------
	// --------------------------------Valores------------------------------

	@GetMapping("/subirArchivoSorteo3Valores")
	public String subirArchivoParaSorteo3Valores() {
		return "sorteo/subirArchivoSorteo3Valores";
	}

	@PostMapping("/guardarArchivoSorteo3Valores")
	public String comprobarArchivoParaSorteo3Valores(@RequestParam("archivo") MultipartFile file, Model model,
			RedirectAttributes attribute) {
		if (file.isEmpty()) {
			attribute.addFlashAttribute("success", "No se ha subido un archivo .txt correctamente");
			return "redirect:/sorteo/subirArchivoSorteo3Valores";
		}
		sorteoService.save(file);
		Sorteo sorteo = new Sorteo();
		model.addAttribute("titulo", "Formulario: Sorteo 3 Valores");
		model.addAttribute("sorteo", sorteo);

		return "sorteo/crearSorteo3Valores";
	}

	@PostMapping("/guardarSorteo3Valores")
	public String guardarSorteo3Valores(@Valid @ModelAttribute Sorteo sorteo, BindingResult result, Model model,
			RedirectAttributes attribute) {
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario: Sorteo 3 Valores");
			model.addAttribute("sorteo", sorteo);
			System.out.println("Hubo errores en la creacion del formulario!");
			return "sorteo/crearSorteo3Valores";
		}
		List<Empleado> participantesRepetidos = sorteoService.leerArchivoParaCargarPotencialesGanadores(sorteo);
		attribute.addFlashAttribute("success", "Sorteo agregado con exito");
		System.out.println("Sorteo guardado con exito!");
		if (!participantesRepetidos.isEmpty()) {
			model.addAttribute("titulo",
					"Los siguientes participantes tienen documentos repetidos, por lo que solo se guardaron la primera vez:");
			model.addAttribute("lista", participantesRepetidos);
			System.out.println("Hay participantes repetidos!");
			return "sorteo/mostrarParticipantesRepetidos3Valores";
		}
		sorteoService.generarNumerosGanadores();
		List<Sorteo> listado = new ArrayList<Sorteo>();
		listado.add(sorteo);
		model.addAttribute("titulo", "Sorteo:");
		model.addAttribute("lista", listado);
		return "sorteo/lista";
	}

	@GetMapping("/seleccionarLista3Valores")
	public String seleccionarLista3Valores(Model model) {
		Sorteo sorteo = sorteoService.generarNumerosGanadores();
		List<Sorteo> listado = new ArrayList<Sorteo>();
		listado.add(sorteo);
		model.addAttribute("titulo", "Sorteo:");
		model.addAttribute("lista", listado);
		return "sorteo/lista";
	}

	// ---------------------"Hilo de ejecucion" de Sorteo 5-----------------
	// --------------------------------Valores------------------------------

	@GetMapping("/crearSorteo5Valores")
	public String crearSorteo5Valores(Model model) {
		Sorteo sorteo = new Sorteo();
		model.addAttribute("titulo", "Formulario: Nuevo Sorteo");
		model.addAttribute("sorteo", sorteo);
		return "sorteo/crearSorteo5Numeros";
	}

	@PostMapping("/guardarSorteo5Valores")
	public String guardarSorteo5Valores(@Valid @ModelAttribute Sorteo sorteo, BindingResult result, Model model,
			RedirectAttributes attribute) {
		if (result.hasErrors()) {
			System.out.println("Hubo errores en la creacion del formulario!");
			attribute.addFlashAttribute("success", "Hubo errores en la creacion del sorteo.");
			return "redirect:/sorteo/crearSorteo5Valores";
		}
		sorteoService.cargarNuevoSorteoDeCincoNumeros(sorteo);
		attribute.addFlashAttribute("success", "Sorteo agregado con exito");
		System.out.println("Sorteo guardado con exito!");
		List<Sorteo> listado = new ArrayList<Sorteo>();
		listado.add(sorteo);
		model.addAttribute("titulo", "Sorteo:");
		model.addAttribute("lista", listado);
		return "sorteo/lista";
	}

	// ---------------------"Hilo de ejecucion" de Sorteo Quini 6-----------------

	@GetMapping("/crearSorteoQuini6")
	public String crearSorteoQuini6(Model model) {
		Sorteo sorteo = new Sorteo();
		model.addAttribute("titulo", "Formulario: Nuevo Sorteo Quini 6");
		model.addAttribute("sorteo", sorteo);
		return "sorteo/crearSorteoQuini6";
	}

	@PostMapping("/guardarSorteoQuini6")
	public String guardarSorteoQuini6(@Valid @ModelAttribute Sorteo sorteo, BindingResult result, Model model,
			RedirectAttributes attribute) {
		if (result.hasErrors()) {
			System.out.println("Hubo errores en la creacion del formulario!");
			attribute.addFlashAttribute("success", "Hubo errores en la creacion del sorteo.");
			return "redirect:/sorteo/crearSorteoQuini6";
		}
		sorteoService.cargarNuevoSorteoQuini(sorteo);
		attribute.addFlashAttribute("success", "Sorteo agregado con exito");
		System.out.println("Sorteo guardado con exito!");
		List<Sorteo> listado = new ArrayList<Sorteo>();
		listado.add(sorteo);
		model.addAttribute("titulo", "Sorteo:");
		model.addAttribute("lista", listado);
		return "sorteo/lista";
	}
	
	// ---------------------Generar Archivo .txt-----------------------
	
	@GetMapping("/generarArchivoTxt/{id}")
	public String generarArchivoTxt(@PathVariable("id") long id, Model model, RedirectAttributes attribute) throws Exception {
		Sorteo sorteo = sorteoService.buscar(id);
		sorteoService.generarArchivo(sorteo, sorteo.getNombre());
		attribute.addFlashAttribute("success", "Se ha descargado con exito el archivo!");
		return "redirect:/sorteo/lista";
	}

}
