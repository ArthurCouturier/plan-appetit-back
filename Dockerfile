# ----------- Étape 1 : Build -----------
FROM --platform=linux/amd64 maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copier le pom.xml, télécharger les dépendances (mise en cache)
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copier le code source et compiler
COPY . .
RUN mvn clean package -DskipTests

# ----------- Étape 2 : Runtime -----------
FROM --platform=linux/amd64 eclipse-temurin:21-jre AS runner

WORKDIR /app
COPY --from=builder /app/target/plan-appetit-back-0.0.1-SNAPSHOT.jar app.jar

# >>> On copie le fichier JSON Firebase dans l'image <<<
COPY planappetit-firebase-adminsdk-fbsvc-ec808c24c1.json /app/

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
