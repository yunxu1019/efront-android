@setlocal
@chcp 65001
set JAVA_OPTS=--enable-native-access=ALL-UNNAMED
cls
gradlew clean build --warning-mode all
@endlocal