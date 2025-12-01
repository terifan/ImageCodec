package org.terifan.imagecodec;

import static org.terifan.imagecodec.ColorSpace.toRGB2;
import static org.terifan.imagecodec.ColorSpace.toYUV2;
import static org.testng.Assert.*;
import org.testng.annotations.Test;


public class ColorSpaceNGTest
{
	@Test
	public void testSomething()
	{
		try
		{
			int ymin=9999999, ymax=-99999999, umin=9999999, umax=-999999999, vmin=999999999, vmax=-999999999;
			int rmin=9999999, rmax=-99999999, gmin=9999999, gmax=-999999999, bmin=999999999, bmax=-999999999;
			int rerr=0, gerr=0, berr=0;

			int[] c = new int[1];
			int[] Y = new int[1];
			int[] U = new int[1];
			int[] V = new int[1];

			for (int R = 0 ; R < 256; R++)
			{
				for (int G = 0 ; G < 256; G++)
				{
					for (int B = 0 ; B < 256; B++)
					{
						c[0] = (R << 16) + (G << 8) + B;

						toYUV2(c, Y, U, V);

						int y = 255 & (c[0] >> 16);
						int u = 255 & (c[0] >> 8);
						int v = 255 & (c[0]);

						toRGB2(c, Y, U, V);

						int r = 255 & (c[0] >> 16);
						int g = 255 & (c[0] >> 8);
						int b = 255 & (c[0]);

						if (R != r) rerr++;
						if (G != g) gerr++;
						if (B != b) berr++;
						if (y < ymin) ymin = y; if (y > ymax) ymax = y;
						if (u < umin) umin = u; if (u > umax) umax = u;
						if (v < vmin) vmin = v; if (v > vmax) vmax = v;
						if (r < rmin) rmin = r; if (r > rmax) rmax = r;
						if (g < gmin) gmin = g; if (g > gmax) gmax = g;
						if (b < bmin) bmin = b; if (b > bmax) bmax = b;
					}
				}
			}

			System.out.println(ymin+" "+umin+" "+vmin+" // " + ymax+" "+umax+" "+vmax);
			System.out.println(rmin+" "+gmin+" "+bmin+" // " + rmax+" "+gmax+" "+bmax);
			System.out.println(rerr+" "+gerr+" "+berr);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}
