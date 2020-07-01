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

import kong.unirest.HttpResponse;
import kong.unirest.StringResponse;
import kong.unirest.Unirest;
import org.junit.Assert;
import org.junit.Test;
import spark.Spark;
import java.net.ConnectException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import static BehaviorTests.get.MockServer.*;
import static junit.framework.TestCase.assertEquals;
import static spark.Spark.exception;
import static spark.Spark.get;

public class RestAsStringGetTest extends  BddTest{
    private static final FailsafeFactory failsafeFactory = FailsafeFactory.INSTANCE;
    private static long count  = 4L;

    static {
        get("/get", (req, res) -> "Hello World");
        get("/exception", (request, response) -> {
            Thread.sleep(60000);
            System.out.println("Current !!!!!");
            throw new java.net.ConnectException("server resouces error");
        });
        get("/option", (request, response) -> {
            if (count < 0L){
                return null;
            }else {
                Thread.sleep( count * 1000);
                System.out.println("current server wait time is = "+count * 1000);
            }
            count--;
            System.out.println("Option ~~~~~");
            return "option";
        });
        exception(ConnectException.class, (e, request, response) -> {
            response.status(500);
            response.body("Server resouces error");
        });
        Runtime.getRuntime().addShutdownHook(new Thread(Spark::stop));
        try {
            new CountDownLatch(1).await(4, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void canGetBinaryResponse()  {
        HttpResponse res = Unirest.get(GET).asString();
        assertEquals(200, res.getStatus());
        assertEquals("Hello World", res.getBody());
        assertEquals("text/html;charset=utf-8", res.getHeaders().getFirst("Content-Type"));
    }

    @Test
    public void testFailsafeFactoryDefault() throws ExecutionException, InterruptedException {
        Unirest.config().connectTimeout(2000);
        Unirest.config().socketTimeout(2000);
        CompletableFuture<HttpResponse> defaultPolicy = failsafeFactory.createDefaultPolicy1(() -> Unirest.get(EXCEPTION).asString());
        HttpResponse o = defaultPolicy.get();
//        assertEquals(o.getBody(), "Hello World");
    }


    @Test
    public void testFailsafeFactoryTimeout() throws ExecutionException, InterruptedException {
        CompletableFuture<HttpResponse> defaultPolicy = failsafeFactory.createDefaultPolicyTimeOut(() -> Unirest.get(EXCEPTION).asString());
        HttpResponse o = defaultPolicy.get();
        System.out.println(o.toString());
    }


    @Test
    public void testFailsafeFactoryjitter() throws ExecutionException, InterruptedException {
        Unirest.config().socketTimeout(2000);
        CompletableFuture<HttpResponse> defaultPolicy = failsafeFactory.createDefaultPolicyJitter(() -> Unirest.get(EXCEPTION).asString());
        HttpResponse o = defaultPolicy.get();
        System.out.println(((StringResponse)o).getBody());
    }

    /**
     * current server wait time is = 4000
     * Option ~~~~~
     * current server wait time is = 3000
     * Option ~~~~~
     * retry ==> ExecutionAttemptedEvent[result=null, failure=kong.unirest.UnirestException: java.net.SocketTimeoutException: Read timed out]
     * current server wait time is = 2000
     * Option ~~~~~
     * option
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testFailsafeFactoryOption() throws ExecutionException, InterruptedException {
        Unirest.config().socketTimeout(2000);
        CompletableFuture<HttpResponse> defaultPolicy = failsafeFactory.createDefaultPolicyJitter(() -> Unirest.get(OPTION).asString());
        HttpResponse o = defaultPolicy.get();
        System.out.println(((StringResponse)o).getBody());
    }



    @Test
    public void testFailsafeFactoryFallBack() throws ExecutionException, InterruptedException {
        CompletableFuture<?> defaultPolicy = failsafeFactory.createDefaultPolicyFallback(() -> Unirest.get(EXCEPTION).asString());
        Object o = defaultPolicy.get();
        Assert.assertEquals(o,"backup");
    }


}
