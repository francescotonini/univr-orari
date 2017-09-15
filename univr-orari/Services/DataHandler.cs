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
using System.Net.Http;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using ModernHttpClient;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Realms;
using univr_orari.Models;

#endregion

namespace univr_orari.Services
{
	public class DataHandler
	{
		public DataHandler()
		{
			// Configuring local client
			RealmConfiguration localClientConfiguration;
			this.localClientConfiguration = RealmConfiguration.DefaultConfiguration;
			this.localClientConfiguration.SchemaVersion = 1;
#if DEBUG
			this.localClientConfiguration.ShouldDeleteIfMigrationNeeded = true;
#endif
			this.localClientConfiguration.MigrationCallback += LocalClient_MigrationCallBack;
			LocalClient = Realm.GetInstance(this.localClientConfiguration);

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
		public async Task<List<Course>> GetCourses()
		{
			// Get the stuff
			HttpResponseMessage response = await RemoteClient.GetAsync(GET_COURSES_ENDPOINT);
			if (!response.IsSuccessStatusCode)
				return null;

			// Process response
			string rawResponse = await response.Content.ReadAsStringAsync();
			Regex regex = new Regex("var elenco_corsi = (\\[{.+}\\])");
			Match match = regex.Match(rawResponse);
			if (!match.Success)
				return null;

			// Try to serialize
			try
			{
				JArray d = JArray.Parse(match.Groups[1].Value);
				List<Course> result = JsonConvert.DeserializeObject<List<Course>>(d[0]["elenco"].ToString());
				return result.Select(x =>
				{
					x.Label = x.Label.Replace("Laurea", "L.");
					return x;
				}).ToList();
			}
			catch (Exception)
			{
				return null;
			}
		}

		private const string GET_COURSES_ENDPOINT = "https://logistica.univr.it/aule/Orario/combo_call.php";
		private const string POST_COURSE_TIMETABLE_ENDPOINT = "";
		private const string POST_ROOMS_ENDPOINT = "";
		private readonly RealmConfiguration localClientConfiguration;

		private void LocalClient_MigrationCallBack(Migration migration, ulong oldSchemaVersion)
		{
			throw new NotImplementedException();
		}
	}
}