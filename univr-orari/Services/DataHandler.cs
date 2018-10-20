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
using System.Globalization;
using System.Linq;
using System.Net.Http;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using ModernHttpClient;
using MonkeyCache.FileStore;
using Newtonsoft.Json;
using univr_orari.Helpers;
using univr_orari.Models;

#endregion

namespace univr_orari.Services
{
	public class DataHandler
	{
		public DataHandler()
		{
			// Configuring remote client
			RemoteClient = new HttpClient(new NativeMessageHandler());
		}

		/// <summary>
		/// Remote client
		/// </summary>
		public HttpClient RemoteClient { get; }

		/// <summary>
		/// Get courses
		/// </summary>
		/// <returns></returns>
		public async Task<AcademicYear> GetCourses()
		{
			try
			{
				// Get the stuff
				HttpResponseMessage response = await RemoteClient.GetAsync(GET_ACADEMIC_YEAR_ENDPOINT);
				if (!response.IsSuccessStatusCode)
					return null;

				// Process response
				string rawResponse = await response.Content.ReadAsStringAsync();
				Regex regex = new Regex("var elenco_corsi = (\\[{.+}\\])");
				Match match = regex.Match(rawResponse);
				if (!match.Success)
					return null;
                
				// Try to serialize
				List<AcademicYear> academicYears = JsonConvert.DeserializeObject<List<AcademicYear>>(match.Groups[1].Value);
				if (academicYears == null)
					return null;

				// Replace "Laurea" with "L."
				academicYears[0].Courses = academicYears[0].Courses.Select(x =>
				{
					x.Label = x.Label.Replace("Laurea", "L.");
					return x;
				}).ToList();

				// Return the first element
				return academicYears[0];
			}
			catch (Exception e)
			{
				Logger.Write("Exception on GetCurrentAcademicYear", e.Message);

				return null;
			}
		}

        /// <summary>
        /// Gets a list of lessons from the local cache
        /// </summary>
        /// <param name="year">lesson's year</param>
        /// <param name="month">lesson's month</param>
        /// <returns></returns>
        public List<Lesson> GetLessonsFromCache(int year, int month) =>
            Barrel.Current.Get<List<Lesson>>($"{Settings.CourseId}-{Settings.CourseYearId}-{Settings.AcademicYearId}:{month}-{year}");

        /// <summary>
        /// Gets a list of lessons from the network
        /// </summary>
        /// <param name="year">lesson's year</param>
        /// <param name="month">lesson's month</param>
        /// <returns></returns>
        public async Task<List<Lesson>> GetLessonsFromNetwork(int year, int month)
		{
			try
			{
				// This piece of code contains a "simple" parse logic. Basically it "translates" a "week timetable"
				// into a list of "lessons" which is more usable
				// Since the source of these data is not reliable, 
				// I decided to add a try-catch statement
				List<Lesson> lessons = new List<Lesson>();
				List<WeeklyTimetable> thisMonthTimetables = new List<WeeklyTimetable>();

				string courseId = Settings.CourseId;
				string courseYearId = Settings.CourseYearId;
				string academicYearId = Settings.AcademicYearId;

				DateTime thisMonth = new DateTime(year, month, 1);
				while (thisMonth.Day != DateTime.DaysInMonth(year, month))
				{
					if (thisMonth.DayOfWeek == DayOfWeek.Monday)
						thisMonthTimetables.Add(await GetWeeklyTimetable(thisMonth.ToString("dd-MM-yyyy"),
							academicYearId, courseId, courseYearId));

					thisMonth = thisMonth.AddDays(1);
				}

				foreach (WeeklyTimetable weeklyTimetable in thisMonthTimetables)
				{
					if (weeklyTimetable.Events == null)
						continue;

					lessons.AddRange(from e in weeklyTimetable.Events.Where(e => e.Name != null && e.Room != null && e.Teacher != null)
						let day = new List<Day>(weeklyTimetable.Days).Find(x => x.Value == e.Day)
						let startDateTime = DateTime
							.ParseExact($"{weeklyTimetable.FirstDayOfWeek} {e.Start}", "dd-MM-yyyy HH:mm", CultureInfo.CurrentCulture,
								DateTimeStyles.AssumeLocal)
							.AddDays(int.Parse(day.Value) - 1)
						let endDateTime = DateTime
							.ParseExact($"{weeklyTimetable.FirstDayOfWeek} {e.End}", "dd-MM-yyyy HH:mm", CultureInfo.CurrentCulture,
								DateTimeStyles.AssumeLocal)
							.AddDays(int.Parse(day.Value) - 1)
						select new Lesson
						{
							Name = e.Name,
							Teacher = e.Teacher,
							StartDateTimeOffset = startDateTime,
							EndDateTimeOffset = endDateTime,
							Room = e.Room,
							Month = month,
							Year = year,
							LastUpdateDateTimeOffset = DateTimeOffset.UtcNow
						});
				}

                // Update the local cache
                Barrel.Current.Add($"{Settings.CourseId}-{Settings.CourseYearId}-{Settings.AcademicYearId}:{month}-{year}", lessons, TimeSpan.FromDays(5));

                return lessons;
			}
			catch (Exception e)
			{
				Logger.Write("Exception on GetLessons", e.Message);

				return null;
			}
		}

        /// <summary>
        /// Deletes the local cache
        /// </summary>
        public void ClearCache()
        {
            Barrel.Current.EmptyAll();
        }

		private async Task<WeeklyTimetable> GetWeeklyTimetable(string firstDayOfWeek, string academicYearId, string course,
			string courseYear)
		{
			// Prepare request
			HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Post, POST_COURSE_TIMETABLE_ENDPOINT);
			List<KeyValuePair<string, string>> body = new List<KeyValuePair<string, string>>
			{
				new KeyValuePair<string, string>("_lang", "it"),
				new KeyValuePair<string, string>("aa", "2018"),
				new KeyValuePair<string, string>("all_events", "0"),
				new KeyValuePair<string, string>("anno", academicYearId),
				new KeyValuePair<string, string>("anno2", courseYear),
				new KeyValuePair<string, string>("cdl", course),
				new KeyValuePair<string, string>("corso", course),
				new KeyValuePair<string, string>("date", firstDayOfWeek),
				new KeyValuePair<string, string>("form-type", "corso")
			};
			request.Content = new FormUrlEncodedContent(body);

			// Try to serialize
			try
			{
				// Get and process response
				HttpResponseMessage response =
					await RemoteClient.PostAsync(POST_COURSE_TIMETABLE_ENDPOINT, new FormUrlEncodedContent(body));
				if (!response.IsSuccessStatusCode)
					return null;

				string rawResponse = await response.Content.ReadAsStringAsync();
				WeeklyTimetable result = JsonConvert.DeserializeObject<WeeklyTimetable>(rawResponse);

				return result;
			}
			catch (Exception e)
			{
				Logger.Write("Exception on GetWeeklyTimetable", e.Message);

				return null;
			}
		}
        
		private const string GET_ACADEMIC_YEAR_ENDPOINT = "https://logistica.univr.it/aule/Orario/combo_call.php";
		private const string POST_COURSE_TIMETABLE_ENDPOINT = "https://logistica.univr.it/aule/Orario/grid_call.php";
	}
}