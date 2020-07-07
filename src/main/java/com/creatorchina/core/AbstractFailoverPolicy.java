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
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.function.CheckedSupplier;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @ClassName: AbstractFailoverPolicy
 * @Description: $
 * @Author: jack.liang
 * @Date: 2020/6/29 下午10:28
 **/
public abstract class AbstractFailoverPolicy<S, R, U> implements Collect<List<Policy<R>>, Policy<R>> {

    Logger logger = Logger.getGlobal();
    protected List<Policy<R>> policyCollects = new ArrayList<>();
    protected RetryPolicy retryPolicy;
    protected SafeFailback safeFailback;
    protected MinaFallback minaFallback;

    abstract List<Policy<R>> doCollect(Policy<R>... t);

    /**
     * Core handle method for assemblyFailover
     * @param method
     * @return
     */
    CompletableFuture<S> registerFailoverHandler$(CheckedSupplier<U> method, boolean isAsync)  {
        if (isAsync){
            logger.log(Level.INFO,"Current is async method exec ......~");
            return this.registerFailoverHandlerMethodAsync(method);
        }else {
            try {
                logger.log(Level.INFO,"Current is sync method exec ......~");
                return  this.registerFailoverHandlerMethodSync(method);
            } catch (Exception e) {
                //ignore
                logger.log(Level.WARNING,"Current method execution failed" + e.getMessage());
                return CompletableFuture.completedFuture(null);
            }
        }
    }


    CompletableFuture<S> registerFailoverHandler(@NotNull CheckedSupplier<U> method,Boolean isAsync) {
       return this.registerFailoverHandler$(method,isAsync);
    }


    @Override
    public List<Policy<R>> collect(Policy<R>... t) {
        if (t != null && t.length > 0) {
            return doCollect(t);
        }
        return new ArrayList<>();
    }


    /**
     * @param method
     * @return
     */
    protected CompletableFuture<S> registerFailoverHandlerMethodAsync(
            @NotNull CheckedSupplier<U> method) {
        return this.safeFailback.doRenderAsync(method, policyCollects.toArray(new Policy[policyCollects.size()]));
    }


    /**
     * @param method
     * @return
     */
    protected CompletableFuture<S> registerFailoverHandlerMethodSync(
            @NotNull CheckedSupplier<U> method) throws Exception {
        return this.safeFailback.doRenderSync(method, policyCollects.toArray(new Policy[policyCollects.size()]));
    }


}
