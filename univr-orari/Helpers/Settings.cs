// Helpers/Settings.cs This file was automatically added when you installed the Settings Plugin. If you are not using a PCL then comment this file back in to use it.
using Plugin.Settings;
using Plugin.Settings.Abstractions;

namespace univr_orari.Helpers
{
	/// <summary>
	/// This is the Settings static class that can be used in your Core solution or in any
	/// of your client applications. All settings are laid out the same exact way with getters
	/// and setters. 
	/// </summary>
	public static class Settings
	{
		private static ISettings AppSettings
		{
			get
			{
				return CrossSettings.Current;
			}
		}

		#region Setting Constants
		private const string IsFirstStartupKey = "is_first_startup";
		private static readonly bool IsFirstStartupDefault = true;

		private const string CourseIdKey = "course_id";
		private static readonly string CourseIdDefault = string.Empty;

		private const string CourseYearIdKey = "course_year_id";
		private static readonly string CourseYearIdDefault = string.Empty;

		private const string AcademicYearIdKey = "academic_year_id";
		private static readonly string AdademicYearIdDefault = string.Empty;

		private const string WeekViewModeKey = "week_view_mode_key";
		private static readonly int WeekViewModeDefault = 3;
		#endregion

		public static bool IsFirstStartup
		{
			get
			{
				return AppSettings.GetValueOrDefault(IsFirstStartupKey, IsFirstStartupDefault);
			}
			set
			{
				AppSettings.AddOrUpdateValue(IsFirstStartupKey, value);
			}
		}

		public static string CourseId
		{
			get
			{
				return AppSettings.GetValueOrDefault(CourseIdKey, CourseIdDefault);
			}
			set
			{
				AppSettings.AddOrUpdateValue(CourseIdKey, value);
			}
		}

		public static string CourseYearId
		{
			get
			{
				return AppSettings.GetValueOrDefault(CourseYearIdKey, CourseYearIdDefault);
			}
			set
			{
				AppSettings.AddOrUpdateValue(CourseYearIdKey, value);
			}
		}

		public static string AcademicYearId
		{
			get
			{
				return AppSettings.GetValueOrDefault(AcademicYearIdKey, AdademicYearIdDefault);
			}
			set
			{
				AppSettings.AddOrUpdateValue(AcademicYearIdKey, value);
			}
		}

		public static int WeekViewMode
		{
			get
			{
				return AppSettings.GetValueOrDefault(WeekViewModeKey, WeekViewModeDefault);
			}
			set
			{
				AppSettings.AddOrUpdateValue(WeekViewModeKey, value);
			}
		}
	}
}