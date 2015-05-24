package org.terifan.multimedia.pic;


public class H264
{
	static int[] tmp = new int[64];


	static void forward4x4(int[] input, int[] output)
	{
		int i, ii;
		int p0, p1, p2, p3;
		int t0, t1, t2, t3;
		int pTmp = 0;

		// Horizontal
		for (i = 0; i < 4; i++)
		{
			int pblock = 4 * i;
			p0 = input[pblock + 0];
			p1 = input[pblock + 1];
			p2 = input[pblock + 2];
			p3 = input[pblock + 3];

			t0 = p0 + p3;
			t1 = p1 + p2;
			t2 = p1 - p2;
			t3 = p0 - p3;

			tmp[pTmp++] = t0 + t1;
			tmp[pTmp++] = (t3 << 1) + t2;
			tmp[pTmp++] = t0 - t1;
			tmp[pTmp++] = t3 - (t2 << 1);
		}

		// Vertical
		for (i = 0; i < 4; i++)
		{
			p0 = tmp[i + 0];
			p1 = tmp[i + 4];
			p2 = tmp[i + 8];
			p3 = tmp[i + 12];

			t0 = p0 + p3;
			t1 = p1 + p2;
			t2 = p1 - p2;
			t3 = p0 - p3;

			output[0 + i] = t0 + t1;
			output[4 + i] = t2 + (t3 << 1);
			output[8 + i] = t0 - t1;
			output[12 + i] = t3 - (t2 << 1);
		}
	}


	static void inverse4x4(int[] input, int[] output)
	{
		int i, ii;
		int p0, p1, p2, p3;
		int t0, t1, t2, t3;
		int pTmp = 0;

		// Horizontal
		for (i = 0; i < 4; i++)
		{
			int pblock = 4 * i;
			t0 = input[pblock + 0];
			t1 = input[pblock + 1];
			t2 = input[pblock + 2];
			t3 = input[pblock + 3];

			p0 = t0 + t2;
			p1 = t0 - t2;
			p2 = (t1 >> 1) - t3;
			p3 = t1 + (t3 >> 1);

			tmp[pTmp++] = p0 + p3;
			tmp[pTmp++] = p1 + p2;
			tmp[pTmp++] = p1 - p2;
			tmp[pTmp++] = p0 - p3;
		}

		// Vertical
		for (i = 0; i < 4; i++)
		{
			t0 = tmp[i + 0];
			t1 = tmp[i + 4];
			t2 = tmp[i + 8];
			t3 = tmp[i + 12];

			p0 = t0 + t2;
			p1 = t0 - t2;
			p2 = (t1 >> 1) - t3;
			p3 = t1 + (t3 >> 1);

			output[0 + i] = p0 + p3;
			output[4 + i] = p1 + p2;
			output[8 + i] = p1 - p2;
			output[12 + i] = p0 - p3;
		}
	}
//	void hadamard4x4(int (*block)[4], int (*tblock)[4])
//	{
//	  int i;
//	  int *pTmp = tmp, *pblock;
//	  static int p0,p1,p2,p3;
//	  static int t0,t1,t2,t3;
//
//	  // Horizontal
//	  for (i = 0; i < BLOCK_SIZE; i++)
//	  {
//		pblock = block[i];
//		p0 = *(pblock++);
//		p1 = *(pblock++);
//		p2 = *(pblock++);
//		p3 = *(pblock  );
//
//		t0 = p0 + p3;
//		t1 = p1 + p2;
//		t2 = p1 - p2;
//		t3 = p0 - p3;
//
//		*(pTmp++) = t0 + t1;
//		*(pTmp++) = t3 + t2;
//		*(pTmp++) = t0 - t1;
//		*(pTmp++) = t3 - t2;
//	  }
//
//	  // Vertical
//	  for (i = 0; i < BLOCK_SIZE; i++)
//	  {
//		pTmp = tmp + i;
//		p0 = *pTmp;
//		p1 = *(pTmp += BLOCK_SIZE);
//		p2 = *(pTmp += BLOCK_SIZE);
//		p3 = *(pTmp += BLOCK_SIZE);
//
//		t0 = p0 + p3;
//		t1 = p1 + p2;
//		t2 = p1 - p2;
//		t3 = p0 - p3;
//
//		tblock[0][i] = (t0 + t1) >> 1;
//		tblock[1][i] = (t2 + t3) >> 1;
//		tblock[2][i] = (t0 - t1) >> 1;
//		tblock[3][i] = (t3 - t2) >> 1;
//	  }
//	}
//
//
//	void ihadamard4x4(int (*tblock)[4], int (*block)[4])
//	{
//	  int i;
//	  int *pTmp = tmp, *pblock;
//	  static int p0,p1,p2,p3;
//	  static int t0,t1,t2,t3;
//
//	  // Horizontal
//	  for (i = 0; i < BLOCK_SIZE; i++)
//	  {
//		pblock = tblock[i];
//		t0 = *(pblock++);
//		t1 = *(pblock++);
//		t2 = *(pblock++);
//		t3 = *(pblock  );
//
//		p0 = t0 + t2;
//		p1 = t0 - t2;
//		p2 = t1 - t3;
//		p3 = t1 + t3;
//
//		*(pTmp++) = p0 + p3;
//		*(pTmp++) = p1 + p2;
//		*(pTmp++) = p1 - p2;
//		*(pTmp++) = p0 - p3;
//	  }
//
//	  //  Vertical
//	  for (i = 0; i < BLOCK_SIZE; i++)
//	  {
//		pTmp = tmp + i;
//		t0 = *pTmp;
//		t1 = *(pTmp += BLOCK_SIZE);
//		t2 = *(pTmp += BLOCK_SIZE);
//		t3 = *(pTmp += BLOCK_SIZE);
//
//		p0 = t0 + t2;
//		p1 = t0 - t2;
//		p2 = t1 - t3;
//		p3 = t1 + t3;
//
//		block[0][i] = p0 + p3;
//		block[1][i] = p1 + p2;
//		block[2][i] = p1 - p2;
//		block[3][i] = p0 - p3;
//	  }
//	}


