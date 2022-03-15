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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.sorteo.entities.Empleado;
import com.example.sorteo.entities.NumerosAsignados;
import com.example.sorteo.entities.Sorteo;
import com.example.sorteo.services.EmpleadoService;
import com.example.sorteo.services.NumerosAsignadosService;
import com.example.sorteo.services.NumerosDelSorteoService;
import com.example.sorteo.services.SorteoService;

@Controller
@RequestMapping("/empleado")
public class EmpleadoController {
	@Autowired
	@Qualifier("empleadoService")
	private EmpleadoService empleadoService;

	@Autowired
	@Qualifier("sorteoService")
	private SorteoService sorteoService;

	@Autowired
	@Qualifier("numerosDelSorteoService")
	private NumerosDelSorteoService numerosDelSorteoService;

	@Autowired
	@Qualifier("numerosAsignadosService")
	private NumerosAsignadosService numerosAsignadosService;

	@GetMapping("/")
	public String crear(Model model) {
		Empleado empleado = new Empleado();
		model.addAttribute("titulo", "Formulario: Nuevo Participante");
		List<Sorteo> todosLosSorteos = sorteoService.getAll();
		List<Sorteo> listado = new ArrayList<Sorteo>();
		for (Sorteo s : todosLosSorteos) {
			if (s.getTipo().equals("Sorteo Normal") == false)
				listado.add(s);
		}
		model.addAttribute("lista", listado);
		model.addAttribute("empleado", empleado);
		return "empleado/crear";
	}

	@PostMapping("/")
	public String guardar(@Valid @ModelAttribute Empleado empleado, BindingResult result, Model model,
			RedirectAttributes attribute) {
		if (result.hasErrors() || empleado.getSorteo() == null) {
			model.addAttribute("titulo", "Formulario: Nuevo Participante");
			List<Sorteo> todosLosSorteos = sorteoService.getAll();
			List<Sorteo> listado = new ArrayList<Sorteo>();
			for (Sorteo s : todosLosSorteos) {
				if (s.getTipo().equals("Sorteo Normal") == false)
					listado.add(s);
			}
			model.addAttribute("empleado", empleado);
			model.addAttribute("lista", listado);
			System.out.println("Hubo errores en la creacion del formulario!");
			attribute.addFlashAttribute("failure", "Hay errores en la creacion del participante");
			return "redirect:/empleado/";
		}
		if (empleadoService.traerXDocumento(empleado.getDocumento()) != null) {
			attribute.addFlashAttribute("failure", "El documento seleccionado ya existe");
			return "redirect:/empleado/";
		}
		empleadoService.save(empleado);

		// ---------------Parte de crear los numeros asignados---------------

		if (empleado.getSorteo().getTipo().equals("Sorteo 3 Valores") || empleado.getSorteo().getTipo().equals("Sorteo Normal")) {
			empleadoService.crearNumerosAsignadosDeAcuerdoAlTipoDeSorteo(empleado, numerosDelSorteoService,
					numerosAsignadosService, sorteoService);
			System.out.println("Participante guardado con exito!");
			attribute.addFlashAttribute("success", "Participante agregado con exito");
			return "redirect:/empleado/";
		}
		if (empleado.getSorteo().getTipo().equals("Sorteo Quini 6")) {
			empleadoService.generar6NumerosAsignadosAleatoriosParaUnParticipante(empleado, numerosDelSorteoService,
					numerosAsignadosService, sorteoService);
			System.out.println("Participante guardado con exito!");
			Sorteo sorteo = sorteoService.buscar(empleado.getSorteo().getIdSorteo());
			List<Sorteo> listado = new ArrayList<Sorteo>();
			listado.add(sorteo);
			model.addAttribute("titulo", "Sorteo:");
			model.addAttribute("lista", listado);
			return "sorteo/lista";
		}
		NumerosAsignados numerosAsignados = new NumerosAsignados(0, 0, 0, 0, 0, 0, empleado);
		model.addAttribute("titulo", "Formulario: Asignar Numeros");
		model.addAttribute("numerosAsignados", numerosAsignados);
		return "numerosAsignados/seleccionarValoresSorteo5Numeros";
	}

	@GetMapping("/edit/{id}")
	public String editar(@PathVariable("id") long id, Model model) {
		Empleado empleado = empleadoService.buscar(id);
		model.addAttribute("titulo", "Editar perfil");
		model.addAttribute("empleado", empleado);
		return "empleado/editar";
	}

	@GetMapping("/delete/{id}")
	public String eliminar(@PathVariable("id") long id, RedirectAttributes attribute, Model model) {
		Empleado empleado = empleadoService.buscar(id);
		numerosAsignadosService.eliminar(numerosAsignadosService.traerXEmpleado(empleado).getIdNumerosAsignados());
		empleadoService.eliminar(id);
		System.out.println("Participante eliminado con exito");
		attribute.addFlashAttribute("warning", "Participante eliminado con exito");
		List<Sorteo> listado = new ArrayList<Sorteo>();
		listado.add(sorteoService.buscar(empleado.getSorteo().getIdSorteo()));
		model.addAttribute("titulo", "Sorteo:");
		model.addAttribute("lista", listado);
		return "sorteo/lista";
	}

	@PostMapping("/guardarEdit")
	public String guardarEdit(@Valid @ModelAttribute Empleado empleado, BindingResult result, Model model,
			RedirectAttributes attribute) {
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Editar perfil");
			model.addAttribute("empleado", empleado);
			System.out.println("Hubo errores en la creacion del formulario!");
			return "empleado/editar";
		}
		empleadoService.save(empleado);
		System.out.println("Participante guardado con exito!");
		attribute.addFlashAttribute("success", "Participante editado con exito");
		Sorteo sorteo = sorteoService.buscar(empleado.getSorteo().getIdSorteo());
		List<Empleado> participantes = sorteoService.traerParticipantes(sorteo);
		model.addAttribute("titulo", "Participantes");
		model.addAttribute("lista", participantes);
		return "sorteo/participantes";
	}

	@GetMapping("/crear/{idSorteo}")
	public String crearPorSorteo(@PathVariable("idSorteo") int idSorteo, Model model) {
		Sorteo sorteo = sorteoService.buscar(idSorteo);
		Empleado empleado = new Empleado();
		model.addAttribute("titulo", "Formulario: Nuevo Participante");
		List<Sorteo> todosLosSorteos = new ArrayList<Sorteo>();
		todosLosSorteos.add(sorteo);
		model.addAttribute("lista", todosLosSorteos);
		model.addAttribute("empleado", empleado);
		return "empleado/crear";
	}

}
