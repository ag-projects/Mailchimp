package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

public class InfrastructureStack extends Stack {
    public InfrastructureStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InfrastructureStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // The code that defines your stack goes here

        // Define Lambda Function
        Function lambdaFunction = Function.Builder.create(this, "mailchimp-webhooks")
                .memorySize(1024)
                .runtime(Runtime.JAVA_21)
                .timeout(Duration.seconds(20))
                .functionName("MailchimpWebhooksLambda")
                .code(Code.fromAsset("../assets/function.jar"))
                .handler("com.quickfee.lambda.WebhooksHandler::handleRequest")
                .build();
    }
}
