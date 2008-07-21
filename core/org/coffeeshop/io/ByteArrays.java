package org.coffeeshop.io;

public class ByteArrays {

	/**
	 * Writes a long integer to a byte array at the given offset. The data is 8
	 * bytes long.
	 * 
	 * @param l
	 * @param array
	 * @param offset
	 */
	public static void writeLong(long l, byte[] array, int offset) {
		int i = offset + 7;
		array[i] = (byte) (l);
		l >>>= 8;
		array[--i] = (byte) (l);
		l >>>= 8;
		array[--i] = (byte) (l);
		l >>>= 8;
		array[--i] = (byte) (l);
		l >>>= 8;
		array[--i] = (byte) (l);
		l >>>= 8;
		array[--i] = (byte) (l);
		l >>>= 8;
		array[--i] = (byte) (l);
		l >>>= 8;
		array[--i] = (byte) (l);
	}

	/**
	 * Reads a long integer from a byte array.
	 * 
	 * @param l
	 * @param array
	 * @param offset
	 */
	public static void writeInteger(int l, byte[] array, int offset) {
		int i = offset + 3;
		array[i] = (byte) (l);
		l >>>= 8;
		array[--i] = (byte) (l);
		l >>>= 8;
		array[--i] = (byte) (l);
		l >>>= 8;
		array[--i] = (byte) (l);
	}

	public static void writeShort(short l, byte[] array, int offset) {
		int i = offset + 1;
		array[i] = (byte) (l);
		l >>>= 8;
		array[--i] = (byte) (l);
	}

	public static long readLong(byte[] array, int offset) {
		return ((((long) array[offset + 7]) & 0xFF)
				+ ((((long) array[offset + 6]) & 0xFF) << 8)
				+ ((((long) array[offset + 5]) & 0xFF) << 16)
				+ ((((long) array[offset + 4]) & 0xFF) << 24)
				+ ((((long) array[offset + 3]) & 0xFF) << 32)
				+ ((((long) array[offset + 2]) & 0xFF) << 40)
				+ ((((long) array[offset + 1]) & 0xFF) << 48) + ((((long) array[offset]) & 0xFF) << 56));
	}

	public static int readInteger(byte[] array, int offset) {
		return ((((int) array[offset + 3]) & 0xFF)
				+ ((((int) array[offset + 2]) & 0xFF) << 8)
				+ ((((int) array[offset + 1]) & 0xFF) << 16) + ((((int) array[offset]) & 0xFF) << 24));
	}

	public static short readShort(byte[] array, int offset) {
		return (short) ((((short) array[offset + 1]) & 0xFF) + ((((short) array[offset]) & 0xFF) << 8));
	}

	public static void dumpArray(byte[] array) {
		System.out.print("[");

		if (array.length > 0) {

			System.out.print(array[0]);

			for (int i = 1; i < array.length; i++)
				System.out.print("," + array[i]);

		}
		System.out.println("]");
	}

}
