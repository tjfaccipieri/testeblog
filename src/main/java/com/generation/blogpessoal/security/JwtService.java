package com.generation.blogpessoal.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

	public static final String SECRET = "7ee9c40e2d827826aba45e7fd48f7541ec7a70ad38c0ad8bb0c291ae680f350f";
	
	private Key getSingKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	/*
	 * claims - declarações Usuario /declaração data que expira / declaração da assinatura
	 * nesse caso assinatura
	 * /*
	 * esse método tem como objeto retornar todas as claims inseridas no Payload do token
	 */
	
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder() 
				.setSigningKey(getSingKey()).build()
				.parseClaimsJws(token).getBody();
	}
	
	//peg as informações  extraidas e trata ela para tornar ela entendivel 
		/*
		 * esse método tem como saida um dado do tipo T qu é um tipo generico de dado
		 * para isso vamos passar a ele dois parametros um token String e uma função Java
		 */

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	/*
	 * recuperar os dados da parte sub do claim onde encontramos o email(Usuario)
	 */

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	/*
	 * data que o token expira
	 */

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	/*
	 * valida se a data que o token expira esta dentro da validade ou seja a data atual ainda não atingiu essa data 
	 */

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	/*
	 * validar se o Usuario que foi extraido do token condiz com o Usuario que a userDetails tem e se esta dentro da
	 * data de validade ainda o token
	 */

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
	/*
	 * objetivo pe caLcular o tempo de validade do token, formar o claim com as informações do token
	 * o calculo que estamos fazendo para formar a data de validade do token esta no cookbook pode ser aumentado ou diminuido conforme for melhor para a experiência do seu usuário
	 */

	private String createToken(Map<String, Object> claims, String userName) {
		return Jwts.builder()
					.setClaims(claims)
					.setSubject(userName)
					.setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
					.signWith(getSingKey(), SignatureAlgorithm.HS256).compact();
	}
	
	/*
	 * gerar o token puxando os claims formados no metodo anterior
	 */

	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userName);
	}
}
