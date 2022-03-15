package com.example.sorteo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.sorteo.entities.Empleado;

@Repository("empleadoRepository")
public interface IEmpleadoRepository extends JpaRepository<Empleado, Long> {
	@Query("SELECT e FROM Empleado e WHERE e.documento = (:documento)")
	public abstract Empleado traerXDocumento(@Param("documento") long documento);
	
	@Query("SELECT MAX(idEmpleado) AS id FROM Empleado")
	public abstract int obtenerUltimoId();
}
