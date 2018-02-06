/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.util;

import java.util.ArrayList;
import java.util.List;
import me.ferrybig.javacoding.servermanagerbackend.internal.ByteListener;

/**
 *
 * @author Fernando van Loenhout
 */
public class LogRecorder implements ByteListener {

	/**
	 * Buffer for current buffer view
	 */
	private final byte[] buffer;
	/**
	 * write index in the array
	 */
	private int writeIndex;
	/**
	 * total bytes written so far (ignoring overflows)
	 */
	private long totalBytes;

	private final List<ByteListener> listeners = new ArrayList<>();

	public LogRecorder() {
		this(1024 * 16);
	}

	public LogRecorder(int size) {
		this.buffer = new byte[size];
	}

	public void onIncomingBytes(byte[] source, int start, int end) {
		if (end <= start) {
			throw new IllegalArgumentException("end <= start");
		}
		int length = end - start;
		if (length > buffer.length) {
			start = end - buffer.length;
			length = buffer.length;
		}
		synchronized (this) {
			if (this.writeIndex + length >= buffer.length) {
				// overflow
				int overflow = this.writeIndex + length - buffer.length;
				if (length != overflow) {
					System.arraycopy(source, start, buffer, writeIndex, length - overflow);
				}
				System.arraycopy(source, start + overflow, buffer, 0, overflow);
				writeIndex = overflow;
			} else {
				System.arraycopy(source, start, buffer, writeIndex, length);
				writeIndex += length;
			}
			totalBytes += length;
			this.notifyAll();
			for (ByteListener l : this.listeners) {
				l.onIncomingBytes(source, start, end);
			}
		}
	}

	public synchronized long getCurrentWriteIndex() {
		return this.totalBytes;
	}

	public int getBufferSize() {
		return this.buffer.length;
	}

	public synchronized int readBytesBlocking(byte[] target, int targetStart, int targetLength, long readIndex, int maxAttempts) throws InterruptedException {
		int r = readBytes0(target, targetStart, targetLength, readIndex);
		int attempt = 0;
		while (r == 0 && attempt++ < maxAttempts) {
			if (r == 0) {
				this.wait(1000);
			}
			r = readBytes0(target, targetStart, targetLength, readIndex);
		}
		return r;
	}

	private int readBytes0(byte[] target, int targetStart, int targetLength, long readIndex) {
		if (readIndex < getLowestValidReadIndex()) {
			return -1;
		}
		long startPos = readIndex;
		long endPos = Math.min(this.totalBytes, startPos + targetLength);
		long total = endPos - startPos;
		if (total < 0) {
			return -1;
		}
		if (total == 0) {
			return 0;
		}
		int startRelative = (int) (startPos % this.getBufferSize());
		int endRelative = (int) (endPos % this.getBufferSize());

		if (endRelative < startRelative) {
			// Overflowing array index
			int bytesBeforeOverflow = this.getBufferSize() - startRelative;
			int bytesAfterOverflow = (int) (total - bytesBeforeOverflow);
			if (bytesBeforeOverflow != 0) {
				System.arraycopy(this.buffer, startRelative, target, targetStart, bytesBeforeOverflow);
			}
			if (bytesAfterOverflow != 0) {
				System.arraycopy(this.buffer, 0, target, targetStart + bytesBeforeOverflow, bytesAfterOverflow);
			}
		} else {
			System.arraycopy(this.buffer, startRelative, target, targetStart, (int) total);
		}
		return (int) total;
	}

	public synchronized int readBytes(byte[] buff, int start, int length, long readIndex) {
		if (readIndex < getLowestValidReadIndex()) {
			return -1;
		}
		return readBytes0(buff, start, length, readIndex);
	}

	public long getLowestValidReadIndex() {
		return Math.max(0, this.getCurrentWriteIndex() - this.getBufferSize() + 1);
	}

	public long addByteListener(ByteListener listener) {
		return addByteListener(listener, false);
	}

	public synchronized long addByteListener(ByteListener listener, boolean sendBuffer) {
		this.listeners.add(listener);
		if (sendBuffer) {
			if (this.totalBytes != 0) {
				if (this.totalBytes < this.buffer.length) {
					assert this.totalBytes == this.writeIndex;
					listener.onIncomingBytes(this.buffer, 0, this.writeIndex);
				} else if (this.writeIndex == 0) {
					listener.onIncomingBytes(this.buffer, 0, this.buffer.length);
				} else {
					listener.onIncomingBytes(this.buffer, this.writeIndex, this.buffer.length - this.writeIndex);
					listener.onIncomingBytes(this.buffer, 0, this.writeIndex);
				}
			}
		}
		return this.totalBytes;
	}

	public synchronized void removeByteListener(ByteListener listener) {
		this.listeners.add(listener);
	}

}
