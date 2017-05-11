First of all I would like to thank everyone in the DBpedia team for selecting me for this Chatbot project. I would also like to thank my mentor Ricardo for helping me by giving valuable feedback during the proposal phase as well as selecting me for working on this project.

# Project Description
The requirement of the project is to build a conversational Chatbot for DBpedia which would be deployed in at least two platforms. The following platforms were chosen to be in scope for the project:
* A Web Interface
* Facebook Messenger

# Architecture
![Chatbot Architecture](http://i.imgur.com/f2pJ0gZ.png)

The process by which the Chatbot server handles requests can be divided into 6 steps as follows:
* **Incoming Request:** Webhooks that handle incoming requests from each platform
* **Request Classification:** Incoming requests can be classified into four types they are
    * **Natural Language Question:** For example *‘Give me the capital of Germany’* 
    * **Command Requests:** About, Help, etc.
    * **Banter:** Casual conversation such as *'Hi'*, *'How are you?'*
    * **Parameterized Requests:** When user clicks on links in information already presented. For example clicking on a Learn More button when presented information about Germany
* **Session Management:** Store or retrieve user information if needed to service request
* **Handling Natural Language Questions:** 
    * **Query QA Frameworks:** First the query is passed to the Question Answering Frameworks to find the answer for the query which could be a DBpedia resource, string, literal, etc.
    * **Query DBpedia:** Then we query DBpedia to get the information about the resource.
* **Generate Response:** The extracted response is summarized as needed using existing summarization tools and the response is customized for each platform.
* **Send Response:** Finally the response is sent back to each platform. Additionally, for the web interface, the client side code to handle the responses are written using standard frontend technologies such as HTML, CSS and Javascript.

# Tools & Technologies
Following list of tools and technologies have been finalized.
## Server Side Technologies
* **Java:** Web Server Language: 
* **Spring:** REST/Web Framework
* **Rivescript:** Chat Framework
* **Gradle:** Java Dependency Management

## Tools
* **IntelliJ:** Java IDE

## DevOps
* **Git:** Version Control
* **GitHub:** Version Control Management
* **GitLab:** Continuous Integration
* **Logging yet to be decided**

## Analytics
* Google Analytics

# Weekly Updates
> The following section tracks the weekly progress that was completed.

## Community Bonding Period
### Week 1: May 4 to May 10
* Touch base with mentor (Ricardo)
* Subscribed to DBpedia Developer and Discussion Mailing Lists
* Created GitHub Repository
* Determine Initial System Architecture and Technologies needed. Following were chosen:
    * Java with Spring for the Server Side Language
    * Rivescript as a Chat framework for canned responses
    * Git for version control along with GitHub for managing repo 
    * GitLab for Continuous Integration

### Week 2: May 11 to May 17 (to be done)
* Uploaded progress page
* Created initial REST application using Java and Spring and deployed a simple echo bot on Facebook. 
