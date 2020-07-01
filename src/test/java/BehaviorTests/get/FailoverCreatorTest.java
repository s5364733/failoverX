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

import com.creatorchina.core.FailoverCreator;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import static BehaviorTests.get.MockServer.DEFAULT;
import static spark.Spark.get;

/**
 * @ClassName: FailoverCreatorTest
 * @Description: $
 * @Author: jack.liang
 * @Date: 2020/6/30 上午3:44
 **/
public class FailoverCreatorTest {

    static {
        get("/default/creator", (request, response) -> {
            Thread.sleep(100000);
            return "register";
        });
    }

    @Test
    public void failoverCreatorForSync() throws Exception {
        Unirest.config().socketTimeout(2000);
        Unirest.config().connectTimeout(2000);
        CompletableFuture<HttpResponse> future = FailoverCreator.defaultFailover((e) -> System.out.println("onFailedAttempt"),
                () -> Unirest.get(DEFAULT).asString(), null);

        if (future == null){
            System.out.println("null");
            return;
        }
        Assert.assertEquals(future.get()/*因为是同步封装的所以这里get的时候就已经拿到值了，无需阻塞*/.getBody(),"register");
    }


    @Test
    public void failoverCreatorForASync() throws Exception {
        Unirest.config().socketTimeout(2000);
        Unirest.config().connectTimeout(2000);
        CompletableFuture<HttpResponse> future =
                FailoverCreator.defaultFailover((e) -> System.out.println("onFailedAttempt"),
                () -> Unirest.get(DEFAULT).asString(),Boolean.TRUE);

        if (future == null){
            System.out.println("null");
            return;
        }
        Assert.assertEquals(future.get().getBody(),"register");
    }
}
