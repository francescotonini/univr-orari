## UniVR Orari e Aule

<a href='https://play.google.com/store/apps/details?id=it.francescotonini.univrorari&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Disponibile su Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/it_badge_web_generic.png' height='70px' /></a>

**UniVR Orari e Aule** is an Android application that shows timetables and room availability for every department of University of Verona

### About this repository
This repository contains the entire source code of UniVR Orari under MIT license. In order to compile this project you need two additional files:
- `google-services.json` (obtainable through Firebase)
- `Constants.java`

```java
package it.francescotonini.univrorari;

/**
 * App's constants
 */
public class Constants {
    public static final String API_BASE_URL = "YOUR_BASE_URL";
}
```
*Example of Constants.java*

App icon by [SimpleIcon](https://www.flaticon.com/authors/simpleicon) from [flaticon.com](https://www.flaticon.com/)