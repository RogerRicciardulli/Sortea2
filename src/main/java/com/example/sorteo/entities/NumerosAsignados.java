package com.example.sorteo.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "NumerosAsignados")
public class NumerosAsignados {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idNumerosAsignados;
	@Column(name = "primero")
	@NotNull
	private int primero;
	@Column(name = "segundo")
	@NotNull
	private int segundo;
	@Column(name = "tercero")
	@NotNull
	private int tercero;
	@Column(name = "cuarto")
	@NotNull
	private int cuarto;
	@Column(name = "quinto")
	@NotNull
	private int quinto;
	@Column(name = "sexto")
	@NotNull
	private int sexto;
	@OneToOne
	@JoinColumn(name = "Empleado_idEmpleado")
	private Empleado empleado;

	public NumerosAsignados() {
		super();
	}

	public NumerosAsignados(@NotNull int primero, @NotNull int segundo, @NotNull int tercero, Empleado empleado) {
		super();
		this.primero = primero;
		this.segundo = segundo;
		this.tercero = tercero;
		this.setEmpleado(empleado);
	}

	public NumerosAsignados(@NotNull int primero, @NotNull int segundo, @NotNull int tercero, @NotNull int cuarto,
			@NotNull int quinto, Empleado empleado) {
		super();
		this.primero = primero;
		this.segundo = segundo;
		this.tercero = tercero;
		this.cuarto = cuarto;
		this.quinto = quinto;
		this.setEmpleado(empleado);
	}

	public NumerosAsignados(@NotNull int primero, @NotNull int segundo, @NotNull int tercero, @NotNull int cuarto,
			@NotNull int quinto, @NotNull int sexto, Empleado empleado) {
		super();
		this.primero = primero;
		this.segundo = segundo;
		this.tercero = tercero;
		this.cuarto = cuarto;
		this.quinto = quinto;
		this.sexto = sexto;
		this.setEmpleado(empleado);
	}

	public long getIdNumerosAsignados() {
		return idNumerosAsignados;
	}

	protected void setIdNumerosAsignados(long idNumerosAsignados) {
		this.idNumerosAsignados = idNumerosAsignados;
	}

	public int getPrimero() {
		return primero;
	}

	public void setPrimero(int primero) {
		this.primero = primero;
	}

	public int getSegundo() {
		return segundo;
	}

	public void setSegundo(int segundo) {
		this.segundo = segundo;
	}

	public int getTercero() {
		return tercero;
	}

	public void setTercero(int tercero) {
		this.tercero = tercero;
	}

	public int getCuarto() {
		return cuarto;
	}

	public void setCuarto(int cuarto) {
		this.cuarto = cuarto;
	}

	public int getQuinto() {
		return quinto;
	}

	public void setQuinto(int quinto) {
		this.quinto = quinto;
	}

	public int getSexto() {
		return sexto;
	}

	public void setSexto(int sexto) {
		this.sexto = sexto;
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	@Override
	public String toString() {
		return "NumerosAsignados [idNumerosAsignados=" + idNumerosAsignados + ", primero=" + primero + ", segundo="
				+ segundo + ", tercero=" + tercero + ", cuarto=" + cuarto + ", quinto=" + quinto + ", sexto=" + sexto
				+ ", empleado=" + empleado + "]";
	}

}
