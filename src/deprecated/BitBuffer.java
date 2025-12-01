package deprecated;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import org.terifan.util.Debug;


public class BitBuffer
{
	private final static double LOG2 = Math.log(2);

	private byte[] mBuffer;
	private int mPosition;
	private int mLength;


	public BitBuffer()
	{
		this(32);
	}


	public BitBuffer(int aInitialCapacity)
	{
		mBuffer = new byte[(aInitialCapacity + 7) / 8];
	}


	public BitBuffer(byte[] aBuffer)
	{
		mBuffer = aBuffer;
		mLength = 8 * aBuffer.length;
	}


	public int getBit(int aIndex)
	{
		int byteIndex = aIndex >>> 3;
		int bitIndex = (aIndex & 7);

		if (mBuffer.length <= byteIndex)
		{
			return 0;
		}

		return 1 & (mBuffer[byteIndex] >>> bitIndex);
	}


	public BitBuffer setBit(int aIndex, int aBit)
	{
		int byteIndex = aIndex >>> 3;
		int bitIndex = (aIndex & 7);

		if (mBuffer.length <= byteIndex)
		{
			capacity(aIndex + 8*1024);
		}

		if (aBit == 0)
		{
			mBuffer[byteIndex] &= ~(1 << bitIndex);
		}
		else
		{
			mBuffer[byteIndex] |= 1 << bitIndex;
		}

		if (aIndex+1 > mLength)
		{
			mLength = aIndex+1;
		}
		
		return this;
	}


	public int readBit()
	{
		if (mPosition >= mLength)
		{
			throw new IllegalStateException("Position is beyond length of buffer: position: "+mPosition+", length: "+mLength);
		}

		return getBit(mPosition++);
	}

	
	public void writeAlign()
	{
		int len = (8-(mPosition & 7)) & 7;
		if (len > 0)
		{
			writeBits(0, len);
		}
	}

	
	public void readAlign()
	{
		mPosition += (8-(mPosition & 7)) & 7;
	}
	

	public BitBuffer writeBit(int aBit)
	{
		setBit(mPosition++, aBit);

		return this;
	}


	public BitBuffer writeBit(boolean aBit)
	{
		setBit(mPosition++, aBit ? 1 : 0);

		return this;
	}


	public BitBuffer writeBits(int aValue, int aLength)
	{
		if (aLength < 0 || aLength > 32)
		{
			throw new IllegalArgumentException("Illegal length: " + aLength);
		}

		if (capacity() <= mPosition + aLength)
		{
			capacity(mPosition + aLength + 4 * 1024 * 8);
		}

		int byteIndex = mPosition / 8;
		int bitIndex = (mPosition & 7);

		for (int i = 0; i < aLength; i++)
		{
			if (((aValue >>> i) & 1) == 0)
			{
				mBuffer[byteIndex] &= ~(1 << bitIndex);
			}
			else
			{
				mBuffer[byteIndex] |= 1 << bitIndex;
			}

			bitIndex++;
			if (bitIndex == 8)
			{
				bitIndex = 0;
				byteIndex++;
			}
		}

		mPosition += aLength;

		if (mPosition > mLength)
		{
			mLength = mPosition;
		}

		return this;
	}


	public BitBuffer writeBits(long aValue, int aLength)
	{
		if (aLength < 0 || aLength > 64)
		{
			throw new IllegalArgumentException("Illegal length: " + aLength);
		}
		
		for (int i = 0; i < aLength; i++)
		{
			setBit(mPosition++, (int)(aValue & 1L));
			aValue >>>= 1;
		}

		return this;
	}


	public int readBits(int aLength)
	{
		if (aLength < 0 || aLength > 32)
		{
			throw new IllegalArgumentException("Illegal length: " + aLength);
		}

		int value = 0;
		for (int i = 0; i < aLength; i++)
		{
			value += readBit() << i;
		}

		return value;
	}


	public long readBits(long aLength)	
	{
		if (aLength < 0 || aLength > 64)
		{
			throw new IllegalArgumentException("Illegal length: " + aLength);
		}
		
		long value = 0;
		for (int i = 0; i < aLength; i++)
		{
			value += (long)readBit() << i;
		}

		return value;
	}


