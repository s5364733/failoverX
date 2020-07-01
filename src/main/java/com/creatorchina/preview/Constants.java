/**
 * The MIT License
 *
 * Copyright for portions of failover-safe are held by creatorchina Inc (c) 2020.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.creatorchina.preview;

import java.time.temporal.ChronoUnit;

/**
 * @author jack
 * @date 2020-6-28
 */
public interface Constants {

    int SUCCESS_THRESHOLD = 3;

    int SUCCESS_THRESHOLDING_CAPACITY = 5;

    int CIRCUIT_BREAKER_DURTION = 1;

    int WITH_BACKOFF_DELAY = 2000;

    int WITH_BACKOFF_MAX_DELAY = 1000_0;

    int WITH_MAX_ATTEMPTS = 2 << 1;

    int DELAY_RETRY = 2 << 1;

    //------------------------------Time-ChronoUnit ---------------------------------\\

    ChronoUnit CHRONOUNIT_MILLIS = ChronoUnit.MILLIS;

    ChronoUnit CHRONOUNIT_SECONDS = ChronoUnit.SECONDS;

    ChronoUnit CHRONOUNIT_MINUTES = ChronoUnit.MINUTES;
}
