# DBpedia Chatbot

## Environment Configurations
     chatbot.baseUrl = <https-url-to-access-the-bot>
     chatbot.fb.appSecret = <secret>
     chatbot.fb.verifyToken = <token>
     chatbot.fb.pageAccessToken = <access-token>
     
     logging.level.com.github.messenger4j=<log-level>

### Development Only Configurations
     spring.thymeleaf.cache = false
     spring.devtools.livereload.enabled = true

## Deployment
     mvn clean install
     java $JAVA_OPTS -Dserver.port=$PORT -jar target/*.jar // $PORT is the port number you want the server to run in for example 8080         
 
## Development
     node/node node_modules/.bin/webpack --watch     
