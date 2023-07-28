#FROM eclipse-temurin:17-jre-alpine
FROM amazoncorretto:17.0.7-al2023-headless

#se nao existir vai ser criado
WORKDIR /app

COPY *.jar /app/api.jar
COPY wait-for-it.sh /wait-for-it.sh

RUN chmod +x /wait-for-it.sh

#documentacao - nao publica a porta
EXPOSE 8080

#Comando Padrao para executar na inicialização do container. Pode ser substituido no Compose
#estamos dentro do WORKDIR e o api.jar tambem
CMD ["java", "-jar", "api.jar"]
