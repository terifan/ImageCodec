package org.terifan.imagecodec.dct;


public interface IntDCT
{
	public void forward(int [] aBlock);

	public void inverse(int [] aBlock);
}
