package com.prelimtek.android.crypto;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import java.security.SecureRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.ECKey.ECDSASignature;
import org.bitcoinj.crypto.EncryptedData;
import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.encoders.Base64;
import org.spongycastle.util.encoders.Hex;


public class BitcoinCryptoUtils {
	
	public static ECKey generateBitCoinPrivateKey(){
		ECKey key = new ECKey(new SecureRandom());
		return key;
	}
	
	/**Expects 128,192,256 bit = 16, 24,32 bytes
	 * Therefpre appends 0s to satisfy max.**/
	public static CharSequence generatePassPhrase(@Nonnull CharSequence passPhrase, boolean hashed){

		String ret = passPhrase.toString().trim();

		int length  = ret.length();

		if(hashed)
			return Hashing.md5().hashString(passPhrase, Charsets.UTF_8).toString();
			//return Hex.toHexString(passPhrase.toString().getBytes());//Hashing.goodFastHash(128).hashString(passPhrase, Charsets.UTF_8).toString();

		
		if(length==16 || length==24 || length ==32)return ret;
		
		if(length<16){ 
			ret = String.format("%16s", passPhrase.toString());
		}else if (length<24){
			ret = String.format("%24s", passPhrase.toString());
		}else{
			ret =  String.format("%32s", passPhrase.toString());
		}

		ret = ret.replace(' ', '0');

		return ret;
	}
	
	public static ECKey encryptPrivateKey(@Nonnull ECKey privateKey, @Nonnull CharSequence passPhrase){
		
		KeyParameter aeskey1 = new KeyParameter(passPhrase.toString().getBytes());
		KeyCrypter crypter1 = new KeyCrypterScrypt();
		//encrypted key
		ECKey pk2 = privateKey.encrypt(crypter1, aeskey1);
		
		return pk2;
	}
	
	public static ECKey recoverPrimaryKey(@Nonnull byte[]initializationData, 
			@Nonnull byte[] encryptedPrimaryKey,  
			@Nonnull byte[] publicKeyBytes,
			@Nullable CharSequence passphrase){

		KeyCrypter crypter = new KeyCrypterScrypt();

		EncryptedData encryptedData = new EncryptedData(initializationData,encryptedPrimaryKey);
		//reborn encrypted key
		ECKey recoveredkey = ECKey.fromEncrypted(encryptedData, crypter, publicKeyBytes);

		if(recoveredkey.isEncrypted() && passphrase!=null)
			recoveredkey = recoveredkey.decrypt(new KeyParameter(passphrase.toString().getBytes()));

		if(!recoveredkey.hasPrivKey()) return null;

		return recoveredkey;
	}


	public static ECKey recoverPrimaryKey(@Nonnull byte[] privateKeyBytes,
										  @Nonnull byte[] publicKeyBytes){

		ECKey recoveredkey = ECKey.fromPrivate(privateKeyBytes);

		return recoveredkey;
	}

	public static ECKey recoverPrimaryKeyFromASN1(@Nonnull byte[] asn1,
										  @Nonnull byte[] publicKeyBytes,
										  @Nullable CharSequence passphrase){

		ECKey recoveredkey = ECKey.fromASN1(asn1);
		if(recoveredkey.isEncrypted()){
			recoveredkey = recoveredkey.decrypt(new KeyParameter(passphrase.toString().getBytes()));

			//EncryptedData data = key1.getEncryptedData();
			//key1 = this.recoverPrimaryKey(data.initialisationVector,data.encryptedBytes,publicKeyBytes,passphrase);
		}
		return recoveredkey;
	}

	public static ECKey changePrivateKeyEncryption(@Nonnull byte[] privateKeyBytes,
										  @Nonnull byte[] publicKeyBytes,
										  @Nullable CharSequence originalPassphrase,
												   @Nullable CharSequence newPassphrase ) throws BitcointCryptoUtilsException {

		ECKey decryptedKey = null;
		if(originalPassphrase==null&&newPassphrase==null){throw new BitcointCryptoUtilsException("changePrivateKeyEncryption requires one passPhrase to NOT be null.");}
		ECKey recoveredkey = ECKey.fromPrivate(privateKeyBytes);
		if(recoveredkey.isEncrypted() && originalPassphrase==null){
			throw new BitcointCryptoUtilsException("changePrivateKeyEncryption requires a valid original passphrase.");
		}else {
			decryptedKey = recoveredkey.decrypt(new KeyParameter(originalPassphrase.toString().getBytes()));

			if (!recoveredkey.hasPrivKey()) {
				throw new BitcointCryptoUtilsException("changePrivateKeyEncryption could not recover your privatekey. Please try another passphrase or create another private key with (Caution!)");
			}
		}

		KeyCrypter crypter = new KeyCrypterScrypt();


		return newPassphrase==null?decryptedKey:encryptPrivateKey(decryptedKey,newPassphrase);
	}




	public static String createPrimaryKeyBase64DERCertificate(@Nonnull ECKey privateKey, @Nonnull byte[] data, @Nullable CharSequence passphrase){
		
		Sha256Hash hashedMsg = Sha256Hash.of(data);

		ECDSASignature signture = null;
		
		if(passphrase!=null){
		KeyParameter aesKey = new KeyParameter(passphrase.toString().getBytes());
		 	signture= privateKey.sign(hashedMsg,aesKey);
		}else{
			signture= privateKey.sign(hashedMsg);
		}		
		
		String encodedDERSignature = Base64.toBase64String(signture.encodeToDER());

		return encodedDERSignature;
	}


	public static class BitcointCryptoUtilsException extends Exception{
		public BitcointCryptoUtilsException(String s){
			super(s);
		}
	}
	
	
}
