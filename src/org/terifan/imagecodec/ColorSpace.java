package org.terifan.imagecodec;


public class ColorSpace
{
	public static void toYUV(int[] rgb, int[] aY, int[] aU, int[] aV)
	{
		for (int i = 0; i < rgb.length; i++)
		{
			int c = rgb[i];
			int R = 255 & (c >> 16);
			int G = 255 & (c >> 8);
			int B = 255 & (c);

			int V = B - R;
			int tmp = R - G + ceilDiv2(V);
			int Y = G + floorDiv2(tmp);
			int U = -tmp;

			aY[i] = Y;
			aU[i] = 255 + U;
			aV[i] = 255 + V;
		}
	}


	public static void toRGB(int[] rgb, int[] aY, int[] aU, int[] aV)
	{
		for (int i = 0; i < rgb.length; i++)
		{
			int Y = clamp(aY[i]);
			int U = clamp2(aU[i] - 255);
			int V = clamp2(aV[i] - 255);

			int G = Y - floorDiv2(-U);
			int R = G - U - ceilDiv2(V);
			int B = V + R;

			rgb[i] = (clamp(R) << 16) + (clamp(G) << 8) + clamp(B);
		}
	}


	public static void toYUV2(int[] rgb, int[] aY, int[] aU, int[] aV)
	{
		for (int i = 0; i < rgb.length; i++)
		{
			int c = rgb[i];
			int R = 255 & (c >> 16);
			int G = 255 & (c >> 8);
			int B = 255 & (c);

			int Y = clamp(R * 0.29900 + G * 0.58700 + B * 0.11400);
			int U = clamp(R * -0.16874 + G * -0.33126 + B * 0.50000 + 128);
			int V = clamp(R * 0.50000 + G * -0.41869 + B * -0.08131 + 128);

			aY[i] = Y;
			aU[i] = U;
			aV[i] = V;
		}
	}


	public static void toRGB2(int[] rgb, int[] aY, int[] aU, int[] aV)
	{
		for (int i = 0; i < rgb.length; i++)
		{
			int Y = aY[i];
			int U = aU[i];
			int V = aV[i];

			int R = clamp(Y + 1.40200 * (V - 128));
			int G = clamp(Y - 0.34414 * (U - 128) - 0.71414 * (V - 128));
			int B = clamp(Y + 1.77200 * (U - 128));

			rgb[i] = (R << 16) + (G << 8) + B;
		}
	}


	private static int clamp(double v)
	{
		return v < 0 ? 0 : v > 255 ? 255 : (int)(v+0.5);
	}


	private static int clamp(int v)
	{
		return v < 0 ? 0 : v > 255 ? 255 : v;
	}


	private static int clamp2(int v)
	{
		return v < -255 ? -255 : v > 255 ? 255 : v;
	}


	private static int floorDiv2(int x)
	{
		return x >= 0 ? x / 2 : -((-x + 1) / 2);
	}


	private static int ceilDiv2(int x)
	{
		return x >= 0 ? (x + 1) / 2 : -((-x) / 2);
	}
}
