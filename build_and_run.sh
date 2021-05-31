gradle clean --refresh-dependencies
gradle compileJavacc
gradle compileJava
gradle processResources
gradle classes
gradle bootWar
java -jar build/libs/webapp-1.0-SNAPSHOT.war