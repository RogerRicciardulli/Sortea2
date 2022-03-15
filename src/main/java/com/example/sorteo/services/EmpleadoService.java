package com.example.sorteo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.sorteo.entities.Empleado;
import com.example.sorteo.entities.NumerosAsignados;
import com.example.sorteo.repositories.IEmpleadoRepository;

@Service("empleadoService")
public class EmpleadoService {
	@Autowired
	@Qualifier("empleadoRepository")
	private IEmpleadoRepository empleadoRepository;

	public List<Empleado> getAll() {
		return empleadoRepository.findAll();
	}

	public void save(Empleado empleado) {
		empleadoRepository.save(empleado);
	}

	public Empleado buscar(long id) {
		return empleadoRepository.findById(id).orElse(null);
	}

	public Optional<Empleado> list(long id) {
		return empleadoRepository.findById(id);
	}

	public void eliminar(long id) {
		empleadoRepository.deleteById(id);
	}

	public int obtenerUltimoId() {
		return empleadoRepository.obtenerUltimoId();
	}

	public Empleado traerXDocumento(long documento) {
		return empleadoRepository.traerXDocumento(documento);
	}

	public void crearNumerosAsignadosDeAcuerdoAlTipoDeSorteo(Empleado empleado,
			NumerosDelSorteoService numerosDelSorteoService, NumerosAsignadosService numerosAsignadosService,
			SorteoService sorteoService) {
		if (empleado.getSorteo().getTipo().equals("Sorteo Normal")) {
			generarNumerosAsignados0ParaUnParticipante(empleado, numerosAsignadosService);
		}
		if (empleado.getSorteo().getTipo().equals("Sorteo 3 Valores")) {
			generarNumerosAsignadosAleatoriosParaUnParticipante(empleado, numerosDelSorteoService,
					numerosAsignadosService, sorteoService);
		}
	}

	public void generarNumerosAsignados0ParaUnParticipante(Empleado empleado,
			NumerosAsignadosService numerosAsignadosService) {
		NumerosAsignados numerosAsignadosAux = new NumerosAsignados(0, 0, 0, 0, 0, 0, empleado);
		numerosAsignadosService.save(numerosAsignadosAux);
	}

	public void generarNumerosAsignadosAleatoriosParaUnParticipante(Empleado empleado,
			NumerosDelSorteoService numerosDelSorteoService, NumerosAsignadosService numerosAsignadosService,
			SorteoService sorteoService) {
		int[] numerosAleatoriosGanadores = new int[3];
		int[] numerosAleatoriosAsignados = new int[3];
		numerosAleatoriosGanadores[0] = numerosDelSorteoService.buscar(empleado.getSorteo().getIdSorteo()).getPrimero();
		numerosAleatoriosGanadores[1] = numerosDelSorteoService.buscar(empleado.getSorteo().getIdSorteo()).getSegundo();
		numerosAleatoriosGanadores[2] = numerosDelSorteoService.buscar(empleado.getSorteo().getIdSorteo()).getTercero();
		for (int i = 0; i < 3; i++) {
			numerosAleatoriosAsignados[i] = (int) Math.floor(Math.random() * (10 - 1 + 1)) + 1;
		}
		NumerosAsignados numerosAsignadosAux = new NumerosAsignados(0, 0, 0, 0, 0, 0, empleado);
		numerosAsignadosAux.setPrimero(numerosAleatoriosAsignados[0]);
		numerosAsignadosAux.setSegundo(numerosAleatoriosAsignados[1]);
		numerosAsignadosAux.setTercero(numerosAleatoriosAsignados[2]);
		numerosAsignadosService.save(numerosAsignadosAux);
		sorteoService.comprobarSiGano(empleado, numerosAleatoriosGanadores);
	}

	public void generar6NumerosAsignadosAleatoriosParaUnParticipante(Empleado empleado,
			NumerosDelSorteoService numerosDelSorteoService, NumerosAsignadosService numerosAsignadosService,
			SorteoService sorteoService) {
		ArrayList<Integer> numeros = new ArrayList<Integer>();
		ArrayList<Integer> numerosGanadores = new ArrayList<Integer>();
		numeros = sorteoService.generarSeisNumerosAleatorios();
		numerosGanadores.add(numerosDelSorteoService.buscar(empleado.getSorteo().getIdSorteo()).getPrimero());
		numerosGanadores.add(numerosDelSorteoService.buscar(empleado.getSorteo().getIdSorteo()).getSegundo());
		numerosGanadores.add(numerosDelSorteoService.buscar(empleado.getSorteo().getIdSorteo()).getTercero());
		numerosGanadores.add(numerosDelSorteoService.buscar(empleado.getSorteo().getIdSorteo()).getCuarto());
		numerosGanadores.add(numerosDelSorteoService.buscar(empleado.getSorteo().getIdSorteo()).getQuinto());
		numerosGanadores.add(numerosDelSorteoService.buscar(empleado.getSorteo().getIdSorteo()).getSexto());
		numerosAsignadosService.save(new NumerosAsignados(numeros.get(0), numeros.get(1), numeros.get(2),
				numeros.get(3), numeros.get(4), numeros.get(5), empleado));
		sorteoService.comprobarSiGanoConSeisNumeros(empleado, numerosGanadores);
	}

}
