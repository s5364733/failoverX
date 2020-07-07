# failover-safe<br/>
### Description：<br/>

Super lightweight auxiliary fault-tolerance tool, almost keep 0 dependencies ，as much as possible, the rebuilt package is very small but very powerful, to achieve convenience and integration as much as possible, and we can customize our own business coupling.<br/>



### Technology<br/>
- Use spark as a microservice embedded web engine, with zero dependencies, small, concise, easy to deploy and other advantages
- Using unirest as a rest client, with high performance, fully asynchronous operation, rich data analysis and Tls operation, simple use and other characteristics
- Use failsafe as the bottom package to provide fault-tolerant processing in various modes, with small, flexible and rich features

### example:<br/>
```
   Unirest.config().socketTimeout(2000);
        Unirest.config().connectTimeout(2000);
        CompletableFuture<HttpResponse> future = FailoverCreator.defaultFailover((e) -> System.out.println("onFailedAttempt"),
                () -> Unirest.get(DEFAULT).asString(), null);

        if (future == null){
            System.out.println("null");
            return;
        }
```
>默认方法的使用请参考方法注释,该方法已满足绝大部分场景，如有需要，可以自己组装定制化容错，方法已注明详细注释