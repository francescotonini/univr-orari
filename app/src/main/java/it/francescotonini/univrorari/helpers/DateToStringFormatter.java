/*
 * The MIT License
 *
 * Copyright (c) 2017-2019 Francesco Tonini - francescotonini.me
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package it.francescotonini.univrorari.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateToStringFormatter {
    public static String getETAString(long endTimestamp) {
        Date now = new Date();
        Date end = new Date(endTimestamp);

        long secs = (end.getTime() - now.getTime()) / 1000;
        long hours = secs / 3600;
        secs = secs % 3600;
        long mins = (secs / 60) + 1; // +1 --> approx.

        String output = "";
        if (hours > 0) {
            output = hours + "h ";
        }
        output += mins + "m";

        return output;
    }

    public static String getTimeString(long endTimestamp) {
        Date end = new Date(endTimestamp);

        return new SimpleDateFormat("HH:mm").format(end);
    }
}
