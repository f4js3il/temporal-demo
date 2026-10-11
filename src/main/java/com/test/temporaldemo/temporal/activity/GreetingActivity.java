package com.test.temporaldemo.temporal.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface GreetingActivity {

    // Optional: without a name, the activity type defaults to "ComposeGreeting"
    @ActivityMethod(name = "ComposeGreeting")
    String composeGreeting(String name);
}
