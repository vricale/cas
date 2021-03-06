package org.apereo.cas.oidc.token;

import org.apereo.cas.util.cipher.BaseStringCipherExecutor;

import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jwk.PublicJsonWebKey;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * This is {@link OidcJwtAccessTokenCipherExecutor}.
 *
 * @author Misagh Moayyed
 * @since 6.4.0
 */
@Getter
@RequiredArgsConstructor
public class OidcJwtAccessTokenCipherExecutor extends BaseStringCipherExecutor {
    /**
     * The default keystore for OIDC tokens.
     */
    protected final LoadingCache<String, Optional<PublicJsonWebKey>> defaultJsonWebKeystoreCache;

    /**
     * OIDC issuer.
     */
    protected final String issuer;

    @Override
    public String getName() {
        return "OpenID Connect JWT Access Tokens";
    }

    @Override
    public String encode(final Serializable value, final Object[] parameters) {
        getPublicJsonWebKey().ifPresent(jwks -> {
            setSigningKey(jwks.getPrivateKey());

            setEncryptionKey(jwks.getPublicKey());
            setContentEncryptionAlgorithmIdentifier(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
            setEncryptionAlgorithm(KeyManagementAlgorithmIdentifiers.RSA_OAEP_256);
        });
        return super.encode(value, parameters);
    }

    @Override
    public String decode(final Serializable value, final Object[] parameters) {
        getPublicJsonWebKey().ifPresent(jwks -> {
            setEncryptionKey(jwks.getPrivateKey());
            setContentEncryptionAlgorithmIdentifier(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
            setEncryptionAlgorithm(KeyManagementAlgorithmIdentifiers.RSA_OAEP_256);

            setSigningKey(jwks.getPublicKey());
        });
        return super.decode(value, parameters);
    }

    private Optional<PublicJsonWebKey> getPublicJsonWebKey() {
        return Objects.requireNonNull(defaultJsonWebKeystoreCache.get(this.issuer));
    }
}
