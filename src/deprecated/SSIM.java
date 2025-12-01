package deprecated;

import org.terifan.imagecodec.ColorSpace;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;


/**
 * http://tdistler.com/iqa/ssim_8c_source.html
 */
public class SSIM
{
	public static void main(String... args)
	{
		try
		{
//			BufferedImage image_1_imp = ImageIO.read(new File("c:/temp/ssim/einstein.gif"));
//			BufferedImage image_2_imp = ImageIO.read(new File("c:/temp/ssim/blur.gif"));
//			BufferedImage image_3_imp = ImageIO.read(new File("c:/temp/ssim/contrast.gif"));
//			BufferedImage image_4_imp = ImageIO.read(new File("c:/temp/ssim/impulse.gif"));
//			BufferedImage image_5_imp = ImageIO.read(new File("c:/temp/ssim/meanshift.gif"));
//			BufferedImage image_6_imp = ImageIO.read(new File("c:/temp/ssim/jpg.gif"));
//
//			double d;
//			System.out.printf("0.694 = %6f % 6f\n", (d = new SSIM().run(image_1_imp, image_2_imp)), (d - 0.694));
//			System.out.printf("0.913 = %6f % 6f\n", (d = new SSIM().run(image_1_imp, image_3_imp)), (d - 0.913));
//			System.out.printf("0.840 = %6f % 6f\n", (d = new SSIM().run(image_1_imp, image_4_imp)), (d - 0.840));
//			System.out.printf("0.988 = %6f % 6f\n", (d = new SSIM().run(image_1_imp, image_5_imp)), (d - 0.988));
//			System.out.printf("0.662 = %6f % 6f\n", (d = new SSIM().run(image_1_imp, image_6_imp)), (d - 0.662));

			BufferedImage image1 = ImageIO.read(new File("d:/temp/image compression/in/Lenna.png"));

			int [][][] samples = new int[3][4][image1.getWidth()*image1.getHeight()];

			image1.getRGB(0, 0, image1.getWidth(), image1.getHeight(), samples[0][0], 0, image1.getWidth());
			ColorSpace.toYUV(samples[0][0], samples[0][1], samples[0][2], samples[0][3]);

			for (int i = 10; i <= 100; i+=5)
			{
				BufferedImage image2 = ImageIO.read(new File("d:/temp/image compression/Lenna"+(i==100?99:i)+".jpg.png"));
				BufferedImage image3 = ImageIO.read(new File("d:/temp/image compression/Lenna"+(i==100?99:i)+".wdp.png"));

				image2.getRGB(0, 0, image1.getWidth(), image1.getHeight(), samples[1][0], 0, image1.getWidth());
				image3.getRGB(0, 0, image1.getWidth(), image1.getHeight(), samples[2][0], 0, image1.getWidth());
				ColorSpace.toYUV(samples[1][0], samples[1][1], samples[1][2], samples[1][3]);
				ColorSpace.toYUV(samples[2][0], samples[2][1], samples[2][2], samples[2][3]);

				double a = new SSIM().run(samples[0][1], samples[1][1], image1.getWidth(), image1.getHeight());
				double b = new SSIM().run(samples[0][1], samples[2][1], image1.getWidth(), image1.getHeight());

				System.out.printf("%.8f %.8f\n", a, b);
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}


	public double run(int [] image1, int [] image2, int imageWidth, int imageHeight)
	{
		int bitsPerPixel = 8;

		double K1 = 0.01;
		double K2 = 0.03;

		double C1 = Math.pow(((1 << bitsPerPixel) - 1) * K1, 2);
		double C2 = Math.pow(((1 << bitsPerPixel) - 1) * K2, 2);

		int pixelCount = imageWidth * imageHeight;

		double[] ref = getRaster(image1);
		double[] cmp = getRaster(image2);

		double[] ref_mu = new double[pixelCount];
		double[] cmp_mu = new double[pixelCount];
		double[] ref_sigma_sqd = new double[pixelCount];
		double[] cmp_sigma_sqd = new double[pixelCount];
		double[] sigma_both = new double[pixelCount];

		int filterWidth = 11;
		double sigmaGauss = 1.5;
		double[] kernel = new double[filterWidth * filterWidth];

		double total = 0;
		double sigmaSq = sigmaGauss * sigmaGauss;
		int center = filterWidth / 2;
		for (int y = 0; y < filterWidth; y++)
		{
			for (int x = 0; x < filterWidth; x++)
			{
				double distance = (x - center) * (x - center) + (y - center) * (y - center);
				int i = y * filterWidth + x;
				kernel[i] = Math.exp(-0.5 * distance / sigmaSq);
				total += kernel[i];
			}
		}
		for (int i = 0; i < kernel.length; i++)
		{
			kernel[i] /= total;
		}

//		Tools.print(filterWidth, filterWidth, kernel);

		convolve(ref, imageWidth, imageHeight, ref_mu, kernel, filterWidth, filterWidth);
		convolve(cmp, imageWidth, imageHeight, cmp_mu, kernel, filterWidth, filterWidth);

		for (int y = 0; y < imageHeight; y++)
		{
			for (int x = 0, i = imageWidth * y; x < imageWidth; x++, i++)
			{
				ref_sigma_sqd[i] = ref[i] * ref[i];
				cmp_sigma_sqd[i] = cmp[i] * cmp[i];
				sigma_both[i] = ref[i] * cmp[i];
			}
		}

		convolve(ref_sigma_sqd, imageWidth, imageHeight, null, kernel, filterWidth, filterWidth);
		convolve(cmp_sigma_sqd, imageWidth, imageHeight, null, kernel, filterWidth, filterWidth);
		Dimension d = convolve(sigma_both, imageWidth, imageHeight, null, kernel, filterWidth, filterWidth);

		imageWidth = d.width;
		imageHeight = d.height;

		for (int y = 0; y < imageHeight; y++)
		{
			for (int x = 0, i = imageWidth * y; x < imageWidth; x++, i++)
			{
				ref_sigma_sqd[i] -= ref_mu[i] * ref_mu[i];
				cmp_sigma_sqd[i] -= cmp_mu[i] * cmp_mu[i];
				sigma_both[i] -= ref_mu[i] * cmp_mu[i];
			}
		}

//		ImageIO.write(toImage(ref, imageWidth, imageHeight), "png", new File("c:/ref.png"));
//		ImageIO.write(toImage(ref_mu, imageWidth, imageHeight), "png", new File("c:/ref_mu.png"));
//		ImageIO.write(toImage(cmp_mu, imageWidth, imageHeight), "png", new File("c:/cmp_mu.png"));
//		ImageIO.write(toImage(ref_sigma_sqd, imageWidth, imageHeight), "png", new File("c:/ref_sigma_sqd.png"));
//		ImageIO.write(toImage(cmp_sigma_sqd, imageWidth, imageHeight), "png", new File("c:/cmp_sigma_sqd.png"));
//		ImageIO.write(toImage(sigma_both, imageWidth, imageHeight), "png", new File("c:/sigma_both.png"));

		double ssimSum = 0;
		for (int y = 0; y < imageHeight; y++)
		{
			for (int x = 0, i = imageWidth * y; x < imageWidth; x++, i++)
			{
				double numerator = (2.0 * ref_mu[i] * cmp_mu[i] + C1) * (2.0 * sigma_both[i] + C2);
				double denominator = (ref_mu[i] * ref_mu[i] + cmp_mu[i] * cmp_mu[i] + C1) * (ref_sigma_sqd[i] + cmp_sigma_sqd[i] + C2);
				ssimSum += numerator / denominator;
			}
		}

		return ssimSum / (imageWidth * imageHeight);
	}


	private static Dimension convolve(double[] input, int width, int height, double[] output, double[] kernel, int w, int h)
	{
		int x, y, kx, ky, u, v;
		int uc = w / 2;
		int vc = h / 2;
		int kw_even = (w & 1) != 0 ? 0 : 1;
		int kh_even = (h & 1) != 0 ? 0 : 1;
		int dst_w = width - w + 1;
		int dst_h = height - h + 1;
		int img_offset, k_offset;
		double sum;
		double[] dst = output;

		if (dst == null)
		{
			dst = input; // Convolve in-place
		}

		for (y = 0; y < dst_h; ++y)
		{
			for (x = 0; x < dst_w; ++x)
			{
				sum = 0.0;
				k_offset = 0;
				ky = y + vc;
				kx = x + uc;
				for (v = -vc; v <= vc - kh_even; ++v)
				{
					img_offset = (ky + v) * width + kx;
					for (u = -uc; u <= uc - kw_even; ++u, ++k_offset)
					{
						sum += input[img_offset + u] * kernel[k_offset];
					}
				}
				dst[y * dst_w + x] = sum;
			}
		}

		return new Dimension(dst_w, dst_h);
	}


//	private static void convolve(double[] input, int width, int height, double [] output, double [] kernel, int w, int h)
//	{
//		w /= 2;
//		h /= 2;
//
//		double [] dst = output != null ? output : new double[width*height];
//
//		for (int y = 0, i = 0; y < height; y++)
//		{
//			for (int x = 0; x < width; x++, i++)
//			{
//				double sum = 0;
//				double scale = 0;
//				for (int fy = -w, fi = 0; fy <= w; fy++)
//				{
//					for (int fx = -h; fx <= h; fx++, fi++)
//					{
//						if (x + fx >= 0 && x + fx < width && y + fy >= 0 && y + fy < height)
//						{
//							double f = kernel[fi];
//							double c = input[x + fx + (y + fy) * width];
//							sum += c * f;
//							scale += f;
//						}
//					}
//				}
//				dst[i] = sum / scale;
//			}
//		}
//
//		if (output == null)
//		{
//			System.arraycopy(dst, 0, input, 0, dst.length);
//		}
//	}

	private static double[] getRaster(int [] aSamples)
	{
		double[] samples = new double[aSamples.length];

		for (int i = 0; i < aSamples.length; i++)
		{
			samples[i] = aSamples[i];
		}

		return samples;
	}


	private static BufferedImage toImage(double[] pixels, int width, int height)
	{
		return toImage(pixels, width, height, 0);
	}


	private static BufferedImage toImage(double[] pixels, int width, int height, int scale)
	{
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int y = 0, i = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++, i++)
			{
				int c = Math.min(Math.max((int) pixels[i] >>> scale, 0), 255);

				image.setRGB(x, y, (c << 16) + (c << 8) + c);
			}
		}

		return image;
	}
}
