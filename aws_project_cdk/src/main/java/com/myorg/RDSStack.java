package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.rds.*;
import software.constructs.Construct;

import java.util.Collections;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class RDSStack extends Stack {
    public RDSStack(final Construct scope, final String id, final Vpc vpc) {
        this(scope, id, vpc, null);
    }

    public RDSStack(final Construct scope, final String id, final Vpc vpc, final StackProps props) {
        super(scope, id, props);

        CfnParameter dbPassword = CfnParameter.Builder
                .create(this, "dbPassword")
                .type("String")
                .description("The RDS instance password")
                .build();

        ISecurityGroup iSecurityGroup = SecurityGroup.fromSecurityGroupId(this, id, vpc.getVpcDefaultSecurityGroup());
        iSecurityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(3306));

        DatabaseInstance dbInstance = DatabaseInstance.Builder
                .create(this, "Rds01")
                .instanceIdentifier("aws-project01-db")
                .vpc(vpc)
                .engine(getRDSEngine())
                .credentials(getCredentials(dbPassword))
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .multiAz(false)
                .allocatedStorage(10)
                .securityGroups(Collections.singletonList(iSecurityGroup))
                .vpcSubnets(
                        SubnetSelection.builder()
                                .subnets(vpc.getPrivateSubnets())
                                .build())
                .build();

        CfnOutput.Builder.create(this, "rds-endpoint")
                .exportName("rds-endpoint")
                .value(dbInstance.getDbInstanceEndpointAddress())
                .build();

        CfnOutput.Builder.create(this, "rds-password")
                .exportName("rds-password")
                .value(dbPassword.getValueAsString())
                .build();
    }

    private Credentials getCredentials(CfnParameter dbPassword) {
        CredentialsFromUsernameOptions fromInput = CredentialsFromUsernameOptions.builder()
                .password(SecretValue.unsafePlainText(dbPassword.getValueAsString()))
                .build();

        return Credentials.fromUsername("admin", fromInput);
    }

    private IInstanceEngine getRDSEngine() {
        return DatabaseInstanceEngine.mysql(
                MySqlInstanceEngineProps.builder()
                        .version(MysqlEngineVersion.VER_5_7)
                        .build()
        );
    }
}
