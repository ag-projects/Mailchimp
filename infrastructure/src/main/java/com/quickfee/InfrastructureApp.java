package com.quickfee;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;

public class InfrastructureApp {
    public static void main(final String[] args) {
        App app = new App();
        new InfrastructureStack(app, "mailchimp-stack", StackProps.builder().build());
        app.synth();
    }
}

