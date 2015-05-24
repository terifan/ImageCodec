package org.terifan.imagecodec.dct;

import java.util.Random;
import org.terifan.imagecodec.deprecated.Tools;
import org.terifan.util.log.Log;


public class IntDCT8 implements IntDCT
{
	private final static int CONST_BITS = 13;
	private final static int PASS1_BITS = 1;
	private final static int FIX_0_298631336 = 2446;
	private final static int FIX_0_390180644 = 3196;
	private final static int FIX_0_541196100 = 4433;
	private final static int FIX_0_765366865 = 6270;
	private final static int FIX_0_899976223 = 7373;
	private final static int FIX_1_175875602 = 9633;
	private final static int FIX_1_501321110 = 12299;
	private final static int FIX_1_847759065 = 15137;
	private final static int FIX_1_961570560 = 16069;
	private final static int FIX_2_053119869 = 16819;
	private final static int FIX_2_562915447 = 20995;
	private final static int FIX_3_072711026 = 25172;

	private final static int[] data = new int[64];

	private final static int W1 = 2841;
	private final static int W2 = 2676;
	private final static int W3 = 2408;
	private final static int W5 = 1609;
	private final static int W6 = 1108;
	private final static int W7 = 565;

	private final static int[] iclip = new int[2048];
	static
	{
		for (int i = -1024; i < 1024; i++)
		{
			iclip[1024 + i] = (i < 0) ? 0 : ((i > 255+255) ? 255+255 : i);
		}
	}


	@Override
	public void forward(int[] block)
	{
		for (int i = 0, blkptr = 0, dataptr = 0; i < 8; i++)
		{
			int tmp0 = block[blkptr + 0] + block[blkptr + 7];
			int tmp7 = block[blkptr + 0] - block[blkptr + 7];
			int tmp1 = block[blkptr + 1] + block[blkptr + 6];
			int tmp6 = block[blkptr + 1] - block[blkptr + 6];
			int tmp2 = block[blkptr + 2] + block[blkptr + 5];
			int tmp5 = block[blkptr + 2] - block[blkptr + 5];
			int tmp3 = block[blkptr + 3] + block[blkptr + 4];
			int tmp4 = block[blkptr + 3] - block[blkptr + 4];

			int tmp10 = tmp0 + tmp3;
			int tmp13 = tmp0 - tmp3;
			int tmp11 = tmp1 + tmp2;
			int tmp12 = tmp1 - tmp2;

			data[dataptr + 0] = (tmp10 + tmp11) << PASS1_BITS;
			data[dataptr + 4] = (tmp10 - tmp11) << PASS1_BITS;

			int z1 = (tmp12 + tmp13) * FIX_0_541196100;
			data[dataptr + 2] = DESCALE(z1 + tmp13 * FIX_0_765366865, CONST_BITS - PASS1_BITS);
			data[dataptr + 6] = DESCALE(z1 + tmp12 * (-FIX_1_847759065), CONST_BITS - PASS1_BITS);

			int z6 = tmp4 + tmp7;
			int z2 = tmp5 + tmp6;
			int z3 = tmp4 + tmp6;
			int z4 = tmp5 + tmp7;
			int z5 = (z3 + z4) * FIX_1_175875602;

			tmp4 *= FIX_0_298631336;
			tmp5 *= FIX_2_053119869;
			tmp6 *= FIX_3_072711026;
			tmp7 *= FIX_1_501321110;
			z6 *= -FIX_0_899976223;
			z2 *= -FIX_2_562915447;
			z3 *= -FIX_1_961570560;
			z4 *= -FIX_0_390180644;

			z3 += z5;
			z4 += z5;

			data[dataptr + 7] = DESCALE(tmp4 + z6 + z3, CONST_BITS - PASS1_BITS);
			data[dataptr + 5] = DESCALE(tmp5 + z2 + z4, CONST_BITS - PASS1_BITS);
			data[dataptr + 3] = DESCALE(tmp6 + z2 + z3, CONST_BITS - PASS1_BITS);
			data[dataptr + 1] = DESCALE(tmp7 + z6 + z4, CONST_BITS - PASS1_BITS);

			dataptr += 8;
			blkptr += 8;
		}

		for (int i = 0, dataptr = 0; i < 8; i++)
		{
			int tmp0 = data[dataptr + 0*8] + data[dataptr + 7*8];
			int tmp7 = data[dataptr + 0*8] - data[dataptr + 7*8];
			int tmp1 = data[dataptr + 1*8] + data[dataptr + 6*8];
			int tmp6 = data[dataptr + 1*8] - data[dataptr + 6*8];
			int tmp2 = data[dataptr + 2*8] + data[dataptr + 5*8];
			int tmp5 = data[dataptr + 2*8] - data[dataptr + 5*8];
			int tmp3 = data[dataptr + 3*8] + data[dataptr + 4*8];
			int tmp4 = data[dataptr + 3*8] - data[dataptr + 4*8];

			int tmp10 = tmp0 + tmp3;
			int tmp13 = tmp0 - tmp3;
			int tmp11 = tmp1 + tmp2;
			int tmp12 = tmp1 - tmp2;

			data[dataptr + 0*8] = DESCALE(tmp10 + tmp11, PASS1_BITS);
			data[dataptr + 4*8] = DESCALE(tmp10 - tmp11, PASS1_BITS);

			int z1 = (tmp12 + tmp13) * FIX_0_541196100;
			data[dataptr + 2*8] = DESCALE(z1 + tmp13 * FIX_0_765366865, CONST_BITS + PASS1_BITS);
			data[dataptr + 6*8] = DESCALE(z1 + tmp12 * (-FIX_1_847759065), CONST_BITS + PASS1_BITS);

			int z6 = tmp4 + tmp7;
			int z2 = tmp5 + tmp6;
			int z3 = tmp4 + tmp6;
			int z4 = tmp5 + tmp7;
			int z5 = (z3 + z4) * FIX_1_175875602;

			tmp4 *= FIX_0_298631336;
			tmp5 *= FIX_2_053119869;
			tmp6 *= FIX_3_072711026;
			tmp7 *= FIX_1_501321110;
			z6 *= -FIX_0_899976223;
			z2 *= -FIX_2_562915447;
			z3 *= -FIX_1_961570560;
			z4 *= -FIX_0_390180644;

			z3 += z5;
			z4 += z5;

			data[dataptr + 7*8] = DESCALE(tmp4 + z6 + z3, CONST_BITS + PASS1_BITS);
			data[dataptr + 5*8] = DESCALE(tmp5 + z2 + z4, CONST_BITS + PASS1_BITS);
			data[dataptr + 3*8] = DESCALE(tmp6 + z2 + z3, CONST_BITS + PASS1_BITS);
			data[dataptr + 1*8] = DESCALE(tmp7 + z6 + z4, CONST_BITS + PASS1_BITS);

			dataptr++;
		}

		for (int i = 0; i < 64; i++)
		{
			block[i] = DESCALE(data[i], 3);
		}
	}

