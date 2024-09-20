package co.mizrahi.currency.conversion.services;

import co.mizrahi.currency.conversion.config.CDPJsonProperties;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.HashMap;
import java.time.Instant;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyFactory;
import java.io.StringReader;
import java.security.PrivateKey;
import java.security.Security;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Created at 14/09/2024
 * https://docs.cdp.coinbase.com/coinbase-app/docs/api-key-authentication#code-samples
 * @author David Mizrahi
 */
@Component
@RequiredArgsConstructor
public class CoinbaseAuthenticationService implements AuthenticationService {

    public static final String BROKERAGE_ACCOUNTS = "api.coinbase.com/api/v3/brokerage/accounts";
    public static final String REQUEST_METHOD = "GET";
    public static final String URI = REQUEST_METHOD + " " + BROKERAGE_ACCOUNTS;

    private String name;
    private String key;

    private final CDPJsonProperties jsonProperties;

    @PostConstruct
    void init() {
        this.name = this.jsonProperties.getName();
        this.key = this.jsonProperties.getPrivateKey();
    }

    @Override
    @Cacheable(value = "coinbaseAuthCache", key = "'coinbaseJWT'")
    public String authenticate() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Map<String, Object> header = this.getHeader();
        Map<String, Object> data = this.getData();
        PrivateKey privateKey = this.getPrivateKey();
        ECPrivateKey ecPrivateKey = this.getEcPrivateKey(privateKey);
        SignedJWT signedJWT = this.getSignedJWT(data, header, ecPrivateKey);
        return signedJWT.serialize();
    }

    @NotNull
    private SignedJWT getSignedJWT(Map<String, Object> data, Map<String, Object> header, ECPrivateKey ecPrivateKey) throws JOSEException {
        JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            claimsSetBuilder.claim(entry.getKey(), entry.getValue());
        }
        JWTClaimsSet claimsSet = claimsSetBuilder.build();

        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.ES256).customParams(header).build();
        SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);

        JWSSigner signer = new ECDSASigner(ecPrivateKey);
        signedJWT.sign(signer);
        return signedJWT;
    }

    private ECPrivateKey getEcPrivateKey(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        return (ECPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    private PrivateKey getPrivateKey() throws Exception {
        PEMParser pemParser = new PEMParser(new StringReader(this.key.replace("\"", "")));
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        Object object = pemParser.readObject();
        PrivateKey privateKey;

        if (object instanceof PrivateKey) {
            privateKey = (PrivateKey) object;
        } else if (object instanceof PEMKeyPair) {
            privateKey = converter.getPrivateKey(((PEMKeyPair) object).getPrivateKeyInfo());
        } else {
            throw new Exception("Unexpected private key format");
        }
        pemParser.close();
        return privateKey;
    }

    @NotNull
    private Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();
        data.put("iss", "cdp");
        data.put("nbf", Instant.now().getEpochSecond());
        data.put("exp", Instant.now().getEpochSecond() + 120);
        data.put("sub", this.name);
        data.put("uri", URI);
        return data;
    }

    @NotNull
    private Map<String, Object> getHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "ES256");
        header.put("typ", "JWT");
        header.put("kid", this.name);
        header.put("nonce", String.valueOf(Instant.now().getEpochSecond()));
        return header;
    }
}
