package io.mtini.security;
  /*
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.LazyECPoint;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

import java.security.KeyPair;
import java.security.SecureRandom;

public class AssymetricKeyGenerator {

    KeyPair kp = null;
    public static final ECDomainParameters CURVE;

    static{
        CURVE = null;
    }


    public void generateKeyPair(SecureRandom secureRandom){
        ECKeyPairGenerator generator = new ECKeyPairGenerator();
        ECKeyGenerationParameters keygenParams = new ECKeyGenerationParameters(CURVE, secureRandom);
        generator.init(keygenParams);
        AsymmetricCipherKeyPair keypair = generator.generateKeyPair();
        ECPrivateKeyParameters privParams = (ECPrivateKeyParameters) keypair.getPrivate();
        ECPublicKeyParameters pubParams = (ECPublicKeyParameters) keypair.getPublic();
        this.priv = privParams.getD();
        this.pub = new LazyECPoint(CURVE.getCurve(), pubParams.getQ().getEncoded(true));

        this.creationTimeSeconds = Utils.currentTimeSeconds();
    }


}
*/
