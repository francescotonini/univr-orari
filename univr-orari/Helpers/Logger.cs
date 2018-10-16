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

#region

using System.Collections.Generic;

#endregion

namespace univr_orari.Helpers
{
	public static class Logger
	{
		/// <summary>
		/// Writes to Info log
		/// </summary>
		/// <param name="title">title</param>
		/// <param name="details">Dictionary to write</param>
		public static void Write(string title, Dictionary<string, string> details)
		{
            string text = $"{title} ";
            foreach (string detail in details.Values)
            {
                text += "${detail} ";
            }
            Android.Util.Log.Info("it.francescotonini.univrorari", text);
		}

        /// <summary>
		/// Writes to Info log
		/// </summary>
		/// <param name="title">title</param>
		/// <param name="message">message</param>
		public static void Write(string title, string message)
        {
            string text = $"{title}: {message}";
            Android.Util.Log.Info("it.francescotonini.univrorari", text);
        }
    }
}