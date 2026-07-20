package com.myorg.services;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.logs.LogGroup;
import software.constructs.Construct;

import java.util.HashMap;
import java.util.Map;

public class Service01Stack extends Stack {

    public Service01Stack(final Construct scope, final String id, final Cluster cluster) {
        this(scope, id, cluster, null);
    }

    public Service01Stack(final Construct scope, final String id ,final Cluster cluster, final StackProps props) {
        super(scope, id, props);

        Map<String, String> envVars = new HashMap<>();
        envVars.put("SPRING_DATASOURCE_URL", "jdbc:mariadb://" + Fn.importValue("rds-endpoint") + ":3306/aws-project01?createDatabaseIfNotExist=true");
        envVars.put("SPRING_DATASOURCE_USERNAME", "admin");
        envVars.put("SPRING_DATASOURCE_PASSWORD", Fn.importValue("rds-password"));

        ApplicationLoadBalancedTaskImageOptions taskImageOptions = ApplicationLoadBalancedTaskImageOptions.builder()
                .containerName("aws_project01")
                .image(ContainerImage.fromRegistry("devricardorocha/aws_project01:0.0.2-SNAPSHOT"))
                .containerPort(8080)
                .logDriver(getLogDriver())
                .environment(envVars)
                .build();

        ApplicationLoadBalancedFargateService service01 = ApplicationLoadBalancedFargateService.Builder
                .create(this, "ALB01")
                .serviceName("service-01")
                .cluster(cluster)
                .cpu(256)
                .desiredCount(2)
                .listenerPort(8080)
                .memoryLimitMiB(1024)
                .publicLoadBalancer(Boolean.TRUE)
                .assignPublicIp(Boolean.TRUE)
                .taskImageOptions(taskImageOptions)
                .circuitBreaker(DeploymentCircuitBreaker.builder().rollback(true).build())
                .minHealthyPercent(100)
                .build();

        service01.getTargetGroup().configureHealthCheck(
            new HealthCheck.Builder()
                .path("/actuator/health")
                .port("8080")
                .healthyHttpCodes("200")
                .build());

        ScalableTaskCount scalableTaskCount = service01.getService().autoScaleTaskCount(
                EnableScalingProps.builder()
                        .minCapacity(2)
                        .maxCapacity(4)
                        .build());

        scalableTaskCount.scaleOnCpuUtilization("Service01AutoScaling",
                CpuUtilizationScalingProps.builder()
                        .targetUtilizationPercent(50)
                        .scaleInCooldown(Duration.seconds(60))
                        .scaleOutCooldown(Duration.seconds(60))
                        .build());
    }

    private @NotNull LogDriver getLogDriver() {
        return LogDriver.awsLogs(
                AwsLogDriverProps.builder()
                    .logGroup(getLogGroup())
                    .streamPrefix("Service01")
                    .build());
    }

    private @NotNull LogGroup getLogGroup() {
        return LogGroup.Builder.create(this, "Service01LogGroup")
                .logGroupName("Service01")
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
    }

}
