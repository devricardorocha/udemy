# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

This is a Maven-based AWS CDK (Java) project. The CDK app entry point is invoked via `mvn exec:java` (configured in `cdk.json` as `mvn -e -q compile exec:java`), not by running a jar directly.

- `mvn package` — compile and run tests
- `mvn test` — run tests only
- `mvn test -Dtest=AwsProjectCdkTest` — run a single test class
- `cdk ls` — list all stacks in the app
- `cdk synth` — synthesize CloudFormation templates for all stacks into `cdk.out/`
- `cdk synth Vpc` / `cdk synth Cluster` — synthesize a single stack
- `cdk diff` — compare deployed stacks with current state
- `cdk deploy` — deploy stack(s) to the default AWS account/region (requires `cdk deploy --all` to deploy both stacks, since there's no default stack)

## Architecture

The app (`AwsProjectCdkApp`) wires together two separate CDK stacks with an explicit dependency, rather than defining everything in one stack:

1. **`VpcStack`** — creates a VPC (`Vpc01`) with 2 AZs and **no NAT gateways** (`natGateways(0)`), and exposes the `Vpc` construct via `getVpc()`.
2. **`ClusterStack`** — takes the `Vpc` from `VpcStack` as a constructor argument and creates an ECS `Cluster` (`Cluster-01`) inside it.

`ClusterStack.addDependency(vpcStack)` in `AwsProjectCdkApp` enforces deploy ordering (VPC before cluster) since the cluster consumes the VPC's construct reference directly rather than importing it by ID/ARN.

Because there are no NAT gateways, any ECS workload placed in private subnets will have no outbound internet access unless VPC endpoints or a NAT gateway are added later — keep this in mind when adding Fargate services or tasks that need to pull images or reach AWS APIs.

`AwsProjectCdkStack` is the original CDK-init boilerplate stack and is currently unused by `AwsProjectCdkApp` (not instantiated). Its matching test in `AwsProjectCdkTest` is entirely commented out.

## Notes

- CDK feature flags in `cdk.json` under `context` follow current CDK defaults; don't remove/change them without understanding the behavioral change they gate.
- `cdk.out/` is generated output (gitignored) — never hand-edit templates there.
