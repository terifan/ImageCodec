package org.terifan.multimedia.pic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import static java.lang.Math.min;
import static java.lang.Math.max;
import java.util.UUID;


public class Pyramid
{
//	private final static double[] GAUSSIAN_KERNEL =
//	{
//		0.000001, 0.000008, 0.000037, 0.000112, 0.000219, 0.000274, 0.000219, 0.000112, 0.000037, 0.000008, 0.000001,
//		0.000008, 0.000058, 0.000274, 0.000831, 0.001619, 0.002021, 0.001619, 0.000831, 0.000274, 0.000058, 0.000008,
//		0.000037, 0.000274, 0.001296, 0.003937, 0.007668, 0.009577, 0.007668, 0.003937, 0.001296, 0.000274, 0.000037,
//		0.000112, 0.000831, 0.003937, 0.011960, 0.023294, 0.029091, 0.023294, 0.011960, 0.003937, 0.000831, 0.000112,
//		0.000219, 0.001619, 0.007668, 0.023294, 0.045371, 0.056662, 0.045371, 0.023294, 0.007668, 0.001619, 0.000219,
//		0.000274, 0.002021, 0.009577, 0.029091, 0.056662, 0.070762, 0.056662, 0.029091, 0.009577, 0.002021, 0.000274,
//		0.000219, 0.001619, 0.007668, 0.023294, 0.045371, 0.056662, 0.045371, 0.023294, 0.007668, 0.001619, 0.000219,
//		0.000112, 0.000831, 0.003937, 0.011960, 0.023294, 0.029091, 0.023294, 0.011960, 0.003937, 0.000831, 0.000112,
//		0.000037, 0.000274, 0.001296, 0.003937, 0.007668, 0.009577, 0.007668, 0.003937, 0.001296, 0.000274, 0.000037,
//		0.000008, 0.000058, 0.000274, 0.000831, 0.001619, 0.002021, 0.001619, 0.000831, 0.000274, 0.000058, 0.000008,
//		0.000001, 0.000008, 0.000037, 0.000112, 0.000219, 0.000274, 0.000219, 0.000112, 0.000037, 0.000008, 0.000001
//	};


