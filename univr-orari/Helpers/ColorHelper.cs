// The MIT License (MIT)
// 
// Copyright (c) 2017-2017 Francesco Tonini <francescoantoniotonini@gmail.com>
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
// documentation files (the "Software"), to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
// to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in all copies or substantial portions
// of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
// BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

namespace univr_orari.Helpers
{
	public static class ColorHelper
	{
		public static int GetColor(int index)
		{
			while (true)
				switch (index)
				{
					case 0:
						return Resource.Color.cell_0;
					case 1:
						return Resource.Color.cell_1;
					case 2:
						return Resource.Color.cell_2;
					case 3:
						return Resource.Color.cell_3;
					case 4:
						return Resource.Color.cell_4;
					case 5:
						return Resource.Color.cell_5;
					case 6:
						return Resource.Color.cell_6;
					case 7:
						return Resource.Color.cell_7;
					case 8:
						return Resource.Color.cell_8;
					case 9:
						return Resource.Color.cell_9;
					case 10:
						return Resource.Color.cell_10;
					case 11:
						return Resource.Color.cell_11;
					case 12:
						return Resource.Color.cell_12;
					case 13:
						return Resource.Color.cell_13;
					case 14:
						return Resource.Color.cell_14;
					case 15:
						return Resource.Color.cell_15;
					case 16:
						return Resource.Color.cell_16;
					case 17:
						return Resource.Color.cell_17;
					case 18:
						return Resource.Color.cell_18;
					case 19:
						return Resource.Color.cell_19;
					case 20:
						return Resource.Color.cell_20;
					case 21:
						return Resource.Color.cell_21;
					case 22:
						return Resource.Color.cell_22;
					case 23:
						return Resource.Color.cell_23;
					case 24:
						return Resource.Color.cell_24;
					case 25:
						return Resource.Color.cell_25;
					case 26:
						return Resource.Color.cell_26;
					case 27:
						return Resource.Color.cell_27;
					case 28:
						return Resource.Color.cell_28;
					case 29:
						return Resource.Color.cell_29;
					case 30:
						return Resource.Color.cell_30;
					default:
						// Fallback to first color
						index = 1;
						continue;
				}
		}
	}
}