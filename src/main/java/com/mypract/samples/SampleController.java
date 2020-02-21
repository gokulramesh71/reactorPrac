package com.mypract.samples;

import org.json.JSONArray;
import org.json.JSONObject;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import sun.font.CreatedFontTracker;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class SampleController {

    @Autowired
    private Sample1 sample1;

    /*
     * @RequestMapping(path = "/getInfo", method = RequestMethod.GET) public
     * Flux<Object> getResponse(@RequestParam(value = "name") String name) throws
     * InterruptedException { System.out.println("Thread  " +
     * Thread.currentThread().getName() + " " + Thread.currentThread().getId());
     * JSONObject resp = sample1.getObj(name); System.out.println("response  " +
     * resp); return Flux.just(resp.toString()); }
     */

    @RequestMapping(path = "/getInfo", method = RequestMethod.GET)
    @ResponseBody
    public Mono<Object> getResponse(@RequestParam(value = "name") String name) throws InterruptedException {
        System.out.println("in time  " + new Date());
        /* Mono<Object> obj = sample1.getObj(name).map(i -> i.get("name")); */
        /*Mono<Object> resp = sample1.getObj(name).flatMap(i -> sample1.someFunc(i.get("name"))).map(i -> {
            if (i.toString().equalsIgnoreCase("Gokul Ramesh")) {
                return "1 Gokul";
            } else {
                return "2 Gokul";
            }
        });
        resp = resp.map(i -> {
            if (String.valueOf(i).equalsIgnoreCase("1 Gokul")) {
                return sample1.someFunc2(i);
            } else if (String.valueOf(i).equalsIgnoreCase("2 Gokul")) {
                return "inner 2 Gokul";
            } else {
                return "None Gokul";
            }
        });*/
        /*Mono<Object> fin = Mono.just(sample1.blockCode(resp));*/
        /* JSONObject res = new JSONObject().put("resp", resp); */
        /*
         * if (t.toString().equalsIgnoreCase("MonoDelayElement")) { return
         * Mono.just("1 Gokul"); } else { return Mono.just("2 Gokul"); }
         */
        // });
        /* resp.map(i -> sample1.someFunc(i)); */
        /* Object name1 = resp.block(); */
        /* String name1 = sample1.getObj(name).getString("name"); */
        System.out.println("Out time  :" + new Date());
        /* return Mono.just(sample1.getObj(name).get("name")).log(); */
        /* Mono<Object> resp = sample1.getObj(name).map(i -> i.get("name")); */

        /* return Mono.just(sample1.getObj(name).get("name")).log(); */
        return null;
    }

    @RequestMapping(path = "/generate", method = RequestMethod.GET)
    @ResponseBody
    public Flux<String> getOther() {
        return sample1.generate().log();
    }

    @RequestMapping(path = "/publishOn", method = RequestMethod.GET)
    @ResponseBody
    public Flux<String> getPub() {
        return sample1.publishOn();
    }

    @RequestMapping(path = "/innerMono", method = RequestMethod.GET)
    @ResponseBody
    public Mono<Object> checkInnerMono() throws InterruptedException {
        return sample1.checkInnerMono().log();
    }

    /*
     * @RequestMapping(path = "/retry", method = RequestMethod.GET)
     * 
     * @ResponseBody public Mono<Object> retry() throws InterruptedException { Long
     * name; Mono<Object> resp = sample1.getObj("Gokul") .flatMap(i -> { if
     * (sample1.checkName(i.optString("name"))) { return
     * Mono.just(sample1.modifyName(i.optString("name"))).log() .flatMap(t -> { if
     * (t.contains("g")) { return Mono.just(t); } return Mono.empty(); }); } return
     * Mono.just("Not Gokul"); }); resp.repeatWhenEmpty(2, o -> o.doOnNext(t -> {
     * this.name = t; })); return resp; }
     */

    @RequestMapping(path = "/sub", method = RequestMethod.GET)
    @ResponseBody
    public Flux<String> subscribeFlow() throws InterruptedException {
        JSONObject resp = new JSONObject();
        Mono<String> response = null;
        Map<String, Mono<String>> temp = new HashMap<>();
        Flux resp1 = sample1.createFlux1();
        /*
         * AtomicReference<String> resp2 = new AtomicReference<>(); return
         * (Mono<String>) sample1.getObj("Gokul").zipWith(sample1.getObj(" Ramesh"), (a,
         * b) -> a.opt("name") + b.optString("name"));
         *//*
//        for (int m = 0; m < 3; m++) {
            System.out.println(11);
            resp1 = Flux.merge(sample1.getObj(resp.optString("name")).map(i -> {
                if (i.optString("name").isEmpty()) {
                    resp.put("name", "Empty Gokul");
                } else {
                    resp.put("name", i.optString("name"));
                }
                System.out.println(12);
                return i;
            }));
//        }*/
        System.out.println(13);
        return resp1.log().delayElements(Duration.ofSeconds(30));
    }

    @RequestMapping(path = "/flux", method = RequestMethod.GET)
    @ResponseBody
    public Flux<String> fluxCheck() {
        Map<String, String> mp = new HashMap<>();
        Flux<String> inp = Flux.generate(() -> "1", (state, sink) -> {
                                            sample1.getObj(state).log().subscribe(i -> mp.put("resp", i.toString()), Throwable::printStackTrace);
                                            /*sink.next(mp.get("resp"));*/
                                            System.out.println("map val :" + mp);
                                            sink.next("ioio");
                                            /*sink.next(sample1.getObj(state).map(JSONObject::toString));*/
                                        System.out.println("state :" + state + "  sink :" + sink.currentContext());
                                if (state.equalsIgnoreCase("2")) {
                                    sink.complete();
                                }
                                return String.valueOf(Integer.valueOf(state) + 1);
                            });
        // Flux<JSONObject> response;
        // response = inp.flatMap(i -> {
        //     try {
        //         return sample1.getObj(i).map(t -> {
        //             if (t.optString("firstName").contains("1")) {
        //                 return t;
        //             } else {
        //                 return t.put("firstName", "Gokul");
        //             }
        //         });
        //     } catch (InterruptedException e) {
        //         e.printStackTrace();
        //     }
        //     return Mono.just(new JSONObject());
        // });
        return inp;
    }

    @RequestMapping(path = "/multi", method = RequestMethod.GET)
    @ResponseBody
    public Mono<String> scopeCheck() throws InterruptedException {
        System.out.println("1");
        Mono<JSONObject> result = sample1.createFlux().doOnError(i -> {
            System.out.println("err ::" + i);
        });
        System.out.println("2");
        System.out.println("3");
        return result.map(JSONObject::toString).log();
    }

    @RequestMapping(value="/flux1", method=RequestMethod.GET)
    public Flux<String> requestMethodName() {
        String key = "message";
        Flux<String> r = Flux.just("Hello", "Hi")
                .flatMap( s -> Mono.subscriberContext()
                        .map( ctx -> s + " " + ctx.getOrDefault(key, "Stranger")))
                .subscriberContext(ctx -> ctx.put(key, ctx.getOrDefault(key, "Different")));
        return r;
    }

    @RequestMapping(path = "/retry", method = RequestMethod.GET)
    @ResponseBody
    public Mono<String> test1C() {
        return sample1.retryCheck().map(JSONObject::toString);
    }

    @RequestMapping(path = "/parallel", method = RequestMethod.GET)
    @ResponseBody
    public Flux<String> parallel() {
        return sample1.parallelPro().map(JSONObject::toString);
    }
    

}
