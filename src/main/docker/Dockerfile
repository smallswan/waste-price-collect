FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /home/apps/waste-price-collect 
ARG JAR_FILE
ADD waste-price-collect.jar app.jar

#定义环境变量
ENV  TIME_ZONE Asia/Shanghai
RUN  apk add --no-cache tzdata && echo "${TIME_ZONE}" > /etc/timezone && ln -sf /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime 


ENTRYPOINT ["java","-Xmx400m","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
