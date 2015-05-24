package org.terifan.imagecodec;

import org.terifan.imagecodec.dct.IntDCT8;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;


public class AreaSubdivision
{
	private static IntDCT8 dct = new IntDCT8();


	public static void main(String... args)
	{
		try
		{
			for (File file : new File("D:\\temp\\image_compression\\in").listFiles())
//			File file = new File("D:\\temp\\image_compression\\in\\Lenna.png");
			{
				System.out.println(file);

				BufferedImage srcImage = ImageIO.read(file);
				BufferedImage workImage = padImage(srcImage);

				double[][] coefficients = new double[workImage.getHeight() / 8][workImage.getWidth() / 8];

				for (int y = 0; y < coefficients.length; y++)
				{
					for (int x = 0; x < coefficients[0].length; x++)
					{
						coefficients[y][x] = getCoefficient(workImage, 8 * x, 8 * y);
					}
				}

				BufferedImage debugImage = new BufferedImage(workImage.getWidth(), workImage.getHeight(), BufferedImage.TYPE_INT_RGB);

				Graphics g = debugImage.createGraphics();
				g.drawImage(workImage, 0, 0, null);

				int m = Math.min(6, (int)(Math.log(Math.min(workImage.getWidth(), workImage.getHeight())) / Math.log(2)));
				int n = 1 << m;
				for (int y = 0; y < coefficients.length; y += n)
				{
					for (int x = 0; x < coefficients[0].length; x += n)
					{
						split(g, coefficients, x, y, m, workImage, debugImage);
					}
				}

				g.dispose();
				
				BufferedImage outputImage = debugImage.getSubimage(0, 0, srcImage.getWidth(), srcImage.getHeight());

				ImageIO.write(outputImage, "png", new File("D:\\temp\\image_compression\\out", file.getName()));
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


	private static boolean split(Graphics g, double[][] aCoefficients, int aBlockX, int aBlockY, int aLevel, BufferedImage aInput, BufferedImage aDebug)
	{
		if (aBlockY >= aCoefficients.length || aBlockX >= aCoefficients[0].length)
		{
			return true;
		}

		int n = 1 << aLevel;
		double min = 100000;
		double max = -100000;

		for (int y = 0; y < n; y++)
		{
			for (int x = 0; x < n; x++)
			{
				if (aBlockY + y < aCoefficients.length && aBlockX + x < aCoefficients[0].length)
				{
					double r = aCoefficients[aBlockY + y][aBlockX + x];
					if (r < min)
					{
						min = r;
					}
					if (r > max)
					{
						max = r;
					}
				}
			}
		}

		double limit = 0.0625*2; // 128=0.0625
		double delta = Math.abs(max - min);

		if (aLevel == 0 || 8 * aBlockX + 8 * n <= aInput.getWidth() && 8 * aBlockY + 8 * n <= aInput.getHeight() && aLevel <= 6 && delta < limit)
		{
			int[] block = new int[8 * n * 8 * n];
			int[][] samples = new int[3][block.length];

			aInput.getRGB(8 * aBlockX, 8 * aBlockY, 8 * n, 8 * n, block, 0, 8 * n);

			ColorSpace.toYUV2(block, samples[0], samples[1], samples[2]);

			for (int i = 0; i < block.length; i++)
			{
				block[i] = (samples[0][i] << 16) + (samples[0][i] << 8) + (samples[0][i]);
			}

			aDebug.setRGB(8 * aBlockX, 8 * aBlockY, 8 * n, 8 * n, block, 0, 8 * n);

			g.setColor(new Color(0, 255, 0));
			g.drawRect(8 * aBlockX, 8 * aBlockY, 8 * n - 1, 8 * n - 1);

			return true;
		}
		else
		{
			n >>= 1;

			boolean b = true;
			b &= split(g, aCoefficients, aBlockX, aBlockY, aLevel - 1, aInput, aDebug);
			b &= split(g, aCoefficients, aBlockX + n, aBlockY, aLevel - 1, aInput, aDebug);
			b &= split(g, aCoefficients, aBlockX, aBlockY + n, aLevel - 1, aInput, aDebug);
			b &= split(g, aCoefficients, aBlockX + n, aBlockY + n, aLevel - 1, aInput, aDebug);

			return b;
		}
	}


	private static double getCoefficient(BufferedImage aImage, int aBlockX, int aBlockY)
	{
		int[] block = new int[64];
		int[] y = new int[64];
		int[] u = new int[64];
		int[] v = new int[64];

		aImage.getRGB(aBlockX, aBlockY, 8, 8, block, 0, 8);

		ColorSpace.toYUV2(block, y, u, v);

		dct.forward(y);
//		dct.forward(u);
//		dct.forward(v);
		
		if (y[0] >= 2048)
		{
			throw new RuntimeException();
		}

		return y[0] / 2048.0;
//		return (u[0]+v[0])/2048.0/2;
//		return (y[0]+u[0]+v[0])/2048.0/3;
	}


	private static BufferedImage padImage(BufferedImage aImage)
	{
		int w = aImage.getWidth();
		int h = aImage.getHeight();
		int s = 512;

		if ((w & (s-1)) != 0 || (h & (s-1)) != 0)
		{
			BufferedImage dst = new BufferedImage(s * ((w + (s-1)) / s), s * ((h + (s-1)) / s), BufferedImage.TYPE_INT_RGB);

			Graphics g = dst.createGraphics();

			g.drawImage(aImage, w, 0, dst.getWidth(), h, w-1, 0, w, h, null);
			g.drawImage(aImage, 0, h, w, dst.getHeight(), 0, h-1, w, h, null);
			g.drawImage(aImage, w, h, dst.getWidth(), dst.getHeight(), w-1, h-1, w, h, null);
			g.drawImage(aImage, 0, 0, null);

			g.dispose();

			aImage = dst;
		}

		return aImage;
	}
}
