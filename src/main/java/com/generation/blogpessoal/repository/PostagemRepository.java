package com.generation.blogpessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.generation.blogpessoal.model.Postagem;


//JPARepository - classe JPA - dentro delas tem metodos que vão realizar query no banco
public interface PostagemRepository extends JpaRepository <Postagem, Long> {
	//SELECT * FROM tb_postagem (JPA) 
	
	public List <Postagem> findAllByTituloContainingIgnoreCase (@Param("titulo") String titulo);
	
	
}
