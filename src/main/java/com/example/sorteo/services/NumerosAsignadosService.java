package com.example.sorteo.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.sorteo.entities.Empleado;
import com.example.sorteo.entities.NumerosAsignados;
import com.example.sorteo.repositories.INumerosAsignadosRepository;

@Service("numerosAsignadosService")
public class NumerosAsignadosService {
	@Autowired
	@Qualifier("numerosAsignadosRepository")
	private INumerosAsignadosRepository numerosAsignadosRepository;

	public List<NumerosAsignados> getAll() {
		return numerosAsignadosRepository.findAll();
	}

	public void save(NumerosAsignados numerosAsignados) {
		numerosAsignadosRepository.save(numerosAsignados);
	}

	public NumerosAsignados buscar(long id) {
		return numerosAsignadosRepository.findById(id).orElse(null);
	}

	public void eliminar(long id) {
		numerosAsignadosRepository.deleteById(id);
	}

	public int obtenerUltimoId() {
		return numerosAsignadosRepository.obtenerUltimoId();
	}

	public NumerosAsignados traerXEmpleado(Empleado empleado) {
		return numerosAsignadosRepository.traerXEmpleado(empleado.getIdEmpleado());
	}

	public boolean comprobarQueNoSeRepitanLosNumeros(int primero, int segundo, int tercero, int cuarto, int quinto) {
		List<NumerosAsignados> numerosAsignadosConCinco = traerNumerosAsignadosConCincoNumeros();
		boolean flag = false;
		for (NumerosAsignados na : numerosAsignadosConCinco) {
			if (na.getPrimero() == primero || na.getPrimero() == segundo || na.getPrimero() == tercero
					|| na.getPrimero() == cuarto || na.getPrimero() == quinto || na.getSegundo() == primero
					|| na.getSegundo() == segundo || na.getSegundo() == tercero || na.getSegundo() == cuarto
					|| na.getSegundo() == quinto || na.getTercero() == primero || na.getTercero() == segundo
					|| na.getTercero() == tercero || na.getTercero() == cuarto || na.getTercero() == quinto
					|| na.getCuarto() == primero || na.getCuarto() == segundo || na.getCuarto() == tercero
					|| na.getCuarto() == cuarto || na.getCuarto() == quinto || na.getQuinto() == primero
					|| na.getQuinto() == segundo || na.getQuinto() == tercero || na.getQuinto() == cuarto
					|| na.getQuinto() == quinto) {
				flag = true;
			}
		}
		return flag;
	}

	public List<NumerosAsignados> traerNumerosAsignadosConCincoNumeros() {
		List<NumerosAsignados> todosLosNumeros = getAll();
		List<NumerosAsignados> numerosAsignadosConCinco = new ArrayList<NumerosAsignados>();
		for (NumerosAsignados na : todosLosNumeros) {
			if (na.getSexto() == 0 && na.getQuinto() != 0) {
				numerosAsignadosConCinco.add(na);
			}
		}
		return numerosAsignadosConCinco;
	}
}
