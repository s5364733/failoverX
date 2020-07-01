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
import net.jodah.failsafe.function.CheckedConsumer;
import net.jodah.failsafe.function.CheckedSupplier;
import net.jodah.failsafe.function.DelayFunction;
import net.jodah.failsafe.internal.util.Assert;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Predicate;

/**
 * @author jack.liang
 * @date 2020-6-29
 */
public class FailoverRegister<S, R, U> extends AbstractFailoverPolicy<S, R, U> {

    public static FailoverRegister instance = FallbackRegisterInstance.FALLBACK_REGISTER_INSTANCE;

    @Override
    List<Policy<R>> doCollect(Policy<R>... t) {
        this.policyCollects.addAll(Arrays.asList(t));
        return policyCollects;
    }


    static class FallbackRegisterInstance {
        private static final FailoverRegister FALLBACK_REGISTER_INSTANCE = new FailoverRegister();
    }

    public FailoverRegister buildPolicy() {
        this.retryPolicy = new RetryPolicy();
        this.safeFallback = new SafeFallback();
        this.fallback = MinaFallback.triggerInitializeFallback().orElseThrow(() -> new InitialFallbackException());
        this.collect(fallback.getDefaultFallback(),retryPolicy);
        return this;
    }

    public FailoverRegister buildFallback() {
        Assert.notNull(retryPolicy, "Please do not create objects repeatedly");
        return this;
    }

    /**
     * <BR>Only one delay time strategy is currently supported<BR/>
     *
     * @param delay time
     * @return
     */
    public FailoverRegister withDelay(@NotNull Duration delay) {
        Assert.notNull(retryPolicy, "Please init this policy~ == > build");
        Assert.notNull(delay, "Delay is not null ");
        retryPolicy.withDelay(delay);
        return this;
    }


    /**
     * <BR>Define the delay time interval and unit<BR/>
     *
     * @param delayMin   min delay time
     * @param delayMax   max delay time
     * @param chronoUnit time unit
     * @return current core object
     */
    public FailoverRegister withDelay(long delayMin, long delayMax, @NotNull ChronoUnit chronoUnit) {
        Assert.notNull(retryPolicy, "Please init this policy~ == > build");
        Assert.notNull(delayMin, "DelayMin is not null ");
        Assert.notNull(delayMax, "DelayMax is not null ");
        Assert.notNull(chronoUnit, "Time unit is not null ");
        retryPolicy.withDelay(delayMin, delayMax, chronoUnit);
        return this;
    }


    /**
     * <BR>Delay method processing<BR/>
     *
     * @param
     * @return
     */
    @Deprecated
    public FailoverRegister withDelay(@NotNull DelayFunction delayFunction) {
        Assert.notNull(retryPolicy, "Please init this policy~ == > build");
        Assert.notNull(delayFunction, "Time unit is not null ");
        retryPolicy.withDelay(delayFunction);
        return this;
    }


    /**
     * <BR>Delayed events trigger listeners to listen to event objects<BR/>
     * CheckedConsumer
     * <BR>example:<BR/>
     * <code>
     * retryPolicy.onRetry(new CheckedConsumer<ExecutionAttemptedEvent>() {
     *
     * @param listener
     * @return
     * @Override public void accept(ExecutionAttemptedEvent executionAttemptedEvent) throws Throwable {
     * //ignore
     * }
     * });
     * <code/>
     */
    public FailoverRegister onRetry(@NotNull CheckedConsumer listener) {
        Assert.notNull(retryPolicy, "Please init this policy~ == > build");
        retryPolicy.onRetry(listener);
        return this;
    }

    /**
     * Copy all current object properties to new object
     *
     * @return
     */
    public RetryPolicy copy() {
        Assert.notNull(retryPolicy, "Please init this policy~ == > build");
        return this.retryPolicy.copy();
    }

    /**
     * Copy all current object properties to new object
     *
     * @return
     */
    public FailoverRegister withMaxAttempts(int maxAttempts) {
        Assert.notNull(retryPolicy, "Please init this policy~ == > build");
        Assert.isTrue(maxAttempts > 0, "MaxAttempts must > 0 ~");
        this.retryPolicy.withMaxAttempts(maxAttempts);
        return this;
    }

