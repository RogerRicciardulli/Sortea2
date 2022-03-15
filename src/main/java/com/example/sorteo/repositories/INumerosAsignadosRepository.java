package com.example.sorteo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.sorteo.entities.NumerosAsignados;

@Repository("numerosAsignadosRepository")
public interface INumerosAsignadosRepository extends JpaRepository<NumerosAsignados, Long> {
	@Query("SELECT na FROM NumerosAsignados na INNER JOIN FETCH na.empleado e WHERE e.idEmpleado = (:idEmpleado)")
	public abstract NumerosAsignados traerXEmpleado(@Param("idEmpleado") long idEmpleado);
	
	@Query("SELECT MAX(idNumerosAsignados) AS id FROM NumerosAsignados")
	public abstract int obtenerUltimoId();
}
