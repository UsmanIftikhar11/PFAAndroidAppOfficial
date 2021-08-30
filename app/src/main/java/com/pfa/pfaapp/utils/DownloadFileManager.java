package com.pfa.pfaapp.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DownloadFileManager {

    private static boolean downloadInProgress = false;

    /**
     * download video from url
     *
     * @param urlMain    url of video
     * @param file       file to save video
     * @param pbListener progress listener
     */
    public static void downloadVideo(final String urlMain, final File file, final OnDownloadListener pbListener, final Context context) {

        if (downloadInProgress)
            return;
        new AsyncTask<String, Void, String>() {

            protected String doInBackground(String... params) {

                HttpURLConnection urlConnection = null;
                int contentLength = 0;

                try {

                    URL url = new URL(urlMain);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    List values = urlConnection.getHeaderFields().get("content-Length");
                    if (values != null && !values.isEmpty()) {

                        String sLength = (String) values.get(0);

                        if (sLength != null) {

                            try {
                                contentLength = Integer.parseInt(sLength);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    pbListener.onSetMax(contentLength);

                    InputStream is = new BufferedInputStream(urlConnection.getInputStream());

                    OutputStream os = new FileOutputStream(file);

                    copyStream(is, os, contentLength, pbListener);

                    is.close();
                    os.close();

                    return file.getAbsolutePath();

                } catch (Exception e) {

                    e.printStackTrace();

                } finally {

                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

                return null;
            }

            protected void onPostExecute(String path) {
                super.onPostExecute(path);

                if (pbListener != null) {

                    pbListener.onResponse(true, path);
                    scanFile(path, context);
                }
                downloadInProgress = false;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (pbListener != null) {
                    downloadInProgress = true;
                    pbListener.onStart();
                }
            }
        }.execute();
    }

    /**
     * download video from url
     *
     * @param urlMain    url of video
     * @param file       file to save video
     * @param pbListener progress listener
     */
    public static void downloadImage(final String urlMain, final File file, final OnDownloadListener pbListener, final Context context) {

        if (downloadInProgress)
            return;
        new AsyncTask<String, Void, String>() {

            protected String doInBackground(String... params) {

                HttpURLConnection urlConnection = null;
                int contentLength = 0;

                try {

                    URL url = new URL(urlMain);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    List values = urlConnection.getHeaderFields().get("content-Length");
                    if (values != null && !values.isEmpty()) {

                        String sLength = (String) values.get(0);

                        if (sLength != null) {

                            try {
                                contentLength = Integer.parseInt(sLength);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    pbListener.onSetMax(contentLength);

                    InputStream is = new BufferedInputStream(urlConnection.getInputStream());

                    OutputStream os = new FileOutputStream(file);

                    copyStream(is, os, contentLength, pbListener);

                    is.close();
                    os.close();

                    return file.getAbsolutePath();

                } catch (Exception e) {

                    e.printStackTrace();

                } finally {

                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

                return null;
            }

            protected void onPostExecute(String path) {
                super.onPostExecute(path);
                downloadInProgress = false;
                if (pbListener != null) {

                    pbListener.onResponse(true, path);

                    scanFile(path, context);
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (pbListener != null) {
                    downloadInProgress = true;
                    pbListener.onStart();
                }
            }
        }.execute();
    }

    /**
     * download progress listener
     */
    public interface OnDownloadListener {
        /**
         * start downloading
         */
        void onStart();

        /**
         * set size of file
         *
         * @param max size of file
         */
        void onSetMax(int max);

        /**
         * current progress of downloading
         *
         * @param current current progress [bytes downloaded]
         */
        void onProgress(int current);

        /**
         * download file finished
         */
        void onFinishDownload();

        /**
         * communication with server finished
         *
         * @param isSuccess is success
         * @param path      path of saved file
         */
        void onResponse(boolean isSuccess, String path);
    }


    /**
     * copy stream with progress listener
     *
     * @param is       input stream
     * @param os       output stream
     * @param length   length of stream
     * @param listener progress listener
     */
    private static void copyStream(InputStream is, OutputStream os, long length, DownloadFileManager.OnDownloadListener listener) {
        final int buffer_size = 1024;
        int totalLen = 0;
        try {

            byte[] bytes = new byte[buffer_size];
            while (true) {
                // Read byte from input stream

                int count = is.read(bytes, 0, buffer_size);
                if (count == -1) {
                    assert listener != null;
                    listener.onFinishDownload();
                    break;
                }

                // Write byte from output stream
                if (length != -1 && listener != null) {
                    totalLen = totalLen + count;
                    listener.onProgress(totalLen);
                }
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Scan Media files as the downloaded media should be shown in gallery
     *
     * @param file    Absolute path of downloaded file
     * @param context Activity context in which the current download async task is running
     */
    private static void scanFile(String file, Context context) {

        MediaScannerConnection.scanFile(context,
                new String[]{file}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });


    }
}