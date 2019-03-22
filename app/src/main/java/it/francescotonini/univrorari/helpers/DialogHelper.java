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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Helps generate and show {@link AlertDialog}
 */
public class DialogHelper {
    /**
     * Generate and show a {@link AlertDialog} with one button
     * @param context context where the dialog should be generated
     * @param title dialog's title
     * @param message dialog's message
     * @param buttonText text shown on the button
     * @return an instance of {@link AlertDialog}
     */
    public static AlertDialog show(Context context, String title, String message, String buttonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setCancelable(false).setPositiveButton(buttonText, null);
        return builder.show();
    }

    /**
     * Generate and show a {@link AlertDialog} with one button
     * @param context context where the dialog should be generated
     * @param title dialog's title
     * @param message dialog's message
     * @param buttonText text shown on the button
     * @return an instance of {@link AlertDialog}
     */
    public static AlertDialog show(Context context, int title, int message, int buttonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setCancelable(false).setPositiveButton(buttonText, null);
        return builder.show();
    }

    /**
     * Generate and show a {@link AlertDialog} with one button
     * @param context context where the dialog should be generated
     * @param title dialog's title
     * @param message dialog's message
     * @param buttonText text shown on the button
     * @return an instance of {@link AlertDialog}
     */
    public static AlertDialog show(Context context, String title, String message, String buttonText, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setCancelable(false).setPositiveButton(buttonText, listener);
        return builder.show();
    }

    /**
     * Generate and show a {@link AlertDialog} with one button
     * @param context context where the dialog should be generated
     * @param title dialog's title
     * @param message dialog's message
     * @param buttonText text shown on the button
     * @return an instance of {@link AlertDialog}
     */
    public static AlertDialog show(Context context, int title, int message, int buttonText, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setCancelable(false).setPositiveButton(buttonText, listener);
        return builder.show();
    }
}
