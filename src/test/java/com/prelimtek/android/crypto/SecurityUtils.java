package com.prelimtek.android.crypto;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.DSAGenParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;


import org.bitcoinj.core.ECKey;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;


/**
 * If you want to generate a key pair for sawtooth
 * Use Signing.generatePrivateKey(new SecureRandom()) */
public class SecurityUtils {
	
	
	
	static{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		//Security.addProvider(new SpongyCastleProvider());
		//Security.addProvider(sun.security.ec.ECPrivateKeyImpl);
	}
	
	public static KeyPair generateSECP256KeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC" ,"BC");
		
		ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
		keyGen.initialize(ecSpec);
		KeyPair kp = keyGen.generateKeyPair();
		
		return kp;
	}
	
	public static KeyPair generateSECP256KeyPair_Spongy(){
		/*
        org.spongycastle.jce.spec.ECNamedCurveParameterSpec secp256k1 = org.spongycastle.jce.ECNamedCurveTable.getParameterSpec("secp256k1");
        org.spongycastle.jce.spec.ECPrivateKeySpec privSpec = new org.spongycastle.jce.spec.ECPrivateKeySpec(new BigInteger(1, inMemoryPrivateKey.getPrivateKeyBytes()), secp256k1);
        KeyFactory keyFactory = KeyFactory.getInstance("EC","SC");

        PrivateKey bcpriv = keyFactory.generatePrivate(privSpec);
        Point pubPoint = inMemoryPrivateKey.getPublicKey().getQ();
        org.spongycastle.math.ec.ECPoint ecpubPoint = new org.spongycastle.math.ec.custom.sec.SecP256K1Curve().createPoint(pubPoint.getX().toBigInteger(), pubPoint.getY().toBigInteger());
        PublicKey publicKey = keyFactory.generatePublic(new org.spongycastle.jce.spec.ECPublicKeySpec(ecpubPoint, secp256k1));
        */
		//new Signer().generateSignature();
		return null;
	}
	
	
	public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec ecSpec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
		//keyGen.initialize(ecSpec);
		keyGen.initialize(1024);
		KeyPair kp = keyGen.generateKeyPair();
		return kp;
	}

	
	/**
	 * primePLen must be 1024, 2048, or 3072")
	 * and primePLen > subPrimePLen
	 * 
	 * Getting Inappropriate parameter message InvalidAlgorithmParameterException
	 * 
	 * TODO investigate further.
	 * */
	public static KeyPair generateDSAKeyPair(int primePLen, int subPrimePLen) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA","SUN");
		DSAGenParameterSpec ecSpec = new DSAGenParameterSpec(primePLen, subPrimePLen,subPrimePLen);
		keyGen.initialize(ecSpec,new SecureRandom());
		//keyGen.initialize(1024, new SecureRandom());
		KeyPair kp = keyGen.generateKeyPair();
		return kp;
	}
	
	
	public static KeyPair generateECKeyPair_112Bit() throws NoSuchAlgorithmException{
		//BigInteger privKey = BigInteger.probablePrime(16, new Random());
		//ECKey key = ECKey.fromPrivate(privKey);
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
		//112 to 512
		keyGen.initialize(160,new SecureRandom());
		return keyGen.generateKeyPair();
	}
	
	public static ECKey toASN1Encoded(KeyPair keyPair) throws IOException{
		
		 PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance(keyPair.getPrivate().getEncoded());
        ASN1Encodable encodable = pkInfo.parsePrivateKey();
        ASN1Primitive primitive = encodable.toASN1Primitive();
        byte[] privBytesEncoded =  primitive.getEncoded();
		 ECKey key = ECKey.fromPrivate(privBytesEncoded);
		 return key;
		 
	}
	
	public static ECKey generateECASN1Encoded_112Bit() throws NoSuchAlgorithmException, IOException{
		return toASN1Encoded(generateECKeyPair_112Bit());
	}
	
	
}
