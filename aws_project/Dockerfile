FROM eclipse-temurin:21-jdk
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib/app/lib
COPY ${DEPENDENCY}/BOOT-INF/app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes/app

ENTRYPOINT ["java","-cp","app:app/lib/*","br.com.udemy.aws_ecs_fargate_java.aws_project.AwsProjectApplication"]