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
@RequestMapping("/numerosAsignados")
public class NumerosAsignadosController {
	@Autowired
	@Qualifier("numerosAsignadosService")
	private NumerosAsignadosService numerosAsignadosService;
	
	@Autowired
	@Qualifier("numerosDelSorteoService")
	private NumerosDelSorteoService numerosDelSorteoService;

	@Autowired
	@Qualifier("sorteoService")
	private SorteoService sorteoService;
	
	@Autowired
	@Qualifier("empleadoService")
	private EmpleadoService empleadoService;

	@GetMapping("/crear")
	public String crear(Model model) {
		Empleado empleado = empleadoService.buscar(empleadoService.obtenerUltimoId());
		NumerosAsignados numerosAsignados = new NumerosAsignados(0, 0, 0, 0, 0, empleado);
		model.addAttribute("titulo", "Formulario: Nuevo Participante");
		model.addAttribute("numerosAsignados", numerosAsignados);
		return "numerosAsignados/seleccionarValoresSorteo5Numeros";
	}

	@GetMapping("/lista")
	public String listarNumerosAsignados(Model model) {
		List<NumerosAsignados> numerosAsignados = numerosAsignadosService.getAll();
		model.addAttribute("titulo", "Todos los numeros de los participantes");
		model.addAttribute("lista", numerosAsignados);
		return "numerosAsignados/lista";
	}

	@PostMapping("/guardarSorteo5Valores")
	public String guardarSorteo5Valores(@Valid @ModelAttribute NumerosAsignados numerosAsignados, BindingResult result,
			Model model, RedirectAttributes attribute) {
		if (result.hasErrors() || numerosAsignadosService.comprobarQueNoSeRepitanLosNumeros(
				numerosAsignados.getPrimero(), numerosAsignados.getSegundo(), numerosAsignados.getTercero(),
				numerosAsignados.getCuarto(), numerosAsignados.getQuinto())) {
			model.addAttribute("titulo", "Formulario: Asignar Numeros");
			model.addAttribute("numerosAsignados", numerosAsignados);
			attribute.addFlashAttribute("success", "Alguno de los siguientes valores ya esta reservado.");
			System.out.println("Hubo errores en la creacion del los Numeros Asignados!");
			return "redirect:/numerosAsignados/crear";
		}
		numerosAsignadosService.save(numerosAsignados);
		ArrayList<Integer> numeros = new ArrayList<Integer>();
		numeros.add(numerosDelSorteoService.traerXSorteo(numerosAsignados.getEmpleado().getSorteo()).getPrimero());
		numeros.add(numerosDelSorteoService.traerXSorteo(numerosAsignados.getEmpleado().getSorteo()).getSegundo());
		numeros.add(numerosDelSorteoService.traerXSorteo(numerosAsignados.getEmpleado().getSorteo()).getTercero());
		numeros.add(numerosDelSorteoService.traerXSorteo(numerosAsignados.getEmpleado().getSorteo()).getCuarto());
		numeros.add(numerosDelSorteoService.traerXSorteo(numerosAsignados.getEmpleado().getSorteo()).getQuinto());
		sorteoService.comprobarSiGanoConCincoNumeros(numerosAsignados.getEmpleado(), numeros);
		List<Sorteo> listado = new ArrayList<Sorteo>();
		listado.add(numerosAsignados.getEmpleado().getSorteo());
		model.addAttribute("titulo", "Sorteo:");
		model.addAttribute("lista", listado);
		return "sorteo/lista";
	}
	
}
