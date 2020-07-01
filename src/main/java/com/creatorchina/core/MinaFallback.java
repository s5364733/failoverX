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
import net.jodah.failsafe.Fallback;
import net.jodah.failsafe.function.CheckedConsumer;
import net.jodah.failsafe.internal.util.Assert;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * @ClassName: MinaFallback
 * @Description: $
 * @Author: jack.liang
 * @Date: 2020/6/29 下午9:16
 **/
public class MinaFallback<R> {

    private MinaFallback() {//Ignore constructor initializing
    }

    private static Optional<MinaFallback> MINA_FALLBACK = Optional.empty();

    private static final Object lock = new Object();

    public static Optional<Fallback> fallback = Optional.empty();


    public Fallback getDefaultFallback(){
        return fallback.orElseGet(null);
    }


    public static Optional<MinaFallback> triggerInitializeFallback() {
        if (!MINA_FALLBACK.isPresent()) {
            synchronized (lock) {
                if (!MINA_FALLBACK.isPresent()) {
                    MINA_FALLBACK = Optional.of(new MinaFallback());
                    if (!fallback.isPresent()) {
                        fallback = Optional.of(Fallback.of(() -> null));
                    }
                }
            }
        }
        return MINA_FALLBACK;
    }

    /**
     * <BR><P>determine which execution results or failures to handle and how to handle them. By default,
     * policies handle any Exception that is thrown.<P/>
     * <P>But policies can also be configured to handle more specific failures or conditions<P/>
     * <BR/>
     * policy
     *
     * @param failures Specify the exception to be handled
     * @return
     */
    public MinaFallback handle(@NotNull Class<? extends Throwable>... failures) {
        Assert.notNull(fallback, "Current fallback is non null~~");
        fallback.get().handle(failures);
        return this;
    }

    /**
     * <p>Specify that the conditions are met to trigger fallback<p/>
     *
     * @param failurePredicate
     * @return
     */
    public MinaFallback handleIf(@NotNull Predicate<? extends Throwable> failurePredicate) {
        Assert.notNull(fallback, "Current fallback is non null~~");
        fallback.get().handleIf(failurePredicate);
        return this;
    }

    /**
     * <PRE>fallback.onFailure(e -> log.error("Failed to connect to backup", e.getFailure()));<PRE/>
     * <BR>When the fallback attempt failed:
      * @see #onFailure(CheckedConsumer)
     * @param
     * @return MinaFallback
     * <BR/>
     */
    public MinaFallback onFailure(@NotNull CheckedConsumer listener) {
        Assert.notNull(fallback, "Current fallback is non null~~");
        fallback.get().onFailure(listener);
        return this;
    }

    /**
     * Specify the return result in case of retry failure
     * <pre>
     * policy
     *   .handleResult(null)
     *   .handleResultIf(result -> result == null);
     *     <pre/>
     * @param result
     * @return
     */
    public MinaFallback handleResult(@NotNull Object result) {
        Assert.notNull(fallback, "Current fallback is non null~~");
        fallback.get().handleResult(result);
        return this;
    }


    /**
     * <BR>They can also be configured to handle specific results or result conditions:<BR/>
     * <p>Trigger fallback when the condition is resolved<p/>
     * @param resultPredicate Condition for trigger
     * @return
     */
    public MinaFallback handleResultIf(@NotNull Predicate<R> resultPredicate) {
        Assert.notNull(fallback, "Current fallback is non null~~");
        fallback.get().handleResultIf(resultPredicate);
        return this;
    }
}
