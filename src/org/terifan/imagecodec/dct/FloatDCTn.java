package org.terifan.imagecodec.dct;

import java.util.Random;
import org.terifan.imagecodec.deprecated.Tools;


public class FloatDCTn implements FloatDCT
{
	private final int N;
	private final double[][] C;
	private final double[][] Ct;
	private final double[][] temp;


	public FloatDCTn(int N)
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
	public void forward(double[] block)
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
				block[i * N + j] = sum;
			}
		}
	}


	@Override
	public void inverse(double[] block)
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
				block[i * N + j] = sum;
			}
		}
	}


	public static void main(String... args)
	{
		try
		{
			Random rnd = new Random(1);

			int N = 16;

			double[] input = new double[N * N];
			double[] original = new double[N * N];

			for (int i = 0; i < N * N; i++)
			{
				input[i] = original[i] = 4096 * rnd.nextDouble();
			}

			FloatDCT dct = new FloatDCTn(N);

			dct.forward(input);

			double[] output = input.clone();

			dct.inverse(output);

			double[] err = new double[N * N];
			for (int i = 0; i < N * N; i++)
			{
				err[i] = Math.abs(original[i] - output[i]);
			}

			Tools.print(N, N, original);
			System.out.println("");
			Tools.print(N, N, input);
			System.out.println("");
			Tools.print(N, N, output);
			System.out.println("");
			Tools.print(N, N, err);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}