	public static void main(String ... args)
	{
		try
		{
//			File file = new File("d:/temp/in/Avril Lavigne_-_179.jpg");
			File file = new File("d:/temp/image compression/in/Lenna.png");
			BufferedImage source = ImageIO.read(file);
			BufferedImage debug1 = ImageIO.read(file);
			BufferedImage debug2 = ImageIO.read(file);
			BufferedImage debug3 = ImageIO.read(file);
			BufferedImage debug4 = ImageIO.read(file);

			Graphics g = debug1.createGraphics();

//			split(g, source, 0, 0, 9, 128);

			int [][] samples = new int[4][512*512];
			source.getRGB(0, 0, 512, 512, samples[0], 0, 512);
			ColorSpace.toYUV(samples[0], samples[1], samples[2], samples[3]);

			int [] orig = samples[1].clone();

			int [] preview = samples[1].clone();

			IntDCT dct = new IntDCTn(512);
			dct.forward(preview);

			int [] qt = QuantizationTable.buildQuantTable(1, 512, 512, 1);

			for (int y = 0, i=0; y < 512; y++)
			{
				for (int x = 0; x < 512; x++, i++)
				{
//					preview[i] = x >= 45-y ? 0 : (int)Math.round(preview[i]/(double)qt[i]);
//					preview[i] = x >= 32 || y >= 32 ? 0 : (int)Math.round(preview[i]/(double)qt[i]);
					preview[i] = (int)Math.round(preview[i]/(double)qt[i]);
				}
			}

//			Tools.print(512, 512, preview);

			for (int y = 0, i=0; y < 512; y++)
			{
				for (int x = 0; x < 512; x++, i++)
				{
					preview[i] = qt[i]*preview[i];
				}
			}

			dct.inverse(preview);


			int sum = 0;
			for (int y = 0, i=0; y < 512; y++)
			{
				for (int x = 0; x < 512; x++, i++)
				{
					sum+=Math.abs(preview[i]);

					int c = preview[i];
					c = Math.min(255, Math.max(0, c));
					debug1.setRGB(x,y,(c<<16)+(c<<8)+c);

					c = 128+Math.abs(orig[i]-preview[i]);
					c = Math.min(255, Math.max(0, c));
					debug4.setRGB(x,y,(c<<16)+(c<<8)+c);
				}
			}
//			System.out.println(sum);

			int w = 512;
			int h = 512;

			int errAccum1 = 0;
			int errAccum2 = 0;
			int errAccum1sq = 0;
			int errAccum2sq = 0;

			for (int by = 4; by < h; by+=4)
			{
				for (int bx = 4; bx < w; bx+=4)
				{
					int [] T = {
						samples[1][bx+0+(by-1)*w],
						samples[1][bx+1+(by-1)*w],
						samples[1][bx+2+(by-1)*w],
						samples[1][bx+3+(by-1)*w]
					};

					int [] L = {
						samples[1][bx-1+(by+0)*w],
						samples[1][bx-1+(by+1)*w],
						samples[1][bx-1+(by+2)*w],
						samples[1][bx-1+(by+3)*w]
					};

					int TL = samples[1][bx-1+(by-1)*w];

					for (int iy = 0; iy < 4; iy++)
					{
						for (int ix = 0, i = bx+(by+iy)*w; ix < 4; ix++, i++)
						{
							{
								int p = Math.min(Math.max(T[ix] + L[iy] - TL, 0), 255);

								int c = samples[1][bx+ix+(by+iy)*w];

								int q = c - p;

								errAccum1 += Math.abs(q);
								errAccum1sq += q*q;

								q += 128;
								int o = q > 255 ? q-255 : q < 0 ? 256 + q : q;

								if (o < 0 || o > 255) throw new RuntimeException();
								int t = p+q-128;
								int u = t < 0 ? 256+t : t > 255 ? t-256 : t;
								if (c != u) throw new RuntimeException(String.format("ERROR prev=%-3d  input=%-3d  tempin=%-3d  output=%-3d  tempout=%-3d  restore=%-3d\n", p, c, q, o, t, u));

								debug2.setRGB(bx+ix, by+iy, (o<<16)+(o<<8)+o);
							}
							{
								int p = (Math.min(Math.max(T[ix] + L[iy] - TL, 0), 255) + preview[i] + 1) / 2;

								int c = samples[1][bx+ix+(by+iy)*w];

								int q = c - p;

								errAccum2 += Math.abs(q);
								errAccum2sq += q*q;

								q += 128;
								int o = q > 255 ? q-255 : q < 0 ? 256 + q : q;

								if (o < 0 || o > 255) throw new RuntimeException();
								int t = p+q-128;
								int u = t < 0 ? 256+t : t > 255 ? t-256 : t;
								if (c != u) throw new RuntimeException(String.format("ERROR prev=%-3d  input=%-3d  tempin=%-3d  output=%-3d  tempout=%-3d  restore=%-3d\n", p, c, q, o, t, u));

								debug3.setRGB(bx+ix, by+iy, (o<<16)+(o<<8)+o);
							}
						}
					}
				}
			}

			System.out.println(errAccum1+" "+errAccum1sq);
			System.out.println(errAccum2+" "+errAccum2sq);

			g.dispose();

			ImageIO.write(debug1, "png", new File("d:/temp/image compression/out/thumb_" + file.getName()));
			ImageIO.write(debug2, "png", new File("d:/temp/image compression/out/pred1_" + file.getName()));
			ImageIO.write(debug3, "png", new File("d:/temp/image compression/out/pred2_" + file.getName()));
			ImageIO.write(debug4, "png", new File("d:/temp/image compression/out/diff_" + file.getName()));
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
/*
1024x1024	1
512x512		4
256x256		16
128x128		64
64x64		256
32x32		1024
16x16		4096
8x8			16384
4x4			65536
*/


//	private static void convolve(int[] input, int width, int height, double [] kernel, int w, int h)
//	{
//		int [] output = new int[width*height];
//
//		w /= 2;
//		h /= 2;
//
//		for (int y = 0, i = 0; y < height; y++)
//		{
//			for (int x = 0; x < width; x++, i++)
//			{
//				double sum = 0;
//				double scale = 0;
//				for (int fy = -w, fi = 0; fy <= w; fy++)
//				{
//					for (int fx = -h; fx <= h; fx++, fi++)
//					{
//						if (x + fx >= 0 && x + fx < width && y + fy >= 0 && y + fy < height)
//						{
//							double f = kernel[fi];
//							double c = input[x + fx + (y + fy) * width];
//							sum += c * f;
//							scale += f;
//						}
//					}
//				}
//				output[i] = (int)(sum / scale);
//			}
//		}
//
//		System.arraycopy(output, 0, input, 0, output.length);
//	}

	private static void split(Graphics g, BufferedImage image, int x, int y, int level, int avg)
	{
		int dim = 1<<level;
		int sum = 0;
		int min = 255;
		int max = 0;

		for (int iy = 0; iy < dim; iy++)
		{
			for (int ix = 0; ix < dim; ix++)
			{
				int c = image.getRGB(x+ix,y+iy);
				c = ((255 & (c >> 16))+(255 & (c >> 8))+(255 & c)) / 3;

				if (c < min) min = c;
				if (c > max) max = c;

				sum += c;
			}
		}
		sum /= dim*dim;

		if (level <= 2 || max-min < 64)
		{
			g.setColor(new Color(sum,sum,sum));
			g.fillRect(x, y, dim, dim);
//			g.setColor(max-min < 32 ? Color.GREEN : Color.RED);
//			g.drawRect(x, y, dim, dim);
		}
		else
		{
			avg = sum;
			level--;

			split(g, image, x, y, level, avg);
			split(g, image, x+(1<<level), y, level, avg);
			split(g, image, x, y+(1<<level), level, avg);
			split(g, image, x+(1<<level), y+(1<<level), level, avg);
		}
	}
}
