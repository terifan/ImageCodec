package org.terifan.imagecodec.dct;

import java.util.Arrays;
import java.util.Random;
import deprecated.BitBuffer;
import static deprecated.Tools.*;


public class Hadamard
{
	private final int steps;
	private final int size;
	private final double scale;
//	private final int [] opArray;


	public Hadamard(int k)
	{
		if (k < 1 || k > 29)
		{
			throw new IllegalArgumentException();
		}
		steps = k;
		size = 1 << k;
		scale = 1.0 / Math.sqrt(size);
//		opArray = new int[size];
	}


//	public void transformOP(int [] data)
//	{
//		int [] next = opArray;// opArray is final (bug control) so get a variable reference to it.
//		if (data.length != size)
//		{
//			throw new IllegalArgumentException();
//		}
//		for (int i = 0; i < steps; i++)
//		{
//			int pairwise = 0;
//			int lowerHalf = 0;
//			int upperHalf = size >> 1;
//			while (pairwise < size)
//			{
//				int a = data[pairwise++];
//				int b = data[pairwise++];
//				next[lowerHalf++] = a + b;
//				next[upperHalf++] = a - b;
//			}
//			int [] temp = data; // swap arrays around.
//			data = next;
//			next = temp;
//		}
//		for (int i = 0; i < size; i++)
//		{
//			data[i] = (int)Math.round(data[i] * scale);
//		}
//		// if an odd number of transform steps have been done then you need to copy back
//		// the result to the original data array reference (in next)
//		if ((steps & 1) == 1)
//		{
//			System.arraycopy(data, 0, next, 0, size);
//		}
//	}


	public void transformIP(int [] data, int offset, int step)
	{
		if (data.length < size)
		{
			throw new IllegalArgumentException(data.length+" "+size+" "+steps);
		}
		int blockSize = 1;
		while (blockSize < size)
		{
			for (int blockPair = 0; blockPair < size; blockPair += blockSize << 1)
			{
				for (int i = 0; i < blockSize; i++)
				{
					int a = data[offset + step * (blockPair + i)];
					int b = data[offset + step * (blockPair + blockSize + i)];
					data[offset + step * (blockPair + i)] = a + b;
					data[offset + step * (blockPair + blockSize + i)] = a - b;
				}
			}
			blockSize <<= 1;
		}
		for (int i = 0; i < size; i++)
		{
			int j = offset + step * i;
			data[j] = (int)Math.round(data[j] * scale);
		}
	}


//	public void transformSeq(float[] data)
//	{
//		if (data.length != size)
//		{
//			throw new IllegalArgumentException();
//		}
//		for (int i = 0; i < size; i++)
//		{
//			float sum = 0;
//			for (int j = 0; j < size; j++)
//			{
//				sum += data[j] * parity(j & i);
//			}
//			opArray[i] = sum;
//		}
//		for (int i = 0; i < size; i++)
//		{
//			opArray[i] *= scale;
//		}
//		System.arraycopy(opArray, 0, data, 0, size);
//	}
//
//
//	public int indexToSequency(int index)
//	{
//		if (index < 0 || index >= size)
//		{
//			throw new IllegalArgumentException();
//		}
//		int result = 0;
//		int j = bitReverse(index, steps);
//		for (; j > 0; j >>>= 1)
//		{
//			result ^= j;
//		}
//		return result;
//	}
//
//
//	public int sequencyToIndex(int seq)
//	{
//		if (seq < 0 || seq >= size)
//		{
//			throw new IllegalArgumentException();
//		}
//		seq ^= seq >> 1;
//		return bitReverse(seq, steps);
//	}
//
//	// Returns 1 if a is parity even and -1 if a is parity odd
//
//	private static int parity(int a)
//	{
//		a ^= a >> 16;
//		a ^= a >> 8;
//		a ^= a >> 4;
//		a ^= a >> 2;
//		a ^= a >> 1;
//		return 1 - ((a & 1) << 1);
//	}
//
//	static final int[] mirrorAr = new int[256];
//
//
//	static
//	{
//		for (int i = 0; i < 256; i++)
//		{
//			int res = 0;
//			for (int j = 0, k = i; j < 8; j++)
//			{
//				res <<= 1;
//				res |= k & 1;
//				k >>= 1;
//			}
//			mirrorAr[i] = res;
//		}
//	}
//
//
//	private static int bitReverse(int i, int nBits)
//	{
//		int result;
//		result = mirrorAr[i & 255];
//		if (nBits <= 8)
//		{
//			return result >>> (8 - nBits);
//		}
//		result <<= 8;
//		i >>>= 8;
//		result |= mirrorAr[i & 255];
//		if (nBits <= 16)
//		{
//			return result >>> (16 - nBits);
//		}
//		result <<= 8;
//		i >>>= 8;
//		result |= mirrorAr[i & 255];
//		if (nBits <= 24)
//		{
//			return result >>> (24 - nBits);
//		}
//		result <<= 8;
//		i >>>= 8;
//		result |= mirrorAr[i];
//		return result >>> (32 - nBits);
//	}


