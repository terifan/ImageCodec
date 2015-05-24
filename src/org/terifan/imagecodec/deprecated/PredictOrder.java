package org.terifan.imagecodec.deprecated;


public class PredictOrder
{
	private static int [][] samples;

	public static void main(String ... args)
	{
		try
		{
			int x = 0;
			int y = 0;
			samples = new int[16][16];

//			decode a
			samples[y+2][x+2] = (getSample(y,x)+getSample(y,x+4)+getSample(y+4,x)+getSample(y+4,x+4)) / 4;

//			decode b
			samples[y  ][x+2] = (getSample(y,x)+getSample(y,x+4)+getSample(y+2,x+2)+getSample(y-2,x+2)) / 4;
			samples[y+2][x  ] = (getSample(y,x)+getSample(y+4,x)+getSample(y+2,x+2)+getSample(y+2,x-2)) / 4;

			int [] xs1 = {1,3,1,3};
			int [] ys1 = {1,1,3,3};
			int [] dx1 = {-1,+1,-1,+1};
			int [] dy1 = {-1,-1,+1,+1};

//			decode c, d
			for (int i = 0; i < 4; i++)
			{
				int ix = x+xs1[i];
				int iy = y+ys1[i];
				samples[iy][ix] = (getSample(iy+dy1[0],ix+dx1[0])+getSample(iy+dy1[1],ix+dx1[1])+getSample(iy+dy1[2],ix+dx1[2])+getSample(iy+dy1[3],ix+dy1[3])) / 4;
			}

			int [] xs2 = {1,3,0,2,1,3,1,2};
			int [] ys2 = {0,0,1,1,2,2,3,3};
			int [] dx2 = {-1,+1,0,0};
			int [] dy2 = {0,0,-1,+1};

//			decode e, f, g, h
			for (int i = 0; i < 8; i++)
			{
				int ix = x+xs2[i];
				int iy = y+ys2[i];
				samples[iy][ix] = (getSample(iy+dy2[0],ix+dx2[0])+getSample(iy+dy2[1],ix+dx2[1])+getSample(iy+dy2[2],ix+dx2[2])+getSample(iy+dy2[3],ix+dy2[3])) / 4;
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}

	private static int getSample(int y, int x)
	{
		return 0;
	}
}