package com.prelimtek.utils.crypto;


import javax.annotation.Nonnull;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.base.Charsets;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Is a simplified class for creating expirable JWT tokens.
 * Default expiration is 2hrs.
 * Default signing algorithm is HS256
 * Note: when using ES (elliptic curve) such ES256 signingkey should be a private key.
 * @author kndungu
 **/
public class JWTManager {

	//The JWT signature algorithm we will be using to sign the token
	private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	//2hrs
	long expirationMillis  = 720000;

	private  String signingKey = null;
	public JWTManager(String _signingKey){
		signingKey =_signingKey;
	}

	public JWTManager(SignatureAlgorithm algo, String _signingKey){
		signatureAlgorithm = algo;
		signingKey =_signingKey;
	}

	public SignatureAlgorithm getSignatureAlgorithm(){
		return signatureAlgorithm;
	}

	public String createJWT(String id, String issuer, String subject, long ttlMillis) {

		long nowMillis = System.currentTimeMillis();
		long expMillis = nowMillis + ttlMillis;
		Date expire = new Date(expMillis);

		return createJWT( id,  issuer,  subject,  expire) ;
	}

	public String createJWT(@Nonnull String id, @Nonnull String issuer, String subject, Date expire ) {

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		//We will sign our JWT with our ApiKey secret
		//byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(signingKey);
		byte[] apiKeySecretBytes = Base64.getEncoder().encode(signingKey.getBytes(Charsets.UTF_8));

		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		//Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder()
				.setId(id)
				.setIssuedAt(now)
				.setSubject(subject)
				.setIssuer(issuer)
				.signWith(signatureAlgorithm, signingKey);

		//if it has been specified, let's add the expiration
		if(expire!=null)
			builder.setExpiration(expire);

		//TODO add header based on data and expiration for verification

		//Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}

	public Claims parseJWT(String jwt) {

		//This line will throw an exception if it is not a signed JWS (as expected)
		byte[] apiKeySecretBytes = Base64.getEncoder().encode(signingKey.getBytes(Charsets.UTF_8));

		Claims claims = Jwts
				.parser()
				.setSigningKey(apiKeySecretBytes)
				.parseClaimsJws(jwt)
				.getBody();

		return claims;
	}

	public static Claims parseJWT(String signingKey, String jwt) {

		byte[] apiKeySecretBytes = Base64.getEncoder().encode(signingKey.getBytes(Charsets.UTF_8));

		//This line will throw an exception if it is not a signed JWS (as expected)
		Claims claims = Jwts.parser()
				.setSigningKey(apiKeySecretBytes)
				.parseClaimsJws(jwt).getBody();

		return claims;
	}

	public Jwt<Header,Claims> parseJWTAndHeader(String jwt) {

		//This line will throw an exception if it is not a signed JWS (as expected)
		byte[] apiKeySecretBytes = Base64.getEncoder().encode(signingKey.getBytes(Charsets.UTF_8));

		Jwt<Header,Claims> headClaims = Jwts
				.parser()
				.setSigningKey(apiKeySecretBytes)
				.parseClaimsJwt(jwt);

		Claims claims	= headClaims.getBody();
		Header header =  headClaims.getHeader();


		return headClaims;
	}
	//UTILITIES

	public static Date incrementDate(Date date, int days){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	public static Date incrementMillis(Date date, int millis){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MILLISECOND , millis);
		return cal.getTime();
	}

	public static Date incrementHours(Date date, int hours){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR , hours);
		return cal.getTime();
	}

	public static String createJWT(String id, String subject , String signingKey, int expiration_milliseconds){
		JWTManager mn =  new JWTManager(signingKey);
		long expiration = JWTManager.incrementMillis(new Date(),expiration_milliseconds).getTime();
		String jwt =  mn.createJWT(id,"mtini",subject, expiration );

		//Log.(TAG,jwt);

		return jwt;
	}

}
