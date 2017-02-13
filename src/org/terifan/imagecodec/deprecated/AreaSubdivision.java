package org.terifan.imagecodec.deprecated;

import org.terifan.imagecodec.ColorSpace;
import org.terifan.imagecodec.dct.IntDCTn;
import org.terifan.imagecodec.dct.IntDCT8;
import org.terifan.imagecodec.dct.IntDCT;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;


public class AreaSubdivision
{
	private static IntDCT dct = new IntDCT8();
	private static IntDCT [] dcts =
	{
		new IntDCTn(8),
		new IntDCTn(16),
		new IntDCTn(32),
		new IntDCTn(64),
		new IntDCTn(128),
		new IntDCTn(256),
		new IntDCTn(512)
	};
	private static IntDCT [] dcts2 =
	{
		new IntDCTn(2),
		new IntDCTn(4),
		new IntDCTn(8),
		new IntDCTn(16),
		new IntDCTn(32),
		new IntDCTn(64),
		new IntDCTn(128),
		new IntDCTn(256),
		new IntDCTn(512)
	};

	static int Q = 80;
	static int QQ = 80;

	static int [][][] quanttbl = {
		{QuantizationTable.buildQuantTable(Q, 8, 8, 0), QuantizationTable.buildQuantTable(QQ, 8, 8, 1)},
		{QuantizationTable.buildQuantTable(Q, 16, 16, 0), QuantizationTable.buildQuantTable(QQ, 16, 16, 1)},
		{QuantizationTable.buildQuantTable(Q, 32, 32, 0), QuantizationTable.buildQuantTable(QQ, 32, 32, 1)},
		{QuantizationTable.buildQuantTable(Q, 64, 64, 0), QuantizationTable.buildQuantTable(QQ, 64, 64, 1)},
		{QuantizationTable.buildQuantTable(Q, 128, 128, 0), QuantizationTable.buildQuantTable(QQ, 128, 128, 1)},
		{QuantizationTable.buildQuantTable(Q, 256, 256, 0), QuantizationTable.buildQuantTable(QQ, 256, 256, 1)},
		{QuantizationTable.buildQuantTable(Q, 512, 512, 0), QuantizationTable.buildQuantTable(QQ, 512, 512, 1)}
	};

