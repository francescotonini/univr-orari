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

#region

using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Net.Http;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using ModernHttpClient;
using Newtonsoft.Json;
using Plugin.Connectivity;
using Realms;
using univr_orari.Helpers;
using univr_orari.Models;

#endregion

#endregion

namespace univr_orari.Services
{
	public class DataHandler
	{
		public DataHandler()
		{
			// Configuring local client
			localClientConfiguration = RealmConfiguration.DefaultConfiguration;
			localClientConfiguration.SchemaVersion = 1;
#if DEBUG
			localClientConfiguration.ShouldDeleteIfMigrationNeeded = true;
#endif
			localClientConfiguration.MigrationCallback += LocalClient_MigrationCallBack;
			LocalClient = Realm.GetInstance(localClientConfiguration);

			// Configuring remote client
			RemoteClient = new HttpClient(new NativeMessageHandler());
		}

		/// <summary>
		///     Remote client
		/// </summary>
		public HttpClient RemoteClient { get; }

		/// <summary>
		///     Local client
		/// </summary>
		public Realm LocalClient { get; }

		/// <summary>
		///     Get courses
		/// </summary>
		/// <returns></returns>
		public async Task<AcademicYear> GetCurrentAcademicYear()
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
				Logger.Write("Exception on GetCurrentAcademicYear", new Dictionary<string, string>
				{
					{"Message", e.Message}
				});

				return null;
			}
		}

		public async Task<List<Lesson>> GetLessons(int year, int month, bool preferCache = false)
		{
			try
			{
				// This piece of code contains a "simple" parse logic. Basically it "translates" a "week timetable"
				// into a list of "lessons" which is more usable
				// Since the source of these data is not reliable, 
				// I decided to add a try-catch statement
				if (preferCache)
				{
					IQueryable<Lesson> currentLessonStored = LocalClient.All<Lesson>().Where(x => x.Month == month && x.Year == year);
					if (!CrossConnectivity.Current.IsConnected || currentLessonStored.Count() != 0 && currentLessonStored?.ToList()
						    .FindAll(x => (DateTimeOffset.UtcNow - x.LastUpdateDateTimeOffset).Days > 7).Count == 0)
						return currentLessonStored.ToList();
				}

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

				// Save to db updated lessons
				using (Transaction transaction = LocalClient.BeginWrite())
				{
					IQueryable<Lesson> currentLessonStored = LocalClient.All<Lesson>().Where(x => x.Month == month && x.Year == year);
					LocalClient.RemoveRange(currentLessonStored);
					foreach (Lesson l in lessons)
						LocalClient.Add(l);

					transaction.Commit();
				}

				return lessons;
			}
			catch (Exception e)
			{
				Logger.Write("Exception on GetLessons", new Dictionary<string, string>
				{
					{"Message", e.Message}
				});

				return null;
			}
		}

		public async Task<WeeklyTimetable> GetWeeklyTimetable(string firstDayOfWeek, string academicYearId, string course,
			string courseYear)
		{
			// Prepare request
			HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Post, POST_COURSE_TIMETABLE_ENDPOINT);
			List<KeyValuePair<string, string>> body = new List<KeyValuePair<string, string>>
			{
				new KeyValuePair<string, string>("_lang", "it"),
				new KeyValuePair<string, string>("aa", "2017"),
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
				Logger.Write("Exception on GetWeeklyTimetable", new Dictionary<string, string>
				{
					{"Message", e.Message}
				});

				return null;
			}
		}

		public void ClearDb()
		{
			using (Transaction transaction = LocalClient.BeginWrite())
			{
				IQueryable<Lesson> currentLessonStored = LocalClient.All<Lesson>();
				LocalClient.RemoveRange(currentLessonStored);

				transaction.Commit();
			}
		}

		private const string GET_ACADEMIC_YEAR_ENDPOINT = "https://logistica.univr.it/aule/Orario/combo_call.php";
		private const string POST_COURSE_TIMETABLE_ENDPOINT = "https://logistica.univr.it/aule/Orario/grid_call.php";
		private const string POST_ROOMS_ENDPOINT = "";
		private readonly RealmConfiguration localClientConfiguration;

		private void LocalClient_MigrationCallBack(Migration migration, ulong oldSchemaVersion)
		{
			Logger.Write("Migration callback triggered", new Dictionary<string, string>
			{
				{"oldSchemaVersion", oldSchemaVersion.ToString()}
			});
		}
	}
}