/*
 * Copyright (c) 2010, Sony Ericsson Mobile Communication AB. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    * Redistributions of source code must retain the above copyright notice, this
 *      list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation
 *      and/or other materials provided with the distribution.
 *    * Neither the name of the Sony Ericsson Mobile Communication AB nor the names
 *      of its contributors may be used to endorse or promote products derived from
 *      this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.pfa.pfaapp.utils;

import android.content.Context;

/**
 * Class containing static utility methods for bitmap decoding and scaling
 *
 * @author Andreas Agvard (andreas.agvard@sonyericsson.com)
 */
public class ScalingUtilities extends SharedPrefUtils {

    public ScalingUtilities(Context activity) {
        super(activity);
    }

    /*
     * Utility function for decoding an image resource. The decoded bitmap will
     * be optimized for further scaling to the requested destination dimensions
     * and scaling logic.
     *
     * @param res          The resources object containing the image data
     * @param resId        The resource id of the image data
     * @param dstWidth     Width of destination area
     * @param dstHeight    Height of destination area
     * @param scalingLogic Logic to use to avoid image stretching
     * @return Decoded bitmap
     */
//    private static Bitmap decodeResource(Resources res, int resId, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
//        Options options = new Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(res, resId, options);
//        options.inJustDecodeBounds = false;
//        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth,
//                dstHeight, scalingLogic);
//
//        return BitmapFactory.decodeResource(res, resId, options);
//    }

    /*
     * Utility function for creating a scaled version of an existing bitmap
     *
     * @param unscaledBitmap Bitmap to scale
     * @param dstWidth       Wanted width of destination bitmap
     * @param dstHeight      Wanted height of destination bitmap
     * @param scalingLogic   Logic to use to avoid image stretching
     * @return New scaled bitmap object
     */
//    private static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight,ScalingLogic scalingLogic) {
//        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
//                dstWidth, dstHeight, scalingLogic);
//        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
//                dstWidth, dstHeight, scalingLogic);
//        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(),
//                Config.ARGB_8888);
//        Canvas canvas = new Canvas(scaledBitmap);
//        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
//
//        return scaledBitmap;
//    }

    /*
     * ScalingLogic defines how scaling should be carried out if source and
     * destination image has different aspect ratio.
     * <p>
     * CROP: Scales the image the minimum amount while making sure that at least
     * one of the two dimensions fit inside the requested destination area.
     * Parts of the source image will be cropped to realize this.
     * <p>
     * FIT: Scales the image the minimum amount while making sure both
     * dimensions fit inside the requested destination area. The resulting
     * destination dimensions might be adjusted to a smaller size than
     * requested.
     */
//    public enum ScalingLogic {
//        CROP, FIT
//    }

    /*
     * Calculate optimal down-sampling factor given the dimensions of a source
     * image, the dimensions of a destination area and a scaling logic.
     *
     * @param srcWidth     Width of source image
     * @param srcHeight    Height of source image
     * @param dstWidth     Width of destination area
     * @param dstHeight    Height of destination area
     * @param scalingLogic Logic to use to avoid image stretching
     * @return Optimal down scaling sample size for decoding
     */
//    private static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
//        if (scalingLogic == ScalingLogic.FIT) {
//            final float srcAspect = (float) srcWidth / (float) srcHeight;
//            final float dstAspect = (float) dstWidth / (float) dstHeight;
//
//            if (srcAspect > dstAspect) {
//                return srcWidth / dstWidth;
//            } else {
//                return srcHeight / dstHeight;
//            }
//        } else {
//            final float srcAspect = (float) srcWidth / (float) srcHeight;
//            final float dstAspect = (float) dstWidth / (float) dstHeight;
//
//            if (srcAspect > dstAspect) {
//                return srcHeight / dstHeight;
//            } else {
//                return srcWidth / dstWidth;
//            }
//        }
//    }

    /*
     * Calculates source rectangle for scaling bitmap
     *
     * @param srcWidth     Width of source image
     * @param srcHeight    Height of source image
     * @param dstWidth     Width of destination area
     * @param dstHeight    Height of destination area
     * @param scalingLogic Logic to use to avoid image stretching
     * @return Optimal source rectangle
     */
//    private static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,ScalingLogic scalingLogic) {
//        if (scalingLogic == ScalingLogic.CROP) {
//            final float srcAspect = (float) srcWidth / (float) srcHeight;
//            final float dstAspect = (float) dstWidth / (float) dstHeight;
//
//            if (srcAspect > dstAspect) {
//                final int srcRectWidth = (int) (srcHeight * dstAspect);
//                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
//                return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
//            } else {
//                final int srcRectHeight = (int) (srcWidth / dstAspect);
//                final int scrRectTop = (srcHeight - srcRectHeight) / 2;
//                return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
//            }
//        } else {
//            return new Rect(0, 0, srcWidth, srcHeight);
//        }
//    }

    /*
     * Calculates destination rectangle for scaling bitmap
     *
//     * @param srcWidth     Width of source image
//     * @param srcHeight    Height of source image
//     * @param dstWidth     Width of destination area
//     * @param dstHeight    Height of destination area
//     * @param scalingLogic Logic to use to avoid image stretching
     * @return Optimal destination rectangle
     */
//    private static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,ScalingLogic scalingLogic) {
//        if (scalingLogic == ScalingLogic.FIT) {
//            final float srcAspect = (float) srcWidth / (float) srcHeight;
//            final float dstAspect = (float) dstWidth / (float) dstHeight;
//
//            if (srcAspect > dstAspect) {
//                return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));
//            } else {
//                return new Rect(0, 0, (int) (dstHeight * srcAspect), dstHeight);
//            }
//        } else {
//            return new Rect(0, 0, dstWidth, dstHeight);
//        }
//    }

//    File scaleImageFile(File file) {
//        // 1. Convert file to bitmap
//        Bitmap unscaledBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//
//        if (unscaledBitmap == null)
//            return null;
//        // Part 2: Scale bitmap image
//        Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, unscaledBitmap.getWidth() / 2,
//                unscaledBitmap.getHeight() / 2, ScalingLogic.FIT);
//        unscaledBitmap.recycle();
//
//        //3. convert scaled bitmap to file
////        File localFile = new File(mContext.getCacheDir(), ""+(System.currentTimeMillis())+".png");
////        File localFile = new File(mContext.getExternalCacheDir(), ""+(System.currentTimeMillis())+".png");
//        File localFile = new File(Environment.getExternalStorageDirectory() + "/pfaTemp", "" + (System.currentTimeMillis()) + ".png");
//
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60 /*ignored for PNG*/, bos);
//        byte[] bitmapData = bos.toByteArray();
//
////write the bytes in file
//        try {
//            FileOutputStream fos = new FileOutputStream(localFile);
//            fos.write(bitmapData);
//            fos.flush();
//            fos.close();
//            return localFile;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return file;
//
//    }


//    static Bitmap drawableToBitmap(Drawable drawable) {
//        if (drawable instanceof BitmapDrawable) {
//            return ((BitmapDrawable) drawable).getBitmap();
//        }
//
//        int width = drawable.getIntrinsicWidth();
//        width = width > 0 ? width : 1;
//        int height = drawable.getIntrinsicHeight();
//        height = height > 0 ? height : 1;
//
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//        drawable.draw(canvas);
//
//        return bitmap;
//    }

}
