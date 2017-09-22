using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Microsoft.Azure.Mobile.Analytics;

namespace univr_orari.Helpers
{
	public static class Logger
	{
		/// <summary>
		/// Send anonymous statistics to Mobile Center
		/// </summary>
		/// <param name="title"></param>
		/// <param name="details"></param>
		public static void Write(string title, Dictionary<string, string> details)
		{
			Analytics.TrackEvent(title, details);
		}
	}
}