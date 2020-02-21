package com.mypract.samples;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class Sample1 {

    private ThreadLocal threadLocal = new ThreadLocal();

    public Mono<JSONObject> getObj1(JSONObject name) throws InterruptedException {
        System.out.println(123);
        JSONObject ret = new JSONObject();
        ret.put("firstName", name.optString("firstName"));
        ret.put("age", "26");
        ret.put("dob", "12-03-1993");
        System.out.println("7");
        /*Mono<JSONObject> resp = Mono.just(ret).log();*/
        /*Thread.sleep(5000);*/

        Mono<JSONObject> mo = Mono.just(ret).map(i -> innFunc(i, name)).delayElement(Duration.ofSeconds(5));
        /*Thread.sleep(5000);*/
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
        return mo;
    }

    public Flux<JSONObject> getObj(String name) {
        System.out.println("input name :" + name);
        JSONObject ret = new JSONObject();
        ret.put("firstName", name);
        ret.put("age", "26");
        ret.put("dob", "12-03-1993");

        /*return mo.delayElement(Duration.ofSeconds(4));*/
        return Flux.just(ret);
    }

    public Mono<String> getOther() {
        return Mono.just("Other").log();
    }

    public Flux<String> generate() {
        Flux<String> flux = Flux.generate(
                AtomicLong::new,
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    sink.next("3 x " + i + " = " + 3 * i);
                    if (i == 10) sink.complete();
                    return state;
                }, (state) -> System.out.println("state: " + state));
        return flux;
    }

    public Flux<String> publishOn() {
        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);

        final Flux<String> flux = Flux
                .range(1, 4)
                .map(i -> 10 + i)
