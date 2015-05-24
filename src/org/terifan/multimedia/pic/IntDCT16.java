package org.terifan.multimedia.pic;

import java.util.Random;


public class IntDCT16 implements IntDCT
{
	private final static int S = 8;
	private final static int F = 1<<S;
	private final static int C16_1R = (int)(F*0.35185093438159561476);
	private final static int C16_1I = (int)(F*0.03465429229977286565);
	private final static int C16_2R = (int)(F*0.34675996133053686546);
	private final static int C16_2I = (int)(F*0.06897484482073575308);
	private final static int C16_3R = (int)(F*0.33832950029358816957);
	private final static int C16_3I = (int)(F*0.10263113188058934529);
	private final static int C16_4R = (int)(F*0.32664074121909413196);
	private final static int C16_4I = (int)(F*0.13529902503654924610);
	private final static int C16_5R = (int)(F*0.31180625324666780814);
	private final static int C16_5I = (int)(F*0.16666391461943662432);
	private final static int C16_6R = (int)(F*0.29396890060483967924);
	private final static int C16_6I = (int)(F*0.19642373959677554532);
	private final static int C16_7R = (int)(F*0.27330046675043937206);
	private final static int C16_7I = (int)(F*0.22429189658565907106);
	private final static int C16_8R = (int)(F*0.25);
	private final static int W16_4R = (int)(F*0.92387953251128675613);
	private final static int W16_4I = (int)(F*0.38268343236508977173);
	private final static int W16_8R = (int)(F*0.70710678118654752440);