	@Override
	public void inverse(int[] block)
	{
		for (int i = 0; i < 8; i++)
		{
			int blk = 8 * i;

			int tmp0;
			int tmp1 = block[blk + 4] << 11;
			int tmp2 = block[blk + 6];
			int tmp3 = block[blk + 2];
			int tmp4 = block[blk + 1];
			int tmp5 = block[blk + 7];
			int tmp6 = block[blk + 5];
			int tmp7 = block[blk + 3];
			int tmp8;

			if (tmp1 == 0 && tmp2 == 0 && tmp3 == 0 && tmp4 == 0 && tmp5 == 0 && tmp6 == 0 && tmp7 == 0)
			{
				block[blk + 0] = block[blk + 1] = block[blk + 2] = block[blk + 3] = block[blk + 4] = block[blk + 5] = block[blk + 6] = block[blk + 7] = block[blk + 0] << 3;
				continue;
			}

			tmp0 = (block[blk + 0] << 11) + 128;

			// first stage
			tmp8 = W7 * (tmp4 + tmp5);
			tmp4 = tmp8 + (W1 - W7) * tmp4;
			tmp5 = tmp8 - (W1 + W7) * tmp5;
			tmp8 = W3 * (tmp6 + tmp7);
			tmp6 = tmp8 - (W3 - W5) * tmp6;
			tmp7 = tmp8 - (W3 + W5) * tmp7;

			// second stage
			tmp8 = tmp0 + tmp1;
			tmp0 -= tmp1;
			tmp1 = W6 * (tmp3 + tmp2);
			tmp2 = tmp1 - (W2 + W6) * tmp2;
			tmp3 = tmp1 + (W2 - W6) * tmp3;
			tmp1 = tmp4 + tmp6;
			tmp4 -= tmp6;
			tmp6 = tmp5 + tmp7;
			tmp5 -= tmp7;

			// third stage
			tmp7 = tmp8 + tmp3;
			tmp8 -= tmp3;
			tmp3 = tmp0 + tmp2;
			tmp0 -= tmp2;
			tmp2 = (181 * (tmp4 + tmp5) + 128) >> 8;
			tmp4 = (181 * (tmp4 - tmp5) + 128) >> 8;

			// fourth stage

			block[blk + 0] = ((tmp7 + tmp1) >> 8);
			block[blk + 1] = ((tmp3 + tmp2) >> 8);
			block[blk + 2] = ((tmp0 + tmp4) >> 8);
			block[blk + 3] = ((tmp8 + tmp6) >> 8);
			block[blk + 4] = ((tmp8 - tmp6) >> 8);
			block[blk + 5] = ((tmp0 - tmp4) >> 8);
			block[blk + 6] = ((tmp3 - tmp2) >> 8);
			block[blk + 7] = ((tmp7 - tmp1) >> 8);
		}

		for (int i = 0; i < 8; i++)
		{
			int blk = i;

			int tmp0;
			int tmp1 = block[blk + 8 * 4] << 8;
			int tmp2 = block[blk + 8 * 6];
			int tmp3 = block[blk + 8 * 2];
			int tmp4 = block[blk + 8 * 1];
			int tmp5 = block[blk + 8 * 7];
			int tmp6 = block[blk + 8 * 5];
			int tmp7 = block[blk + 8 * 3];
			int tmp8;

			if (tmp1 == 0 && tmp2 == 0 && tmp3 == 0 && tmp4 == 0 && tmp5 == 0 && tmp6 == 0 && tmp7 == 0)
			{
				block[blk + 8 * 0] = block[blk + 8 * 1] = block[blk + 8 * 2] = block[blk + 8 * 3] = block[blk + 8 * 4] = block[blk + 8 * 5] = block[blk + 8 * 6] = block[blk + 8 * 7] = iclip[1024 + ((block[blk + 8 * 0] + 32) >> 6)];
				continue;
			}

			tmp0 = (block[blk + 8 * 0] << 8) + 8192;

			// first stage
			tmp8 = W7 * (tmp4 + tmp5) + 4;
			tmp4 = (tmp8 + (W1 - W7) * tmp4) >> 3;
			tmp5 = (tmp8 - (W1 + W7) * tmp5) >> 3;
			tmp8 = W3 * (tmp6 + tmp7) + 4;
			tmp6 = (tmp8 - (W3 - W5) * tmp6) >> 3;
			tmp7 = (tmp8 - (W3 + W5) * tmp7) >> 3;

			// second stage
			tmp8 = tmp0 + tmp1;
			tmp0 -= tmp1;
			tmp1 = W6 * (tmp3 + tmp2) + 4;
			tmp2 = (tmp1 - (W2 + W6) * tmp2) >> 3;
			tmp3 = (tmp1 + (W2 - W6) * tmp3) >> 3;
			tmp1 = tmp4 + tmp6;
			tmp4 -= tmp6;
			tmp6 = tmp5 + tmp7;
			tmp5 -= tmp7;

			// third stage
			tmp7 = tmp8 + tmp3;
			tmp8 -= tmp3;
			tmp3 = tmp0 + tmp2;
			tmp0 -= tmp2;
			tmp2 = (181 * (tmp4 + tmp5) + 128) >> 8;
			tmp4 = (181 * (tmp4 - tmp5) + 128) >> 8;

			// fourth stage
			block[blk + 8 * 0] = iclip[1024 + DESCALE(tmp7 + tmp1, 14)];
			block[blk + 8 * 1] = iclip[1024 + DESCALE(tmp3 + tmp2, 14)];
			block[blk + 8 * 2] = iclip[1024 + DESCALE(tmp0 + tmp4, 14)];
			block[blk + 8 * 3] = iclip[1024 + DESCALE(tmp8 + tmp6, 14)];
			block[blk + 8 * 4] = iclip[1024 + DESCALE(tmp8 - tmp6, 14)];
			block[blk + 8 * 5] = iclip[1024 + DESCALE(tmp0 - tmp4, 14)];
			block[blk + 8 * 6] = iclip[1024 + DESCALE(tmp3 - tmp2, 14)];
			block[blk + 8 * 7] = iclip[1024 + DESCALE(tmp7 - tmp1, 14)];
		}
	}


	private static int DESCALE(int x, int n)
	{
		return (x + (1 << (n - 1))) >> n;
	}


	public static void main(String... args)
	{
		int N = 8;

		int[] original = new int[N*N];
		Random rnd = new Random();
		for (int i = 0; i < N*N; i++)
		{
			original[i] = rnd.nextInt(256);
		}

		int [] block = original.clone();
		IntDCT8 dct = new IntDCT8();
		long t = System.nanoTime();
		for (int i = 0; i < 100000; i++)
		{
			dct.forward(block);
			dct.inverse(block);
		}
		System.out.println((System.nanoTime() - t) / 1000000);

		int [] input = original.clone();
		dct.forward(input);
		int [] output = input.clone();
		dct.inverse(output);

		int[] err = new int[N*N];
		for (int i = 0; i < N*N; i++)
		{
			err[i] = (int)Math.abs(original[i] - output[i]);
		}

		Tools.print(N, N, original);
		Log.out.println("");
		Tools.print(N, N, input);
		Log.out.println("");
		Tools.print(N, N, output);
		Log.out.println("");
		Tools.print(N, N, err);
	}
}
