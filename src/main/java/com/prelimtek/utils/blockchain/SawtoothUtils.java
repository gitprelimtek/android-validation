package com.prelimtek.utils.blockchain;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bitcoinj.core.ECKey;

import org.spongycastle.util.encoders.Hex;

import com.google.common.base.Charsets;
import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessageV3;

import sawtooth.sdk.protobuf.Batch;
import sawtooth.sdk.protobuf.BatchHeader;
import sawtooth.sdk.protobuf.Transaction;
import sawtooth.sdk.protobuf.TransactionHeader;
import sawtooth.sdk.signing.Context;
import sawtooth.sdk.signing.CryptoFactory;
import sawtooth.sdk.signing.PrivateKey;
import sawtooth.sdk.signing.Secp256k1PrivateKey;
import sawtooth.sdk.signing.Signer;
//import sawtooth.sdk.client.Signing;
import sawtooth.sdk.processor.Utils;

public class SawtoothUtils {
	
	static{
		//TODO read  a config file with attribute data
		
	}
	
	public static final String FAMILY = "eatms";
	public static final String VERSION= "1.0";

	public static String calculateAddress(String familyName, byte[] publicKey) throws UnsupportedEncodingException{
		return Utils.hash512(familyName.getBytes(Charsets.UTF_8)).substring(0, 6)
		+ Utils.hash512(publicKey).substring(0, 64);
	}

	public static byte[] hashWith512(byte[] byteOfTextToHash) throws NoSuchAlgorithmException {
	    MessageDigest digest = MessageDigest.getInstance("SHA-512");
	    byte[] hashedByetArray = digest.digest(byteOfTextToHash);
	    return hashedByetArray;
	}
	
	public static String hashWith512(String textToHash) throws NoSuchAlgorithmException {
	    MessageDigest digest = MessageDigest.getInstance("SHA-512");
	    byte[] byteOfTextToHash = textToHash.getBytes(StandardCharsets.UTF_8);
	    byte[] hashedByetArray = digest.digest(byteOfTextToHash);
	    String encoded = Hex.toHexString(hashedByetArray);
	    return encoded;
	}
	
	public static Transaction createTransaction(ByteString encodedPayload, ECKey privateKey, ByteString publicKey,List<String> inputAddresses, List<String> outputAddresses) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		
		ByteString hashedPayload = ByteString.copyFromUtf8(
				Utils.hash512(encodedPayload.toByteArray()));
		
		//Assert.assertTrue(hashedPayload.isValidUtf8());
		
		TransactionHeader header = createTrxnHeader(hashedPayload,publicKey,inputAddresses,outputAddresses);
		
		Transaction.Builder builder = Transaction.newBuilder(); 
		builder
		.setHeader(header.toByteString())
		.setPayload(encodedPayload)
		.setHeaderSignature(createHeaderSignature(header,privateKey));
		
		return builder.build();
	}

	public static Batch createTransactionBatch(List<Transaction> transactionList, ECKey privateKey,ByteString publicKey ){

		BatchHeader header = createBatchHeader(transactionList,publicKey);
		
		Batch.Builder builder = Batch.newBuilder();
		builder.addAllTransactions(transactionList)
		.setHeader(header.toByteString())
		.setHeaderSignature(createHeaderSignature(header,privateKey));
		
		return builder.build();
	}
	
	/**TODO test! This is the most significant change since switching from sawtooth-SNAPSHOT*/
	public static String createHeaderSignature(GeneratedMessageV3 header, ECKey privateKey){
		
		//String signedHeader  = Signing.sign(privateKey,header.toByteArray());
		//ECKey.fromPrivate(privKeyBytes)
		Context context = CryptoFactory.createContext("secp256k1");
		PrivateKey spk_privateKey = Secp256k1PrivateKey.fromHex(privateKey.getPrivateKeyAsHex());
		String signedHeader = new Signer(context,spk_privateKey).sign(header.toByteArray());
		return signedHeader;
	}
	
	
	public static String sign(byte[] data , ECKey privateKey){
		
		//String signedHeader  = Signing.sign(data);
		//ECKey.fromPrivate(privKeyBytes)
		Context context = CryptoFactory.createContext("secp256k1");
		PrivateKey spk_privateKey = Secp256k1PrivateKey.fromHex(privateKey.getPrivateKeyAsHex());
		String signedHeader = new Signer(context,spk_privateKey).sign(data);
		return signedHeader;
	}
	
	public static TransactionHeader createTrxnHeader(ByteString hashed512Data, ByteString pubicKey, List<String> inputAddresses, List<String> outputAddresses){
		
		TransactionHeader.Builder builder =  TransactionHeader.newBuilder()
		.setPayloadSha512Bytes(hashed512Data)
		.setBatcherPublicKeyBytes(pubicKey)
		.setSignerPublicKeyBytes(pubicKey)
		.setFamilyName(FAMILY)
		.setFamilyVersion(VERSION)
		.setNonce(generateNonce())
		.addAllInputs(inputAddresses)
		.addAllOutputs(outputAddresses);
		
		return builder.build();
	}
	
	
	public static BatchHeader createBatchHeader(List<Transaction> transactionList,ByteString publicKeyBytes){
		
		List<String> transactionHeaders = new ArrayList<String>();
		
		for(Transaction t : transactionList){
			transactionHeaders.add(t.getHeaderSignature());
		}
		
		BatchHeader.Builder builder =  BatchHeader.newBuilder()
		.setSignerPublicKeyBytes(publicKeyBytes)
		.addAllTransactionIds(transactionHeaders);

		return builder.build();
	}
	
	public static String generateNonce(){
		return new Random().nextInt()+"";//Math.random();//Hex.toHexString(Long.toString(new Date().getTime()).getBytes());
	}

}
