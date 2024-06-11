package com.generation.blogpessoal.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * objetivos da classe
 * trazer as validações do token feitas na JWTService
 * confirmar se o token esta chegando pelo HEader quando o usuário ja estiver logado
 * tratar o token
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	// injeção de dependencias para validação do token
	// injeção de dependencias para validação do token
	@Autowired
	private JwtService jwtService;

	// injeção de dependencias da classe que conversa com banco e valida Usuario se
	// existe
	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		// informando que o insomnia o token vem via header e com a nomenclatura
		// Authorization
		String authHeader = request.getHeader("Authorization");
		// inicio null
		String token = null;
		// inica user
		String username = null;

		try {
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				// metodo string retirando 7 caracteres
				token = authHeader.substring(7);
				username = jwtService.extractUsername(token); // chama a jwtService para extrair o usuário que esta no
																// token
			}

			// validação de existe um user name que foi extraido do token e não temos regras
			// configuradas de autorização
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				// validando se o usuário extraido do token existe no banco
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				// if valida o token
				if (jwtService.validateToken(token, userDetails)) {

					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
							null, userDetails.getAuthorities());

					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);

				}

			}
			filterChain.doFilter(request, response);

			// aqui vamos ter o status setado para caso o usuário não for validado por algum
			// motivo
			// motivos = token expirou, usuário não existe no banco de dados
		} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException 
				| SignatureException | ResponseStatusException e) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			return;
		}

	}

}