package com.amazon.ask.helloworld.responses;

public class ConsumoMensal {
	private Long id;
	private String nome;
	private Double killowattsHoraTotal;
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

	public Double getKillowattsHoraTotal() {
		return killowattsHoraTotal;
	}

	public void setKillowattsHoraTotal(Double killowattsHoraTotal) {
		this.killowattsHoraTotal = killowattsHoraTotal;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
