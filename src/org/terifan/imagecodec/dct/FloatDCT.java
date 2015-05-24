package org.terifan.imagecodec.dct;


public interface FloatDCT
{
	public void forward(double [] aBlock);

	public void inverse(double [] aBlock);
}