	public static void main(String... args)
	{
		run();
	}


	public static int[] buildQuantTable(int aQuality)
	{
		aQuality = Math.max(Math.min(aQuality, 100), 1);

		if (aQuality < 50)
		{
			aQuality = 5000 / aQuality;
		}
		else
		{
			aQuality = 200 - aQuality * 2;
		}

		int[] tbl =
		{
			10, 12, 14, 14,  18,  24,  49,  72,
			12, 12, 13, 17,  22,  35,  64,  92,
			14, 13, 16, 22,  37,  55,  78,  95,
			14, 17, 22, 29,  56,  64,  87,  98,
			18, 22, 37, 56,  68,  81, 103, 112,
			24, 35, 55, 64,  81, 104, 121, 100,
			49, 64, 78, 87, 103, 121, 120, 103,
			72, 92, 95, 98, 112, 100, 103,  70
		};

		for (int i = 0; i < 64; i++)
		{
			tbl[i] = Math.max(Math.min((tbl[i] * aQuality + 50) / 100, 255), 1);
		}

		return tbl;
	}

	private static int oldlen = 9999;
	private static int olderr = 9999;
	private static int [] bestquant = new int[8*8];

	private static void run()
	{
		int [] original = new int[8*8];
		Random rnd = new Random();
		for (int i = 0; i < original.length; i++) original[i] = (int)(63*rnd.nextFloat());

		int [] quant = buildQuantTable(80);
//		Arrays.fill(quant, 10);

//		for (int q = 0; q < 1000000; q++)
		{
			run(3, quant, original);
		}
	}

