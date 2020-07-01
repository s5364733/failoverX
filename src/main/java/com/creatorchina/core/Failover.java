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

import com.creatorchina.util.NotNull;
import net.jodah.failsafe.Policy;
import net.jodah.failsafe.function.CheckedSupplier;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName: Failover
 * @Description: $
 * @Author: jack.liang
 * @Date: 2020/6/29 上午5:48
 **/
public interface Failover<S, U> {

    /**
     * Asynchronous rendering retry strategy
     *
     * @param supplier
     * @param policys
     * @return
     */
    CompletableFuture<S> doRenderAsync(@NotNull CheckedSupplier supplier, @NotNull Policy<U>... policys);

    /**
     * Synchronous rendering retry strategy
     *
     * @param supplier
     * @param policys
     * @return
     */
    CompletableFuture<S> doRenderSync(@NotNull CheckedSupplier supplier, @NotNull Policy<U>... policys) throws ExecutionException, InterruptedException;
}