	public static void main(String ... args)
	{
		try
		{
			for (File file : new File("D:\\Resources\\image compression reference images\\in").listFiles())
//			File file = new File("D:\Resources\image compression reference images\\Lenna.png");
			{
				System.out.println(file);

				BufferedImage image = ImageIO.read(file);

				if ((image.getWidth() & 7) != 0 || (image.getHeight() & 7) != 0)
				{
					BufferedImage temp = new BufferedImage(8*((image.getWidth() + 7)/8), 8*((image.getHeight()+7)/8), BufferedImage.TYPE_INT_RGB);
					Graphics g = temp.createGraphics();
					for (int y = temp.getHeight()-image.getHeight(); y >= 0; y--)
					{
						for (int x = temp.getWidth()-image.getWidth(); x >= 0; x--)
						{
							g.drawImage(image, x, y, null);
						}
					}
					g.dispose();
					image = temp;
				}

				BufferedImage dest = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

				double [][] coefficients = new double[image.getHeight()/8][image.getWidth()/8];

				for (int y = 0; y < coefficients.length; y++)
				{
					for (int x = 0; x < coefficients[0].length; x++)
					{
						coefficients[y][x] = getCoefficient(image, 8*x, 8*y);
					}
				}

				BufferedImage debug = new BufferedImage(8*((image.getWidth() + 7)/8), 8*((image.getHeight()+7)/8), BufferedImage.TYPE_INT_RGB);
				Graphics g = debug.createGraphics();
				g.drawImage(image, 0, 0, null);

				int m = Math.min(dcts.length-1, (int)(Math.log(Math.min(image.getWidth(), image.getHeight()))/Math.log(2)));
				int n = 1 << m;
				for (int y = 0; y < coefficients.length; y+=n)
				{
					for (int x = 0; x < coefficients[0].length; x+=n)
					{
						split(g, coefficients, x, y, m, image, dest, debug);
					}
				}

				ImageIO.write(debug, "png", new File("D:\\temp\\image_compression\\out", file.getName() + ".png"));
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


	private static boolean split(Graphics g, double [][] aCoefficients, int aBlockX, int aBlockY, int aLevel, BufferedImage aInput, BufferedImage aOutput, BufferedImage aDebug)
	{
		if (aBlockY >= aCoefficients.length || aBlockX >= aCoefficients[0].length)
		{
			return true;
		}

		int n = 1 << aLevel;
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;

		for (int y = 0; y < n; y++)
		{
			for (int x = 0; x < n; x++)
			{
				if (aBlockY+y < aCoefficients.length && aBlockX+x < aCoefficients[0].length)
				{
					double r = aCoefficients[aBlockY+y][aBlockX+x];
					if (r < min) min = r;
					if (r > max) max = r;
				}
			}
		}

		if (aLevel == 0 || 8*aBlockX+8*n <= aInput.getWidth() && 8*aBlockY+8*n <= aInput.getHeight() && aLevel <= 6 && Math.abs(max-min) < 0.0625) // 128=0.0625
		{
			int [] block = new int[8*n*8*n];
			int [][] samples = new int[3][block.length];
			aInput.getRGB(8*aBlockX, 8*aBlockY, 8*n, 8*n, block, 0, 8*n);
			ColorSpace.toYUV2(block, samples[0], samples[1], samples[2]);
//			for (int i = 0; i < 3; i++)
//			{
//				dcts[aLevel].forward(samples[i]);
//				int [] tbl = i == 0 ? quanttbl[aLevel][0] : quanttbl[aLevel][1];
//				for (int j = 0; j < samples[i].length; j++)
//				{
//					samples[i][j] = (int)Math.round(samples[i][j] / (double)tbl[j]);
//				}
//				for (int j = 0; j < samples[i].length; j++)
//				{
//					samples[i][j] *= tbl[j];
//				}
//				if (i != 0)
//				{
//					for (int j = 1; j < samples[i].length; j++)
//					{
//						samples[i][j] *= 0;
//					}
//				}
//				dcts[aLevel].inverse(samples[i]);
//			}
//			ColorSpace.toRGB2(block, samples[0], samples[1], samples[2]);
			for (int i = 0; i < block.length; i++)
			{
				block[i] = (samples[0][i]<<16)+(samples[0][i]<<8)+(samples[0][i]);
			}
			aOutput.setRGB(8*aBlockX, 8*aBlockY, 8*n, 8*n, block, 0, 8*n);

//			if (aLevel > 0)
//			{
//				System.out.println(Math.round(2047*min)+" "+Math.round(2047*max));
//				System.out.println("--------------------------");
//				int [] temp = new int[n*n];
//				for (int y = 0; y < n; y++)
//				{
//					for (int x = 0; x < n; x++)
//					{
//						if (aBlockY+y < aCoefficients.length && aBlockX+x < aCoefficients[0].length)
//						{
//							double r = aCoefficients[aBlockY+y][aBlockX+x];
//							System.out.printf("%5d ", Math.round(2047*r));
//							temp[x+n*y] = (int)Math.round(2047*(r-min));
//						}
//					}
//					System.out.println();
//				}
//				System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//				int [] dst = temp.clone();
//				new Hadamard((int)(Math.log(n*n)/Math.log(2))).transformIP(dst);
//				for (int y = 0; y < n; y++)
//				{
//					for (int x = 0; x < n; x++)
//					{
//						System.out.printf("%5d ", dst[x+y*n]);
//					}
//					System.out.println();
//				}
//				System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//				dcts2[aLevel-1].forward(temp);
//				for (int y = 0; y < n; y++)
//				{
//					for (int x = 0; x < n; x++)
//					{
//						System.out.printf("%5d ", temp[x+y*n]);
//					}
//					System.out.println();
//				}
//				System.out.println("==============================================================================================================");
//			}

			aDebug.setRGB(8*aBlockX, 8*aBlockY, 8*n, 8*n, block, 0, 8*n);

			if (aLevel == 0)
			{
				g.setColor(new Color(0,255,0,64));
				g.fillRect(8*aBlockX, 8*aBlockY, 8*n-1, 8*n-1);
				g.setColor(new Color(0,255,0,128));
				g.drawRect(8*aBlockX, 8*aBlockY, 8*n-1, 8*n-1);
			}
//			else if (Math.abs(max-min) == 0)
//			{
//				g.setColor(new Color(255,0,0,64));
//				g.fillRect(8*aBlockX, 8*aBlockY, 8*n-1, 8*n-1);
//				g.setColor(new Color(255,0,0,128));
//				g.drawRect(8*aBlockX, 8*aBlockY, 8*n-1, 8*n-1);
//			}
//			else if (Math.abs(max-min) < 0.05)
//			{
//				g.setColor(new Color(0,0,255,64));
//				g.fillRect(8*aBlockX, 8*aBlockY, 8*n-1, 8*n-1);
//				g.setColor(new Color(0,0,255,128));
//				g.drawRect(8*aBlockX, 8*aBlockY, 8*n-1, 8*n-1);
//			}

			return aLevel == 0;
		}
		else
		{
			n >>= 1;
			boolean b = true;
			b &= split(g, aCoefficients, aBlockX, aBlockY, aLevel-1, aInput, aOutput, aDebug);
			b &= split(g, aCoefficients, aBlockX+n, aBlockY, aLevel-1, aInput, aOutput, aDebug);
			b &= split(g, aCoefficients, aBlockX, aBlockY+n, aLevel-1, aInput, aOutput, aDebug);
			b &= split(g, aCoefficients, aBlockX+n, aBlockY+n, aLevel-1, aInput, aOutput, aDebug);

//			if (b)
//			{
//				n <<= 1;
//				g.setColor(new Color(0,255,0,128));
//				g.drawRect(8*aBlockX, 8*aBlockY, 8*n-1, 8*n-1);
//			}

			return b;
		}
	}


	private static double getCoefficient(BufferedImage aImage, int aBlockX, int aBlockY)
	{
		int [] block = new int[64];
		int [] y = new int[64];
		int [] u = new int[64];
		int [] v = new int[64];
		aImage.getRGB(aBlockX, aBlockY, 8, 8, block, 0, 8);
		ColorSpace.toYUV2(block, y, u, v);
		dct.forward(y);
//		dct.forward(u);
//		dct.forward(v);

		if (y[0]>=2048) throw new IllegalStateException();

		return y[0]/2048.0;
//		return (u[0]+v[0])/2048.0/2;
//		return (y[0]+u[0]+v[0])/2048.0/3;
	}
}
