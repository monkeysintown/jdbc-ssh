package com.m11n.jdbc.ssh.util;

import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;

import java.io.*;

public class EchoShellFactory implements Factory<Command> {
    public Command create() {
        return new EchoShell();
    }
    public static class EchoShell implements Command, Runnable {
        private InputStream in;
        private OutputStream out;
        private OutputStream err;
        private ExitCallback callback;
        private Environment environment;
        private Thread thread;
        public InputStream getIn() {
            return in;
        }
        public OutputStream getOut() {
            return out;
        }
        public OutputStream getErr() {
            return err;
        }
        public Environment getEnvironment() {
            return environment;
        }
        public void setInputStream(InputStream in) {
            this.in = in;
        }
        public void setOutputStream(OutputStream out) {
            this.out = out;
        }
        public void setErrorStream(OutputStream err) {
            this.err = err;
        }
        public void setExitCallback(ExitCallback callback) {
            this.callback = callback;
        }
        public void start(Environment env) throws IOException {
            environment = env;
            thread = new Thread(this, "EchoShell");
            thread.start();
        }
        public void destroy() {
            thread.interrupt();
        }
        public void run() {
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            try {
                for (;;) {
                    String s = r.readLine();
                    if (s == null) {
                        return;
                    }
                    out.write((s + "\n").getBytes());
                    out.flush();
                    if ("exit".equals(s)) {
                        return;
                    }
                }
            } catch (InterruptedIOException e) {
                // Ignore
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                callback.onExit(0);
            }
        }
    }
}