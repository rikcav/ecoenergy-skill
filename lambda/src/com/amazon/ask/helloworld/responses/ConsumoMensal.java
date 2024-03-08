package com.amazon.ask.helloworld.responses;

public class ConsumoMensal {
	private Long id;
	private String nome;
	private Double kilowattsHoraTotal;
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

	public Double getKilowattsHoraTotal() {
		return kilowattsHoraTotal;
	}

	public void setKilowattsHoraTotal(Double kilowattsHoraTotal) {
		this.kilowattsHoraTotal = kilowattsHoraTotal;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public String toString() {
		return "ConsumoMensal{" +
				"id=" + id +
				", nome='" + nome + '\'' +
				", kilowattsHoraTotal=" + kilowattsHoraTotal +
				", usuario=" + usuario +
				'}';
	}
}