	/**
	 * Read a variable length Exponential Golomb value from the buffer and advances the buffer position.
	 */
	public long readGolomb(int aExponent) throws IOException
	{
		int m = 0;
		for (;;)
		{
			if (readBit() == 1)
			{
				break;
			}
			m++;
		}

		long v = ((1L << m) + readBits((long)m)) - 1;
		
		if (aExponent > 0)
		{
			v <<= aExponent;
			v |= readBits((long)aExponent);
		}

		return v;
	}
	

	/**
	 * Writes a variable length Exponential Golomb value from the buffer and advances the buffer position.
	 */
	public void writeGolomb(long aValue, int aExponent) throws IOException
	{
		if (aValue < 0)
		{
			throw new IllegalArgumentException("Positive numbers only supported: " + aValue);
		}
		
		long s = (1L << aExponent) - 1;
		long n = ((aValue & ~s) >>> aExponent) + 1;
		int m = (int)Math.ceil(Math.log(n + 1) / LOG2) - 1;
		long v = aValue & s;

		writeBits(0L, m);
		writeBit(1);
		writeBits(n, m);
		writeBits(v, aExponent);
	}


	public BitBuffer capacity(int aBitCount)
	{
		int len = 1 + (aBitCount + 7) / 8;
		
		if (len < mBuffer.length)
		{
			return this;
		}
		
		mBuffer = Arrays.copyOfRange(mBuffer, 0, len);

		return this;
	}

	
	public int capacity()
	{
		return 8 * mBuffer.length;
	}
	

	public BitBuffer position(int aPosition)
	{
		if (aPosition < 0 || aPosition > mLength)
		{
			throw new IllegalArgumentException("Position out of rage: length: " + mLength+", position: "+aPosition);
		}
		
		mPosition = aPosition;
		return this;
	}


	public int position()
	{
		return mPosition;
	}


	public int length()
	{
		return mLength;
	}


	public int lengthInBytes()
	{
		return (mLength+7)/8;
	}


	public BitBuffer length(int aLength)
	{
		mLength = aLength;

		capacity(mLength);

		return this;
	}


	public BitBuffer trim()
	{
		outer:
		for (int i = (mLength + 7) / 8; --i >= 0;)
		{
			if (mBuffer[i] != 0)
			{
				byte b = mBuffer[i];
				for (int j = 8; --j >= 0;)
				{
					if ((b & (1 << j)) != 0)
					{
						int t = 8 * i + j + 1;
						if (t < mLength)
						{
							mLength = t;
							break outer;
						}
					}
				}
				throw new IllegalStateException();
			}
		}

		mBuffer = toByteArray();
		mPosition = Math.min(mPosition, mLength);

		return this;
	}


	public byte[] array()
	{
		return mBuffer;
	}


	public byte[] toByteArray()
	{
		byte[] temp = new byte[(mLength + 7) / 8];
		System.arraycopy(mBuffer, 0, temp, 0, temp.length);
		return temp;
	}


	public int writeTo(byte[] aOutput, int aOffset)
	{
		int len = (mLength + 7) / 8;
		System.arraycopy(mBuffer, 0, aOutput, aOffset, len);
		return len;
	}


	public int writeTo(java.nio.ByteBuffer aBuffer)
	{
		int len = (mLength + 7) / 8;
		aBuffer.put(mBuffer, 0, len);
		return len;
	}


	@Override
	public String toString()
	{
		char [] c = new char[mLength];
		for (int i = 0; i < mLength; i++)
		{
			c[i] = (char)('0' + getBit(i));
		}
		return new String(c);
	}


	public int indexOf(int aBit, int aOffset)
	{
		for (int i = aOffset; i < mLength; i++)
		{
			if (getBit(i) == aBit)
			{
				return i;
			}
		}

		return -1;
	}



