package com.mypract.samples;

import org.json.JSONObject;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;
import java.util.function.Consumer;

@Controller
public class SampleController {

    @Autowired
    private Sample1 sample1;

    /*@RequestMapping(path = "/getInfo", method = RequestMethod.GET)
    public Flux<Object> getResponse(@RequestParam(value = "name") String name) throws InterruptedException {
        System.out.println("Thread  " + Thread.currentThread().getName() + " " + Thread.currentThread().getId());
        JSONObject resp = sample1.getObj(name);
        System.out.println("response  " + resp);
        return Flux.just(resp.toString());
    }*/

    @RequestMapping(path = "/getInfo", method = RequestMethod.GET)
    @ResponseBody
    public Mono<Object> getResponse(@RequestParam(value = "name") String name) throws InterruptedException {
        System.out.println("in time  " + new Date());
        Mono<Object> obj = sample1.getObj(name).map(i -> i.get("name"));
        /*Mono<Object> resp = sample1.getObj(name).map(i -> i.get("name"));*/
        System.out.println("Out time  :" + new Date());
        return obj;
        /*return resp;*/
    }

    @RequestMapping(path = "/other", method = RequestMethod.GET)
    public Flux<String> getOther() {
        return sample1.getOther().flux().log();
    }
}
