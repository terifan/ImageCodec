package org.terifan.multimedia.pic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Random;
import javax.imageio.ImageIO;

// http://www.ijg.org/

// 4:4:4 = 4Y + 4U + 4V
// 4:2:2 = 4Y + 2U + 2V
// 4:2:0 = 4Y + 1U + 1V

public class Test
{
	public static void main(String... args)
	{
		try
		{
			int [] quantval = new int[16*16];
//			for (int i = 1, k = 0; i < list.length; i++)
//			{
//				for (int j = 0; j < i; j++)
//				{
//					if (k >= list.length) break;
//					list[k++] = i;
//				}
//			}
			for (int i = 0; i < quantval.length; i++)
			{
				quantval[i] = 128-(int)Math.round(127*Math.cos(Math.PI/2*i/(double)(quantval.length-1)));
			}
			print(16, 16, quantval);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
	/*
	public static void main(String... args)
	{
		try
		{
			DCT a = new FloatDCT(8, 8);
			DCT b = new FastIntDCT(8, 8);
			BufferedImage image = ImageIO.read(new File("c:/temp/in/avril.jpg"));
			int [] rgb = new int[8*8];
			int [] Y = new int[8*8];
			int [] U = new int[8*8];
			int [] V = new int[8*8];
			image.getRGB(new Random().nextInt(image.getWidth()-7), new Random().nextInt(image.getHeight()-7), 8, 8, rgb, 0, 8);
			ColorSpace.toYUV(rgb, Y, U, V);
			
			int [] Ya = Y.clone();
			int [] Yb = Y.clone();
			a.forward(Ya);
			b.forward(Yb);
			
			print(8, 8, Ya, Yb);
			
			a.inverse(Ya);
			b.inverse(Yb);
			
			print(8, 8, Ya, Yb);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
	*/
	/*
	public static void main(String ... args)
	{
		try
		{
			Random rnd = new Random(1);

			int W = 32;
			int H = 32;

			DCT dct = new FloatDCT(W,H);
//			DCT dct = new FastIntDCT(W,H);

			int [] rgb = new int[W*H];

			int[] Q1 = buildQuantTable(80, W, H, true);
			int[] Q2 = buildQuantTable(80, W, H, false);

			int[] Y = new int[W * H];
			int[] U = new int[W * H];
			int[] V = new int[W * H];

			BufferedImage image = ImageIO.read(new File("c:/avril.jpg"));

			for (int y = 0; y < image.getHeight()-H+1; y += H)
			{
				System.out.println((y/H)+"/"+(image.getHeight()/H));

				for (int x = 0; x < image.getWidth()-W+1; x += W)
				{
					image.getRGB(x, y, W, H, rgb, 0, W);

					ColorSpace.toYUV(rgb, Y, U, V);

//					System.out.println("----------------------------------------------------");
//					print(W, H, new int[][]{Y});

					int [] Ya = Y.clone();
					int [] Ua = U.clone();
					int [] Va = V.clone();

					encodeBlock(dct, Y, Q1, W, H);
					encodeBlock(dct, U, Q2, W, H);
					encodeBlock(dct, V, Q2, W, H);

//					print(W, H, new int[][]{Y});

					// coefficient reorder
					// coefficient prediction
					// entropy encoder

					decodeBlock(dct, Y, Q1, W, H);
					decodeBlock(dct, U, Q2, W, H);
					decodeBlock(dct, V, Q2, W, H);

					if (getErr(Y,Ya) > 0) System.out.println(getErr(Y,Ya));
					if (getErr(U,Ua) > 0) System.out.println(getErr(U,Ua));
					if (getErr(V,Va) > 0) System.out.println(getErr(V,Va));

//					print(W, H, new int[][]{Y});

					ColorSpace.toRGB(rgb, Y, U, V);

					image.setRGB(x, y, W, H, rgb, 0, W);
				}
			}

			ImageIO.write(image, "png", new File("c:/output.png"));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


	private static int getErr(int [] a, int [] b)
	{
		int err = 0;
		for (int i = 0; i < a.length; i++)
		{
			if (a[i] != b[i]) err++;
		}
		return err;
	}


	private static void encodeBlock(DCT dct, int [] samples, int [] quantTable, int W, int H)
	{
//		double [][] dd = new double[W][H];
//		for (int i = 0, k = 0; i < H; i++)
//		{
//			for (int j = 0; j < W; j++)
//			{
//				dd[i][j] = samples[k++];
//			}
//		}
//		DCT16a.ddct16x16s(1, dd);
//		for (int i = 0, k = 0; i < H; i++)
//		{
//			for (int j = 0; j < W; j++)
//			{
//				samples[k++] = (int)(256 * dd[i][j]);
//			}
//		}

		dct.forward(samples);

		for (int i = 0; i < samples.length; i++)
		{
			samples[i] /= quantTable[i];
		}
	}


	private static void decodeBlock(DCT dct, int [] samples, int [] quantTable, int W, int H)
	{
		for (int i = 0; i < samples.length; i++)
		{
			samples[i] *= quantTable[i];
		}

//		double [][] dd = new double[W][H];
//		for (int i = 0, k = 0; i < H; i++)
//		{
//			for (int j = 0; j < W; j++)
//			{
//				dd[i][j] = samples[k++]/256.0;
//			}
//		}
//		DCT16a.ddct16x16s(-1, dd);
//		for (int i = 0, k = 0; i < H; i++)
//		{
//			for (int j = 0; j < W; j++)
//			{
//				samples[k++] = (int)(dd[i][j]);
//			}
//		}

		dct.inverse(samples);
	}


	private static int DESCALE(int x, int n)
	{
		return (x + (1 << (n - 1))) >> n;
	}


	private static int[] buildQuantTable(int aQuality, int W, int H, boolean aLuminance)
	{
		if (aQuality <= 0)
		{
			aQuality = 1;
		}
		if (aQuality > 100)
		{
			aQuality = 100;
		}

		if (aQuality < 50)
		{
			aQuality = 5000 / aQuality;
		}
		else
		{
			aQuality = 200 - aQuality * 2;
		}

		int[] std_luminance_quant_tbl =
		{
			16, 11, 10, 16, 24, 40, 51, 61,
			12, 12, 14, 19, 26, 58, 60, 55,
			14, 13, 16, 24, 40, 57, 69, 56,
			14, 17, 22, 29, 51, 87, 80, 62,
			18, 22, 37, 56, 68, 109, 103, 77,
			24, 35, 55, 64, 81, 104, 113, 92,
			49, 64, 78, 87, 103, 121, 120, 101,
			72, 92, 95, 98, 112, 100, 103, 99
		};
		int[] std_chrominance_quant_tbl =
		{
			17, 18, 24, 47, 99, 99, 99, 99,
			18, 21, 26, 66, 99, 99, 99, 99,
			24, 26, 56, 99, 99, 99, 99, 99,
			47, 66, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99
		};

		int[] std_luminance_quant_tbl16 =
		{
			16, 12, 11, 11, 10, 10, 16, 16,  24, 24,  40,  40, 51, 51,  61,  61,
			12, 12, 11, 11, 10, 10, 16, 16,  24, 24,  40,  40, 51, 51,  61,  61,
			12, 12, 12, 12, 14, 14, 19, 19,  26, 26,  58,  58, 60, 60,  55,  55,
			12, 12, 12, 12, 14, 14, 19, 19,  26, 26,  58,  58, 60, 60,  55,  55,
			14, 14, 13, 13, 16, 16, 24, 24,  40, 40,  57,  57, 69, 69,  56,  56,
			14, 14, 13, 13, 16, 16, 24, 24,  40, 40,  57,  57, 69, 69,  56,  56,
			14, 14, 17, 17, 22, 22, 29, 29,  51, 51,  87,  87, 80, 80,  62,  62,
			14, 14, 17, 17, 22, 22, 29, 29,  51, 51,  87,  87, 80, 80,  62,  62,
			18, 18, 22, 22, 37, 37, 56, 56,  68, 68, 109, 109,103,103,  77,  77,
			18, 18, 22, 22, 37, 37, 56, 56,  68, 68, 109, 109,103,103,  77,  77,
			24, 24, 35, 35, 55, 55, 64, 64,  81, 81, 104, 104,113,113,  92,  92,
			24, 24, 35, 35, 55, 55, 64, 64,  81, 81, 104, 104,113,113,  92,  92,
			49, 49, 64, 64, 78, 78, 87, 87, 103,103, 121, 121,120,120, 101, 101,
			49, 49, 64, 64, 78, 78, 87, 87, 103,103, 121, 121,120,120, 101, 101,
			72, 72, 92, 92, 95, 95, 98, 98, 112,112, 100, 100,103,103,  99,  99,
			72, 72, 92, 92, 95, 95, 98, 98, 112,112, 100, 100,103,103,  99,  99
		};
		int[] std_chrominance_quant_tbl16 =
		{
			17, 17, 18, 18, 24, 24, 47, 47, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			17, 17, 18, 18, 24, 24, 47, 47, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			18, 18, 21, 21, 26, 26, 66, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			18, 18, 21, 21, 26, 26, 66, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			24, 24, 26, 26, 56, 56, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			24, 24, 26, 26, 56, 56, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			47, 47, 66, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			47, 47, 66, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99
		};

		int[] std_luminance_quant_tbl32 =
		{
			16, 12, 12, 12, 11, 11, 11, 11, 10, 10, 10, 10, 16, 16, 16, 16,  24, 24, 24, 24,  40,  40,  40,  40, 51, 51, 51, 51,  61,  61,  61,  61,
			12, 12, 12, 12, 11, 11, 11, 11, 10, 10, 10, 10, 16, 16, 16, 16,  24, 24, 24, 24,  40,  40,  40,  40, 51, 51, 51, 51,  61,  61,  61,  61,
			12, 12, 12, 12, 11, 11, 11, 11, 10, 10, 10, 10, 16, 16, 16, 16,  24, 24, 24, 24,  40,  40,  40,  40, 51, 51, 51, 51,  61,  61,  61,  61,
			12, 12, 12, 12, 11, 11, 11, 11, 10, 10, 10, 10, 16, 16, 16, 16,  24, 24, 24, 24,  40,  40,  40,  40, 51, 51, 51, 51,  61,  61,  61,  61,
			12, 12, 12, 12, 12, 12, 12, 12, 14, 14, 14, 14, 19, 19, 19, 19,  26, 26, 26, 26,  58,  58,  58,  58, 60, 60, 60, 60,  55,  55,  55,  55,
			12, 12, 12, 12, 12, 12, 12, 12, 14, 14, 14, 14, 19, 19, 19, 19,  26, 26, 26, 26,  58,  58,  58,  58, 60, 60, 60, 60,  55,  55,  55,  55,
			12, 12, 12, 12, 12, 12, 12, 12, 14, 14, 14, 14, 19, 19, 19, 19,  26, 26, 26, 26,  58,  58,  58,  58, 60, 60, 60, 60,  55,  55,  55,  55,
			12, 12, 12, 12, 12, 12, 12, 12, 14, 14, 14, 14, 19, 19, 19, 19,  26, 26, 26, 26,  58,  58,  58,  58, 60, 60, 60, 60,  55,  55,  55,  55,
			14, 14, 14, 14, 13, 13, 13, 13, 16, 16, 16, 16, 24, 24, 24, 24,  40, 40, 40, 40,  57,  57,  57,  57, 69, 69, 69, 69,  56,  56,  56,  56,
			14, 14, 14, 14, 13, 13, 13, 13, 16, 16, 16, 16, 24, 24, 24, 24,  40, 40, 40, 40,  57,  57,  57,  57, 69, 69, 69, 69,  56,  56,  56,  56,
			14, 14, 14, 14, 13, 13, 13, 13, 16, 16, 16, 16, 24, 24, 24, 24,  40, 40, 40, 40,  57,  57,  57,  57, 69, 69, 69, 69,  56,  56,  56,  56,
			14, 14, 14, 14, 13, 13, 13, 13, 16, 16, 16, 16, 24, 24, 24, 24,  40, 40, 40, 40,  57,  57,  57,  57, 69, 69, 69, 69,  56,  56,  56,  56,
			14, 14, 14, 14, 17, 17, 17, 17, 22, 22, 22, 22, 29, 29, 29, 29,  51, 51, 51, 51,  87,  87,  87,  87, 80, 80, 80, 80,  62,  62,  62,  62,
			14, 14, 14, 14, 17, 17, 17, 17, 22, 22, 22, 22, 29, 29, 29, 29,  51, 51, 51, 51,  87,  87,  87,  87, 80, 80, 80, 80,  62,  62,  62,  62,
			14, 14, 14, 14, 17, 17, 17, 17, 22, 22, 22, 22, 29, 29, 29, 29,  51, 51, 51, 51,  87,  87,  87,  87, 80, 80, 80, 80,  62,  62,  62,  62,
			14, 14, 14, 14, 17, 17, 17, 17, 22, 22, 22, 22, 29, 29, 29, 29,  51, 51, 51, 51,  87,  87,  87,  87, 80, 80, 80, 80,  62,  62,  62,  62,
			18, 18, 18, 18, 22, 22, 22, 22, 37, 37, 37, 37, 56, 56, 56, 56,  68, 68, 68, 68, 109, 109, 109, 109,103,103,103,103,  77,  77,  77,  77,
			18, 18, 18, 18, 22, 22, 22, 22, 37, 37, 37, 37, 56, 56, 56, 56,  68, 68, 68, 68, 109, 109, 109, 109,103,103,103,103,  77,  77,  77,  77,
			18, 18, 18, 18, 22, 22, 22, 22, 37, 37, 37, 37, 56, 56, 56, 56,  68, 68, 68, 68, 109, 109, 109, 109,103,103,103,103,  77,  77,  77,  77,
			18, 18, 18, 18, 22, 22, 22, 22, 37, 37, 37, 37, 56, 56, 56, 56,  68, 68, 68, 68, 109, 109, 109, 109,103,103,103,103,  77,  77,  77,  77,
			24, 24, 24, 24, 35, 35, 35, 35, 55, 55, 55, 55, 64, 64, 64, 64,  81, 81, 81, 81, 104, 104, 104, 104,113,113,113,113,  92,  92,  92,  92,
			24, 24, 24, 24, 35, 35, 35, 35, 55, 55, 55, 55, 64, 64, 64, 64,  81, 81, 81, 81, 104, 104, 104, 104,113,113,113,113,  92,  92,  92,  92,
			24, 24, 24, 24, 35, 35, 35, 35, 55, 55, 55, 55, 64, 64, 64, 64,  81, 81, 81, 81, 104, 104, 104, 104,113,113,113,113,  92,  92,  92,  92,
			24, 24, 24, 24, 35, 35, 35, 35, 55, 55, 55, 55, 64, 64, 64, 64,  81, 81, 81, 81, 104, 104, 104, 104,113,113,113,113,  92,  92,  92,  92,
			49, 49, 49, 49, 64, 64, 64, 64, 78, 78, 78, 78, 87, 87, 87, 87, 103,103,103,103, 121, 121, 121, 121,120,120,120,120, 101, 101, 101, 101,
			49, 49, 49, 49, 64, 64, 64, 64, 78, 78, 78, 78, 87, 87, 87, 87, 103,103,103,103, 121, 121, 121, 121,120,120,120,120, 101, 101, 101, 101,
			49, 49, 49, 49, 64, 64, 64, 64, 78, 78, 78, 78, 87, 87, 87, 87, 103,103,103,103, 121, 121, 121, 121,120,120,120,120, 101, 101, 101, 101,
			49, 49, 49, 49, 64, 64, 64, 64, 78, 78, 78, 78, 87, 87, 87, 87, 103,103,103,103, 121, 121, 121, 121,120,120,120,120, 101, 101, 101, 101,
			72, 72, 72, 72, 92, 92, 92, 92, 95, 95, 95, 95, 98, 98, 98, 98, 112,112,112,112, 100, 100, 100, 100,103,103,103,103,  99,  99,  99,  99,
			72, 72, 72, 72, 92, 92, 92, 92, 95, 95, 95, 95, 98, 98, 98, 98, 112,112,112,112, 100, 100, 100, 100,103,103,103,103,  99,  99,  99,  99,
			72, 72, 72, 72, 92, 92, 92, 92, 95, 95, 95, 95, 98, 98, 98, 98, 112,112,112,112, 100, 100, 100, 100,103,103,103,103,  99,  99,  99,  99,
			72, 72, 72, 72, 92, 92, 92, 92, 95, 95, 95, 95, 98, 98, 98, 98, 112,112,112,112, 100, 100, 100, 100,103,103,103,103,  99,  99,  99,  99
		};

		int[] std_chrominance_quant_tbl32 =
		{
			17, 17, 17, 17, 18, 18, 18, 18, 24, 24, 24, 24, 47, 47, 47, 47, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			17, 17, 17, 17, 18, 18, 18, 18, 24, 24, 24, 24, 47, 47, 47, 47, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			17, 17, 17, 17, 18, 18, 18, 18, 24, 24, 24, 24, 47, 47, 47, 47, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			17, 17, 17, 17, 18, 18, 18, 18, 24, 24, 24, 24, 47, 47, 47, 47, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			18, 18, 18, 18, 21, 21, 21, 21, 26, 26, 26, 26, 66, 66, 66, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			18, 18, 18, 18, 21, 21, 21, 21, 26, 26, 26, 26, 66, 66, 66, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			18, 18, 18, 18, 21, 21, 21, 21, 26, 26, 26, 26, 66, 66, 66, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			18, 18, 18, 18, 21, 21, 21, 21, 26, 26, 26, 26, 66, 66, 66, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			24, 24, 24, 24, 26, 26, 26, 26, 56, 56, 56, 56, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			24, 24, 24, 24, 26, 26, 26, 26, 56, 56, 56, 56, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			24, 24, 24, 24, 26, 26, 26, 26, 56, 56, 56, 56, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			24, 24, 24, 24, 26, 26, 26, 26, 56, 56, 56, 56, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			47, 47, 47, 47, 66, 66, 66, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			47, 47, 47, 47, 66, 66, 66, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			47, 47, 47, 47, 66, 66, 66, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			47, 47, 47, 47, 66, 66, 66, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99
		};

		int[] basic_table;
		if (W == 32)
		{
			basic_table = aLuminance ? std_luminance_quant_tbl32 : std_chrominance_quant_tbl32;
		}
		else if (W == 16)
		{
			basic_table = aLuminance ? std_luminance_quant_tbl16 : std_chrominance_quant_tbl16;
		}
		else
		{
			basic_table = aLuminance ? std_luminance_quant_tbl : std_chrominance_quant_tbl;
		}

		int[] quantval = new int[W*H];

		for (int i = 0; i < W*H; i++)
		{
			int temp = (basic_table[i] * aQuality + 50) / 100;
			if (temp <= 0)
			{
				temp = 1;
			}
			if (temp > 255) // 32767 for 12-bit samles
			{
				temp = 255;
			}
			quantval[i] = temp;
		}

		return quantval;
	}


	private static void testQuality()
	{
		for (int j = 0; j < 1; j++)
		{
			int W = 8;
			int H = 8;

			int [] original = new int[W*H];

			Random rnd = new Random();
			int x = rnd.nextInt(1920-W);
			int y = rnd.nextInt(1200-H);
//			ImageIO.read(new File("c:/avril.jpg")).getRGB(x, y, W, H, original, 0, W);
			for (int i = 0; i < W*H; i++) original[i] &= 0xff;
			original[0] = 8;

			{
			int [] block = Arrays.copyOf(original, W*H);
			OldFDCT.transformFloat(block, W, H);
//				FDCT.forward8x8(block);
			quant(block);
			int [] encoded = Arrays.copyOf(block, W*H);
			OldIDCT.transformFloat(block, W, H);
//				IDCT.inverse8x8(block);
			int [] decoded = Arrays.copyOf(block, W*H);
			int [] diff = Arrays.copyOf(block, W*H);
			for (int i = 0; i < W*H; i++) diff[i] -= original[i];
			print(W, H, new int[][]{original, encoded, decoded, diff});
			}

			{
			int [] block = Arrays.copyOf(original, W*H);
			OldFDCT.fdct(block.clone(), block, W);
			quant(block);
			int [] encoded = Arrays.copyOf(block, W*H);
			OldIDCT.idct(block.clone(), block, W);
			int [] decoded = Arrays.copyOf(block, W*H);
			int [] diff = Arrays.copyOf(block, W*H);
			for (int i = 0; i < W*H; i++) diff[i] -= original[i];
			print(W, H, new int[][]{original, encoded, decoded, diff});
			}

//				{
//				int [] block = Arrays.copyOf(original, W*H);
//				FDCT.transformFloat(block, W, H);
//				int [] encoded = Arrays.copyOf(block, W*H);
//				IDCT.transformFloatFP(block, W, H);
//				int [] decoded = Arrays.copyOf(block, W*H);
//				int [] diff = Arrays.copyOf(block, W*H);
//				for (int i = 0; i < W*H; i++) diff[i] -= original[i];
//				print(W, H, new int[][]{original, encoded, decoded, diff});
//				}

			{
			int [] block = Arrays.copyOf(original, W*H);
			OldFDCT.forward8x8(block);
			quant(block);
			int [] encoded = Arrays.copyOf(block, W*H);
			OldIDCT.inverse8x8(block);
			int [] decoded = Arrays.copyOf(block, W*H);
			int [] diff = Arrays.copyOf(block, W*H);
			for (int i = 0; i < W*H; i++) diff[i] -= original[i];
			print(W, H, new int[][]{original, encoded, decoded, diff});
			}

//				{
//				int [] block = Arrays.copyOf(original, W*H);
//				FDCT.forward8x8(block);
//				int [] encoded = Arrays.copyOf(block, W*H);
//				IDCT.inverse8x8Float(block);
//				int [] decoded = Arrays.copyOf(block, W*H);
//				int [] diff = Arrays.copyOf(block, W*H);
//				for (int i = 0; i < W*H; i++) diff[i] -= original[i];
//				print(W, H, new int[][]{original, encoded, decoded, diff});
//				}
		}
	}

	private static void quant(int [] block)
	{
		for (int i = 0; i < block.length; i++)
		{
			block[i] &= ~0x4;
		}
	}
	*/


	private static void print(int W, int H, float[] ... block)
	{
		for (int i = 0; i < H; i++)
		{
			for (int k = 0; k < block.length; k++)
			{
				for (int j = 0; j < W; j++)
				{
					System.out.printf("% 8.2f ", block[k][W*i+j]);
				}
				System.out.print("\t");
			}
			System.out.println();
		}
		System.out.println("");
	}


	private static void print(int W, int H, int[] ... block)
	{
		for (int i = 0; i < H; i++)
		{
			for (int k = 0; k < block.length; k++)
			{
				for (int j = 0; j < W; j++)
				{
					System.out.printf("% 8d ", block[k][W*i+j]);
				}
				System.out.print("\t");
			}
			System.out.println();
		}
		System.out.println("");
	}
}
