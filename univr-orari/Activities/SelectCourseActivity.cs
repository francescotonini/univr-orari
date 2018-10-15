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

using System;
using System.Collections.Generic;
using System.Linq;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Support.V7.Widget;
using Android.Views;
using Android.Widget;
using Plugin.Connectivity;
using univr_orari.Helpers;
using univr_orari.Models;
using AlertDialog = Android.Support.V7.App.AlertDialog;
using Toolbar = Android.Support.V7.Widget.Toolbar;

#endregion

namespace univr_orari.Activities
{
	[Activity(Label = "@string/select_course_activity_title",
		NoHistory = true)]
	public class SelectCourseActivity : BaseActivity
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
		private RelativeLayout loadingLayout;
		private RelativeLayout mainLayout;
		private Spinner courseSpinner;
		private Spinner courseYearSpinner;
		private ProgressBar loadingPrg;
		private AppCompatButton okBtn;
		private List<Course> courses;
		private AcademicYear currentAcademicYear;
		private Course selectedCourse;
		private CourseYear selectedCourseYear;

		private void CourseSpinnerOnItemSelected(object sender, AdapterView.ItemSelectedEventArgs itemSelectedEventArgs)
		{
			selectedCourse = courses[itemSelectedEventArgs.Position];
			SetCourseYearSpinner();
		}

		private void CourseYearSpinnerOnItemSelected(object sender, AdapterView.ItemSelectedEventArgs itemSelectedEventArgs)
		{
			selectedCourseYear = selectedCourse.Years[itemSelectedEventArgs.Position];
			okBtn.Enabled = true;
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

		private void OkBtnOnClick(object sender, EventArgs eventArgs)
		{
			// Saving course details
			Settings.AcademicYearId = currentAcademicYear.Id;
			Settings.CourseId = selectedCourse.Value;
			Settings.CourseYearId = selectedCourseYear.Value;
			Settings.IsFirstStartup = false;

			// Collecting statistics
			Logger.Write("Course selected", new Dictionary<string, string>
			{
				{"academicYearId", Settings.AcademicYearId},
				{"courseId", Settings.CourseId},
				{"courseYearId", Settings.CourseYearId}
			});

			// Open a new Activity
			// BUG: this doesn't clear back stack
			StartActivity(new Intent(this, typeof(MainActivity)));
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
			okBtn = FindViewById<AppCompatButton>(Resource.Id.select_course_activity_ok_btn);

			courseSpinner.ItemSelected += CourseSpinnerOnItemSelected;
			courseYearSpinner.ItemSelected += CourseYearSpinnerOnItemSelected;
			okBtn.Click += OkBtnOnClick;

			SetSupportActionBar(toolbar);
			SupportActionBar.SetDisplayHomeAsUpEnabled(true);
			SupportActionBar.SetDisplayShowHomeEnabled(true);
		}

		protected override async void OnResume()
		{
			base.OnResume();

			// Checks for internet connection. Shows a dialog if no connection is currenty available
			if (!CrossConnectivity.Current.IsConnected)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.SetTitle(GetString(Resource.String.no_connection_dialog_title));
				builder.SetMessage(GetString(Resource.String.no_connection_dialog_message));
				builder.SetPositiveButton(GetString(Resource.String.no_connection_dialog_button_text),
					(sender, e) => OnBackPressed());

				builder.Create().Show();
				return;
			}

			// Load courses
			currentAcademicYear = await DataStore.GetCurrentAcademicYear();
			if (currentAcademicYear?.Courses != null)
			{
				courses = currentAcademicYear.Courses;

				loadingLayout.Visibility = ViewStates.Gone;
				mainLayout.Visibility = ViewStates.Visible;

				SetCourseSpinner();
			}
			else
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.SetTitle(GetString(Resource.String.no_connection_dialog_title));
				builder.SetMessage(GetString(Resource.String.unknown_error_message));
				builder.SetPositiveButton(GetString(Resource.String.no_connection_dialog_button_text),
					(sender, e) => OnBackPressed());

				builder.Create().Show();
			}
		}
	}
}