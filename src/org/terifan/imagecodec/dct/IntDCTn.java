package org.terifan.imagecodec.dct;

import java.util.Random;
import deprecated.Tools;


public class IntDCTn implements IntDCT
{
	private final int N;
	private final double [][] C;
	private final double [][] Ct;
	private final double[][] temp;


	public IntDCTn(int N)
	{
		this.N = N;

		C = new double[N][N];
		Ct = new double[N][N];
		temp = new double[N][N];

		for (int j = 0; j < N; j++)
		{
			C[0][j] = 1.0 / Math.sqrt(N);
			Ct[j][0] = C[0][j];
		}
		for (int i = 1; i < N; i++)
		{
			for (int j = 0; j < N; j++)
			{
				C[i][j] = Math.sqrt(2.0 / N) * Math.cos(Math.PI * (2 * j + 1) * i / (2.0 * N));
				Ct[j][i] = C[i][j];
			}
		}
	}


	@Override
	public void forward(int[] block)
	{
		for (int i = 0; i < N; i++)
		{
			for (int j = 0; j < N; j++)
			{
				double sum = 0;
				for (int k = 0; k < N; k++)
				{
					sum += block[N * i + k] * C[j][k];
				}
				temp[j][i] = sum;
			}
		}
		for (int i = 0; i < N; i++)
		{
			for (int j = 0; j < N; j++)
			{
				double sum = 0;
				for (int k = 0; k < N; k++)
				{
					sum += C[i][k] * temp[j][k];
				}
				block[i * N + j] = (int) Math.round(sum);
			}
		}
	}


	@Override
	public void inverse(int[] block)
	{
		for (int i = 0; i < N; i++)
		{
			for (int j = 0; j < N; j++)
			{
				double sum = 0;
				for (int k = 0; k < N; k++)
				{
					sum += block[i * N + k] * Ct[j][k];
				}
				temp[j][i] = sum;
			}
		}

		for (int i = 0; i < N; i++)
		{
			for (int j = 0; j < N; j++)
			{
				double sum = 0;
				for (int k = 0; k < N; k++)
				{
					sum += Ct[i][k] * temp[j][k];
				}
				block[i * N + j] = (int) Math.round(sum);
			}
		}
	}


	public static void main(String ... args)
	{
		try
		{
			int N = 16;

			int [] block = new int[N*N];
			Random rnd = new Random(1);
			for (int i = 0; i < block.length; i++)
			{
				block[i] = rnd.nextInt(256);
			}
			int [] original = block.clone();

			IntDCTn dct = new IntDCTn(N);
			long t = System.nanoTime();
			for (int i = 0; i < 100000; i++)
			{
				dct.forward(block);
				dct.inverse(block);
			}
			System.out.println((System.nanoTime()-t)/1000000);

			int [] err = new int[N*N];
			for (int i = 0; i < block.length; i++)
			{
				err[i] = Math.abs(original[i]-block[i]);
			}

//			Tools.print(N, N, block);
//			System.out.println("");
//			Tools.print(N, N, original);
//			System.out.println("");
			Tools.print(N, N, err);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}