	@Override
	public void forward(int[] a)
	{
		int x0r, x0i, x1r, x1i, x2r, x2i, x3r, x3i;
		int x4r, x4i, x5r, x5i, x6r, x6i, x7r, x7i;
		int xr, xi;

		for (int j = 0; j <= 15; j++)
		{
			x4r = a[16*0+j] - a[16*15+j];
			xr = a[16*0+j] + a[16*15+j];
			x4i = a[16*8+j] - a[16*7+j];
			xi = a[16*8+j] + a[16*7+j];
			x0r = xr + xi;
			x0i = xr - xi;
			x5r = a[16*2+j] - a[16*13+j];
			xr = a[16*2+j] + a[16*13+j];
			x5i = a[16*10+j] - a[16*5+j];
			xi = a[16*10+j] + a[16*5+j];
			x1r = xr + xi;
			x1i = xr - xi;
			x6r = a[16*4+j] - a[16*11+j];
			xr = a[16*4+j] + a[16*11+j];
			x6i = a[16*12+j] - a[16*3+j];
			xi = a[16*12+j] + a[16*3+j];
			x2r = xr + xi;
			x2i = xr - xi;
			x7r = a[16*6+j] - a[16*9+j];
			xr = a[16*6+j] + a[16*9+j];
			x7i = a[16*14+j] - a[16*1+j];
			xi = a[16*14+j] + a[16*1+j];
			x3r = xr + xi;
			x3i = xr - xi;
			xr = x0r + x2r;
			xi = x1r + x3r;
			a[16*0+j] = descaleX(C16_8R * (xr + xi));
			a[16*8+j] = descaleX(C16_8R * (xr - xi));
			xr = x0r - x2r;
			xi = x1r - x3r;
			a[16*4+j] = descaleX(C16_4R * xr - C16_4I * xi);
			a[16*12+j] = descaleX(C16_4R * xi + C16_4I * xr);
			x0r = descaleX(W16_8R * (x1i - x3i));
			x2r = descaleX(W16_8R * (x1i + x3i));
			xr = x0i + x0r;
			xi = x2r + x2i;
			a[16*2+j] = descaleX(C16_2R * xr - C16_2I * xi);
			a[16*14+j] = descaleX(C16_2R * xi + C16_2I * xr);
			xr = x0i - x0r;
			xi = x2r - x2i;
			a[16*6+j] = descaleX(C16_6R * xr - C16_6I * xi);
			a[16*10+j] = descaleX(C16_6R * xi + C16_6I * xr);
			xr = descaleX(W16_8R * (x6r - x6i));
			xi = descaleX(W16_8R * (x6i + x6r));
			x6r = x4r - xr;
			x6i = x4i - xi;
			x4r += xr;
			x4i += xi;
			xr = descaleY(W16_4I * x7r - W16_4R * x7i);
			xi = descaleY(W16_4I * x7i + W16_4R * x7r);
			x7r = descaleY(W16_4R * x5r - W16_4I * x5i);
			x7i = descaleY(W16_4R * x5i + W16_4I * x5r);
			x5r = x7r + xr;
			x5i = x7i + xi;
			x7r -= xr;
			x7i -= xi;
			xr = x4r + x5r;
			xi = x5i + x4i;
			a[16*1+j] = descale2(C16_1R * xr - C16_1I * xi);
			a[16*15+j] = descale2(C16_1R * xi + C16_1I * xr);
			xr = x4r - x5r;
			xi = x5i - x4i;
			a[16*7+j] = descale2(C16_7R * xr - C16_7I * xi);
			a[16*9+j] = descale2(C16_7R * xi + C16_7I * xr);
			xr = x6r - x7i;
			xi = x7r + x6i;
			a[16*5+j] = descale2(C16_5R * xr - C16_5I * xi);
			a[16*11+j] = descale2(C16_5R * xi + C16_5I * xr);
			xr = x6r + x7i;
			xi = x7r - x6i;
			a[16*3+j] = descale2(C16_3R * xr - C16_3I * xi);
			a[16*13+j] = descale2(C16_3R * xi + C16_3I * xr);
		}
		for (int i = 0, j = 0; i <= 15; i++, j+=16)
		{
			x4r = a[j+0] - a[j+15];
			xr = a[j+0] + a[j+15];
			x4i = a[j+8] - a[j+7];
			xi = a[j+8] + a[j+7];
			x0r = xr + xi;
			x0i = xr - xi;
			x5r = a[j+2] - a[j+13];
			xr = a[j+2] + a[j+13];
			x5i = a[j+10] - a[j+5];
			xi = a[j+10] + a[j+5];
			x1r = xr + xi;
			x1i = xr - xi;
			x6r = a[j+4] - a[j+11];
			xr = a[j+4] + a[j+11];
			x6i = a[j+12] - a[j+3];
			xi = a[j+12] + a[j+3];
			x2r = xr + xi;
			x2i = xr - xi;
			x7r = a[j+6] - a[j+9];
			xr = a[j+6] + a[j+9];
			x7i = a[j+14] - a[j+1];
			xi = a[j+14] + a[j+1];
			x3r = xr + xi;
			x3i = xr - xi;
			xr = x0r + x2r;
			xi = x1r + x3r;
			a[j+0] = descale(C16_8R * (xr + xi));
			a[j+8] = descale(C16_8R * (xr - xi));
			xr = x0r - x2r;
			xi = x1r - x3r;
			a[j+4] = descale(C16_4R * xr - C16_4I * xi);
			a[j+12] = descale(C16_4R * xi + C16_4I * xr);
			x0r = descale(W16_8R * (x1i - x3i));
			x2r = descale(W16_8R * (x1i + x3i));
			xr = x0i + x0r;
			xi = x2r + x2i;
			a[j+2] = descale(C16_2R * xr - C16_2I * xi);
			a[j+14] = descale(C16_2R * xi + C16_2I * xr);
			xr = x0i - x0r;
			xi = x2r - x2i;
			a[j+6] = descale(C16_6R * xr - C16_6I * xi);
			a[j+10] = descale(C16_6R * xi + C16_6I * xr);
			xr = descale(W16_8R * (x6r - x6i));
			xi = descale(W16_8R * (x6i + x6r));
			x6r = x4r - xr;
			x6i = x4i - xi;
			x4r += xr;
			x4i += xi;
			xr = descale(W16_4I * x7r - W16_4R * x7i);
			xi = descale(W16_4I * x7i + W16_4R * x7r);
			x7r = descale(W16_4R * x5r - W16_4I * x5i);
			x7i = descale(W16_4R * x5i + W16_4I * x5r);
			x5r = x7r + xr;
			x5i = x7i + xi;
			x7r -= xr;
			x7i -= xi;
			xr = x4r + x5r;
			xi = x5i + x4i;
			a[j+1] = descale(C16_1R * xr - C16_1I * xi);
			a[j+15] = descale(C16_1R * xi + C16_1I * xr);
			xr = x4r - x5r;
			xi = x5i - x4i;
			a[j+7] = descale(C16_7R * xr - C16_7I * xi);
			a[j+9] = descale(C16_7R * xi + C16_7I * xr);
			xr = x6r - x7i;
			xi = x7r + x6i;
			a[j+5] = descale(C16_5R * xr - C16_5I * xi);
			a[j+11] = descale(C16_5R * xi + C16_5I * xr);
			xr = x6r + x7i;
			xi = x7r - x6i;
			a[j+3] = descale(C16_3R * xr - C16_3I * xi);
			a[j+13] = descale(C16_3R * xi + C16_3I * xr);
		}
	}