    /**
     * <BR>Consumption processing after retry failure<BR/>
     *
     * @param listener
     * @return this
     * @see #onRetry(CheckedConsumer)
     */
    public FailoverRegister onFailure(@NotNull CheckedConsumer listener) {
        Assert.notNull(retryPolicy, "Please init this policy~ == > build");
        Assert.notNull(listener, "Retry consumer is non null ");
        this.retryPolicy.onFailure(listener);
        return this;
    }

    /**
     * <BR>Registers the listener to be called when an execution attempt fails.
     * You can also use onFailure ,
     *
     * @param Event listener
     * @return this
     * @see #onFailure(CheckedConsumer)
     * @see #onRetry(CheckedConsumer)
     * to determine when the execution attempt fails and and all retries have failed.
     * <Strong>Note:<Strong/><br/>
     * <p>Any exceptions that are thrown from within the listener are ignored.<p/>
     * <BR/>
     * @see #onRetry(CheckedConsumer)
     */
    public FailoverRegister onFailedAttempt(@NotNull CheckedConsumer listener) {
        Assert.notNull(retryPolicy, "Please init this policy~ == > build");
        Assert.notNull(listener, "Retry consumer is non null ");
        this.retryPolicy.onFailedAttempt(listener);
        return this;
    }


    /**
     * <BR>Retry only when encountering the specified exception<BR/>
     *
     * @param Event listener
     * @return this
     * @see #onRetry(CheckedConsumer)
     */
    public FailoverRegister handle(@NotNull Class<? extends Throwable>... failures) {
        Assert.notNull(failures, "Input's throwable is non null");
        this.retryPolicy.handle(failures);
        return this;
    }


    /**
     * <BR>Retry only when encountering the specified Predicate is true<BR/>
     *
     * @param Event listener
     * @return this
     * @see #onRetry(CheckedConsumer)
     */
    public FailoverRegister handleIf(Predicate<? extends Throwable> predicate) {
        Assert.notNull(predicate, "Input's predicate is non null");
        this.retryPolicy.handleIf(predicate);
        return this;
    }


    /**
     * <BR>
     * Use the exponential backoff algorithm to handle the retry strategy.
     * It should be noted that after applying this strategy,
     * there is no need to customize the delay time.such as follows:
     * <BR/>
     * <p>
     * For example follows:
     *
     * @param Event listener
     * @return this
     * @see #withDelay(Duration)
     * @see #withDelay(long, long, ChronoUnit)
     * @see #withDelay(DelayFunction)
     * @see #onRetry(CheckedConsumer)
     */
    public FailoverRegister withBackoff(long delay, long maxDelay, @NotNull ChronoUnit chronoUnit) {
        this.retryPolicy.withBackoff(delay, maxDelay, chronoUnit);
        return this;
    }


    /**
     * <BR>
     * Use the exponential backoff algorithm to handle the retry strategy.
     * It should be noted that after applying this strategy,
     * there is no need to customize the delay time.such as follows:
     * <BR/>
     * <p>
     * For example follows:
     *
     * @param Event listener
     * @return this
     * @see #withDelay(Duration)
     * @see #withDelay(long, long, ChronoUnit)
     * @see #withDelay(DelayFunction)
     * @see #onRetry(CheckedConsumer)
     */
    public FailoverRegister withBackoff(long delay, long maxDelay, @NotNull ChronoUnit chronoUnit, double factor) {
        Assert.notNull(retryPolicy, "Please init this policy~ == > build");
        this.retryPolicy.withBackoff(delay, maxDelay, chronoUnit, factor);
        return this;
    }


    /**
     *
     * @param invoke  The method you need to pass in
     * @param isAsync  Specify whether to execute asynchronously.
     *                 In fact, the bottom layer is also used for asynchronous execution,
     *                but returns in a synchronous manner.
     * @return
     */
    public CompletableFuture<S> assemblyFailover(CheckedSupplier<U> invoke,Boolean isAsync){
      return this.registerFailoverHandler(invoke, isAsync == null ? false : isAsync);
    }
}





