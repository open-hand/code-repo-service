FROM registry.cn-hangzhou.aliyuncs.com/choerodon-tools/javabase:0.8.0
WORKDIR /choerodon
COPY app.jar hrds-code-repo.jar
RUN chown -R www-data:www-data /choerodon
USER 33
CMD java $JAVA_OPTS $SKYWALKING_OPTS -jar /choerodon/hrds-code-repo.jar