	@Override
	public void inverse(int[] a)
	{
		int x0r, x0i, x1r, x1i, x2r, x2i, x3r, x3i;
		int x4r, x4i, x5r, x5i, x6r, x6i, x7r, x7i;
		int xr, xi;

		for (int j = 0; j <= 15; j++)
		{
			x5r = descale(C16_1R * a[16*1+j] + C16_1I * a[16*15+j]);
			x5i = descale(C16_1R * a[16*15+j] - C16_1I * a[16*1+j]);
			xr = descale(C16_7R * a[16*7+j] + C16_7I * a[16*9+j]);
			xi = descale(C16_7R * a[16*9+j] - C16_7I * a[16*7+j]);
			x4r = x5r + xr;
			x4i = x5i - xi;
			x5r -= xr;
			x5i += xi;
			x7r = descale(C16_5R * a[16*5+j] + C16_5I * a[16*11+j]);
			x7i = descale(C16_5R * a[16*11+j] - C16_5I * a[16*5+j]);
			xr = descale(C16_3R * a[16*3+j] + C16_3I * a[16*13+j]);
			xi = descale(C16_3R * a[16*13+j] - C16_3I * a[16*3+j]);
			x6r = x7r + xr;
			x6i = x7i - xi;
			x7r -= xr;
			x7i += xi;
			xr = x4r - x6r;
			xi = x4i - x6i;
			x4r += x6r;
			x4i += x6i;
			x6r = descale(W16_8R * (xi + xr));
			x6i = descale(W16_8R * (xi - xr));
			xr = x5r + x7i;
			xi = x5i - x7r;
			x5r -= x7i;
			x5i += x7r;
			x7r = descale(W16_4I * x5r + W16_4R * x5i);
			x7i = descale(W16_4I * x5i - W16_4R * x5r);
			x5r = descale(W16_4R * xr + W16_4I * xi);
			x5i = descale(W16_4R * xi - W16_4I * xr);
			xr = descale(C16_4R * a[16*4+j] + C16_4I * a[16*12+j]);
			xi = descale(C16_4R * a[16*12+j] - C16_4I * a[16*4+j]);
			x2r = descale(C16_8R * (a[16*0+j] + a[16*8+j]));
			x3r = descale(C16_8R * (a[16*0+j] - a[16*8+j]));
			x0r = x2r + xr;
			x1r = x3r + xi;
			x2r -= xr;
			x3r -= xi;
			x0i = descale(C16_2R * a[16*2+j] + C16_2I * a[16*14+j]);
			x2i = descale(C16_2R * a[16*14+j] - C16_2I * a[16*2+j]);
			x1i = descale(C16_6R * a[16*6+j] + C16_6I * a[16*10+j]);
			x3i = descale(C16_6R * a[16*10+j] - C16_6I * a[16*6+j]);
			xr = x0i - x1i;
			xi = x2i + x3i;
			x0i += x1i;
			x2i -= x3i;
			x1i = descale(W16_8R * (xi + xr));
			x3i = descale(W16_8R * (xi - xr));
			xr = x0r + x0i;
			xi = x0r - x0i;
			a[16*0+j] = xr + x4r;
			a[16*15+j] = xr - x4r;
			a[16*8+j] = xi + x4i;
			a[16*7+j] = xi - x4i;
			xr = x1r + x1i;
			xi = x1r - x1i;
			a[16*2+j] = xr + x5r;
			a[16*13+j] = xr - x5r;
			a[16*10+j] = xi + x5i;
			a[16*5+j] = xi - x5i;
			xr = x2r + x2i;
			xi = x2r - x2i;
			a[16*4+j] = xr + x6r;
			a[16*11+j] = xr - x6r;
			a[16*12+j] = xi + x6i;
			a[16*3+j] = xi - x6i;
			xr = x3r + x3i;
			xi = x3r - x3i;
			a[16*6+j] = xr + x7r;
			a[16*9+j] = xr - x7r;
			a[16*14+j] = xi + x7i;
			a[16*1+j] = xi - x7i;
		}
		for (int i = 0, j = 0; i <= 15; i++, j+=16)
		{
			x5r = descale(C16_1R * a[j+1] + C16_1I * a[j+15]);
			x5i = descale(C16_1R * a[j+15] - C16_1I * a[j+1]);
			xr = descale(C16_7R * a[j+7] + C16_7I * a[j+9]);
			xi = descale(C16_7R * a[j+9] - C16_7I * a[j+7]);
			x4r = x5r + xr;
			x4i = x5i - xi;
			x5r -= xr;
			x5i += xi;
			x7r = descale(C16_5R * a[j+5] + C16_5I * a[j+11]);
			x7i = descale(C16_5R * a[j+11] - C16_5I * a[j+5]);
			xr = descale(C16_3R * a[j+3] + C16_3I * a[j+13]);
			xi = descale(C16_3R * a[j+13] - C16_3I * a[j+3]);
			x6r = x7r + xr;
			x6i = x7i - xi;
			x7r -= xr;
			x7i += xi;
			xr = x4r - x6r;
			xi = x4i - x6i;
			x4r += x6r;
			x4i += x6i;
			x6r = descale(W16_8R * (xi + xr));
			x6i = descale(W16_8R * (xi - xr));
			xr = x5r + x7i;
			xi = x5i - x7r;
			x5r -= x7i;
			x5i += x7r;
			x7r = descale(W16_4I * x5r + W16_4R * x5i);
			x7i = descale(W16_4I * x5i - W16_4R * x5r);
			x5r = descale(W16_4R * xr + W16_4I * xi);
			x5i = descale(W16_4R * xi - W16_4I * xr);
			xr = descale(C16_4R * a[j+4] + C16_4I * a[j+12]);
			xi = descale(C16_4R * a[j+12] - C16_4I * a[j+4]);
			x2r = descale(C16_8R * (a[j+0] + a[j+8]));
			x3r = descale(C16_8R * (a[j+0] - a[j+8]));
			x0r = x2r + xr;
			x1r = x3r + xi;
			x2r -= xr;
			x3r -= xi;
			x0i = descale(C16_2R * a[j+2] + C16_2I * a[j+14]);
			x2i = descale(C16_2R * a[j+14] - C16_2I * a[j+2]);
			x1i = descale(C16_6R * a[j+6] + C16_6I * a[j+10]);
			x3i = descale(C16_6R * a[j+10] - C16_6I * a[j+6]);
			xr = x0i - x1i;
			xi = x2i + x3i;
			x0i += x1i;
			x2i -= x3i;
			x1i = descale(W16_8R * (xi + xr));
			x3i = descale(W16_8R * (xi - xr));
			xr = x0r + x0i;
			xi = x0r - x0i;
			a[j+0] = xr + x4r;
			a[j+15] = xr - x4r;
			a[j+8] = xi + x4i;
			a[j+7] = xi - x4i;
			xr = x1r + x1i;
			xi = x1r - x1i;
			a[j+2] = xr + x5r;
			a[j+13] = xr - x5r;
			a[j+10] = xi + x5i;
			a[j+5] = xi - x5i;
			xr = x2r + x2i;
			xi = x2r - x2i;
			a[j+4] = xr + x6r;
			a[j+11] = xr - x6r;
			a[j+12] = xi + x6i;
			a[j+3] = xi - x6i;
			xr = x3r + x3i;
			xi = x3r - x3i;
			a[j+6] = xr + x7r;
			a[j+9] = xr - x7r;
			a[j+14] = xi + x7i;
			a[j+1] = xi - x7i;
		}
	}


	private static int descale(int v)
	{
//		return (v + (1 << (S-1))) >> S;
		return v/F;
	}


	private static int descale2(int v)
	{
//		return (v + (1 << (S-1))) >> S;
		return v/F/F;
	}


	private static int descaleX(int v)
	{
		return v/F;
	}


	private static int descaleY(int v)
	{
		return v;
	}


	public static void main(String... args)
	{
		int N = 16;

		int[] original = new int[N*N];
		Random rnd = new Random();
		for (int i = 0; i < N*N; i++)
		{
			original[i] = rnd.nextInt(256);
		}

		int [] block = original.clone();
		IntDCT16 dct = new IntDCT16();
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
		System.out.println("");
		Tools.print(N, N, input);
		System.out.println("");
		Tools.print(N, N, output);
		System.out.println("");
		Tools.print(N, N, err);
	}
}
