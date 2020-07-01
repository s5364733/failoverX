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

package BehaviorTests.get;

import com.creatorchina.preview.Constants;
import net.jodah.failsafe.*;
import net.jodah.failsafe.event.ExecutionCompletedEvent;
import net.jodah.failsafe.function.CheckedConsumer;
import net.jodah.failsafe.function.CheckedSupplier;
import java.net.SocketException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.*;

/**
 * <pre>
 * Failsafe.with(fallback, retryPolicy, circuitBreaker, timeout).get(supplier);
 * <pre/>
 * @author jack
 * @date 2020-6-15
 */
public class FailsafeFactory<S> {

    public static final FailsafeFactory INSTANCE = new FailsafeFactory();

    private static ExecutorService executorService = newFixedThreadPool(2);

    public CompletableFuture<S> createDefaultPolicy1(CheckedSupplier<S> method){
        RetryPolicy retryPolicy = new RetryPolicy();
        retryPolicy.handleIf(throwable -> {
            System.out.println(((Exception)throwable).getMessage());
            return true; /*ifhandle decides whether he will go the following process*/
        });
        retryPolicy.
                withBackoff(Constants.WITH_BACKOFF_DELAY,
                Constants.WITH_BACKOFF_MAX_DELAY,
                Constants.CHRONOUNIT_MILLIS);
        retryPolicy
                .onFailedAttempt(e -> System.out.println("maximum number of auto retries reached"))
                .onRetry(e -> System.out.println("Failure #{}. Retrying.")).
                withMaxAttempts(Constants.WITH_MAX_ATTEMPTS).onFailure(new CheckedConsumer<ExecutionCompletedEvent>() {
            @Override
            public void accept(ExecutionCompletedEvent executionCompletedEvent) throws Throwable {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println(executionCompletedEvent.getResult().toString());
                System.out.println(executionCompletedEvent.getFailure().getMessage());
            }
        });

        /*Customizing broker*/
        return Failsafe.with(Fallback.of(() -> null),retryPolicy).
                with(executorService).getAsync(method);
                ///*阻塞获得结果*/.get()
    }



    public CompletableFuture<S>  createDefaultPolicyAbort(CheckedSupplier<S> method){
        RetryPolicy retryPolicy = new RetryPolicy();
        retryPolicy
                .abortWhen(true)
                .abortOn(SocketException.class)
                .withBackoff(Constants.WITH_BACKOFF_DELAY,
                        Constants.WITH_BACKOFF_MAX_DELAY,
                        Constants.CHRONOUNIT_MILLIS);

        /*Customizing broker*/
        return Failsafe.with(retryPolicy).
                with(executorService).getAsync(method);
    }


    public CompletableFuture<S>  createDefaultPolicyFallback(CheckedSupplier<S> method){
        //断路器主要用于非失败敏感型场合,失败多次之后复原的可能性比较大
//        Assert.notNull(method,"Input method is not null ~~");
//        CircuitBreaker circuitBreaker = new CircuitBreaker<>();
//        circuitBreaker.withSuccessThreshold(Constants.SUCCESS_THRESHOLD, Constants.SUCCESS_THRESHOLDING_CAPACITY);
//        circuitBreaker.withDelay(Duration.ofSeconds(15))
//                .onOpen(() -> System.out.println("The circuit breaker was opened"))
//                .onClose(() -> System.out.println("The circuit breaker was closed"))
//                .onHalfOpen(() -> System.out.println("The circuit breaker was half-opened"));

//        circuitBreaker.withFailureThreshold(5,10);

//        RetryPolicy retryPolicy = new RetryPolicy();
//        retryPolicy.
//                withMaxAttempts(3).
//                withDelay(Duration.ofSeconds(1))
        Fallback<Object> fallback = Fallback.of(() -> "backup");

        /*Customizing broker*/
        return Failsafe.with(fallback).
                with(executorService).getAsync(method);
    }


    public CompletableFuture<S>  createDefaultPolicyTimeOut(CheckedSupplier<S> method){
        //断路器主要用于非失败敏感型场合,失败多次之后复原的可能性比较大
        Timeout<Object> timeout = Timeout.of(Duration.ofSeconds(2));
        RetryPolicy retryPolicy = new RetryPolicy();
        retryPolicy.
                withMaxAttempts(3).
                withDelay(Duration.ofSeconds(1))
                .onRetry((e) -> System.out.println("retry ==> \t" + e.toString()));

        /*Customizing broker*/
        return Failsafe.with(retryPolicy,timeout).
                with(executorService).getAsync(method);
    }

    public  static String connectBackUp(){
        return "backup";
    }


    public CompletableFuture<S>  createDefaultPolicyJitter(CheckedSupplier<S> method){
        RetryPolicy retryPolicy = new RetryPolicy();

        retryPolicy.
                withMaxAttempts(8).
                withDelay(Duration.ofSeconds(2)).
                onRetry((e) -> System.out.println("retry ==> " + e.toString()));

        /*Customizing broker*/
        return Failsafe.with(retryPolicy).
                with(executorService).getAsync(method);
    }
}
