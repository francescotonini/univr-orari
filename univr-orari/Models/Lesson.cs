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
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using Newtonsoft.Json.Linq;

#endregion

namespace univr_orari.Models
{
	/// <summary>
	/// Lesson.
	/// </summary>
	public class Lesson
	{
		public string Name { get; set; }
		public DateTimeOffset StartDateTimeOffset { get; set; }
		public DateTimeOffset EndDateTimeOffset { get; set; }
		public int Month { get; set; }
		public int Year { get; set; }
		public string Room { get; set; }
		public string Teacher { get; set; }
		public DateTimeOffset LastUpdateDateTimeOffset { get; set; }

		public override string ToString()
		{
			JObject obj = new JObject
			{
				{nameof(Name), Name},
				{nameof(StartDateTimeOffset), StartDateTimeOffset.ToString()},
				{nameof(EndDateTimeOffset), EndDateTimeOffset.ToString()},
				{nameof(Month), Month},
				{nameof(Year), Year},
				{nameof(Room), Room},
				{nameof(Teacher), Teacher},
				{nameof(LastUpdateDateTimeOffset), LastUpdateDateTimeOffset.ToString()}
			};

			return obj.ToString();
		}

		public static Lesson Parse(string json)
		{
			return JsonConvert.DeserializeObject<Lesson>(json, new IsoDateTimeConverter());
		}

		public static bool TryParse(string json, out Lesson lesson)
		{
			try
			{
				lesson = Parse(json);
				return true;
			}
			catch (Exception)
			{
				lesson = null;
				return false;
			}
		}
	}
}