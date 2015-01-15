package com.m11n.jdbc.util;

import org.apache.sshd.common.KeyPairProvider;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.auth.CachingPublicKeyAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Map;

public class TestCachingPublicKeyAuthenticator extends CachingPublicKeyAuthenticator {
    private KeyPairProvider keyProvider = new SimpleGeneratorHostKeyProvider("target/hostkey.rsa", "RSA");
    private KeyPair pairRsa = keyProvider.loadKey(KeyPairProvider.SSH_RSA);

    public TestCachingPublicKeyAuthenticator() {
        super(new PublickeyAuthenticator() {
            @Override
            public boolean authenticate(String s, PublicKey publicKey, ServerSession serverSession) {
                return true;
            }
        });
    }
    public Map<ServerSession, Map<PublicKey, Boolean>> getCache() {
        return cache;
    }
    public KeyPairProvider getKeyProvider() {
        return keyProvider;
    }
}
