package com.amazon.ask.helloworld.responses;

public class ConsumoUnico {
	private Long id;
	private Double horasUso;
	private Eletrodomestico eletrodomestico;
	private ConsumoMensal consumoMensal;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getHorasUso() {
		return horasUso;
	}

	public void setHorasUso(Double horasUso) {
		this.horasUso = horasUso;
	}

	public Eletrodomestico getEletrodomestico() {
		return eletrodomestico;
	}

	public void setEletrodomestico(Eletrodomestico eletrodomestico) {
		this.eletrodomestico = eletrodomestico;
	}

	public ConsumoMensal getConsumoMensal() {
		return consumoMensal;
	}

	public void setConsumoMensal(ConsumoMensal consumoMensal) {
		this.consumoMensal = consumoMensal;
	}

	@Override
	public String toString() {
		return "ConsumoUnico{" +
				"id=" + id +
				", horasUso=" + horasUso +
				", eletrodomestico=" + eletrodomestico +
				", consumoMensal=" + consumoMensal +
				'}';
	}
}
