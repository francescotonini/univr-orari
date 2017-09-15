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
using System.Linq;
using Android.App;
using Android.OS;
using Android.Views;
using Android.Widget;
using univr_orari.Models;
using Toolbar = Android.Support.V7.Widget.Toolbar;

#endregion

namespace univr_orari.Activities
{
	[Activity(Label = "@string/select_course_activity_title")]
	public class SelectCourseActivity : BaseActivity
	{
		private Toolbar toolbar;
		private RelativeLayout loadingLayout;
		private RelativeLayout mainLayout;
		private Spinner courseSpinner;
		private Spinner courseYearSpinner;
		private ProgressBar loadingPrg;
		private List<Course> courses;
		private Course selectedCourse;

		private void CourseSpinnerOnItemSelected(object sender, AdapterView.ItemSelectedEventArgs itemSelectedEventArgs)
		{
			selectedCourse = courses[itemSelectedEventArgs.Position];
			SetCourseYearSpinner();
		}

		private void CourseYearSpinnerOnItemSelected(object sender, AdapterView.ItemSelectedEventArgs itemSelectedEventArgs)
		{
		}

		private void SetCourseSpinner()
		{
			ArrayAdapter<string> adapter = new ArrayAdapter<string>(this, Resource.Layout.support_simple_spinner_dropdown_item,
				courses.Select(x => x.Label).ToList());
			adapter.SetDropDownViewResource(Resource.Layout.support_simple_spinner_dropdown_item);
			courseSpinner.Adapter = adapter;
		}

		private void SetCourseYearSpinner()
		{
			ArrayAdapter<string> adapter = new ArrayAdapter<string>(this, Resource.Layout.support_simple_spinner_dropdown_item,
				selectedCourse.Years.Select(x => x.Label).ToList());
			adapter.SetDropDownViewResource(Resource.Layout.support_simple_spinner_dropdown_item);
			courseYearSpinner.Adapter = adapter;
		}

		protected override int LayoutResource => Resource.Layout.select_course_activity;

		protected override void OnCreate(Bundle savedInstanceState)
		{
			base.OnCreate(savedInstanceState);

			toolbar = FindViewById<Toolbar>(Resource.Id.select_course_activity_toolbar);
			loadingLayout = FindViewById<RelativeLayout>(Resource.Id.select_course_activity_loading_layout);
			mainLayout = FindViewById<RelativeLayout>(Resource.Id.select_course_activity_main_layout);
			loadingPrg = FindViewById<ProgressBar>(Resource.Id.select_course_activity_loading_prg);
			courseSpinner = FindViewById<Spinner>(Resource.Id.select_course_activity_course_spinner);
			courseYearSpinner = FindViewById<Spinner>(Resource.Id.select_course_activity_course_year_spinner);

			SetSupportActionBar(toolbar);
		}

		protected override async void OnResume()
		{
			base.OnResume();

			courseSpinner.ItemSelected += CourseSpinnerOnItemSelected;
			courseYearSpinner.ItemSelected += CourseYearSpinnerOnItemSelected;

			// Load courses
			courses = await DataStore.GetCourses();
			if (courses != null)
			{
				loadingLayout.Visibility = ViewStates.Gone;
				mainLayout.Visibility = ViewStates.Visible;

				SetCourseSpinner();
			}
		}

		protected override void OnPause()
		{
			base.OnPause();

			courseSpinner.ItemSelected -= CourseSpinnerOnItemSelected;
			courseYearSpinner.ItemSelected -= CourseYearSpinnerOnItemSelected;
		}
	}
}