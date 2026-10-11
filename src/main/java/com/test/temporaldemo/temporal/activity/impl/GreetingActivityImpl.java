package com.test.temporaldemo.temporal.activity.impl;

import com.test.temporaldemo.temporal.activity.GreetingActivity;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

@Component
@ActivityImpl(taskQueues = "greeting-queue")
public class GreetingActivityImpl implements GreetingActivity {

    @Override
    public String composeGreeting(String name) {
        return "Hello " + name + "!";
    }
}
