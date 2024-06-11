package com.generation.blogpessoal.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity //classe vai ser uma entidade do banco de dados (tabela)
@Table (name="tb_postagens") // nomeando a tabela no banco de dados

public class Postagem {
	
	@Id // torna a Primary Key no database
	@GeneratedValue(strategy=GenerationType.IDENTITY) //tornando a Primary key auto-increment
	private Long id;
	
	@NotBlank(message = "O atributo TITULO é obrigatório!") //validation - valida atributo NN	
	@Size (min = 5, max = 100, message= "O atributo TITULO deve ter no mínimo 5 caracteres e no máximo 100 carateres.")
	private String titulo;
	
	@NotBlank(message = "O atributo TEXTO é obrigatório!") //validation - valida atributo NN	
	@Size (min = 10, max = 1000, message= "O atributo TEXTO deve ter no mínimo 10 caracteres e no máximo 1000 carateres.")
	private String texto;
	
	@UpdateTimestamp //pega a data e hora do sistema e preenche no banco de dados
	private LocalDateTime data;
	
	@ManyToOne
	@JsonIgnoreProperties("postagem")
	private Tema tema;

	@ManyToOne
	@JsonIgnoreProperties("postagem")
	private Usuario Usuario;
	
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

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	public Tema getTema() {
		return tema;
	}

	public void setTema(Tema tema) {
		this.tema = tema;
	}

	public Usuario getUsuario() {
		return Usuario;
	}

	public void setUsuario(Usuario Usuario) {
		this.Usuario = Usuario;
	}
	
}
