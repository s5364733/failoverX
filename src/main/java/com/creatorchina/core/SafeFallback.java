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

package com.creatorchina.core;


import com.creatorchina.json.JSONUtil;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Policy;
import net.jodah.failsafe.function.CheckedSupplier;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.concurrent.Executors.*;

/**
 * @author jack.liang
 * @date 2020-6-29
 */
public class SafeFallback<S, U> implements Failover<S, U> {

    Logger logger = Logger.getGlobal();

    public static ExecutorService executorService = newFixedThreadPool(10);

    /**
     * The retry strategy returns results asynchronously
     * @param supplier
     * @param policys
     * @return CompletableFuture<S>
     */
    @Override
    public CompletableFuture<S> doRenderAsync(CheckedSupplier supplier, Policy<U>... policys) {
        if (logger.isLoggable(Level.INFO)){
            logger.info("Current policy obj array is \t"+ JSONUtil.toJSONString(policys) + "\tcurrent method [doRenderAsync]");
        }
        return Failsafe.with(policys).with(executorService).getAsync(supplier);
    }

    /**
     * The retry strategy returns results asynchronously
     * @param supplier
     * @param policys
     * @return CompletableFuture<S>
     */
    @Override
    public CompletableFuture<S> doRenderSync(CheckedSupplier supplier, Policy<U>... policys) throws ExecutionException, InterruptedException {
        if (logger.isLoggable(Level.INFO)){
            logger.info("Current policy obj array is \t"+ JSONUtil.toJSONString(policys) + "\tcurrent method [doRenderSync]");
        }
        CompletableFuture<S> async = Failsafe.with(policys).with(executorService).getAsync(supplier);
        return CompletableFuture.completedFuture(async.get()) ;
    }
}

