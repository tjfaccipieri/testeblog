package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;

import jakarta.validation.Valid;

@RestController // controladora de rotas
@RequestMapping ("/postagens") //como chegar no "insomnia"
@CrossOrigin  (origins = "*", allowedHeaders = "*") //libera acesso em outras máquinas/allowedHeard - libera a passagem

public class PostagemController {
	
	@Autowired // injeção de dependencias - instanciar a classe Repository
	private PostagemRepository postagemRepository;
	
	@Autowired
	private TemaRepository temaRepository;
	
	@GetMapping // defini o verbo http que atende o metodo
	public ResponseEntity<List<Postagem>> getAll(){
		//ResponseEntity - CLasse
		return ResponseEntity.ok(postagemRepository.findAll());
		//SELECT * FROM tb_postagens
	}
	
	@GetMapping ("/{id}")
	public ResponseEntity <Postagem> getById (@PathVariable Long id) {
		return postagemRepository.findById(id) // SELECT * FROM tb_postagens WHERE id = x
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
	
	@GetMapping ("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo){
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));
	}
	
	//INSERT INTO tb_postagem (titulo, texto data) VALUES ("Titulo", "Texto", "2024-12-31 14:05:01");
	@PostMapping
	public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem){
		if (temaRepository.existsById(postagem.getTema().getId()))
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(postagemRepository.save(postagem));
		
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tema não existe!", null);
			
	}
	
	//ATUALIZAR O POST CADASTRADO
	@PutMapping
	public ResponseEntity<Postagem> put (@Valid @RequestBody Postagem postagem){
			if (postagemRepository.existsById(postagem.getTema().getId()))
		
		return ResponseEntity.status(HttpStatus.OK)
				.body(postagemRepository.save(postagem));
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tema não existe!", null);
					
	}
		
	// DELETA POST CADASTRADO (DELETE FROM tb_postagens WHERE id=x;)
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete (@PathVariable Long id) {
		Optional<Postagem> postagem = postagemRepository.findById(id);
		
		if(postagem.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		postagemRepository.deleteById(id);
	}
	
}
