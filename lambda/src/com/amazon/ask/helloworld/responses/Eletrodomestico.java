package com.amazon.ask.helloworld.responses;

public class Eletrodomestico {
	private Long id;
	private String nome;
	private Double potencia;
	private Usuario usuario;

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

	public Double getPotencia() {
		return potencia;
	}

	public void setPotencia(Double potencia) {
		this.potencia = potencia;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}
