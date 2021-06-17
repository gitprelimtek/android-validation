package com.prelimtek.utils.crypto;

import com.google.protobuf.ByteString;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.crypto.EncryptedData;
import org.spongycastle.util.encoders.Hex;


public class Wallet <T>  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5409453880297356107L;

	private T[]transactionOutputs = null;//TransactionOutputs for this wallet

	/**generated from unencrypted private key*/
	private String publicKeyHex;

	/**May be encrypted or unencrypted*/
	private String privateKeyHex;

	private String initializationVectorHex;

	private boolean encrypted = false;

	/**
	 * These are to be used as hashedEmail, phone etc to be queried by for cloud storage
	 * */
	private String id,id2;

	/*
	private Wallet(){
		generateKeyPairs(null);
	}

	private Wallet(@Nonnull CharSequence keyphrase){
		generateKeyPairs(keyphrase);
	}

	private Wallet(@Nonnull String hashed_idHex, @Nullable String hashed_id2Hex){
		id = hashed_idHex;id2=hashed_id2Hex;
		generateKeyPairs(null);
	}
	*/

	private Wallet(){}

	public Wallet(@Nonnull String hashed_idHex, @Nullable String hashed_id2Hex,@Nonnull CharSequence keyphrase){
		id = hashed_idHex;id2=hashed_id2Hex;
		generateKeyPairs(keyphrase);
	}

	public void generateKeyPairs(@Nullable CharSequence keyphrase){
		ECKey key = BitcoinCryptoUtils.generateBitCoinPrivateKey();// new ECKey(new SecureRandom());
		publicKeyHex = key.getPublicKeyAsHex();

		if(keyphrase!=null) {
			key = BitcoinCryptoUtils.encryptPrivateKey(key, keyphrase);
			EncryptedData encryptedData = key.getEncryptedData();
			privateKeyHex = Hex.toHexString(encryptedData.encryptedBytes);//getPrivateKeyAsHex();
			initializationVectorHex = Hex.toHexString(encryptedData.initialisationVector);
			encrypted = true;
		}else {
			privateKeyHex = key.getPrivateKeyAsHex();
			encrypted = false;
		}
	}

	public Wallet<T> updateWallet(@Nullable  CharSequence newKeyPhrase, @Nullable CharSequence oldKeyPhrase , boolean generateNewKey) throws WalletException {
		if(generateNewKey){
			generateKeyPairs(newKeyPhrase);
		}else{

			ECKey newKey = null;
			if(encrypted)
				newKey = BitcoinCryptoUtils
						.recoverPrimaryKey(
								Hex.decode(initializationVectorHex),
								getPrivateKeyBytes(),
								getPublicKeyBytes(),
								oldKeyPhrase);
			else
				newKey = BitcoinCryptoUtils
						.recoverPrimaryKey(
						getPrivateKeyBytes());


			if(!newKey.hasPrivKey() || newKey.isEncrypted()){
				throw new WalletException("Wallet update failed. Try again.");
			}
			publicKeyHex = newKey.getPublicKeyAsHex();

			privateKeyHex = BitcoinCryptoUtils
					.encryptPrivateKey(
							newKey,
							newKeyPhrase).getPrivateKeyAsHex();

		}

		return this;
	}

	/**
	 * Address = 'WALLET_'+Hex(Sha256(email+phone+6char(Sha256(passphrase))))
	 * Rules preHas.length > 20
	 * */
	@Deprecated
	public static String generateWalletAddress(String email, String phoneNumber)throws WalletException{

		String preHash = email+phoneNumber;
		System.out.println("Prehash -> "+preHash);
		if(preHash.length()<10)throw new WalletException("Email and Phone character Length has to be greater than 10");

		return "WALLET_"+ Sha256Hash.of(preHash.getBytes()).toString();
	}

	public static String generateWalletAddress(UserInterface sModel)throws WalletException{

		//String preHash = strip(sModel.getUserName()+sModel.getEmail()+sModel.getPhoneNumber());
		String preHash = strip(sModel.getEmail()+sModel.getPhoneNumber());
		System.out.println("Prehash -> "+preHash);
		if(preHash.length()<10)throw new WalletException("Email and Phone character Length has to be greater than 10");

		return "WALLET_"+ Sha256Hash.of(preHash.getBytes()).toString();
	}

	private static String strip(String s){
		String ret = null;

		if(s!=null){
			ret=s.replaceAll(" ", "").toLowerCase();
		}
		return ret;
	}

	public String getPublicKeyHex() {
		return publicKeyHex;
	}

	public String getPrivateKeyHex() {
		return privateKeyHex;
	}

	public boolean encrypPrivateKeyHex(CharSequence passPhrase)throws WalletException{

		if(!encrypted){
			ECKey key = BitcoinCryptoUtils.recoverPrimaryKey(getPrivateKeyBytes());
			ECKey enckey = BitcoinCryptoUtils.encryptPrivateKey(key, passPhrase);
			EncryptedData encryptedData = enckey.getEncryptedData();
			privateKeyHex = Hex.toHexString(encryptedData.encryptedBytes);//getPrivateKeyAsHex();
			initializationVectorHex = Hex.toHexString(encryptedData.initialisationVector);
			encrypted = enckey.isEncrypted();
		}else{
			throw new WalletException("PrivateKey is already encrypted.");
		}

		return encrypted;
	}

	public String decryptPrivateKeyHex(CharSequence passPhrase) throws WalletException {
		ECKey ecKey = null;
		if(encrypted)
			ecKey = BitcoinCryptoUtils
					.recoverPrimaryKey(
							Hex.decode(initializationVectorHex),
							Hex.decode(privateKeyHex),getPublicKeyBytes(),
							passPhrase);
		else
			ecKey = BitcoinCryptoUtils.recoverPrimaryKey(
					getPrivateKeyBytes());
		if(ecKey!=null && ecKey.hasPrivKey()){
			privateKeyHex = ecKey.getPrivateKeyAsHex();
			initializationVectorHex = null;
			encrypted = ecKey.isEncrypted();
			return privateKeyHex;
		}else{
			throw new WalletException("PrivateKey not retrieved. Is passphrase correct?");
		}

	}

	public ByteString getPublicKeyUTF8ByteString() {
		ByteString publicKeyByteString = ByteString.copyFromUtf8(publicKeyHex);
		return publicKeyByteString;
	}

	public byte[] getPublicKeyUTF8Bytes() {
		return getPublicKeyUTF8ByteString().toByteArray();
	}

	/***
	 * This method retturn a Hex decoded publicKey hex string.
	 * This is not the same as getPublicKeyUTF8Bytes which does not Hex decode the string.
	 * */
	public byte[] getPublicKeyBytes() {
		return Hex.decode(publicKeyHex);
	}

	public byte[] getPrivateKeyBytes() {
		return Hex.decode(privateKeyHex);
	}

	public String getId() {
		return id;
	}

	public String getId2() {
		return id2;
	}

	public boolean isEncrypted(){return encrypted;}


	public T[] getTransactionOutputs() {
		return transactionOutputs;
	}

	public void setTransactionOutputs(T[] transactionOutputs) {
		this.transactionOutputs = transactionOutputs;
	}

	public String getInitializationVectorHex(){
		return initializationVectorHex;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Wallet)) return false;
		//Wallet<?> wallet = (Wallet<?>) o;
		/*boolean ret;
		ret = Objects.equals(transactionOutputs, wallet.transactionOutputs) &&
				Objects.equals(publicKeyHex, wallet.publicKeyHex) &&
				Objects.equals(privateKeyHex, wallet.privateKeyHex) &&
				Objects.equals(id, wallet.id) &&
				Objects.equals(id2, wallet.id2);*/
		return hashCode()==o.hashCode();
	}

	@Override
	public int hashCode() {
		Object[] elems = new Object[]{transactionOutputs, publicKeyHex, privateKeyHex, id, id2};
		int hash = -1;

		try {

			hash = Objects.hash(elems);

		} catch(RuntimeException e){


			int result = 1;

			for (Object element : elems)
				result = 31 * result + (element == null ? 0 : element.hashCode());

			hash =  result;
		}

		return hash;
	}

	public  static class WalletException extends Exception{
		/**
		 * 
		 */
		private static final long serialVersionUID = -2480207234057205389L;

		public WalletException(String s){super(s);}
	}

	private static Builder<?> builder = null;
	public static <T> Builder<?> newBuilder(){
		if(builder==null){
			builder = new Builder<T>();
		}

		return builder;
	}

	public static class Builder<T>{

		public Builder<T> setTransactionOutputs(T[] transactionOutputs) {
			this.transactionOutputs = transactionOutputs;
			return this;
		}

		public Builder<T> setPublicKeyHex(String publicKeyHex) {
			this.publicKeyHex = publicKeyHex;
			return this;
		}

		public Builder<T> setPrivateKeyHex(String privateKeyHex) {
			this.privateKeyHex = privateKeyHex;
			return this;
		}

		public Builder<T> setId(String id) {
			this.id = id;
			return this;
		}

		public Builder<T> setId2(String id2) {
			this.id2 = id2;
			return this;
		}

		public Builder<T> setEncrypted(boolean encrypted){
			this.encrypted = encrypted;
			return this;
		}

		public Builder<T> setInitializationVectorHex(String initializationVectorHex){
			this.initializationVectorHex=initializationVectorHex;
			return this;
		}

		private T[] transactionOutputs;
		private String publicKeyHex, privateKeyHex, id, id2,initializationVectorHex;
		private boolean encrypted;

		public Wallet<T> build(){
			Wallet<T> wallet = new Wallet<T>();

			wallet.setTransactionOutputs(this.transactionOutputs);
			wallet.privateKeyHex =this.privateKeyHex;
			wallet.publicKeyHex = this.publicKeyHex;
			wallet.id = this.id;
			wallet.id2 = this.id2;
			wallet.encrypted = this.encrypted;
			wallet.initializationVectorHex=this.initializationVectorHex;

			return wallet;
		}

	}
}
