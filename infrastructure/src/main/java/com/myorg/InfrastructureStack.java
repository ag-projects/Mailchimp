package com.myorg;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.*;
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

        // Define Lambda Function
        Function lambdaFunction = Function.Builder.create(this, "mailchimp-webhooks")
                .memorySize(1024)
                .runtime(Runtime.JAVA_21)
                .timeout(Duration.seconds(20))
                .functionName("MailchimpWebhooksLambda")
                .code(Code.fromAsset("../assets/function.jar"))
                .handler("com.quickfee.lambda.WebhooksHandler::handleRequest")
                .build();

        // Create API Gateway
        RestApi api = RestApi.Builder.create(this, "WebhooksApi")
                .build();

        // Create Integration between Lambda and API Gateway
        LambdaIntegration lambdaIntegration = LambdaIntegration.Builder.create(lambdaFunction)
                .build();

        // Create API Gateway Methods
        api.getRoot().addMethod("HEAD", lambdaIntegration);
        api.getRoot().addMethod("POST", lambdaIntegration);

        // Output the API Gateway endpoint
        CfnOutput.Builder.create(this, "ApiEndpoint")
                .value(api.getUrl())
                .build();


    }
}
