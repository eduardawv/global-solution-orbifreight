# ═══════════════════════════════════════════════════════════════════════
#  OrbiFreight API — Dockerfile
#  Global Solution 2026/1 — FIAP — DevOps Tools & Cloud Computing
#  Base: AlmaLinux 9 (RHEL-compatible — por indicação do professor)
#  RM564434 — Eduarda Weiss Ventura
# ═══════════════════════════════════════════════════════════════════════

# ── Stage 1: Build com Maven ────────────────────────────────────────────
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /build

# Cache de dependências (só recompila se o pom.xml mudar)
COPY pom.xml .
RUN mvn dependency:go-offline -B --no-transfer-progress

# Compila o projeto e gera o JAR
COPY src ./src
RUN mvn clean package -DskipTests -B --no-transfer-progress

# ── Stage 2: Runtime — AlmaLinux 9 ─────────────────────────────────────
FROM almalinux:9

# Instala Java 21 JRE e utilitários de usuário
RUN dnf install -y java-21-openjdk-headless shadow-utils && \
    dnf clean all && \
    rm -rf /var/cache/dnf

# ✅ REQUISITO: Usuário não privilegiado (não root)
RUN groupadd -r orbigroup && \
    useradd -r -g orbigroup -m -s /bin/bash orbiuser

# ✅ REQUISITO: Diretório de trabalho definido
WORKDIR /app

# ✅ REQUISITO: Variáveis de ambiente
ENV APP_PORT=8080
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Copia o JAR gerado no Stage 1
COPY --from=build /build/target/*.jar app.jar
RUN chown orbiuser:orbigroup app.jar

# ✅ REQUISITO: Executa com usuário não privilegiado
USER orbiuser

# ✅ REQUISITO: Porta exposta
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
