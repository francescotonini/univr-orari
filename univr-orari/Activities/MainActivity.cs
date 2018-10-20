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
using Android.App;
using Android.Content;
using Android.Content.Res;
using Android.Graphics;
using Android.OS;
using Android.Support.V4.Content;
using Android.Views;
using Android.Widget;
using Com.Alamkanak.Weekview;
using Java.Util;
using Plugin.Connectivity;
using univr_orari.Helpers;
using univr_orari.Models;
using Toolbar = Android.Support.V7.Widget.Toolbar;

#endregion

namespace univr_orari.Activities
{
    [Activity(Label = "@string/app_name", ConfigurationChanges = Android.Content.PM.ConfigChanges.Orientation)]
    public class MainActivity : BaseActivity,
        WeekView.IEventClickListener, MonthLoader.IMonthChangeListener, IDialogInterfaceOnClickListener
    {
        protected override int LayoutResource => Resource.Layout.main_activity;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            // Init
            App.Init();

            // Bind UI
            layout = FindViewById<RelativeLayout>(Resource.Id.main_activity_layout);
            toolbar = FindViewById<Toolbar>(Resource.Id.main_activity_toolbar);
            weekView = FindViewById<WeekView>(Resource.Id.main_activity_week_view);
            lessonColors = new Dictionary<string, int>();
            lessons = new Dictionary<string, List<Lesson>>();

            // Prepare UI
            weekView.NumberOfVisibleDays = Settings.WeekViewMode;
            weekView.SetOnEventClickListener(this);
            weekView.MonthChangeListener = this;
            SetSupportActionBar(toolbar);
        }

        protected override void OnResume()
        {
            base.OnResume();

            // Update weekview
            weekView.GoToHour(8);
            weekView.DateTimeInterpreter = new DateTimeInterpreter();

            // Check for network connectivity
            if (!CrossConnectivity.Current.IsConnected)
            {
                AlertDialogHelper.Show(this, Resource.String.no_connection_dialog_title, Resource.String.no_connection_dialog_message, this);
                return;
            }
        }

        public override void OnConfigurationChanged(Configuration newConfig)
        {
            base.OnConfigurationChanged(newConfig);

            if (newConfig.Orientation == Android.Content.Res.Orientation.Landscape)
            {
                ChangeWeekViewMode(5);
            }
            else
            {
                ChangeWeekViewMode(Settings.WeekViewMode);
            }

        }

        public void OnEventClick(WeekViewEvent p0, RectF p1)
        {
            int year = p0.StartTime.Get(CalendarField.Year);
            int month = p0.StartTime.Get(CalendarField.Month) + 1;
            string key = $"{year}-{month}";
            
            Lesson selectedLesson = lessons[key]?.Find(x => x.StartDateTimeOffset != null &&
                x.StartDateTimeOffset.LocalDateTime.Day == p0.StartTime.Get(CalendarField.DayOfMonth) &&
                x.StartDateTimeOffset.LocalDateTime.Month == p0.StartTime.Get(CalendarField.Month) + 1 &&
                x.StartDateTimeOffset.LocalDateTime.Hour == p0.StartTime.Get(CalendarField.HourOfDay) &&
                x.StartDateTimeOffset.LocalDateTime.Minute == p0.StartTime.Get(CalendarField.Minute) &&
                x.StartDateTimeOffset.LocalDateTime.Year == p0.StartTime.Get(CalendarField.Year) &&
                x.Name == p0.Name && x.Room == p0.Location);

            if (selectedLesson == null)
            {
                // That's weird
                return;
            }

            Intent viewLessonIntent = new Intent(this, typeof(ViewLessonActivity));
            viewLessonIntent.PutExtra("data", selectedLesson.ToString());
            StartActivity(viewLessonIntent);
        }

