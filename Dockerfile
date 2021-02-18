FROM java:latest
LABEL description="KubeEdge Honeypot App"
RUN mkdir -p /usr
RUN mkdir -p /usr/local
COPY . /usr/local/
WORKDIR /usr/local
EXPOSE 8080
EXPOSE 23
EXPOSE 6379
EXPOSE 3306
ENTRYPOINT ["java","-jar","Honeypot_edge-v1.0.0.jar"]
