# 医生帮后端项目

技术栈：Java 21、Spring Boot 3、Maven。

## 开发命令

```powershell
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
..\.tools\apache-maven-3.9.16\bin\mvn.cmd spring-boot:run
```

默认监听 `http://localhost:8080`，并提供前端所需的 HTTP API。当前仅使用模拟数据。
