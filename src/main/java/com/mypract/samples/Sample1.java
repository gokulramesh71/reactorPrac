package com.mypract.samples;


import org.json.JSONObject;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Delayed;

@Component
public class Sample1 {
    public Mono<JSONObject> getObj(String name) throws InterruptedException {
        JSONObject ret = new JSONObject();
        ret.put("name", name);
        ret.put("age", "26");
        ret.put("dob", "12-03-1993");
        /*Thread.sleep(10000);*/
        /*Mono<JSONObject> resp = Mono.just(ret).log();*/
        /*Thread.sleep(5000);*/

        Mono<JSONObject> mo = Mono.just(ret).delayElement(Duration.ofSeconds(10));
        /*mo.subscribe(new Subscriber<Object>() {
            Object name;
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("Subscription :" + s);
            }

            @Override
            public void onNext(Object o) {
                System.out.println(" Object ::" + o.toString());
                *//*name = ((JSONObject) o).get("name") + "Ramesh";*//*
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error Occured");
            }

            @Override
            public void onComplete() {

            }
        });*/
        return mo.log();
    }

    /*public JSONObject getObj(final String name) throws InterruptedException {
        JSONObject ret = new JSONObject();
        ret.put("name", name);
        ret.put("age", "26");
        ret.put("dob", "12-03-1993");
        Thread.sleep(10000);
        return ret;
    }*/

    public Mono<String> getOther() {
        return Mono.just("Other").log();
    }
}
