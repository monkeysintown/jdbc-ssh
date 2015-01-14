package com.m11n.jdbc.ssh.util;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.OutputStream;

/**
 * == An OutputStream that flushes out to a Category.
 *
 * NOTE: no data is written out to the Category until the stream is flushed or closed.
 *
 * Example:
 * [source,java]
 * --
 * // make sure everything sent to System.err is logged
 * System.setErr(new PrintStream(new LoggingOutputStream(Logger.getRootCategory()), true));
 *
 * // make sure everything sent to System.out is also logged
 * System.setOut(new PrintStream(new LoggingOutputStream(Logger.getRootCategory(), Level.INFO), true));
 * --
 *
 * @author https://github.com/vidakovic[Aleksandar Vidakovic]
 */
public class Slf4jOutputStream extends OutputStream {
    /**
     * Used to maintain the contract of #close()}.
     */
    private boolean hasBeenClosed = false;

    /**
     * The internal buffer where data is stored.
     */
    private byte[] buf;

    /**
     * The number of valid bytes in the buffer. This value is always
     * in the range <tt>0</tt> through <tt>buf.length</tt>; elements
     * <tt>buf[0]</tt> through <tt>buf[count-1]</tt> contain valid
     * byte data.
     */
    private int count;

    /**
     * Remembers the size of the buffer for speed.
     */
    private int bufLength;

    /**
     * The default number of bytes in the buffer. =2048
     */
    public static final int DEFAULT_BUFFER_LENGTH = 2048;


    /**
     * The category to write to.
     */
    private Logger logger;

    /**
     * Creates the LoggingOutputStream to flush to the given Category.
     *
     * @param log   the Logger to write to
     * @throws IllegalArgumentException if cat == null or priority == null
     */
    public Slf4jOutputStream(Logger log) throws IllegalArgumentException {
        if (log == null) {
            throw new IllegalArgumentException("cat == null");
        }

        this.logger = log;
        this.bufLength = DEFAULT_BUFFER_LENGTH;
        this.buf = new byte[DEFAULT_BUFFER_LENGTH];
        this.count = 0;
    }


    /**
     * Closes this output stream and releases any system resources
     * associated with this stream. The general contract of
     * `close`
     * is that it closes the output stream. A closed stream cannot
     * perform
     * output operations and cannot be reopened.
     */
    public void close() {
        flush();
        hasBeenClosed = true;
    }


    /**
     * Writes the specified byte to this output stream. The general
     * contract for `write` is that one byte is written
     * to the output stream. The byte to be written is the eight
     * low-order bits of the argument `b`. The 24
     * high-order bits of `b` are ignored.
     *
     * @param b the `byte` to write
     * @throws java.io.IOException if an I/O error occurs. In particular, an `IOException` may be
     *                 thrown if the output stream has been closed.
     */
    public void write(final int b) throws IOException {
        if (hasBeenClosed) {
            throw new IOException("The stream has been closed.");
        }

        // would this be writing past the buffer?

        if (count == bufLength) {
            // grow the buffer
            final int newBufLength = bufLength + DEFAULT_BUFFER_LENGTH;
            final byte[] newBuf = new byte[newBufLength];

            System.arraycopy(buf, 0, newBuf, 0, bufLength);
            buf = newBuf;

            bufLength = newBufLength;
        }

        buf[count] = (byte) b;

        count++;
    }


    /**
     * Flushes this output stream and forces any buffered output bytes
     * to be written out. The general contract of `flush` is
     * that calling it is an indication that, if any bytes previously
     * written have been buffered by the implementation of the output
     * stream, such bytes should immediately be written to their
     * intended destination.
     */
    public void flush() {

        if (count == 0) {
            return;
        }

        // don't print out blank lines; flushing from PrintStream puts

        // For linux system

        if (count == 1 && ((char) buf[0]) == '\n') {
            reset();
            return;
        }

        // For mac system

        if (count == 1 && ((char) buf[0]) == '\r') {
            reset();
            return;
        }

        // On windows system

        if (count == 2 && (char) buf[0] == '\r' && (char) buf[1] == '\n') {
            reset();
            return;
        }

        final byte[] theBytes = new byte[count];
        System.arraycopy(buf, 0, theBytes, 0, count);
        String message = new String(theBytes);

        if(logger.isTraceEnabled()) {
            logger.trace(message);
        } else if(logger.isDebugEnabled()) {
            logger.debug(message);
        } else if(logger.isInfoEnabled()) {
            logger.info(message);
        } else if(logger.isWarnEnabled()) {
            logger.warn(message);
        } else if(logger.isErrorEnabled()) {
            logger.error(message);
        } else {
            logger.info(message);
        }

        reset();

    }

    private void reset() {
        // not resetting the buffer -- assuming that if it grew then it will likely grow similarly again
        count = 0;
    }
}
