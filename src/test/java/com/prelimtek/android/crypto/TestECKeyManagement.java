
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.ECKey.ECDSASignature;
import org.bitcoinj.crypto.EncryptedData;
import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.crypto.KeyCrypterScrypt;

import org.junit.Test;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.encoders.Base64;
import org.spongycastle.util.encoders.Hex;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.prelimtek.utils.crypto.BitcoinCryptoUtils;


public class TestECKeyManagement {
	String password = "smallpwd";
	@Test
	public void encryptdecryptPivateKey(){
		ECKey pk1 = new ECKey();
		System.out.println("pk1 encrypted "+pk1.isEncrypted());

		KeyCrypter crypter1 = new KeyCrypterScrypt();
		//KeyParameter aeskey1 = crypter1.deriveKey("password");
		
		byte[] pwdByte = "passwordpassword".getBytes();
		pwdByte = Hashing.sha256().hashString(password, Charsets.UTF_8).asBytes();//Hex.encode(password.getBytes());
		System.out.println(pwdByte.length);;
		KeyParameter aeskey1 = new KeyParameter(pwdByte);

		//encrypted key
		ECKey pk2 = pk1.encrypt(crypter1, aeskey1);//does not have pri or bub
		System.out.println("pk2 encrypted "+pk2.isEncrypted());
		
		
		KeyCrypter crypter2 = new KeyCrypterScrypt();
		//KeyParameter aeskey2 = crypter2.deriveKey("password");
		byte[] pwdByte2 = "passwordpassword".getBytes();//NB has to be >=16characters
		pwdByte2 = Hashing.sha256().hashString(password, Charsets.UTF_8).asBytes();

		KeyParameter aeskey2 = new KeyParameter(pwdByte2);

		ECKey pk3 = pk2.decrypt(aeskey2);

		assert pk1.equals(pk3);
		
	}
	
	/**
	 * To be used for transporting private key between client and serverside.*/
	@Test
	public void encryptdecryptPivateKeySerialized(){
		ECKey pk1 = new ECKey();
		System.out.println("pk1 encrypted "+pk1.isEncrypted());

		byte[] publicKey = pk1.getPubKey();
		
		KeyCrypter crypter1 = new KeyCrypterScrypt();
		//KeyParameter aeskey1 = crypter1.deriveKey("password");
		
		byte[] pwdByte = "passwordpassword".getBytes();
		System.out.println(pwdByte.length);;
		KeyParameter aeskey1 = new KeyParameter(pwdByte);

		//encrypted key
		ECKey pk2 = pk1.encrypt(crypter1, aeskey1);//does not have pri or bub
		System.out.println("pk2 encrypted "+pk2.isEncrypted());
		
		byte[] encryptedDataByte = pk2.getEncryptedData().encryptedBytes;
		byte[] initializationByte = pk2.getEncryptedData().initialisationVector;
		
		KeyCrypter crypter22 = new KeyCrypterScrypt();
		EncryptedData encryptedData = new EncryptedData(initializationByte,encryptedDataByte);
		//reborn encrypted key
		ECKey pk22 = ECKey.fromEncrypted(encryptedData, crypter22, publicKey);
		
		KeyCrypter crypter2 = new KeyCrypterScrypt();
		//KeyParameter aeskey2 = crypter2.deriveKey("password");
		byte[] pwdByte2 = "passwordpassword".getBytes();//NB has to be >=16characters
		
		KeyParameter aeskey2 = new KeyParameter(pwdByte2);

		ECKey pk3 = pk22.decrypt(aeskey2);

		assert pk1.getPrivateKeyAsHex().equals(pk3.getPrivateKeyAsHex());
		
	}
	
	
	
