package org.terifan.multimedia.pic;


public class VecMath
{
	/*
	private static void cvPow(double [] src, double [] dst, double pow)
	{
		for (int i = 0; i < src.length; i++)
		{
			dst[i] = Math.pow(src[i], pow);
		}
	}


	private static void cvMul(double [] src1, double [] src2, double [] dst, double gamma)
	{
		for (int i = 0; i < src1.length; i++)
		{
			dst[i] = src1[i] * src2[i] + gamma;
		}
	}


	private static void cvSmooth(double [] src, double [] dst, FilterKernel type, int a, double b, double c)
	{
//		double [][] kernel = type.getKernel(width);
//		for (int i = 0; i < src.length; i++)
//		{
//			int sum = 0;
//
//			for (int fx = -width/2, fxp = 0; fxp < width; fx++, fxp++)
//			{
//				if (i + fx >= 0 && i + fx < src.length)
//				{
//					double f = kernel[width/2][fxp];
//					sum += f * src[i + fx];
//				}
//			}
//
//			dst[i] = sum / src.length;
//		}
		System.arraycopy(src, 0, dst, 0, src.length);
	}


	private static void cvScale(double [] src, double [] dst, int scale)
	{
		for (int i = 0; i < src.length; i++)
		{
			dst[i] = src[i] * scale;
		}
	}


	private static double [] cvScalarAll(double ... values)
	{
		return values;
	}


	private static void cvAdd(double [] src, double [] add, double [] dst)
	{
		for (int i = 0; i < src.length; i++)
		{
			dst[i] = src[i] * add[i];
		}
	}


	private static void cvAddS(double [] src, double [] add, double [] dst)
	{
		double sum = cvSum(add);
		for (int i = 0; i < src.length; i++)
		{
			dst[i] = src[i] + sum;
		}
	}


	private static void cvDiv(double [] src1, double [] src2, double [] dst, double gamma)
	{
		for (int i = 0; i < src1.length; i++)
		{
			dst[i] = src1[i] / src2[i] + gamma;
		}
	}


	private static double cvAvg(double [] src)
	{
		double avg = 0;
		for (int i = 0; i < src.length; i++)
		{
			avg += src[i];
		}
		return avg / src.length;
	}


	private static void cvAddWeighted(double [] src1, double alpha, double [] src2, double beta, double gamma, double [] dst)
	{
		for (int i = 0; i < src1.length; i++)
		{
			dst[i] = src1[i] * alpha + src2[i] * beta + gamma;
		}
	}


	private static double cvSum(double [] src)
	{
		double sum = 0;
		for (int i = 0; i < src.length; i++)
		{
			sum += src[i];
		}
		return sum;
	}
*/

}
