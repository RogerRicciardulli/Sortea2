package com.example.sorteo.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Empleado")
public class Empleado implements Comparable<Empleado> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idEmpleado;
	@Column(name = "nombre")
	@NotEmpty(message = "El nombre no debe estar vacio")
	private String nombre;
	@Column(name = "apellido")
	@NotEmpty(message = "El apellido no debe estar vacio")
	private String apellido;
	@Column(name = "documento")
	@NotNull
	private long documento;
	@Column(name = "posicionSorteo")
	private int posicionSorteo;
	@ManyToOne
	@JoinColumn(name = "Sorteo_idSorteo")
	private Sorteo sorteo;

	public Empleado() {
		super();
	}

	public Empleado(@NotEmpty(message = "El nombre no debe estar vacio") String nombre,
			@NotEmpty(message = "El apellido no debe estar vacio") String apellido, @NotNull long documento,
			Sorteo sorteo) {
		super();
		this.nombre = nombre;
		this.apellido = apellido;
		this.documento = documento;
		setSorteo(sorteo);
	}

	public long getIdEmpleado() {
		return idEmpleado;
	}

	public void setIdEmpleado(long idEmpleado) {
		this.idEmpleado = idEmpleado;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public long getDocumento() {
		return documento;
	}

	public void setDocumento(long documento) {
		this.documento = documento;
	}

	public int getPosicionSorteo() {
		return posicionSorteo;
	}

	public void setPosicionSorteo(int posicionSorteo) {
		this.posicionSorteo = posicionSorteo;
	}

	public Sorteo getSorteo() {
		return sorteo;
	}

	public void setSorteo(Sorteo sorteo) {
		this.sorteo = sorteo;
	}

	@Override
	public String toString() {
		return "Empleado [idEmpleado=" + idEmpleado + ", nombre=" + nombre + ", apellido=" + apellido + ", documento="
				+ documento + ", posicionSorteo=" + posicionSorteo + "]";
	}

	@Override
	public int compareTo(Empleado otroEmpleado) {
		if(this.posicionSorteo != 0) {
			if (this.posicionSorteo < otroEmpleado.posicionSorteo) {
				return -1;
			} else if (this.posicionSorteo > otroEmpleado.posicionSorteo) {
				return 1;
			}
		} else {
			if (this.idEmpleado < otroEmpleado.idEmpleado) {
				return -1;
			} else if (this.idEmpleado > otroEmpleado.idEmpleado) {
				return 1;
			}
		}
		return 0;
	}

}