	static void forward8x8(int[] block, int[] tblock)
	{
		int a0, a1, a2, a3;
		int p0, p1, p2, p3, p4, p5, p6, p7;
		int b0, b1, b2, b3, b4, b5, b6, b7;

		// Horizontal
		for (int i = 0; i < 8; i++)
		{
			int pblock = 8 * i;
			p0 = block[pblock++];
			p1 = block[pblock++];
			p2 = block[pblock++];
			p3 = block[pblock++];
			p4 = block[pblock++];
			p5 = block[pblock++];
			p6 = block[pblock++];
			p7 = block[pblock];

			a0 = p0 + p7;
			a1 = p1 + p6;
			a2 = p2 + p5;
			a3 = p3 + p4;

			b0 = a0 + a3;
			b1 = a1 + a2;
			b2 = a0 - a3;
			b3 = a1 - a2;

			a0 = p0 - p7;
			a1 = p1 - p6;
			a2 = p2 - p5;
			a3 = p3 - p4;

			b4 = a1 + a2 + ((a0 >> 1) + a0);
			b5 = a0 - a3 - ((a2 >> 1) + a2);
			b6 = a0 + a3 - ((a1 >> 1) + a1);
			b7 = a1 - a2 + ((a3 >> 1) + a3);

			int pTmp = 8 * i;
			tmp[pTmp++] = b0 + b1;
			tmp[pTmp++] = b4 + (b7 >> 2);
			tmp[pTmp++] = b2 + (b3 >> 1);
			tmp[pTmp++] = b5 + (b6 >> 2);
			tmp[pTmp++] = b0 - b1;
			tmp[pTmp++] = b6 - (b5 >> 2);
			tmp[pTmp++] = (b2 >> 1) - b3;
			tmp[pTmp++] = (b4 >> 2) - b7;
		}

		// Vertical
		for (int i = 0; i < 8; i++)
		{
			int pTmp = i;
			p0 = tmp[pTmp];
			p1 = tmp[pTmp += 8];
			p2 = tmp[pTmp += 8];
			p3 = tmp[pTmp += 8];
			p4 = tmp[pTmp += 8];
			p5 = tmp[pTmp += 8];
			p6 = tmp[pTmp += 8];
			p7 = tmp[pTmp += 8];

			a0 = p0 + p7;
			a1 = p1 + p6;
			a2 = p2 + p5;
			a3 = p3 + p4;

			b0 = a0 + a3;
			b1 = a1 + a2;
			b2 = a0 - a3;
			b3 = a1 - a2;

			a0 = p0 - p7;
			a1 = p1 - p6;
			a2 = p2 - p5;
			a3 = p3 - p4;

			b4 = a1 + a2 + ((a0 >> 1) + a0);
			b5 = a0 - a3 - ((a2 >> 1) + a2);
			b6 = a0 + a3 - ((a1 >> 1) + a1);
			b7 = a1 - a2 + ((a3 >> 1) + a3);

			tblock[8 * 0 + i] = b0 + b1;
			tblock[8 * 1 + i] = b4 + (b7 >> 2);
			tblock[8 * 2 + i] = b2 + (b3 >> 1);
			tblock[8 * 3 + i] = b5 + (b6 >> 2);
			tblock[8 * 4 + i] = b0 - b1;
			tblock[8 * 5 + i] = b6 - (b5 >> 2);
			tblock[8 * 6 + i] = (b2 >> 1) - b3;
			tblock[8 * 7 + i] = (b4 >> 2) - b7;
		}
	}


