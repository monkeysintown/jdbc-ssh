package com.m11n.jdbc.ssh.util;

import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

public class BogusPasswordAuthenticator implements PasswordAuthenticator {
    public boolean authenticate(String username, String password, ServerSession session) {
        return true;
    }
}