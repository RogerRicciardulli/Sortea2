package com.example.sorteo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.sorteo.entities.NumerosDelSorteo;
import com.example.sorteo.services.NumerosDelSorteoService;

@Controller
@RequestMapping("/numerosDelSorteo")
public class NumerosDelSorteoController {
	@Autowired
	@Qualifier("numerosDelSorteoService")
	private NumerosDelSorteoService numerosDelSorteoService;
	
	@GetMapping("/lista")
	public String listarNumerosDelSorteo(Model model) {
		List<NumerosDelSorteo> numerosDelSorteos = numerosDelSorteoService.getAll();
		model.addAttribute("titulo", "Numeros de Todos los Sorteos");
		model.addAttribute("lista", numerosDelSorteos);
		return "numerosDelSorteos/lista";
	}
}
