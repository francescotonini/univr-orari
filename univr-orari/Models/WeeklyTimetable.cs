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
using Newtonsoft.Json;

#endregion

namespace univr_orari.Models
{
	/// <summary>
	/// Weekly timetable.
	/// </summary>
	public class WeeklyTimetable
	{
		/// <summary>
		/// WeeklyTimetable events
		/// </summary>
		[JsonProperty("celle")]
		public List<Event> Events { get; set; }

		/// <summary>
		/// WeeklyTimetable days
		/// </summary>
		[JsonProperty("giorni")]
		public List<Day> Days { get; set; }

		/// <summary>
		/// WeeklyTimetable first day of the week
		/// </summary>
		[JsonProperty("first_day")]
		public string FirstDayOfWeek { get; set; }

		/// <summary>
		/// WeeklyTimetable colors
		/// </summary>
		[JsonProperty("colori")]
		public List<string> Colors { get; set; }
	}

	/// <summary>
	/// Day (inner element of WeeklyTimetable)
	/// </summary>
	public class Day
	{
		/// <summary>
		/// Day label
		/// </summary>
		[JsonProperty("label")]
		public string Label { get; set; }

		/// <summary>
		/// Day value
		/// </summary>
		[JsonProperty("valore")]
		public string Value { get; set; }
	}

	/// <summary>
	/// Event. (inner element of WeeklyTimetable)
	/// </summary>
	public class Event
	{
		/// <summary>
		/// Event room
		/// </summary>
		[JsonProperty("aula")]
		public string Room { get; set; }

		/// <summary>
		/// Event teacher
		/// </summary>
		[JsonProperty("docente")]
		public string Teacher { get; set; }

		/// <summary>
		/// Event name
		/// </summary>
		[JsonProperty("titolo_lezione")]
		public string Name { get; set; }

		/// <summary>
		/// Event day
		/// </summary>
		[JsonProperty("giorno")]
		public string Day { get; set; }

		/// <summary>
		/// Event start time
		/// </summary>
		[JsonProperty("ora_inizio")]
		public string Start { get; set; }

		/// <summary>
		/// Event end time
		/// </summary>
		[JsonProperty("ora_fine")]
		public string End { get; set; }
	}
}