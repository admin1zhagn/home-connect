@echo off
rem Maven Wrapper minimal launcher for Windows
rem Uses the explicit main class because the 3.3.2 wrapper jar ships without a Main-Class in its manifest.
set MY_HOME=%~dp0
java -Dmaven.multiModuleProjectDirectory="%MY_HOME%" -cp "%MY_HOME%.mvn\wrapper\maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain %*
