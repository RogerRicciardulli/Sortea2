package com.example.sorteo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.sorteo.entities.NumerosDelSorteo;

@Repository("numerosDelSorteoRepository")
public interface INumerosDelSorteoRepository extends JpaRepository<NumerosDelSorteo, Long> {
	@Query("SELECT ns FROM NumerosDelSorteo ns INNER JOIN FETCH ns.sorteo s WHERE s.idSorteo = (:idSorteo)")
	public abstract NumerosDelSorteo traerXSorteo(@Param("idSorteo") long idSorteo);
	
	@Query("SELECT MAX(idNumerosDelSorteo) AS id FROM NumerosDelSorteo")
	public abstract int obtenerUltimoId();
}
