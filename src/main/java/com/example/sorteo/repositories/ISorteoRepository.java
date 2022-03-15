package com.example.sorteo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.sorteo.entities.Sorteo;

@Repository("sorteoRepository")
public interface ISorteoRepository extends JpaRepository<Sorteo, Long> {
	@Query("SELECT s FROM Sorteo s WHERE s.nombre = (:nombre)")
	public abstract Sorteo traerPorNombre(@Param("nombre") String nombre);
	
	@Query("SELECT s FROM Sorteo s WHERE s.tipo = (:tipo)")
	public abstract List<Sorteo> listar(@Param("tipo") String tipo);
	
	@Query("SELECT MAX(idSorteo) AS id FROM Sorteo")
	public abstract int obtenerUltimoId();
	
}