	public static void main(String... args)
	{
		try
		{
			BitBuffer s = new BitBuffer();
			s.writeBits(0, 32);
			s.position(0);
			for (int i = 0; i < 16; i++)
			{
				s.writeBit(1);
				System.out.println(s+" "+(255&s.array()[0])+" "+(255&s.array()[1]));
			}
			for (int i = 0; i < 16; i++)
			{
				s.setBit(i, 1-s.getBit(i));
				System.out.println(s+" "+(255&s.array()[0])+" "+(255&s.array()[1]));
			}
			
			s.position(0);
			
			s.writeBits('A', 8);
			s.writeBits(7, 3);
			s.writeBits(4, 3);
			s.writeBits(124, 7);
			s.writeBits(0, 3);
			s.writeBits(0xcafebabe, 32);
			
			s.position(0);
			
			System.out.println(s.readBits(8));
			System.out.println(s.readBits(3));
			System.out.println(s.readBits(3));
			System.out.println(s.readBits(7));
			System.out.println(s.readBits(3));
			System.out.println(Integer.toHexString(s.readBits(32)));
			
			System.out.println(s+" "+(255&s.array()[0])+" "+(255&s.array()[1]));
			
			Debug.hexDump(s.toByteArray());
			System.out.println("");
			//if(true)return;
			
			Random rnd = new Random(1);

			BitBuffer stream = new BitBuffer();
			for (int i = 0; i < 80; i++)
			{
				int b = rnd.nextInt(2);
				System.out.print(b);
				stream.writeBit(b);
			}
			System.out.println("");
			System.out.println(stream);

			stream.position(40);

			System.out.print("                                        ");
			for (int i = 0; i < 78; i++)
			{
				int b = rnd.nextInt(2);
				System.out.print(b);
				stream.writeBit(b);
			}
			System.out.println("");
			System.out.println(stream);

			stream.trim();

			System.out.println(stream);

			stream.position(0);
			for (int i; (i = stream.readBit()) != -1;)
			{
				System.out.print(i);
			}
			System.out.println("");

			stream.length(7);

			System.out.println(stream);

			stream.trim();

			System.out.println(stream);
			
			stream = new BitBuffer();
			long [] values = new long[10];
			for (int i = 0; i < values.length; i++)
			{
				long j = Math.abs(rnd.nextInt(1+(1<<rnd.nextInt(31))));
				int n = (int)Math.ceil(Math.log(j+1)/Math.log(2));
				stream.writeBits(j, n);
				values[i] = j;
			}
			stream.position(0);
			for (int i = 0; i < values.length; i++)
			{
				long j = values[i];
				int n = (int)Math.ceil(Math.log(j+1)/Math.log(2));
				long k = stream.readBits(n);
				if (j != k)
				{
					System.out.printf("%12d %12d %2d\n", j, k, n);
				}
			}

			for (int r = 0; ; r++)
			{
				rnd = new Random(r);
				System.out.println(r);
				stream = new BitBuffer();
				for (int exp = 0; exp < 64; exp++)
				{
					stream.position(0);
					for (int i = 0; i < values.length; i++)
					{
						int n = 12+rnd.nextInt(64-12);
						long j = rnd.nextLong() & ((1L<<n)-1);
						values[i] = j;
						stream.writeGolomb(j, exp);
					}
					stream.position(0);
					for (int i = 0; i < values.length; i++)
					{
						long j = values[i];
						long k = stream.readGolomb(exp);
						if (j != k)
						{
							System.out.printf("%20d %20d %4d %2d\n", j, k, i, exp);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
}


//package org.terifan.io;
//
//import java.util.Arrays;
//import java.util.Random;
//import org.terifan.io.filesystem.IOException;
//import org.terifan.util.Debug;
//
//
//public class BitBuffer
//{
//	private final static double LOG2 = Math.log(2);
//
//	private byte[] mBuffer;
//	private int mPosition;
//	private int mLength;
//
//
//	public BitBuffer()
//	{
//		this(32);
//	}
//
//
//	public BitBuffer(int aInitialCapacity)
//	{
//		mBuffer = new byte[(aInitialCapacity + 7) / 8];
//	}
//
//
//	public BitBuffer(byte[] aBuffer)
//	{
//		mBuffer = aBuffer;
//		mLength = 8 * aBuffer.length;
//	}
//
//
//	public int getBit(int aIndex)
//	{
//		int byteIndex = aIndex >>> 3;
//		int bitIndex = (aIndex & 7);
//
//		if (mBuffer.length <= byteIndex)
//		{
//			return 0;
//		}
//
//		return 1 & (mBuffer[byteIndex] >>> bitIndex);
//	}
//
//
//	public BitBuffer setBit(int aIndex, int aBit)
//	{
//		int byteIndex = aIndex >>> 3;
//		int bitIndex = (aIndex & 7);
//
//		if (mBuffer.length <= byteIndex)
//		{
//			capacity(aIndex);
//		}
//
//		if (aBit == 0)
//		{
//			mBuffer[byteIndex] &= ~(1 << bitIndex);
//		}
//		else
//		{
//			mBuffer[byteIndex] |= 1 << bitIndex;
//		}
//
//		if (aIndex+1 > mLength)
//		{
//			mLength = aIndex+1;
//		}
//		
//		return this;
//	}
//
//
//	public int readBit()
//	{
//		if (mPosition >= mLength)
//		{
//			return -1;
//		}
//
//		return getBit(mPosition++);
//	}
//
//
//	public BitBuffer writeBit(int aBit)
//	{
//		setBit(mPosition++, aBit);
//
//		return this;
//	}
//
//
//	public BitBuffer writeBit(boolean aBit)
//	{
//		setBit(mPosition++, aBit ? 1 : 0);
//
//		return this;
//	}
//
//
//	public BitBuffer writeBits(int aValue, int aLength)
//	{
//		if (aLength < 0 || aLength > 32)
//		{
//			throw new IllegalArgumentException("Illegal length: " + aLength);
//		}
//
//		writeBits((long)aValue, aLength);
//		
////		capacity(mPosition+aLength);
////
////		if ((mPosition & 7) > 0)
////		{
////			int n = Math.min(mPosition & 7, aLength);
////			for (int i = 0; i < n; i++)
////			{
////				int byteIndex = mPosition >>> 3;
////				int bitIndex = (mPosition & 7);
////				if (((aValue >>> i) & 1) == 1)
////				{
////					mBuffer[byteIndex] |= 1 << bitIndex;
////				}
////				else
////				{
////					mBuffer[byteIndex] &= ~(1 << bitIndex);
////				}
////				mPosition++;
////			}
////			aLength -= n;
////		}
////		
////		for (int p = mPosition/8; aLength >= 8; p++)
////		{
////			aLength -= 8;
////			mBuffer[p] = (byte)(aValue >>> aLength);
////			mPosition += 8;
////		}
////		
////		for (int i = aLength; --i >= 0; )
////		{
////			int byteIndex = mPosition >>> 3;
////			int bitIndex = (mPosition & 7);
////			if (((aValue >>> i) & 1) == 1)
////			{
////				mBuffer[byteIndex] |= 1 << bitIndex;
////			}
////			else
////			{
////				mBuffer[byteIndex] &= ~(1 << bitIndex);
////			}
////			mPosition++;
////		}
////		
////		if (mPosition > mLength)
////		{
////			mLength = mPosition;
////		}
//
//		return this;
//	}
//
//
//	public BitBuffer writeBits(long aValue, int aLength)
//	{
//		if (aLength < 0 || aLength > 64)
//		{
//			throw new IllegalArgumentException("Illegal length: " + aLength);
//		}
//
//		capacity(mPosition+aLength);
//
//		if ((mPosition & 7) > 0)
//		{
//			int n = Math.min(mPosition & 7, aLength);
//			for (int i = 0; i < n; i++)
//			{
//				int byteIndex = mPosition >>> 3;
//				int bitIndex = (mPosition & 7);
//				if (((aValue >>> i) & 1) == 1)
//				{
//					mBuffer[byteIndex] |= 1 << bitIndex;
//				}
//				else
//				{
//					mBuffer[byteIndex] &= ~(1 << bitIndex);
//				}
//				mPosition++;
//			}
//			aLength -= n;
//		}
//		
//		for (int p = mPosition/8; aLength >= 8; p++)
//		{
//			mBuffer[p] = (byte)(aValue >>> (aLength-8));
//			aLength -= 8;
//			mPosition += 8;
//		}
//		
//		for (int i = aLength; --i >= 0; )
//		{
//			int byteIndex = mPosition >>> 3;
//			int bitIndex = (mPosition & 7);
//			if (((aValue >>> i) & 1) == 1)
//			{
//				mBuffer[byteIndex] |= 1 << bitIndex;
//			}
//			else
//			{
//				mBuffer[byteIndex] &= ~(1 << bitIndex);
//			}
//			mPosition++;
//		}
//		
//		if (mPosition > mLength)
//		{
//			mLength = mPosition;
//		}
//
//		return this;
//	}
//
//
//	public int readBits(int aLength)
//	{
//		if (aLength < 0 || aLength > 32)
//		{
//			throw new IllegalArgumentException("Illegal length: " + aLength);
//		}
//
//		int value = 0;
//		for (int i = aLength/8; --i > 0;)
//		{
//			value += readBit() << i;
//		}
//		for (int i = 0; i < aLength; i++)
//		{
//			value += readBit() << i;
//		}
//
//		return value;
//	}
//
//
//	public long readBits(long aLength)	
//	{
//		if (aLength < 0 || aLength > 64)
//		{
//			throw new IllegalArgumentException("Illegal length: " + aLength);
//		}
//		
//		long value = 0;
//		for (int i = 0; i < aLength; i++)
//		{
//			value += (long)readBit() << i;
//		}
//
//		return value;
//	}
//
//
//	/**
//	 * Read a variable length Exponential Golomb value from the buffer and advances the buffer position.
//	 */
//	public long readGolomb(int aExponent) throws IOException
//	{
//		int m = 0;
//		for (;;)
//		{
//			if (readBit() == 1)
//			{
//				break;
//			}
//			m++;
//		}
//
//		long v = ((1L << m) + readBits((long)m)) - 1;
//		
//		if (aExponent > 0)
//		{
//			v <<= aExponent;
//			v |= readBits((long)aExponent);
//		}
//
//		return v;
//	}
//	
//
//	/**
//	 * Writes a variable length Exponential Golomb value from the buffer and advances the buffer position.
//	 */
//	public void writeGolomb(long aValue, int aExponent) throws IOException
//	{
//		if (aValue < 0)
//		{
//			throw new IllegalArgumentException("Positive numbers only supported: " + aValue);
//		}
//		
//		long s = (1L << aExponent) - 1;
//		long n = ((aValue & ~s) >>> aExponent) + 1;
//		int m = (int)Math.ceil(Math.log(n + 1) / LOG2) - 1;
//		long v = aValue & s;
//
//		writeBits(0L, m);
//		writeBits(1, 1);
//		writeBits(n, m);
//		writeBits(v, aExponent);
//	}
//
//
//	public BitBuffer capacity(int aBitCount)
//	{
//		int len = 1 + (aBitCount + 7) / 8;
//		
//		if (len < mBuffer.length)
//		{
//			return this;
//		}
//		
//		mBuffer = Arrays.copyOfRange(mBuffer, 0, len);
//
//		return this;
//	}
//
//	
//	public int capacity()
//	{
//		return mBuffer.length;
//	}
//	
//
//	public BitBuffer position(int aPosition)
//	{
//		mPosition = aPosition;
//		return this;
//	}
//
//
//	public int position()
//	{
//		return mPosition;
//	}
//
//
//	public int length()
//	{
//		return mLength;
//	}
//
//
//	public int lengthInBytes()
//	{
//		return (mLength+7)/8;
//	}
//
//
//	public BitBuffer length(int aLength)
//	{
//		mLength = aLength;
//
//		capacity(mLength);
//
//		return this;
//	}
//
//
//	public BitBuffer trim()
//	{
//		outer:
//		for (int i = (mLength + 7) / 8; --i >= 0;)
//		{
//			if (mBuffer[i] != 0)
//			{
//				byte b = mBuffer[i];
//				for (int j = 8; --j >= 0;)
//				{
//					if ((b & (1 << j)) != 0)
//					{
//						int t = 8 * i + j + 1;
//						if (t < mLength)
//						{
//							mLength = t;
//							break outer;
//						}
//					}
//				}
//				throw new IllegalStateException();
//			}
//		}
//
//		mBuffer = toByteArray();
//		mPosition = Math.min(mPosition, mLength);
//
//		return this;
//	}
//
//
//	public byte[] array()
//	{
//		return mBuffer;
//	}
//
//
//	public byte[] toByteArray()
//	{
//		byte[] temp = new byte[(mLength + 7) / 8];
//		System.arraycopy(mBuffer, 0, temp, 0, temp.length);
//		return temp;
//	}
//
//
//	public int writeTo(byte[] aOutput, int aOffset)
//	{
//		int len = (mLength + 7) / 8;
//		System.arraycopy(mBuffer, 0, aOutput, aOffset, len);
//		return len;
//	}
//
//
//	public int writeTo(ByteBuffer aBuffer)
//	{
//		int len = (mLength + 7) / 8;
//		aBuffer.put(mBuffer, 0, len);
//		return len;
//	}
//
//
//	public int writeTo(java.nio.ByteBuffer aBuffer)
//	{
//		int len = (mLength + 7) / 8;
//		aBuffer.put(mBuffer, 0, len);
//		return len;
//	}
//
//
//	@Override
//	public String toString()
//	{
//		char [] c = new char[mLength];
//		for (int i = 0; i < mLength; i++)
//		{
//			c[i] = (char)('0' + getBit(i));
//		}
//		return new String(c);
//	}
//
//
//	public int indexOf(int aBit, int aOffset)
//	{
//		for (int i = aOffset; i < mLength; i++)
//		{
//			if (getBit(i) == aBit)
//			{
//				return i;
//			}
//		}
//
//		return -1;
//	}
//
//
//
//	public static void main(String... args)
//	{
//		try
//		{
//			BitBuffer s = new BitBuffer();
//			s.writeBits(0, 32);
//			s.position(0);
//			for (int i = 0; i < 16; i++)
//			{
//				s.writeBit(1);
//				System.out.println(s+" "+(255&s.array()[0])+" "+(255&s.array()[1]));
//			}
//			for (int i = 0; i < 16; i++)
//			{
//				s.setBit(i, 1-s.getBit(i));
//				System.out.println(s+" "+(255&s.array()[0])+" "+(255&s.array()[1]));
//			}
//			
//			s.position(0);
//			
//			s.writeBits('A', 8);
//			s.writeBits(7, 3);
//			s.writeBits(4, 3);
//			s.writeBits(124, 7);
//			s.writeBits(0, 3);
//			s.writeBits(0xcafebabe, 32);
//			
//			s.position(0);
//			
//			System.out.println(s.readBits(8));
//			System.out.println(s.readBits(3));
//			System.out.println(s.readBits(3));
//			System.out.println(s.readBits(7));
//			System.out.println(s.readBits(3));
//			System.out.println(Integer.toHexString(s.readBits(32)));
//			
//			System.out.println(s+" "+(255&s.array()[0])+" "+(255&s.array()[1]));
//			
//			Debug.hexDump(s.toByteArray());
//			System.out.println("");
//			if(true)return;
//			
//			Random rnd = new Random(1);
//
//			BitBuffer stream = new BitBuffer();
//			for (int i = 0; i < 80; i++)
//			{
//				int b = rnd.nextInt(2);
//				System.out.print(b);
//				stream.writeBit(b);
//			}
//			System.out.println("");
//			System.out.println(stream);
//
//			stream.position(40);
//
//			System.out.print("                                        ");
//			for (int i = 0; i < 78; i++)
//			{
//				int b = rnd.nextInt(2);
//				System.out.print(b);
//				stream.writeBit(b);
//			}
//			System.out.println("");
//			System.out.println(stream);
//
//			stream.trim();
//
//			System.out.println(stream);
//
//			stream.position(0);
//			for (int i; (i = stream.readBit()) != -1;)
//			{
//				System.out.print(i);
//			}
//			System.out.println("");
//
//			stream.length(7);
//
//			System.out.println(stream);
//
//			stream.trim();
//
//			System.out.println(stream);
//			
//			stream = new BitBuffer();
//			long [] values = new long[10];
//			for (int i = 0; i < values.length; i++)
//			{
//				long j = Math.abs(rnd.nextInt(1+(1<<rnd.nextInt(31))));
//				int n = (int)Math.ceil(Math.log(j+1)/Math.log(2));
//				stream.writeBits(j, n);
//				values[i] = j;
//			}
//			stream.position(0);
//			for (int i = 0; i < values.length; i++)
//			{
//				long j = values[i];
//				int n = (int)Math.ceil(Math.log(j+1)/Math.log(2));
//				long k = stream.readBits(n);
//				if (j != k)
//				{
//					System.out.printf("%12d %12d %2d\n", j, k, n);
//				}
//			}
//
//			for (int r = 0; ; r++)
//			{
//				rnd = new Random(r);
//				System.out.println(r);
//				stream = new BitBuffer();
//				for (int exp = 0; exp < 64; exp++)
//				{
//					stream.position(0);
//					for (int i = 0; i < values.length; i++)
//					{
//						int n = 12+rnd.nextInt(64-12);
//						long j = rnd.nextLong() & ((1L<<n)-1);
//						values[i] = j;
//						stream.writeGolomb(j, exp);
//					}
//					stream.position(0);
//					for (int i = 0; i < values.length; i++)
//					{
//						long j = values[i];
//						long k = stream.readGolomb(exp);
//						if (j != k)
//						{
//							System.out.printf("%20d %20d %4d %2d\n", j, k, i, exp);
//						}
//					}
//				}
//			}
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
//}