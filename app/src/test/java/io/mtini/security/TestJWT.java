package io.mtini.security;

import com.prelimtek.utils.crypto.JWTManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

public class TestJWT {
	JWTManager mn = null;
	@Before
	public void init(){
		mn =  new JWTManager("kaniu");
		
	}
	
	@Test(expected=ExpiredJwtException.class)
	public void testTokenExpiration(){
		String jwt = mn.createJWT("testid", "ritho", "mysubject", 1);

		
		mn.parseJWT(jwt);
		
	}
	
	@Test
	public void testParseNewJwt(){
		String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMDU4NjYxMDcxNjI3NDEiLCJpYXQiOjE1NDY4NjE2MzYsInN1YiI6IlNvcGhpYSBBbGNiYWhqZWZpamRhIEdyZWVuZW1hbiIsImlzcyI6Im10aW5pIiwiZXhwIjozMDk1Nzk2ODcyfQ.GLlHUAib1OzVrog2uxr4MP3d2ooGRZfTZX2ivuQ-HMM";
		//String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMDU4NjYxMDcxNjI3NDEiLCJpYXQiOjE1NDY4NTk2NTIsInN1YiI6IlNvcGhpYSBBbGNiYWhqZWZpamRhIEdyZWVuZW1hbiIsImlzcyI6ImFraWxpIiwiZXhwIjoxNTU1NDk2MDUyfQ.FTup35j-u4YoV0SI5hNnDssxLwq0LHLKxUFpxkM4kcU";
		Claims claim = new JWTManager("kaniu").parseJWT(jwt);
		 /*Claims claim = Jwts
		    		.parser()         
		    		.setSigningKey("kaniu")
		    		.parseClaimsJws(jwt)
		    		.getBody();
		    */
		System.out.println(claim.getId());
		System.out.println(claim.getIssuer());
		System.out.println(claim.getSubject() );
		System.out.println(claim.getExpiration() );
		
	}
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test()
	public void testInvalidToken(){
		String jwt = mn.createJWT("testid", "ritho", "mysubject", 1000);
		
		thrown.expect(SignatureException.class);
		thrown.expectMessage("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted");
		
		jwt = jwt+"randomstring";
		mn.parseJWT(jwt);
		
	}
	
	
	@Test
	public void testGoodToken() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
		
		String jwt = mn.createJWT("105866107162741", "akili", "Sophia Alcbahjefijda Greeneman", JWTManager.incrementDate(new Date(), 100));
		System.out.println(jwt);
		new JWTManager("kaniu").parseJWT(jwt);
		
	}


	@Test
	public void testGoodTokenWithPayload() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {

		thrown.expect(java.lang.IllegalStateException.class);

		String jwt = mn.createJWT("somekey", "akili", "100 day JWT token", "Some payload",JWTManager.incrementDate(new Date(), 100));
		System.out.println(jwt);

		mn.parseJWT(jwt);

	}


}
