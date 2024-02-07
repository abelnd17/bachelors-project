package com.example.executor;

import io.camunda.zeebe.spring.client.annotation.JobWorker;

public class TestExecutor {

    @JobWorker(type = "execute")
    public void Execute(){
        System.out.println("*hugs you*");
    }
}
