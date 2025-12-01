package deprecated;


public final class Tools
{
	public static void print(int W, int H, double[] ... block)
	{
		for (int i = 0; i < H; i++)
		{
			for (int k = 0; k < block.length; k++)
			{
				for (int j = 0; j < W; j++)
				{
					System.out.printf("% 10.6f ", block[k][W*i+j]);
				}
				System.out.print("\t");
			}
			System.out.println();
		}
		System.out.println("");
	}


	public static void print(int W, int H, float[] ... block)
	{
		for (int i = 0; i < H; i++)
		{
			for (int k = 0; k < block.length; k++)
			{
				for (int j = 0; j < W; j++)
				{
					System.out.printf("% 8.4f ", block[k][W*i+j]);
				}
				System.out.print("\t");
			}
			System.out.println();
		}
		System.out.println("");
	}


	public static void print(int W, int H, int[] ... block)
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
