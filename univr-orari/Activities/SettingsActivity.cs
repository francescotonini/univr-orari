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

using Android.App;
using Android.OS;
using Android.Support.V7.Widget;
using Android.Views;
using univr_orari.Fragments;

#endregion

namespace univr_orari.Activities
{
	[Activity(Label = "@string/settings_activity_title", Theme = "@style/app_theme_settings")]
	public class SettingsActivity : BaseActivity
	{
		public override bool OnOptionsItemSelected(IMenuItem item)
		{
			switch (item.ItemId)
			{
				case Android.Resource.Id.Home:
					OnBackPressed();
					return true;
				default:
					return base.OnOptionsItemSelected(item);
			}
		}

		private Toolbar toolbar;
		protected override int LayoutResource => Resource.Layout.settings_activity;

		protected override void OnCreate(Bundle savedInstanceState)
		{
			base.OnCreate(savedInstanceState);

			toolbar = FindViewById<Toolbar>(Resource.Id.settings_activity_toolbar);

			SetSupportActionBar(toolbar);
			SupportActionBar.SetDisplayHomeAsUpEnabled(true);
			SupportActionBar.SetDisplayShowHomeEnabled(true);

			SupportFragmentManager.BeginTransaction().Replace(Resource.Id.settings_activity_frame, new SettingsFragment())
				.Commit();
		}
	}
}