	static void inverse8x8(int[] tblock, int[] block)
	{
		int a0, a1, a2, a3;
		int p0, p1, p2, p3, p4, p5, p6, p7;
		int b0, b1, b2, b3, b4, b5, b6, b7;

		// Horizontal
		for (int i = 0; i < 8; i++)
		{
			int pblock = 8 * i;
			p0 = tblock[pblock++];
			p1 = tblock[pblock++];
			p2 = tblock[pblock++];
			p3 = tblock[pblock++];
			p4 = tblock[pblock++];
			p5 = tblock[pblock++];
			p6 = tblock[pblock++];
			p7 = tblock[pblock];

			a0 = p0 + p4;
			a1 = p0 - p4;
			a2 = p6 - (p2 >> 1);
			a3 = p2 + (p6 >> 1);

			b0 = a0 + a3;
			b2 = a1 - a2;
			b4 = a1 + a2;
			b6 = a0 - a3;

			a0 = -p3 + p5 - p7 - (p7 >> 1);
			a1 = p1 + p7 - p3 - (p3 >> 1);
			a2 = -p1 + p7 + p5 + (p5 >> 1);
			a3 = p3 + p5 + p1 + (p1 >> 1);

			b1 = a0 + (a3 >> 2);
			b3 = a1 + (a2 >> 2);
			b5 = a2 - (a1 >> 2);
			b7 = a3 - (a0 >> 2);

			int pTmp = 8 * i;
			tmp[pTmp++] = b0 + b7;
			tmp[pTmp++] = b2 - b5;
			tmp[pTmp++] = b4 + b3;
			tmp[pTmp++] = b6 + b1;
			tmp[pTmp++] = b6 - b1;
			tmp[pTmp++] = b4 - b3;
			tmp[pTmp++] = b2 + b5;
			tmp[pTmp++] = b0 - b7;
		}

		//  Vertical
		for (int i = 0; i < 8; i++)
		{
			int pTmp = i;
			p0 = tmp[pTmp];
			p1 = tmp[pTmp += 8];
			p2 = tmp[pTmp += 8];
			p3 = tmp[pTmp += 8];
			p4 = tmp[pTmp += 8];
			p5 = tmp[pTmp += 8];
			p6 = tmp[pTmp += 8];
			p7 = tmp[pTmp += 8];

			a0 = p0 + p4;
			a1 = p0 - p4;
			a2 = p6 - (p2 >> 1);
			a3 = p2 + (p6 >> 1);

			b0 = a0 + a3;
			b2 = a1 - a2;
			b4 = a1 + a2;
			b6 = a0 - a3;

			a0 = -p3 + p5 - p7 - (p7 >> 1);
			a1 = p1 + p7 - p3 - (p3 >> 1);
			a2 = -p1 + p7 + p5 + (p5 >> 1);
			a3 = p3 + p5 + p1 + (p1 >> 1);

			b1 = a0 + (a3 >> 2);
			b7 = a3 - (a0 >> 2);
			b3 = a1 + (a2 >> 2);
			b5 = a2 - (a1 >> 2);

			block[8 * 0 + i] = b0 + b7;
			block[8 * 1 + i] = b2 - b5;
			block[8 * 2 + i] = b4 + b3;
			block[8 * 3 + i] = b6 + b1;
			block[8 * 4 + i] = b6 - b1;
			block[8 * 5 + i] = b4 - b3;
			block[8 * 6 + i] = b2 + b5;
			block[8 * 7 + i] = b0 - b7;
		}
	}
}
