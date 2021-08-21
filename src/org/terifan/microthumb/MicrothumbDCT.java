package org.terifan.microthumb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Random;
import javax.imageio.ImageIO;
import org.terifan.imagecodec.dct.FloatDCT16;
import org.terifan.imagecodec.deprecated.QuantizationTable;
import org.terifan.ui.ImageResizer;


public class MicrothumbDCT
{
	public static void main(String ... args)
	{
		try
		{
			int Q = 50;

			int[][] lims = {{10,1},{6,4},{4,32},{2,16}};
			int bitLen = 0;
			for (int i = 0; i < lims.length; i++)
			{
				bitLen += lims[i][0] * lims[i][1];
			}
			System.out.println(3 * bitLen / 8);

			for (File file : new File("D:\\Pictures\\Image Compression Suit").listFiles())
			{
				BufferedImage image = ImageIO.read(file);

				BufferedImage src = ImageResizer.getScaledImage(image, 16, 16, false);

				double[][] samples = new double[3][src.getWidth() * src.getHeight()];
				for (int y = 0, i = 0; y < src.getHeight(); y++)
				{
					for (int x = 0; x < src.getWidth(); x++, i++)
					{
						int c = src.getRGB(x, y);
						samples[0][i] = 0xff & (c >> 16);
						samples[1][i] = 0xff & (c >> 8);
						samples[2][i] = 0xff & (c >> 0);
					}
				}

				double[][] input = {samples[0].clone(), samples[1].clone(), samples[2].clone()};

//				print(src, samples[0]);

				for (int comp = 0; comp < 3; comp++)
				{
					new FloatDCT16().forward(samples[comp]);
				}

				int[][] quantTable = {
					QuantizationTable.buildQuantTable(Q, src.getWidth(), src.getHeight(), 0),
					QuantizationTable.buildQuantTable(Q, src.getWidth(), src.getHeight(), 1),
					QuantizationTable.buildQuantTable(Q, src.getWidth(), src.getHeight(), 1)
				};
				for (int comp = 0; comp < 3; comp++)
				{
					for (int i = 0; i < samples[0].length; i++)
					{
						samples[comp][i] = (int)Math.round(samples[comp][i] / (double)quantTable[comp][i]);
					}
				}

				int[] ZIGZAG_ORDER =
				{
					0,1,5,6,14,15,27,28,44,45,65,66,90,91,119,120,
					2,4,7,13,16,26,29,43,46,64,67,89,92,118,121,150,
					3,8,12,17,25,30,42,47,63,68,88,93,117,122,149,151,
					9,11,18,24,31,41,48,62,69,87,94,116,123,148,152,177,
					10,19,23,32,40,49,61,70,86,95,115,124,147,153,176,178,
					20,22,33,39,50,60,71,85,96,114,125,146,154,175,179,200,
					21,34,38,51,59,72,84,97,113,126,145,155,174,180,199,201,
					35,37,52,58,73,83,98,112,127,144,156,173,181,198,202,219,
					36,53,57,74,82,99,111,128,143,157,172,182,197,203,218,220,
					54,56,75,81,100,110,129,142,158,171,183,196,204,217,221,234,
					55,76,80,101,109,130,141,159,170,184,195,205,216,222,233,235,
					77,79,102,108,131,140,160,169,185,194,206,215,223,232,236,245,
					78,103,107,132,139,161,168,186,193,207,214,224,231,237,244,246,
					104,106,133,138,162,167,187,192,208,213,225,230,238,243,247,252,
					105,134,137,163,166,188,191,209,212,226,229,239,242,248,251,253,
					135,136,164,165,189,190,210,211,227,228,240,241,249,250,254,255
				};
				int[] UNZIGZAG_ORDER = new int[ZIGZAG_ORDER.length];

				for (int comp = 0; comp < 3; comp++)
				{
					double[] tmp = new double[src.getWidth() * src.getHeight()];
					for (int i = 0; i < tmp.length; i++)
					{
						UNZIGZAG_ORDER[ZIGZAG_ORDER[i]] = i;
						tmp[ZIGZAG_ORDER[i]] = samples[comp][i];
					}
					samples[comp] = tmp;
				}

//				for (int comp = 0; comp < 3; comp++)
//				{
//					Arrays.fill(samples[comp], 28, 256, 0);
//				}

				for (int comp = 0; comp < 3; comp++)
				{
					int p = 0;
					for (int i = 0; i < lims.length; i++)
					{
						int lim = (1 << (lims[i][0] - 1));
						for (int j = 0; j < lims[i][1]; j++, p++)
						{
							samples[comp][p] = Math.max(-lim, Math.min(lim, samples[comp][p]));
						}
					}
					Arrays.fill(samples[comp], p, 256, 0);
				}

//				print(src, samples[0]);


				for (int comp = 0; comp < 3; comp++)
				{
					double[] tmp = new double[src.getWidth() * src.getHeight()];
					for (int i = 0; i < tmp.length; i++)
					{
						tmp[UNZIGZAG_ORDER[i]] = samples[comp][i];
					}
					samples[comp] = tmp;
				}

				for (int comp = 0; comp < 3; comp++)
				{
					for (int i = 0; i < samples[0].length; i++)
					{
						samples[comp][i] = (int)Math.round(samples[comp][i] * (double)quantTable[comp][i]);
					}
				}

				for (int comp = 0; comp < 3; comp++)
				{
					new FloatDCT16().inverse(samples[comp]);
				}

//				print(src, samples[0]);

				BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
				for (int y = 0, i = 0; y < src.getHeight(); y++)
				{
					for (int x = 0; x < src.getWidth(); x++, i++)
					{
						int r = Math.max(0, Math.min(255, (int)samples[0][i]));
						int g = Math.max(0, Math.min(255, (int)samples[1][i]));
						int b = Math.max(0, Math.min(255, (int)samples[2][i]));
						dst.setRGB(x, y, (r<<16)+(g<<8)+b);
					}
				}

//				double aspect = image.getHeight() / (double)image.getWidth();
//				double[] as = {0.25, 0.5, 0.75, 1, 1.25, 1.5, 1.75, 2};
//				int apectIndex = 0;
//				for (int i = 0; i < 8; i++)
//				{
//					if (Math.abs(as[i] - aspect) < Math.abs(as[apectIndex] - aspect))
//					{
//						apectIndex = i;
//					}
//				}

				double aspect = (int)(image.getHeight() * 255 / (double)image.getWidth());
				int outHeight = (int)(512 * aspect / 255.0);

				dst = ImageResizer.getScaledImage(dst, 512, outHeight, false);
				ImageIO.write(dst, "png", new File("D:\\dev\\thumbs\\" + file.getName() + ".png"));

				double totalError = 0;
				double[] error = new double[src.getWidth() * src.getHeight()];
				for (int i = 0; i < samples[0].length; i++)
				{
					for (int comp = 0; comp < 3; comp++)
					{
						error[i] += Math.abs(samples[comp][i] - input[comp][i]);
					}
					totalError += error[i];
				}

//				print(src, error);
//				System.out.println(totalError);

//				break;
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


	private static void print(BufferedImage aSrc, double[] aSamples)
	{
		for (int y = 0, i = 0; y < aSrc.getHeight(); y++)
		{
			for (int x = 0; x < aSrc.getWidth(); x++, i++)
			{
				System.out.printf(" %13.8f", aSamples[i]);
			}
			System.out.println();
		}
		System.out.println();
	}
}
