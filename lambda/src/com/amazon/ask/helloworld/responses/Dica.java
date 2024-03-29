package com.amazon.ask.helloworld.responses;

public class Dica {
	private Long id;
	private String titulo;
	private String descricao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public String toString() {
		return "Dica{" +
				"id=" + id +
				", titulo='" + titulo + '\'' +
				", descricao='" + descricao + '\'' +
				'}';
	}
}
