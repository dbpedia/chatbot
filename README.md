<h1 id="dbpediachatbot">DBpedia Chatbot</h1>

<p><a href="https://www.openhub.net/p/dbpedia-chatbot"><img src="https://www.openhub.net/p/dbpedia-chatbot/widgets/project_thin_badge.gif" alt="Project Stats" /></a> <a href="https://bettercodehub.com/"><img src="https://bettercodehub.com/edge/badge/dbpedia/chatbot?branch=master" alt="BCH compliance" /></a></p>

<p><strong><a href="http://chat.dbpedia.org">Final Application</a></strong></p>

<p>For more information about this project and <strong>GSoC Progress</strong> please refer to <strong><a href="https://github.com/dbpedia/chatbot/wiki/GSoC-2017:-Chatbot-for-DBpedia">GSoC Wiki</a></strong></p>

<p align="center"><a href="https://www.youtube.com/watch?v=Wk-UUufDpZs"><img src="https://media.giphy.com/media/26CaLmhBRmHjSb3Hy/giphy.gif" alt="DBpedia Chatbot YouTube Video" /></a></p>

<h2 id="environmentconfigurations">Environment Configurations</h2>

<p>When running locally or in development include the following configuration as a properties file in the <code>src/main/resources</code> folder. </p>

<p>In case you do not have a proper CouchDB instance or API keys please use the following <strong><a href="https://github.com/dbpedia/chatbot/wiki/Chatbot-Dummy-Configuration">dummy configuration file</a></strong>. </p>

<p><em>Please note that using the dummy configuration file can result in some features being unavailable since they may require the requisite API services.</em>   </p>

<pre><code> admin.username = &lt;admin-username&gt;
 admin.password = &lt;admin-password&gt;

 chatbot.baseUrl = &lt;https-url-to-access-the-bot&gt;
 chatbot.gaID = &lt;google-analytics-id&gt;

 chatbot.fb.appSecret = &lt;secret&gt;
 chatbot.fb.verifyToken = &lt;token&gt;
 chatbot.fb.pageAccessToken = &lt;access-token&gt;
 chatbot.slack.botToken = &lt;bot-token&gt;

 cloudant.url = &lt;couchdb-url&gt;
 cloudant.username = &lt;couchdb-username&gt;
 cloudant.password = &lt;couchdb-password&gt;
 cloudant.chatDB = &lt;couchdb-chatdb-name&gt;
 cloudant.feedbackDB = &lt;couchdb-feedbackdb-name&gt;
 cloudant.explorerDB = &lt;couchdb-explorerdb-name&gt;

 tmdb.apiKey = &lt;tmdb-api-key&gt;

 wolfram.apiKey = &lt;wolfram-alpha-api-key&gt;
</code></pre>

<h3 id="developmentonlyconfigurations">Development Only Configurations</h3>

<pre><code> spring.thymeleaf.cache = false
 spring.devtools.livereload.enabled = true
 logging.level.com.github.messenger4j = DEBUG
 logging.level.com.github.messenger4j = &lt;log-level&gt;
</code></pre>

<h2 id="deployment">Deployment</h2>

<pre><code> mvn clean install
 java $JAVA_OPTS -Dserver.port=$PORT -jar target/*.jar // $PORT is the port number you want the server to run in for example 8080
</code></pre>

<h2 id="development">Development</h2>

<pre><code> mvn spring-boot:run
 node/node node_modules/.bin/webpack --env.NODE_ENV=dev --watch
</code></pre>

<h2 id="embedcode">Embed Code</h2>

<p>Add the following snippet to the <code>&lt;head&gt;</code> section of the webpage where you want to embed the ChatBot.</p>

<pre><code class="javascript language-javascript">&lt;script type="text/javascript"&gt;
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
            if(event.origin == URL &amp;&amp; event.data == "dbpedia-chatbot-embed-loaded") {
              iframe.style.display = "block";
            }
          }
        }
    })();
&lt;/script&gt;
</code></pre>
