package org.terifan.imagecodec.deprecated;

import org.terifan.imagecodec.ColorSpace;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import javax.imageio.ImageIO;
import static org.terifan.imagecodec.deprecated.Tools.*;


public class Prediction
{
	public static void main(String ... args)
	{
		try
		{
			int modes = 1+10;

			double [] toterr = new double[modes+1];
			int fileCount = 0;

			for (File file : new File("D:\\Resources\\image compression reference images").listFiles())
			{
				BufferedImage image = ImageIO.read(file);

				if ((image.getWidth() & 7) != 0 || (image.getHeight() & 7) != 0)
				{
					BufferedImage temp = new BufferedImage(8*((image.getWidth() + 7)/8), 8*((image.getHeight()+7)/8), BufferedImage.TYPE_INT_RGB);
					Graphics g = temp.createGraphics();
					for (int y = temp.getHeight()-image.getHeight(); y >= 0; y--)
					{
						for (int x = temp.getWidth()-image.getWidth(); x >= 0; x--)
						{
							g.drawImage(image, x, y, null);
						}
					}
					g.dispose();
					image = temp;
				}

				BufferedImage debug = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

				int [][] samples = new int[4][image.getHeight()*image.getWidth()];
				image.getRGB(0, 0, image.getWidth(), image.getHeight(), samples[0], 0, image.getWidth());
				ColorSpace.toYUV(samples[0], samples[1], samples[2], samples[3]);

				int w = image.getWidth();
				int h = image.getHeight();
				int [] data = samples[1];
				int [] colors = new int[]{0xff0000,0x00ff00,0x0000ff,0xffff00,0xff00ff,0x00ffff,0xffffff,0x770000,0x007700,0x000077,0x777700,0x770077,0x007777,0x777777};

				long [] err = new long[modes+1];

				for (int by = 4; by < h; by+=4)
				{
					for (int bx = 4; bx < w; bx+=4)
					{
						int [] T = {
							data[bx+-4+(by+-1)*w],
							data[bx+-3+(by+-1)*w],
							data[bx+-2+(by+-1)*w],
							data[bx+-1+(by+-1)*w],

							data[bx+00+(by+-1)*w],
							data[bx+01+(by+-1)*w],
							data[bx+02+(by+-1)*w],
							data[bx+03+(by+-1)*w],

							data[bx+04+(by+-1)*w],
							data[bx+05+(by+-1)*w],
							data[bx+06+(by+-1)*w],
							data[bx+07+(by+-1)*w]
						};

						int [] L = {
							data[bx+-1+(by+-4)*w],
							data[bx+-1+(by+-3)*w],
							data[bx+-1+(by+-2)*w],
							data[bx+-1+(by+-1)*w],

							data[bx+-1+(by+00)*w],
							data[bx+-1+(by+01)*w],
							data[bx+-1+(by+02)*w],
							data[bx+-1+(by+03)*w]
						};

						int bestMode = -1;
						int bestErr = 1 << 30;

						for (int mode = 0; mode <= modes; mode++)
						{
							int errAccum = 0;

							for (int iy = 0; iy < 4; iy++)
							{
								for (int ix = 0; ix < 4; ix++)
								{
									int p;
									switch (mode == modes ? bestMode : mode)
									{
										case 0: p = (T[4]+T[5]+T[6]+T[7]+2)/4; break;
										case 1: p = (L[4]+L[5]+L[6]+L[7]+2)/4; break;
										case 2: p = (T[4]+T[5]+T[6]+T[7]+L[4]+L[5]+L[6]+L[7]+4)/8; break;
										case 3: p = (T[4]+T[5]+T[6]+T[7]+L[4]+L[5]+L[6]+L[7]+T[8]+T[9]+T[10]+T[11]+6)/12; break;
										case 4: p = (T[3+ix]+2*T[4+ix]+T[5+ix]+2)/4; break;
										case 5: p = (L[3+iy]+2*L[4+iy]+L[5+Math.min(iy,2)]+2)/4; break;
										case 6: p = (T[3+ix]+2*T[4+ix]+T[5+ix]+L[3+iy]+2*L[4+iy]+L[5+Math.min(iy,2)]+4)/8; break;
										case 7: p = T[4+ix]; break;
										case 8: p = L[4+iy]; break;
										case 9:
											switch (ix*16+iy)
											{
												case 0x00:
													p = (T[ 4]+2*T[ 5]+T[ 6]+2)/4; break;
												case 0x01:
												case 0x10:
													p = (T[ 5]+2*T[ 6]+T[ 7]+2)/4; break;
												case 0x02:
												case 0x11:
												case 0x20:
													p = (T[ 6]+2*T[ 7]+T[ 8]+2)/4; break;
												case 0x03:
												case 0x12:
												case 0x21:
												case 0x30:
													p = (T[ 7]+2*T[ 8]+T[ 9]+2)/4; break;
												case 0x31:
												case 0x22:
												case 0x13:
													p = (T[ 8]+2*T[ 9]+T[10]+2)/4; break;
												case 0x32:
												case 0x23:
													p = (T[ 9]+2*T[10]+T[11]+2)/4; break;
												case 0x33:
													p = (T[10]+3*T[11]      +2)/4; break;
												default:
													throw new IllegalStateException();
											}
											break;
										case 10:
											p = Math.min(Math.max(T[4+ix] + L[4+iy] - T[3], 0), 255);
											break;
										default:
											throw new IllegalStateException();
									}

									int c = samples[1][bx+ix+(by+iy)*w];

									int q = c - p;

									errAccum += q*q;

									q += 128;
									int o = q > 255 ? q-255 : q < 0 ? 256 + q : q;

									if (o < 0 || o > 255) throw new IllegalStateException();
									int t = p+q-128;
									int u = t < 0 ? 256+t : t > 255 ? t-256 : t;
									if (c != u) throw new IllegalStateException(String.format("ERROR prev=%-3d  input=%-3d  tempin=%-3d  output=%-3d  tempout=%-3d  restore=%-3d\n", p, c, q, o, t, u));

									debug.setRGB(bx+ix, by+iy, (o<<16)+(o<<8)+o);

//									if (mode == modes)
//									{
//										o = colors[bestMode];
//										debug.setRGB(bx+ix, by+iy, o);
//									}
								}
							}

							err[mode] += errAccum;

							if (errAccum < bestErr)
							{
								bestErr = errAccum;
								bestMode = mode;
							}
						}
					}
				}

				System.out.printf("%-50.50s", file.getName());
				for (int i = 0; i < err.length; i++)
				{
					double v = err[i] == 0 ? 0 : 10*Math.log10((255*255)/(err[i]/(double)(w*h)));
					System.out.printf(" %8.2f", v);
					toterr[i] += v;
				}
				System.out.println();

				ImageIO.write(debug, "png", new File("D:\\temp\\image_compression\\prediction", file.getName()+".png"));
				fileCount++;
			}

			System.out.println("");
			System.out.printf("%-50.50s", "");
			for (int i = 0; i < toterr.length; i++)
			{
				System.out.printf(" %8.2f", toterr[i]/fileCount);
			}
			System.out.println();
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}
