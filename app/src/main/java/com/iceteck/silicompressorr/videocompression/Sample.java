/*
 * This is the source code of Telegram for Android v. 1.7.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2014.
 */

package com.iceteck.silicompressorr.videocompression;

public class Sample {
    private long offset;
    private long size;

    Sample(long offset, long size) {
        this.offset = offset;
        this.size = size;
    }

    long getOffset() {
        return offset;
    }

    public long getSize() {
        return size;
    }
}