        public IList<WeekViewEvent> OnMonthChange(int year, int month)
        {
            List<WeekViewEvent> events = new List<WeekViewEvent>();

            // Check if this request has been made already
            if (!lessons.ContainsKey($"{year}-{month}"))
            {
                LoadLessons(year, month);
                return events;
            }

            // Display lessons
            foreach (Lesson lesson in lessons[$"{year}-{month}"])
            {
                try
                {
                    Calendar startTimeCalendar = Calendar.Instance;
                    startTimeCalendar.Set(CalendarField.Year, lesson.StartDateTimeOffset.LocalDateTime.Year);
                    startTimeCalendar.Set(CalendarField.Month, lesson.StartDateTimeOffset.LocalDateTime.Month - 1);
                    startTimeCalendar.Set(CalendarField.DayOfMonth, lesson.StartDateTimeOffset.LocalDateTime.Day);
                    startTimeCalendar.Set(CalendarField.HourOfDay, lesson.StartDateTimeOffset.LocalDateTime.Hour);
                    startTimeCalendar.Set(CalendarField.Minute, lesson.StartDateTimeOffset.LocalDateTime.Minute);

                    Calendar endTimeCalendar = Calendar.Instance;
                    endTimeCalendar.Set(CalendarField.Year, lesson.EndDateTimeOffset.LocalDateTime.Year);
                    endTimeCalendar.Set(CalendarField.Month, lesson.EndDateTimeOffset.LocalDateTime.Month - 1);
                    endTimeCalendar.Set(CalendarField.DayOfMonth, lesson.EndDateTimeOffset.LocalDateTime.Day);
                    endTimeCalendar.Set(CalendarField.HourOfDay, lesson.EndDateTimeOffset.LocalDateTime.Hour);
                    endTimeCalendar.Set(CalendarField.Minute, lesson.EndDateTimeOffset.LocalDateTime.Minute - 1);

                    events.Add(new WeekViewEvent(startTimeCalendar.TimeInMillis, lesson.Name, lesson.Room, startTimeCalendar,
                        endTimeCalendar)
                    {
                        Color = ContextCompat.GetColor(this, GetCellColor(lesson.Name))
                    });
                }
                catch (Exception e)
                {
                    Logger.Write("Error on OnMonthChange", e.Message);
                }
            }

            return events;
        }

        public override bool OnCreateOptionsMenu(IMenu menu)
        {
            MenuInflater.Inflate(Resource.Menu.main_menu, menu);
            dayViewMenuItem = menu.FindItem(Resource.Id.main_menu_day_view);
            weekViewMenuItem = menu.FindItem(Resource.Id.main_menu_week_view);

            // Update UI
            weekViewMenuItem.SetVisible(Settings.WeekViewMode == 1);
            dayViewMenuItem.SetVisible(Settings.WeekViewMode != 1);

            return true;
        }

        public override bool OnOptionsItemSelected(IMenuItem item)
        {
            switch (item.ItemId)
            {
                case Resource.Id.main_menu_week_view:
                    ChangeWeekViewMode(3);
                    return true;
                case Resource.Id.main_menu_day_view:
                    ChangeWeekViewMode(1);
                    return true;
                case Resource.Id.main_menu_refresh:
                    lessons.Clear();
                    weekView.NotifyDatasetChanged();
                    return true;
                case Resource.Id.main_menu_change_course:
                    StartActivity(new Intent(this, typeof(SelectCourseActivity)));
                    return true;
                case Resource.Id.main_menu_settings:
                    StartActivity(new Intent(this, typeof(SettingsActivity)));
                    return true;
                default:
                    return base.OnOptionsItemSelected(item);
            }
        }

        public void OnClick(IDialogInterface dialog, int which)
        {
            ;
        }

        private async void LoadLessons(int year, int month)
        {
            string key = $"{year}-{month}";
            this.lessons.Add(key, new List<Lesson>());

            SnackbarHelper.Show(layout, Resource.String.main_activity_loading);

            List<Lesson> lessons = await DataStore.GetLessonsFromNetwork(year, month);
            if (lessons == null)
            {
                SnackbarHelper.Show(layout, Resource.String.unknown_error_message, 0);
                return;
            }

            this.lessons[key].AddRange(lessons);
            weekView.NotifyDatasetChanged();
        }

        private int GetCellColor(string id)
        {
            if (!lessonColors.ContainsKey(id))
            {
                // 23 is the number of colors available
                lessonColors.Add(id, ColorHelper.GetColor(lessonColors.Count % 23));
            }

            return lessonColors[id];
        }

        private void ChangeWeekViewMode(int mode)
        {
            if (mode == 1 || mode == 3)
            {
                Settings.WeekViewMode = mode;
                weekView.NumberOfVisibleDays = Settings.WeekViewMode;
            }
            weekView.NumberOfVisibleDays = mode;
            weekViewMenuItem.SetVisible(mode == 1);
            dayViewMenuItem.SetVisible(mode != 1);
            weekView.GoToHour(DateTime.Now.Hour);
        }

        private RelativeLayout layout;
        private Toolbar toolbar;
        private WeekView weekView;
        private Dictionary<string, List<Lesson>> lessons;
        private Dictionary<string, int> lessonColors;
        private IMenuItem dayViewMenuItem;
        private IMenuItem weekViewMenuItem;
    }
}