//                .publishOn(Schedulers.parallel())
                .map(i -> "value " + i);

        /*new Thread(() -> flux.subscribe(System.out::println));*/
        return flux.log();
    }

    public Mono<Object> someFunc(Object o) {
        Object res = o + " Ramesh";
        return Mono.just(res).delayElement(Duration.ofSeconds(2));
    }

    public Mono<Object> someFunc2(Object o) {
        Object res = o + " Ramesh Test";
        return Mono.just(res);
    }

    public Object getSome() {
        return "I'm a test object, i can do whatever";
    }

    public Mono<Object> checkInnerMono() throws InterruptedException {
        Mono<Object> res = Mono.just(getSome()).delayElement(Duration.ofSeconds(3));
        return res;
    }

    public boolean checkName(String name) {
        return name.contains("G");
    }

    public String modifyName(String name) {
        return name.toUpperCase() + " Ramesh";
    }

    public String blockCode(Mono<Object> inp) {
        AtomicReference<String> input = null;
        inp.subscribe(i -> {
            input.set(String.valueOf(i));
        });
        return input + " Block concurrency Successful?";
    }

    public Mono<String> getName(String name) {
        return Mono.just(name).delayElement(Duration.ofSeconds(3)).log();
    }

    public Mono<JSONObject> createFlux() throws InterruptedException {
        try {
            Mono<JSONObject> resp1;
            Queue<Integer> q = new LinkedList<>();
            q.add(1);
            q.add(2);
            q.add(3);
            int qSize = q.size();
            AtomicReference<String> name = new AtomicReference<>("0Gokul");
            AtomicReference<JSONObject> obj = new AtomicReference<>();
            resp1 = createTempMono(name.get()).flatMap(i -> {
                if (((String) threadLocal.get()).equalsIgnoreCase("1Gokul")) {
                    name.set("2Gokul");
                    System.out.println("2 : " + i);
                    return q.isEmpty() ? Mono.just(new JSONObject().put("firstName", threadLocal.get())) : Mono.error(new RuntimeException("Retry"));
                } else if (((String) threadLocal.get()).equalsIgnoreCase("2Gokul")) {
                    name.set("3Gokul");
                    System.out.println("2 : " + i);
                    return q.isEmpty() ? Mono.just(new JSONObject().put("firstName", threadLocal.get())) : Mono.error(new RuntimeException("Retry"));
                } else if (((String) threadLocal.get()).equalsIgnoreCase("3Gokul")) {
//                    name.set("3Gokul");
                    System.out.println("2 : " + i);
                    return Mono.just(new JSONObject().put("firstName", threadLocal.get()));
                } else if (i.optString("firstName").equalsIgnoreCase("0Gokul")) {
                    name.set("1Gokul");
                    System.out.println("1 : " + i);
                    return q.isEmpty() ? Mono.just(i) : Mono.error(new RuntimeException("Retry"));
                } else {
                    return Mono.just(i);
                }
            }).retryWhen(companion -> companion
                            .zipWith(Flux.range(1, q.size()), (error, index) -> {
                                if (error != null) {
                                    System.out.println("eeeee ::" + error);
                                    if (q.isEmpty()) {
                                        System.out.println("did ??");
                                        throw Exceptions.propagate(error);
                                    }
                                    return q.remove();
                                } else {
                                    System.out.println(" errrrr :: " + error);
//                            throw Exceptions.propagate(error);
                                    return 100;
                                }
                            })
                            .flatMap(index -> Mono.delay(Duration.ofMillis(index * 100))
//                .doOnNext(s -> System.out.println("oooppp " + LocalTime.now()))
                                    .doOnNext(s -> createTempMono(name.get())))
            );
            /*.retryWhen(Retry.any()
                    .fixedBackoff(Duration.ofSeconds(q.remove()))
                    .retryMax(q.size()))
            .doOnNext(s -> createTempMono(name.get()))
            .onErrorResume(i -> {
               return Mono.just(obj.get());
            });*/

            return resp1.log();
        } catch (Exception e) {
            System.out.println("eeoeoeoeoeo");
            return Mono.just(new JSONObject(e.getCause()));
        }
    }

    public Mono<JSONObject> createTempMono(String name) {
        /*long rand = Math.round(Math.random());
        System.out.println("random : " + rand);
        String name = rand % 2 == 0 ? nm : "12Gokul";*/
        System.out.println("name  ::" + name);
        JSONObject t = new JSONObject();
        t.put("firstName", name);

//        threadLocal.set(name);

        return Mono.just(t);
    }

    public Flux<Long> checkRep(JSONObject inp) {
        System.out.println("input" + inp);
        if (inp.optString("firstName").equalsIgnoreCase("0Gokul")) {
            System.out.println("hi1" + inp.toString());
            return Flux.just(2L);
        } else {
            System.out.println("hi2");
            return Flux.just(0L);
        }
    }

    public JSONObject innFunc(JSONObject obj, JSONObject ctx) {
        System.out.println("8");
        System.out.println("ctx1" + ctx);
        if (obj.optString("firstName").isEmpty()) {
            ctx.put("firstName", "1Gokul");
        } else {
            ctx.put("firstName", "2Gokul");
        }
        return obj;
    }

    public Flux<String> createFlux1() throws InterruptedException {
        List<Mono<String>> resp1 = new ArrayList<>();
        JSONObject nameObj = new JSONObject();
        nameObj.put("firstName", "Gokul");
        System.out.println("4");
        for (int m = 0; m < 2; m++) {
            resp1.add(getObj1(nameObj).map( i -> {
                System.out.println("777");
                return i.toString();
            }));
        }
        System.out.println("6");
        return Flux.concat(resp1);
    }

    public Flux<String> fluxTest1() {
        Flux<String> flux = Flux.generate(
                AtomicLong::new,
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    sink.next("3 x " + i + " = " + 3 * i);
                    if (i == 10) sink.complete();
                    return state;
                }, (state) -> System.out.println("state: " + state));
        return flux;
    }

    public Flux<String> test1() {
        Map<String, String> input = new HashMap<>();
        input.put("name", "1Gokul");
        AtomicInteger cnt = new AtomicInteger(0);
        Flux<String> resp = null;
        resp = getObj(input.get("name"))
                .expand(i -> {
                    if (i.optString("firstName").contains("4")) {
                        return Mono.empty();
                    } else {
                        return getObj(i.optString("firstName") + cnt.getAndIncrement());
                    }
                })
                .map(JSONObject::toString);
        return resp.log();
    }

    public Mono<JSONObject> createTempMono1() {
        long rand = Math.round(Math.random());
        System.out.println("random num : " + rand);
        if (rand % 2 == 0) {
            System.out.println("1");
            return Mono.error(new RuntimeException());
        } else if ((rand + 3) % 3 == 0) {
            System.out.println("2");
            return Mono.error(new RuntimeException());
        }
        System.out.println("0");
        return Mono.just(new JSONObject().put("status", "SUCCESS"));
    }

    public Mono<JSONObject> retryCheck() {
        Queue<Integer> q = new LinkedList<>();
        q.add(1);
        q.add(3);
        q.add(5);
        AtomicReference<String> nameRef = new AtomicReference<>("0Gokul");
        return Mono.defer(this::createTempMono1)
                .retryWhen((companion -> companion
                        .zipWith(Flux.range(1, q.size()), (error, index) -> {
                            if (error != null) {
                                System.out.println("hi");
                                if (q.isEmpty()) {
                                    throw Exceptions.propagate(error);
                                }
                                return q.remove();
                            } else {
                                return 100;
                            }
                        })
                        .flatMap(index -> Mono.delay(Duration.ofMillis(index * 1000))
                                )
//                        .doOnNext(s -> createTempMono1())
                ));
    }

    public Flux<JSONObject> parallelPro() {
        return Flux.range(1, 10)
                .parallel().runOn(Schedulers.parallel()).log().flatMap(i -> createTempMono(i.toString())).sequential();
    }


}
