package org.terifan.multimedia.pic;


public interface FloatDCT
{
	public void forward(double [] aBlock);

	public void inverse(double [] aBlock);
}