	@Test
	public void encryptdecryptPivateKeySerializedEncryptedData(){
		ECKey pk1 = new ECKey();
		System.out.println("pk1 encrypted "+pk1.isEncrypted());

		byte[] publicKey = pk1.getPubKey();
		
		KeyCrypter crypter1 = new KeyCrypterScrypt();
		//KeyParameter aeskey1 = crypter1.deriveKey("password");
		
		byte[] pwdByte = "passwordpassword".getBytes();
		System.out.println(pwdByte.length);;
		KeyParameter aeskey1 = new KeyParameter(pwdByte);

		//encrypted key
		ECKey pk2 = pk1.encrypt(crypter1, aeskey1);//does not have pri or bub
		System.out.println("pk2 encrypted "+pk2.isEncrypted());
		
		byte[] encryptedDataByte = pk2.getEncryptedData().encryptedBytes;
		byte[] initializationByte = pk2.getEncryptedData().initialisationVector;
		
		//PErsistence of encryption data
		String encryptedDataHex = Hex.toHexString(encryptedDataByte);
		String initializationHex = Hex.toHexString(initializationByte);
		
		
		encryptedDataByte = Hex.decode(encryptedDataHex);
		initializationByte = Hex.decode(initializationHex);
		
		KeyCrypter crypter22 = new KeyCrypterScrypt();
		EncryptedData encryptedData = new EncryptedData(initializationByte,encryptedDataByte);
		//reborn encrypted key
		ECKey pk22 = ECKey.fromEncrypted(encryptedData, crypter22, publicKey);
		
		KeyCrypter crypter2 = new KeyCrypterScrypt();
		//KeyParameter aeskey2 = crypter2.deriveKey("password");
		byte[] pwdByte2 = "passwordpassword".getBytes();//NB has to be >=16characters
		
		KeyParameter aeskey2 = new KeyParameter(pwdByte2);

		ECKey pk3 = pk22.decrypt(aeskey2);

		assert pk1.getPrivateKeyAsHex().equals(pk3.getPrivateKeyAsHex());
		
	}

	/**To be used for verifying public key / tamperproofing*/
	@Test 
	public void encrypDecryptWithSignature(){
		String msg = "Some data containing a message";
		String password = "passwordpassword";//has t be >=16 characters
		ECKey privateKey = new ECKey();
		byte[] publicKey = privateKey.getPubKey();

		KeyCrypter crypter1 = new KeyCrypterScrypt();
		
		KeyParameter aesKey = new KeyParameter(password.getBytes());
		//encrypted key
		ECKey encPrivateKey = privateKey.encrypt(crypter1, aesKey);//does not have pri or bub
		System.out.println("encPrivateKey encrypted "+encPrivateKey.isEncrypted());
		
		Sha256Hash hashedMsg = Sha256Hash.of(msg.getBytes());
		//Sha256Hash hashedMsg = Sha256Hash.of(msg.getBytes(Charsets.UTF_8));
		//Sha256Hash hashedMsg =Sha256Hash.wrap(Sha256Hash.hash(msg.getBytes()));
		
		ECDSASignature signture= encPrivateKey.sign(hashedMsg,aesKey);
		
		System.out.println("isCanonical "+signture.isCanonical());
		
		
		String encodedDERSignature = Base64.toBase64String(signture.encodeToDER());
		System.out.println("Signature: "+encodedDERSignature);

		try {
			
			ECKey unEncryptedPubKey = ECKey.fromPublicOnly(publicKey);

			ECDSASignature recoveredSignature = ECDSASignature.decodeFromDER(Base64.decode(encodedDERSignature.getBytes()));
			
			//Assert.assertEquals(signture, recoveredSignature);
			assert signture.equals(recoveredSignature);
			
			ECKey regenKey = null;
			for(int i = 0 ; i < 10; i++){
				System.out.println("i: "+i);
				regenKey = ECKey.recoverFromSignature(i, recoveredSignature, hashedMsg, true);
				if(regenKey!=null){
					System.out.println("pub: "+regenKey.getPublicKeyAsHex());
					System.out.println("encrypted: "+regenKey.isEncrypted());
					System.out.println("private : "+regenKey.hasPrivKey());
					if(regenKey.equals(unEncryptedPubKey)){
						System.out.println("Found!");
						break;
					}
				}
			}


			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//regenPrivateKey.de
		
		
				
	}
	
	@Test 
	public void testPassPhraseGen(){
		
		CharSequence s = BitcoinCryptoUtils.generatePassPhrase("4721",false);
		
		System.out.println(s);
		assert s.toString().getBytes().length==16;
		
		byte[] b = Hex.encode(s.toString().getBytes());
		
		assert 0 == b.length % 16;
		
		s = BitcoinCryptoUtils.generatePassPhrase("4721",true);

		//s = BitcoinCryptoUtils.generatePassPhrase("passwordpasswordpasswordpassword",true);
		System.out.println(s);
		System.out.println(s.toString().getBytes().length);
		
		assert s.length()==32;
		
		b = Hex.encode(s.toString().getBytes());
		System.out.println(b.length);

		assert b.length == 64;
		
		System.out.println(s);;
	}
}
