package com.example.sorteo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.sorteo.entities.NumerosDelSorteo;
import com.example.sorteo.entities.Sorteo;
import com.example.sorteo.repositories.INumerosDelSorteoRepository;

@Service("numerosDelSorteoService")
public class NumerosDelSorteoService {
	@Autowired
	@Qualifier("numerosDelSorteoRepository")
	private INumerosDelSorteoRepository numerosDelSorteoRepository;

	public List<NumerosDelSorteo> getAll() {
		return numerosDelSorteoRepository.findAll();
	}

	public void save(NumerosDelSorteo numerosDelSorteo) {
		numerosDelSorteoRepository.save(numerosDelSorteo);
	}

	public NumerosDelSorteo buscar(long id) {
		return numerosDelSorteoRepository.findById(id).orElse(null);
	}

	public void eliminar(long id) {
		numerosDelSorteoRepository.deleteById(id);
	}
	
	public int obtenerUltimoId() {
		return numerosDelSorteoRepository.obtenerUltimoId();
	}
	
	public NumerosDelSorteo traerXSorteo(Sorteo sorteo) {
		return numerosDelSorteoRepository.traerXSorteo(sorteo.getIdSorteo());
	}
	
}
