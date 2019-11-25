package com.mypract;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class ReactorPrac {

    static {
        BlockHound.install();
    }
    public static void main(String[] args) {
        SpringApplication.run(ReactorPrac.class);
    }
}
