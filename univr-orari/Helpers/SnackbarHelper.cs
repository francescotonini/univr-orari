using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Support.Design.Widget;
using Android.Views;
using Android.Widget;

namespace univr_orari.Helpers
{
	public static class SnackbarHelper
	{
		public static void Show(View view, int titleId, int buttonId = 0)
		{
			Snackbar snackBar = Snackbar.Make(view, titleId, Snackbar.LengthLong);
			if (buttonId != 0) snackBar.SetAction(buttonId, v => snackBar.Dismiss());
			snackBar.Show();
		}
	}
}