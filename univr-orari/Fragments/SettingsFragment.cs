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

using Android.Content;
using Android.Net;
using Android.OS;
using Android.Support.V7.Preferences;
using univr_orari.Helpers;

#endregion

namespace univr_orari.Fragments
{
	public class SettingsFragment : PreferenceFragmentCompat, ISharedPreferencesOnSharedPreferenceChangeListener
	{
		public override void OnCreate(Bundle savedInstanceState)
		{
			base.OnCreate(savedInstanceState);
            
			leaveFeedback = FindPreference("leave_feedback");
			appVersion = FindPreference("app_version");
		}

		public override void OnResume()
		{
			base.OnResume();

			PreferenceManager.SharedPreferences.RegisterOnSharedPreferenceChangeListener(this);
			leaveFeedback.PreferenceClick += LeaveFeedbackOnPreferenceClick;
			appVersion.PreferenceClick += AppVersionOnPreferenceClick;
			appVersion.Summary = Context.PackageManager.GetPackageInfo(Context.PackageName, 0).VersionName;
		}

		public override void OnPause()
		{
			base.OnPause();

			PreferenceManager.SharedPreferences.UnregisterOnSharedPreferenceChangeListener(this);
			leaveFeedback.PreferenceClick -= LeaveFeedbackOnPreferenceClick;
		}

		public void OnSharedPreferenceChanged(ISharedPreferences sharedPreferences, string key)
		{
			;
		}

		public override void OnCreatePreferences(Bundle savedInstanceState, string rootKey)
		{
			AddPreferencesFromResource(Resource.Xml.preferences);
		}

		private void AppVersionOnPreferenceClick(object sender, Preference.PreferenceClickEventArgs preferenceClickEventArgs)
		{
			SnackbarHelper.Show(View, Resource.String.preferences_easter);
		}

		private void LeaveFeedbackOnPreferenceClick(object sender,
			Preference.PreferenceClickEventArgs preferenceClickEventArgs)
		{
			Intent emailIntent = new Intent(Intent.ActionSendto, Uri.Parse("mailto:francescoantoniotonini@gmail.com"));
			emailIntent.PutExtra(Intent.ExtraSubject, "UniVR Orari - Feedback");
			StartActivity(emailIntent);
		}
        
        private Preference leaveFeedback;
        private Preference appVersion;
    }
}