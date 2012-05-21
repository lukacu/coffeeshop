package org.coffeeshop.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.coffeeshop.io.TempDirectory;

public class ByteArrayCache extends DataCache<String, byte[]> {

	public ByteArrayCache(long memoryLimit, long totalLimit,
			TempDirectory tempDir) throws IOException {
		super(memoryLimit, totalLimit, tempDir);
	}

	@Override
	protected long getDataLength(byte[] object) {
		if (object == null) return 0;
		return object.length;
	}

	@Override
	protected byte[] readData(File file, long length) throws IOException {
		byte[] data = new byte[(int)length];
		
		FileInputStream r = new FileInputStream(file);
		r.read(data);

		r.close();
		
		return data;
	}

	@Override
	protected void writeData(File file, byte[] data) throws IOException {
		
		FileOutputStream w = new FileOutputStream(file);
		
		w.write(data);
		
		w.close();
	}

}
