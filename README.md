# DBpedia Chatbot

[![Project Stats](https://www.openhub.net/p/dbpedia-chatbot/widgets/project_thin_badge.gif)](https://www.openhub.net/p/dbpedia-chatbot) [![BCH compliance](https://bettercodehub.com/edge/badge/dbpedia/chatbot?branch=master)](https://bettercodehub.com/)

**[Final Application](http://chat.dbpedia.org)**

For more information about this project and **GSoC Progress** please refer to **[GSoC Wiki](https://github.com/dbpedia/chatbot/wiki/GSoC-2017:-Chatbot-for-DBpedia)**

## Environment Configurations
When running locally or in development include the following configuration as a properties file in the `src/main/resources` folder. In case you don't have a proper CouchDB instance or API keys please use the following **[dummy configuration file](https://gist.github.com/ram-g-athreya/ba25de412bf7146ccbf25c3c881ff161)**. Please note that using the dummy configuration file can result in some features being unavailable since they may require the requisite API services.   

     admin.username = <admin-username>
     admin.password = <admin-password>
     
     chatbot.baseUrl = <https-url-to-access-the-bot>
     
     chatbot.fb.appSecret = <secret>
     chatbot.fb.verifyToken = <token>
     chatbot.fb.pageAccessToken = <access-token>
     chatbot.slack.botToken = <bot-token>

     cloudant.url = <couchdb-url>
     cloudant.username = <couchdb-username>
     cloudant.password = <couchdb-password>
     cloudant.chatDB = <couchdb-chatdb-name>
     cloudant.feedbackDB = <couchdb-feedbackdb-name>
     cloudant.explorerDB = <couchdb-explorerdb-name>

     tmdb.apiKey = <tmdb-api-key>

     wolfram.apiKey = <wolfram-alpha-api-key>     

### Development Only Configurations
     spring.thymeleaf.cache = false
     spring.devtools.livereload.enabled = true
     logging.level.com.github.messenger4j = DEBUG
     logging.level.com.github.messenger4j = <log-level>

## Deployment
     mvn clean install
     java $JAVA_OPTS -Dserver.port=$PORT -jar target/*.jar // $PORT is the port number you want the server to run in for example 8080

## Development
     mvn spring-boot:run
     node/node node_modules/.bin/webpack --watch

## Embed Code
Add the following snippet to the `<head>` section of the webpage where you want to embed the ChatBot.
```javascript
<script type="text/javascript">
    (function() {
        var URL = "http://chat.dbpedia.org"
        window.onload = function() {
          var iframe = document.createElement("iframe");
          iframe.setAttribute("src", URL + "/embed");
          iframe.setAttribute("frameBorder", 0);
          iframe.style.zIndex = 10000000;
          iframe.style.height = "100%";
          iframe.style.width = "40%";
          iframe.style.position = "fixed";
          iframe.style.bottom = "20px";
          iframe.style.left = "20px";
          iframe.style.display = "none";

          document.body.appendChild(iframe);
          window.addEventListener("message", receiveMessage, false);
          function receiveMessage(event) {
            if(event.origin == URL && event.data == "dbpedia-chatbot-embed-loaded") {
              iframe.style.display = "block";
            }
          }
        }
    })();
</script>
```
