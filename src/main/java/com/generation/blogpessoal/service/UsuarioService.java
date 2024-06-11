package com.generation.blogpessoal.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.security.JwtService;

@Service //Estamos tratando regras de negocios
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	/* autenticar o Usuario
	*classe do security que tem gestão de autenticação
	*permite acessar metodos que podem entregar ao objeto as suas autoridade concedidas
	*/
	
	public Optional<Usuario> cadastrarUsuario(Usuario Usuario){
		// nome | Usuario (email) | senha | foto		
		if(usuarioRepository.findByUsuario(Usuario.getUsuario()).isPresent())
			return Optional.empty(); // se o objeto estiver vazio 
		
		Usuario.setSenha(criptografarSenha(Usuario.getSenha()));
		return Optional.of(usuarioRepository.save(Usuario));
	}
	
	// vai tratar para a senha ser criptografada antes de ser persistida no banco 
	
	private String criptografarSenha(String senha) {
		// Classe que trata a criptografia
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(senha); // encoder sendo aplicado na senha
	}
	
	// segundo problema
	
	public Optional<Usuario> atualizarUsuario (Usuario Usuario) {
		if(usuarioRepository.findById(Usuario.getId()).isPresent()){
			
			Optional<Usuario> buscaUsuario = usuarioRepository.findByUsuario(Usuario.getUsuario());
			
			if(buscaUsuario.isPresent() && (buscaUsuario.get().getId()) != Usuario.getId())
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe", null);
				
			Usuario.setSenha(criptografarSenha(Usuario.getSenha()));	

			return Optional.ofNullable(usuarioRepository.save(Usuario));
		}
		return Optional.empty();
	}
	public Optional <UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin){
		
		var credenciais = new UsernamePasswordAuthenticationToken(usuarioLogin.get().getUsuario(),
			usuarioLogin.get().getSenha());	
		
		//tiver esse Usuario e senha
				Authentication authentication = authenticationManager.authenticate(credenciais);
				
		if(authentication.isAuthenticated()){
					
					Optional<Usuario> Usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());		
		
		if (Usuario.isPresent()) {
			
			usuarioLogin.get().setId(Usuario.get().getId());
			usuarioLogin.get().setNome(Usuario.get().getNome());
			usuarioLogin.get().setFoto(Usuario.get().getFoto());
			usuarioLogin.get().setToken(gerarToken(usuarioLogin.get().getUsuario()));
			usuarioLogin.get().setSenha("");
			
		return usuarioLogin;
	}
		}
	
	return Optional.empty();
}
	/*
	 * metodo que usa o jwt para gerar o token do Usuario
	 * 
	 */
	
		private String gerarToken(String usuario) {
			return "Bearer "+jwtService.generateToken(usuario);
		}
}
