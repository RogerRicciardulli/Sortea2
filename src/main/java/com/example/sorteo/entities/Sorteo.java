package com.example.sorteo.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "Sorteo")
public class Sorteo implements Comparable<Sorteo> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idSorteo;
	@Column(name = "nombre")
	@NotEmpty(message = "El nombre no debe estar vacio")
	private String nombre;
	@Column(name = "tipo")
	@NotEmpty(message = "El tipo no debe estar vacio")
	private String tipo;
	@Column(name = "fecha")
	@NotNull(message = "La fecha debe seleccionarse")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate fecha;
	@Column(name = "hora")
	@NotNull(message = "La hora debe seleccionarse")
	private LocalTime hora;
	@OneToMany(mappedBy = "sorteo")
	private Set<Empleado> participantes;

	public Sorteo() {
		super();
	}

	public Sorteo(@NotEmpty(message = "El nombre no debe estar vacio") String nombre,
			@NotEmpty(message = "El tipo no debe estar vacio") String tipo,
			@NotNull(message = "La fecha debe seleccionarse") LocalDate fecha,
			@NotNull(message = "La hora debe seleccionarse") LocalTime hora) {
		super();
		this.nombre = nombre;
		this.tipo = tipo;
		this.fecha = fecha;
		this.hora = hora;
	}


	public Sorteo(String nombre, String tipo) {
		super();
		this.nombre = nombre;
		this.tipo = tipo;
	}

	public long getIdSorteo() {
		return idSorteo;
	}

	protected void setIdSorteo(int idSorteo) {
		this.idSorteo = idSorteo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public LocalTime getHora() {
		return hora;
	}

	public void setHora(LocalTime hora) {
		this.hora = hora;
	}

	public Set<Empleado> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(Set<Empleado> participantes) {
		this.participantes = participantes;
	}

	@Override
	public String toString() {
		return "Sorteo [idSorteo=" + idSorteo + ", nombre=" + nombre + ", tipo=" + tipo + ", fecha=" + fecha + ", hora="
				+ hora + ", participantes=" + participantes + "]";
	}
	
	@Override
	public int compareTo(Sorteo otroSorteo) {
		if (this.idSorteo < otroSorteo.idSorteo) {
			return -1;
		} else if (this.idSorteo > otroSorteo.idSorteo) {
			return 1;
		}
		return 0;
	}

}
