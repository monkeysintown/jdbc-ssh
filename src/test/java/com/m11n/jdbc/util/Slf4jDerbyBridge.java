package com.m11n.jdbc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Writer;

/**
 * Funnels Derby log outputs into an SLF4J logger. This current implementation logs
 * all errors at the `ERROR` threshold.
 */
public class Slf4jDerbyBridge {
    private static Logger logger = LoggerFactory.getLogger(Slf4jDerbyBridge.class);

    private Slf4jDerbyBridge()
    {
    }

    /**
     * A basic adapter that funnels Derby's logs through an SLF4J logger.
     */
    public static final class LoggingWriter extends Writer
    {
        @Override
        public void write(final char[] buf, final int off, final int len)
        {
            // skip empty lines.
            if (len > 1)
            {
                String message = new String(buf, off, len);

                logger.trace(message);
            }
        }

        @Override
        public void flush()
        {
            // noop.
        }

        @Override
        public void close()
        {
            // noop.
        }
    }

    public static void setLogger(Logger log) {
        logger = log;
    }

    public static Writer bridge()
    {
        return new LoggingWriter();
    }
}