	private static void run(int ttl, int [] quant, int [] original)
	{
		try
		{
			Hadamard h = new Hadamard(3);
			Random rnd = new Random();

			for (int q = 0; q < 100; q++)
			{
				int p = rnd.nextInt(64);
				int o = quant[p];
				if (rnd.nextBoolean())
				{
					quant[p] = Math.min(quant[p]+1, 255);
				}
				else
				{
					quant[p] = Math.max(quant[p]-1, 1);
				}

				int [] input = original.clone();
				for (int a = 0; a < 8; a++)
				{
					h.transformIP(input, a*8, 1);
				}
				for (int a = 0; a < 8; a++)
				{
					h.transformIP(input, a, 8);
				}
				int [] quantOutput = input.clone();
				for (int i = 0; i < original.length; i++) quantOutput[i] = (int)Math.round(input[i] / (double)quant[i]);
				for (int i = 0; i < original.length; i++) input[i] = quant[i] * (int)Math.round(input[i] / (double)quant[i]);
				int [] output = input.clone();
				for (int a = 8; --a >= 0; )
				{
					h.transformIP(output, a, 8);
				}
				for (int a = 8; --a >= 0; )
				{
					h.transformIP(output, a*8, 1);
				}
				int [] err = new int[original.length];
				for (int i = 0; i < original.length; i++) err[i] = Math.round(output[i])-original[i];
				int toterr = 0;
				for (int i = 0; i < original.length; i++) toterr+=Math.abs(err[i]);

				BitBuffer bb = new BitBuffer(1000);
				for (int i = 0; i < quantOutput.length; i++)
				{
					bb.writeGolomb(quantOutput[i] < 0 ? -2*quantOutput[i]+1 : 2*quantOutput[i], 3);
				}

//				print(8, 8, quant);

//				if ((bb.length() < oldlen || toterr < olderr) && Math.abs(bb.length()-oldlen) < Math.abs(toterr-olderr))
				if (toterr < olderr && bb.length() < 300)
				{
					olderr = toterr;
					oldlen = bb.length();
					bestquant = quant.clone();

					System.out.println(toterr+" "+bb.length());

					print(8, 8, quant);

					run(ttl+1, quant.clone(), original);
				}
				else
				{
					if (ttl > 0)
					{
						run(ttl-1, quant.clone(), original);
					}

					quant[p] = o;
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


/*
	private static void run()
	{
		try
		{
			Hadamard h = new Hadamard(3);
			int [] original = new int[8*8];
			int seed = -1779685408; //new Random().nextInt();
			System.out.println(seed);
			Random rnd = new Random(seed);
			for (int i = 0; i < original.length; i++) original[i] = (int)(63*rnd.nextFloat());

			{
				int [] quant = buildQuantTable(95);
				int [] input = original.clone();
//				if (test == 0)
//				{
//					h.transformIP(input, 0, 1);
//				}
//				else
//				{
					for (int a = 0; a < 8; a++)
					{
						h.transformIP(input, a*8, 1);
					}
					for (int a = 0; a < 8; a++)
					{
						h.transformIP(input, a, 8);
					}
//				}
				int [] quantOutput = input.clone();
				for (int i = 0; i < original.length; i++) quantOutput[i] = (int)Math.round(input[i] / (double)quant[i]);
				for (int i = 0; i < original.length; i++) input[i] = quant[i] * (int)Math.round(input[i] / (double)quant[i]);
				int [] output = input.clone();
//				if (test == 0)
//				{
//					h.transformIP(output, 0, 1);
//				}
//				else
//				{
					for (int a = 8; --a >= 0; )
					{
						h.transformIP(output, a, 8);
					}
					for (int a = 8; --a >= 0; )
					{
						h.transformIP(output, a*8, 1);
					}
//				}
				int [] err = new int[original.length];
				for (int i = 0; i < original.length; i++) err[i] = Math.round(output[i])-original[i];
				int toterr = 0;
				for (int i = 0; i < original.length; i++) toterr+=Math.abs(err[i]);
//				print(8, 8, quant);
//				print(8, 8, original);
//				print(8, 8, input, quantOutput);
//				print(8, 8, quantOutput);
//				print(8, 8, output);
//				print(8, 8, err, quantOutput, quant);

				BitBuffer bb = new BitBuffer(1000);
				for (int i = 0; i < quantOutput.length; i++)
				{
					bb.writeGolomb(quantOutput[i] < 0 ? -2*quantOutput[i]+1 : 2*quantOutput[i], 3);
				}

				System.out.println(toterr+" "+bb.length()+" "+(toterr/(double)bb.length()));
//				System.out.println("============================================================================================================================================");
			}
//			{
//			int [] quant = QuantizationTable.buildQuantTable(95, 8, 8, 0);
//			DCT dct = new FloatDCT(8, 8);
//			int [] input = new int[64];
//			for (int i = 0; i < original.length; i++) input[i]=(int)original[i];
//			dct.forward(input);
//			int [] quantOutput = new int[64];
//			for (int i = 0; i < original.length; i++) quantOutput[i] = (int)Math.round(input[i] / quant[i]);
//			for (int i = 0; i < original.length; i++) input[i] = quant[i] * (int)Math.round(input[i] / quant[i]);
//			int [] output = input.clone();
//			dct.inverse(output);
//			int [] err = new int[original.length];
//			for (int i = 0; i < original.length; i++) err[i] = output[i]-original[i];
//			int toterr = 0;
//			for (int i = 0; i < original.length; i++) toterr+=Math.abs(err[i]);
//			BitBuffer bb = new BitBuffer(1000);
//			for (int i = 0; i < quantOutput.length; i++)
//			{
//				bb.writeGolomb(quantOutput[i] < 0 ? -2*quantOutput[i]+1 : 2*quantOutput[i], 3);
//			}
////			print(8, 8, original);
////			print(8, 8, input, quantOutput);
////			print(8, 8, output);
//			print(8, 8, err, quantOutput, quant);
//			System.out.println(toterr+" "+bb.length()+" "+(toterr/(double)bb.length()));
//			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
*/
}
