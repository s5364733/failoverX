/**
 * The MIT License
 * <p>
 * Copyright for portions of failover-safe are held by creatorchina Inc (c) 2020.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p>
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
import net.jodah.failsafe.function.CheckedFunction;
import net.jodah.failsafe.function.CheckedRunnable;
import net.jodah.failsafe.function.CheckedSupplier;
import net.jodah.failsafe.internal.util.Assert;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * @ClassName: MinaFallback
 * @Description: $
 * @Author: jack.liang
 * @Date: 2020/6/29 下午9:16
 **/
final public class MinaFallback<R> {

    private Optional<Fallback> fallback;

    static <T, R> CheckedFunction<T, R> toFn(CheckedSupplier<R> supplier) {
        return (t) -> supplier.get();
    }

    private MinaFallback(CheckedFunction supplier) {//Ignore constructor initializing
        fallback = Optional.of(Fallback.of(supplier));
    }

    public Fallback<R> fetchFallback() {
        return fallback.orElseThrow(() -> new InitialFallbackException(
                MinaFallback.class.getName() +
                        "\t Current fallback is not null"));
    }


    public static <R> MinaFallback of(CheckedSupplier<? extends R> checkedSupplier) {
        return new MinaFallback(toFn(checkedSupplier));
    }


    public static <T, R> MinaFallback ofException(CheckedFunction<T, R> checkedSupplier) {
        return new MinaFallback(checkedSupplier);
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
     *
     * @param
     * @return MinaFallback
     * <BR/>
     * @see #onFailure(CheckedConsumer)
     */
    public MinaFallback onFailure(@NotNull CheckedConsumer listener) {
        Assert.notNull(fallback, "Current fallback is non null~~");
        fallback.get().onFailure(listener);
        return this;
    }

    /**
     * <BR>Specify the return result in case of retry failure<BR/>
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
     *
     * @param resultPredicate Condition for trigger
     * @return
     */
    public MinaFallback handleResultIf(@NotNull Predicate<R> resultPredicate) {
        Assert.notNull(fallback, "Current fallback is non null~~");
        fallback.get().handleResultIf(resultPredicate);
        return this;
    }
}
