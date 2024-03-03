package com.amazon.ask.helloworld.responses;

public class Usuario {
	private Long id;
	private String nome;
	private Double tarifa;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Double getTarifa() {
		return tarifa;
	}

	public void setTarifa(Double tarifa) {
		this.tarifa = tarifa;
	}
}
