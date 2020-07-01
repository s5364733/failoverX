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
import net.jodah.failsafe.function.CheckedConsumer;
import net.jodah.failsafe.function.CheckedSupplier;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static com.creatorchina.preview.Constants.DELAY_RETRY;
import static com.creatorchina.preview.Constants.WITH_MAX_ATTEMPTS;


/**
 * <p>This class is a tool method and cannot be inherited<p/>
 *
 * @ClassName: FailoverCreator
 * @Description: $
 * @Author: jack.liang
 * @Date: 2020/6/30 上午3:02
 **/
final public class FailoverCreator {

    private static FailoverRegister register = FailoverRegister.instance;

    /**
     * <BR>This method is the default constructor, simple functions and I help you achieve<BR/>
     * <PRE>The functions are as follows:<PRE/>
     * <P>
     * A failed request will catch all network anomalies, and each retry will take two seconds.
     * A total of six retry attempts will be made. On six unsuccessful attempts,
     * the onFailure method will be called.and the default will return null.The entire process is asynchronous.
     * <P/>
     * <p>You can get a CompletableFuture object for processing And packaging<p/>
     * example : <code>
     *      Unirest.config().socketTimeout(2000);
     *         Unirest.config().connectTimeout(2000);
     *
     *         CompletableFuture<HttpResponse> future = FailoverRegister.
     *                 instance.
     *                 buildPolicy()
     *                 .onFailedAttempt((e) -> System.out.println("onFailedAttempt"))
     *                 .withMaxAttempts(6)
     *                 .withDelay(Duration.ofSeconds(2)).
     *                         onFailure((e) -> System.out.println("onFailure")).
     *                         assemblyFailover(() -> Unirest.get(FAILOVER).asString(),true);
     *
     *         HttpResponse httpResponse = future.get();
     *         if (httpResponse == null){
     *             System.out.println("null");
     *             return;
     *         }
     *         Assert.assertNotNull(httpResponse.getBody());
     *         Assert.assertEquals(httpResponse.getBody(), "register");
     *
     *     <code/>
     * @param listener Event listener for every failed attempt
     * @param invoke   You need a fault-tolerant method
     * @param isAsync  Whether to get the return value of the method asynchronously.
     *                By default, null  is synchronous and true & nonnull is asynchronous
     * @param <S>
     * @param <U>
     * @return
     */
    public static <S, U> CompletableFuture<S> defaultFailover(@NotNull CheckedConsumer listener,
                                                              @NotNull CheckedSupplier<U> invoke,
                                                              Boolean isAsync) {
      return register.
                buildPolicy()
                .onFailedAttempt(listener)
                .withMaxAttempts(WITH_MAX_ATTEMPTS)
                .withDelay(Duration.ofSeconds(DELAY_RETRY)).
                        assemblyFailover(invoke,isAsync);
    }


}
