package com.prelimtek.android.crypto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import sawtooth.sdk.client.Signing;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.EncryptedData;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.protobuf.InvalidProtocolBufferException;

import io.jsonwebtoken.impl.crypto.EllipticCurveProvider;

public class TestSigning {
	
	KeyPair kp;
	
	@Before
	public void init() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException, UnsupportedEncodingException{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		//kp = SecurityUtils.generateECKeyPair_112Bit();
		//kp = SecurityUtils.generateRSAKeyPair();
		kp = SecurityUtils.generateSECP256KeyPair();
		//kp = SecurityUtils.generateDSAKeyPair(1024, 160);
				
	}
	
	@Test
	public void testSigningwithEC() throws IOException, NoSuchAlgorithmException{
		
		 PrivateKey privKey = kp.getPrivate();
		 PublicKey pubKey = kp.getPublic();
		 //ECPair keyPair = new ECPair(privKey, pubKey);
		 
		 ECKey key = ECKey.fromPrivate(privKey.getEncoded());//new ECKey();
		 //ECKey key = SecurityUtils.toASN1Encoded(kp);
		 
		 byte[] publicKey = key.getPubKey();
		 
		 String signed = Signing.sign(key, "Some data".getBytes(Charsets.UTF_8));

		 System.out.println(signed); 
		 
		 

	}
	
	@Test 
	public void testSignWithJwtProvider(){
		
		kp = EllipticCurveProvider.generateKeyPair();
		
		PrivateKey privKey = kp.getPrivate();
		
		PublicKey pubKey = kp.getPublic();
		
		ECKey key = ECKey.fromPrivate(privKey.getEncoded());
	
		byte[] publicKey = key.getPubKey();
		 
		 String signed = Signing.sign(key, "Some data".getBytes(Charsets.UTF_8));

		 System.out.println(signed);
		 
		 
	}
	
	/*
	public static ECDHKeySet getSharedSecret(ECKey keyServer, ECKey keyClient) {
	    try {
	        //Security.addProvider(new BouncyCastleProvider());
	        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	        AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC", "SunEC");
	        parameters.init(new ECGenParameterSpec("secp256k1"));
	        ECParameterSpec ecParameters = parameters.getParameterSpec(ECParameterSpec.class);
	        ECPrivateKeySpec specPrivate = new ECPrivateKeySpec(keyServer.getPrivKey(), ecParameters);
	        ECPublicKeySpec specPublic = new ECPublicKeySpec(new ECPoint(keyClient.getPubKeyPoint().getXCoord().toBigInteger(), keyClient.getPubKeyPoint().getYCoord().toBigInteger()), ecParameters);
	        KeyFactory kf = KeyFactory.getInstance("EC");
	        ECPrivateKey privateKey = (ECPrivateKey) kf.generatePrivate(specPrivate);
	        ECPublicKey publicKey = (ECPublicKey) kf.generatePublic(specPublic);
	        
	        JCEECPrivateKey ecPrivKey = new JCEECPrivateKey(privateKey);
	        JCEECPublicKey ecPubKey = new JCEECPublicKey(publicKey);
	        new ECKey().getKeyCrypter();
	        
	        KeyAgreement aKeyAgree = KeyAgreement.getInstance("ECDH");
	        aKeyAgree.init(ecPrivKey);
	        aKeyAgree.doPhase(ecPubKey, true);
	        
	        return new ECDHKeySet(aKeyAgree.generateSecret(), keyServer.getPubKey(), keyClient.getPubKey());
	        
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }

	}
	*/
	
	
	
	@Test
	public void testSigningWithKeyPair() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, kp.getPrivate());
		byte[] encrypted = cipher.doFinal("Some data".getBytes());
		System.out.println(new String(encrypted));
		
		
		cipher.init(Cipher.DECRYPT_MODE, kp.getPublic());
		byte[] decrypted = cipher.doFinal(encrypted);
		System.out.println(new String(decrypted));
	}
	

	
	@Test 
	public void testGenerateSignatureWithSawtooth() throws SignatureException, InvalidProtocolBufferException{
		String msg = "Some data";
		
		ECKey privateKey = Signing.generatePrivateKey(new SecureRandom());
		NetworkParameters params = NetworkParameters.fromID(NetworkParameters.ID_MAINNET);
		
		String wiF = privateKey.getPrivateKeyAsWiF(params);
		System.out.println("Wif : "+wiF);
		System.out.println("Pri : "+privateKey.getPrivateKeyAsHex());
		System.out.println("Pub : "+privateKey.getPublicKeyAsHex() );
		

		byte[] publicKey = privateKey.getPubKey();
		
		BigInteger privKeyInt = privateKey.getPrivKey();
		ECKey regenPrivate = Signing.readWif(wiF);
		
		String encrypted = Signing.sign(privateKey, msg.getBytes(Charsets.UTF_8));
		
		System.out.println(encrypted);
		
		//privateKey.verifyMessage(msg, encrypted);
		//EncryptedData encryptedObj = (EncryptedData)org.bitcoinj.wallet.Protos.EncryptedData.parseFrom(encrypted.getBytes());
		regenPrivate.fromEncrypted( 
				new EncryptedData (null,encrypted.getBytes()), 
				regenPrivate.getKeyCrypter(), 
				publicKey);
		//System.out.println("verified = "+verified);

		
	}

}
