package com.quickfee;

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
        Function lambdaFunction = Function.Builder.create(this, "mail-chimp-webhooks")
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

        // Create dev stage
        Deployment devDeployment = createDeployment(api, "DevDeployment", "Dev Staging");
        Stage devStage = createStage("DevStage", "dev", devDeployment);
        api.setDeploymentStage(devStage);

        // Create test stage
        Deployment testDeployment = createDeployment(api, "TestDeployment","Test Staging");
        Stage testStage = createStage("TestStage", "test", testDeployment);
        api.setDeploymentStage(testStage);

        // Output the API Gateway endpoint
        CfnOutput.Builder.create(this, "WebhooksApiEndpoint")
                .value(api.getUrl())
                .build();

    }

    private Deployment createDeployment(RestApi api, String deploymentId, String stageDescription) {
       return new Deployment(this, deploymentId, DeploymentProps.builder()
                .api(api)
                .description(stageDescription)
                .build());
    }

    private Stage createStage(String stageId, String stageName, Deployment deployment) {
        return new Stage(this, stageId, StageProps.builder()
                .stageName(stageName)
                .deployment(deployment)
                .build());
    }
}
