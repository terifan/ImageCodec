package org.terifan.microthumb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import javax.imageio.ImageIO;
import org.terifan.ui.ImageResizer;


public class MicrothumbRaster
{
	public static void main(String ... args)
	{
		try
		{
			double superError = 0;

			int[][][] palette = new int[256][256][3];
			palette[0] = new int[][]{{0x00,0x00,0x00},{0x80,0x00,0x00},{0x00,0x80,0x00},{0x80,0x80,0x00},{0x00,0x00,0x80},{0x80,0x00,0x80},{0x00,0x80,0x80},{0xc0,0xc0,0xc0},{0x80,0x80,0x80},{0xff,0x00,0x00},{0x00,0xff,0x00},{0xff,0xff,0x00},{0x00,0x00,0xff},{0xff,0x00,0xff},{0x00,0xff,0xff},{0xff,0xff,0xff},{0x00,0x00,0x00},{0x00,0x00,0x5f},{0x00,0x00,0x87},{0x00,0x00,0xaf},{0x00,0x00,0xd7},{0x00,0x00,0xff},{0x00,0x5f,0x00},{0x00,0x5f,0x5f},{0x00,0x5f,0x87},{0x00,0x5f,0xaf},{0x00,0x5f,0xd7},{0x00,0x5f,0xff},{0x00,0x87,0x00},{0x00,0x87,0x5f},{0x00,0x87,0x87},{0x00,0x87,0xaf},{0x00,0x87,0xd7},{0x00,0x87,0xff},{0x00,0xaf,0x00},{0x00,0xaf,0x5f},{0x00,0xaf,0x87},{0x00,0xaf,0xaf},{0x00,0xaf,0xd7},{0x00,0xaf,0xff},{0x00,0xd7,0x00},{0x00,0xd7,0x5f},{0x00,0xd7,0x87},{0x00,0xd7,0xaf},{0x00,0xd7,0xd7},{0x00,0xd7,0xff},{0x00,0xff,0x00},{0x00,0xff,0x5f},{0x00,0xff,0x87},{0x00,0xff,0xaf},{0x00,0xff,0xd7},{0x00,0xff,0xff},{0x5f,0x00,0x00},{0x5f,0x00,0x5f},{0x5f,0x00,0x87},{0x5f,0x00,0xaf},{0x5f,0x00,0xd7},{0x5f,0x00,0xff},{0x5f,0x5f,0x00},{0x5f,0x5f,0x5f},{0x5f,0x5f,0x87},{0x5f,0x5f,0xaf},{0x5f,0x5f,0xd7},{0x5f,0x5f,0xff},{0x5f,0x87,0x00},{0x5f,0x87,0x5f},{0x5f,0x87,0x87},{0x5f,0x87,0xaf},{0x5f,0x87,0xd7},{0x5f,0x87,0xff},{0x5f,0xaf,0x00},{0x5f,0xaf,0x5f},{0x5f,0xaf,0x87},{0x5f,0xaf,0xaf},{0x5f,0xaf,0xd7},{0x5f,0xaf,0xff},{0x5f,0xd7,0x00},{0x5f,0xd7,0x5f},{0x5f,0xd7,0x87},{0x5f,0xd7,0xaf},{0x5f,0xd7,0xd7},{0x5f,0xd7,0xff},{0x5f,0xff,0x00},{0x5f,0xff,0x5f},{0x5f,0xff,0x87},{0x5f,0xff,0xaf},{0x5f,0xff,0xd7},{0x5f,0xff,0xff},{0x87,0x00,0x00},{0x87,0x00,0x5f},{0x87,0x00,0x87},{0x87,0x00,0xaf},{0x87,0x00,0xd7},{0x87,0x00,0xff},{0x87,0x5f,0x00},{0x87,0x5f,0x5f},{0x87,0x5f,0x87},{0x87,0x5f,0xaf},{0x87,0x5f,0xd7},{0x87,0x5f,0xff},{0x87,0x87,0x00},{0x87,0x87,0x5f},{0x87,0x87,0x87},{0x87,0x87,0xaf},{0x87,0x87,0xd7},{0x87,0x87,0xff},{0x87,0xaf,0x00},{0x87,0xaf,0x5f},{0x87,0xaf,0x87},{0x87,0xaf,0xaf},{0x87,0xaf,0xd7},{0x87,0xaf,0xff},{0x87,0xd7,0x00},{0x87,0xd7,0x5f},{0x87,0xd7,0x87},{0x87,0xd7,0xaf},{0x87,0xd7,0xd7},{0x87,0xd7,0xff},{0x87,0xff,0x00},{0x87,0xff,0x5f},{0x87,0xff,0x87},{0x87,0xff,0xaf},{0x87,0xff,0xd7},{0x87,0xff,0xff},{0xaf,0x00,0x00},{0xaf,0x00,0x5f},{0xaf,0x00,0x87},{0xaf,0x00,0xaf},{0xaf,0x00,0xd7},{0xaf,0x00,0xff},{0xaf,0x5f,0x00},{0xaf,0x5f,0x5f},{0xaf,0x5f,0x87},{0xaf,0x5f,0xaf},{0xaf,0x5f,0xd7},{0xaf,0x5f,0xff},{0xaf,0x87,0x00},{0xaf,0x87,0x5f},{0xaf,0x87,0x87},{0xaf,0x87,0xaf},{0xaf,0x87,0xd7},{0xaf,0x87,0xff},{0xaf,0xaf,0x00},{0xaf,0xaf,0x5f},{0xaf,0xaf,0x87},{0xaf,0xaf,0xaf},{0xaf,0xaf,0xd7},{0xaf,0xaf,0xff},{0xaf,0xd7,0x00},{0xaf,0xd7,0x5f},{0xaf,0xd7,0x87},{0xaf,0xd7,0xaf},{0xaf,0xd7,0xd7},{0xaf,0xd7,0xff},{0xaf,0xff,0x00},{0xaf,0xff,0x5f},{0xaf,0xff,0x87},{0xaf,0xff,0xaf},{0xaf,0xff,0xd7},{0xaf,0xff,0xff},{0xd7,0x00,0x00},{0xd7,0x00,0x5f},{0xd7,0x00,0x87},{0xd7,0x00,0xaf},{0xd7,0x00,0xd7},{0xd7,0x00,0xff},{0xd7,0x5f,0x00},{0xd7,0x5f,0x5f},{0xd7,0x5f,0x87},{0xd7,0x5f,0xaf},{0xd7,0x5f,0xd7},{0xd7,0x5f,0xff},{0xd7,0x87,0x00},{0xd7,0x87,0x5f},{0xd7,0x87,0x87},{0xd7,0x87,0xaf},{0xd7,0x87,0xd7},{0xd7,0x87,0xff},{0xd7,0xaf,0x00},{0xd7,0xaf,0x5f},{0xd7,0xaf,0x87},{0xd7,0xaf,0xaf},{0xd7,0xaf,0xd7},{0xd7,0xaf,0xff},{0xd7,0xd7,0x00},{0xd7,0xd7,0x5f},{0xd7,0xd7,0x87},{0xd7,0xd7,0xaf},{0xd7,0xd7,0xd7},{0xd7,0xd7,0xff},{0xd7,0xff,0x00},{0xd7,0xff,0x5f},{0xd7,0xff,0x87},{0xd7,0xff,0xaf},{0xd7,0xff,0xd7},{0xd7,0xff,0xff},{0xff,0x00,0x00},{0xff,0x00,0x5f},{0xff,0x00,0x87},{0xff,0x00,0xaf},{0xff,0x00,0xd7},{0xff,0x00,0xff},{0xff,0x5f,0x00},{0xff,0x5f,0x5f},{0xff,0x5f,0x87},{0xff,0x5f,0xaf},{0xff,0x5f,0xd7},{0xff,0x5f,0xff},{0xff,0x87,0x00},{0xff,0x87,0x5f},{0xff,0x87,0x87},{0xff,0x87,0xaf},{0xff,0x87,0xd7},{0xff,0x87,0xff},{0xff,0xaf,0x00},{0xff,0xaf,0x5f},{0xff,0xaf,0x87},{0xff,0xaf,0xaf},{0xff,0xaf,0xd7},{0xff,0xaf,0xff},{0xff,0xd7,0x00},{0xff,0xd7,0x5f},{0xff,0xd7,0x87},{0xff,0xd7,0xaf},{0xff,0xd7,0xd7},{0xff,0xd7,0xff},{0xff,0xff,0x00},{0xff,0xff,0x5f},{0xff,0xff,0x87},{0xff,0xff,0xaf},{0xff,0xff,0xd7},{0xff,0xff,0xff},{0x08,0x08,0x08},{0x12,0x12,0x12},{0x1c,0x1c,0x1c},{0x26,0x26,0x26},{0x30,0x30,0x30},{0x3a,0x3a,0x3a},{0x44,0x44,0x44},{0x4e,0x4e,0x4e},{0x58,0x58,0x58},{0x62,0x62,0x62},{0x6c,0x6c,0x6c},{0x76,0x76,0x76},{0x80,0x80,0x80},{0x8a,0x8a,0x8a},{0x94,0x94,0x94},{0x9e,0x9e,0x9e},{0xa8,0xa8,0xa8},{0xb2,0xb2,0xb2},{0xbc,0xbc,0xbc},{0xc6,0xc6,0xc6},{0xd0,0xd0,0xd0},{0xda,0xda,0xda},{0xe4,0xe4,0xe4},{0xee,0xee,0xee}};

			long seed = new Random().nextLong();
			System.out.println("#" + seed);
			Random seeder = new Random(seed);
			for (int k = 1; k < palette.length; k++)
			{
				Random rnd = new Random(seeder.nextInt());
				for (int i = 0; i < palette[0].length; i++)
				{
					for (int j = 0; j < 3; j++)
					{
						palette[k][i][j] = Math.max(Math.min(palette[0][i][j] + rnd.nextInt(35) - 17, 255), 0);
					}
				}
			}

			for (File file : new File("D:\\Pictures\\Image Compression Suit").listFiles())
			{
				BufferedImage image = ImageIO.read(file);

				BufferedImage src = ImageResizer.getScaledImage(image, 9, 9, false);

				BufferedImage dst = null;

				int palIndex = -1;
				double palDist = Integer.MAX_VALUE;
				for (int pal = 0; pal < palette.length; pal++)
				{
					double totalDist = 0;

					BufferedImage tmp = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
					for (int y = 0; y < tmp.getHeight(); y++)
					{
						for (int x = 0; x < tmp.getWidth(); x++)
						{
							int c = src.getRGB(x, y);
							int r = 0xff & (c >> 16);
							int g = 0xff & (c >> 8);
							int b = 0xff & (c >> 0);

							int[] bestColor = palette[pal][0];
							double bestDist = 1 << 30;
							int bestIndex = 0;

							for (int i = 0; i < palette[pal].length; i++)
							{
								int[] color = palette[pal][i];
								int dr = Math.abs(color[0] - r);
								int dg = Math.abs(color[1] - g);
								int db = Math.abs(color[2] - b);
								double dist = Math.sqrt(Math.pow(dr, 2) + Math.pow(dg, 2) + Math.pow(db, 2));

								if (dist < bestDist)
								{
									bestColor = color;
									bestDist = dist;
									bestIndex = i;
								}
							}
//							System.out.print(bestIndex+",");

							tmp.setRGB(x, y, (bestColor[0] << 16) + (bestColor[1] << 8) + bestColor[2]);
							totalDist += bestDist;
						}
					}
//					System.out.println();

					if (totalDist < palDist)
					{
						palIndex = pal;
						palDist = totalDist;
						dst = tmp;
					}
				}

				superError += palDist;

				double aspect = (int)(image.getHeight() * 255 / (double)image.getWidth());
				int outHeight = (int)(512 * aspect / 255.0);

				int size = 1 + 1 + (src.getWidth() * src.getHeight() * (int)Math.ceil(Math.log(palette[0].length)/Math.log(2)) + 7) / 8;
//				System.out.println(size+" "+palIndex);

				dst = ImageResizer.getScaledImage(dst, 512, outHeight, false);

				ImageIO.write(dst, "png", new File("D:\\dev\\thumbs\\" + file.getName() + ".png"));
			}

			System.out.println(superError);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}
