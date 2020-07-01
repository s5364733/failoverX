# failover-safe<br/>
### Description：<br/>

Super lightweight auxiliary fault-tolerance tool, almost keep 0 dependencies ，as much as possible, the rebuilt package is very small but very powerful, to achieve convenience and integration as much as possible, and we can customize our own business coupling.<br/>



### Technology<br/>
- spark microservice framework
- unirest http client
- failsafe No dependency package plugin

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