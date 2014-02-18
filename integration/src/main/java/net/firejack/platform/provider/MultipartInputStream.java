package net.firejack.platform.provider;

import java.io.IOException;
import java.io.InputStream;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

public class MultipartInputStream extends InputStream {
    public static final int ITERATE = 10;
    private ProgressListener listener;
    private InputStream stream;

    private long percent;
    private long current;
    private long part;

    /**
     * @param stream
     * @param listener
     * @param length
     */
    public MultipartInputStream(InputStream stream, ProgressListener listener, Long length) {
        this.stream = stream;
        this.listener = listener;
        this.part = length / ITERATE;
    }

    @Override
    public int read() throws IOException {
        return stream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        int read = stream.read(b);
        progress(read);
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = stream.read(b, off, len);
        progress(read);
        return read;
    }

    @Override
    public long skip(long n) throws IOException {
        return stream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return stream.available();
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void mark(int readlimit) {
        stream.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        stream.reset();
    }

    @Override
    public boolean markSupported() {
        return stream.markSupported();
    }

    private void progress(int length) {
        if (length == -1) {
            return;
        }
        current += length;
        long count = current / part;
        current = current % part;
        percent += (count * 100) / ITERATE;

        if (count != 0) {
            notify((int) percent);
        }
    }

    private void notify(int percent) {
        if (listener != null) {
            listener.progress(percent);
        }
    }
}
