package com.test.schedultest;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SchedulerTaskTest {


    @Scheduled(fixedDelay = 2000) //或者cron="*/2 * * * * ?"
    public void fixedDelayJob() throws InterruptedException {

        System.out.println("2S一次"+new Date());
    }
}

