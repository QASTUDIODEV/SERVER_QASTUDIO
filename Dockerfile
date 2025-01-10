FROM amazoncorretto:17

# 타임존 설정
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime

# 빌드 아티팩트 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} qa_studio_dev.jar

# 환경 변수 설정 (필요시 기본값으로 develop 지정)
ENV SPRING_PROFILES_ACTIVE=default

# 엔트리포인트 설정
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "/qa_studio_dev.jar"]