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
using Android.Content;
using Android.OS;
using Android.Views;
using Android.Widget;
using univr_orari.Helpers;
using Toolbar = Android.Support.V7.Widget.Toolbar;

#endregion

namespace univr_orari.Activities
{
	[Activity(Label = "@string/view_lesson_activity_title")]
	public class ViewLessonActivity : BaseActivity
	{
		public override bool OnCreateOptionsMenu(IMenu menu)
		{
			MenuInflater.Inflate(Resource.Menu.view_lesson_menu, menu);
			return true;
		}

		public override bool OnOptionsItemSelected(IMenuItem item)
		{
			switch (item.ItemId)
			{
				case Android.Resource.Id.Home:
					OnBackPressed();
					return true;
				case Resource.Id.view_lesson_menu_share:
					Intent sendIntent = new Intent();
					sendIntent.SetAction(Intent.ActionSend);
					sendIntent.PutExtra(Intent.ExtraText,
						$"{lesson.Name}\n{lesson.Teacher}\n{lesson.Room}\n{lesson.StartDateTimeOffset.LocalDateTime:dd/MM HH:mm} - {lesson.EndDateTimeOffset.LocalDateTime:HH:mm}");
					sendIntent.SetType("text/plain");
					StartActivity(sendIntent);
					return true;
				default:
					return base.OnOptionsItemSelected(item);
			}
		}

		private Toolbar toolbar;
		private Lesson lesson;
		private TextView lessonTextView;
		private TextView roomTextView;
		private TextView teacherTextView;
		private TextView dateTextView;
		protected override int LayoutResource => Resource.Layout.view_lesson_activity;

		protected override void OnCreate(Bundle savedInstanceState)
		{
			base.OnCreate(savedInstanceState);

			toolbar = FindViewById<Toolbar>(Resource.Id.view_lesson_activity_toolbar);
			lessonTextView = FindViewById<TextView>(Resource.Id.view_lesson_activity_lesson_txt);
			dateTextView = FindViewById<TextView>(Resource.Id.view_lesson_activity_date_txt);
			teacherTextView = FindViewById<TextView>(Resource.Id.view_lesson_activity_teacher_txt);
			roomTextView = FindViewById<TextView>(Resource.Id.view_lesson_activity_room_txt);

			Lesson.TryParse(Intent.GetStringExtra("data"), out lesson);

			SetSupportActionBar(toolbar);
			SupportActionBar.SetDisplayHomeAsUpEnabled(true);
			SupportActionBar.SetDisplayShowHomeEnabled(true);
		}

		protected override void OnResume()
		{
			base.OnResume();

			Logger.Write("ViewLessonActivity opened", null);

			if (lesson == null) return;
			lessonTextView.Text = lesson.Name;
			roomTextView.Text = lesson.Room;
			teacherTextView.Text = lesson.Teacher;
			dateTextView.Text =
				$"{lesson.StartDateTimeOffset.LocalDateTime:dd/MM HH:mm} - {lesson.EndDateTimeOffset.LocalDateTime:HH:mm}";
		}
	}
}