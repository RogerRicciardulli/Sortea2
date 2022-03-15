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
@Table(name = "NumerosDelSorteo")
public class NumerosDelSorteo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idNumerosDelSorteo;
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
	@JoinColumn(name = "Sorteo_idSorteo")
	private Sorteo sorteo;

	public NumerosDelSorteo() {
		super();
	}

	public NumerosDelSorteo(int primero, int segundo, int tercero, int cuarto, int quinto, int sexto, Sorteo sorteo) {
		super();
		this.primero = primero;
		this.segundo = segundo;
		this.tercero = tercero;
		this.cuarto = cuarto;
		this.quinto = quinto;
		this.sexto = sexto;
		this.setSorteo(sorteo);
	}

	public NumerosDelSorteo(int primero, int segundo, int tercero, int cuarto, int quinto, Sorteo sorteo) {
		super();
		this.primero = primero;
		this.segundo = segundo;
		this.tercero = tercero;
		this.cuarto = cuarto;
		this.quinto = quinto;
		this.setSorteo(sorteo);
	}

	public NumerosDelSorteo(int primero, int segundo, int tercero, Sorteo sorteo) {
		super();
		this.primero = primero;
		this.segundo = segundo;
		this.tercero = tercero;
		this.setSorteo(sorteo);
	}

	public long getIdNumerosDelSorteo() {
		return idNumerosDelSorteo;
	}

	protected void setIdNumerosDelSorteo(long idNumerosDelSorteo) {
		this.idNumerosDelSorteo = idNumerosDelSorteo;
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

	public Sorteo getSorteo() {
		return sorteo;
	}

	public void setSorteo(Sorteo sorteo) {
		this.sorteo = sorteo;
	}

	@Override
	public String toString() {
		return "NumerosDelSorteo [idNumerosDelSorteo=" + idNumerosDelSorteo + ", primero=" + primero + ", segundo="
				+ segundo + ", tercero=" + tercero + ", cuarto=" + cuarto + ", quinto=" + quinto + ", sexto=" + sexto
				+ "]";
	}

}
