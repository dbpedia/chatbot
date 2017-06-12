import React from 'react';

import Messages from './Messages.jsx';
import ChatInput from './ChatInput.jsx';

class ChatApp extends React.Component {

    constructor(props) {
        super();
        this.state = {messages: []};
        this.sendHandler = this.sendHandler.bind(this);
        this.uuid = this.getUuid();
    }

    generateUuid() {
        var d = new Date().getTime();
        var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = (d + Math.random()*16)%16 | 0;
            d = Math.floor(d/16);
            return (c=='x' ? r : (r&0x3|0x8)).toString(16);
        });
        return uuid;
    }

    getUuid() {
        if (localStorage) {
            var uuid = localStorage.getItem('uuid');
            if (uuid) {
                return uuid;
            }

            uuid = this.generateUuid();
            localStorage.setItem('uuid', uuid);
            return uuid;
        }
        return this.generateUuid();
    }

    makeRequest(data) {
        data.userId = this.uuid;
        $.ajax({
            type: 'POST',
            url: '/webhook',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Content-Type': 'application/json; charset=utf-8'
            },
            dataType: 'text',
            data: JSON.stringify(data),
            success: (response) => {
                this.renderMessages(JSON.parse(response));
            }
        });
    }

    renderMessages(messages) {
        for (let index in messages) {
            setTimeout(() => {
                this.addMessage({
                    username: 'bot',
                    fromMe: false,
                    message: messages[index]
                });
            }, 500 * index);
        }
    }

    componentDidMount() {
        setTimeout(() => {
            this.makeRequest({
                messageType: 'parameter',
                messageData: [{
                    payload: 'start'
                }]
            });
        }, 500);
    }

    sendHandler(message) {
        const messageObject = {
            username: this.props.username,
            message: message
        };

        messageObject.fromMe = true;
        this.addMessage(messageObject);
        this.makeRequest(message);
    }

    addMessage(message) {
        // Append the message to the component state
        let messages = this.state.messages;
        messages.push(message);
        this.setState({ messages });
    }

    render() {
        return (
            <div className="card expandOpen" id="chat-app-container">
                <Messages messages={this.state.messages} />
                <ChatInput onSend={this.sendHandler} />
            </div>

        );
    }
}

ChatApp.defaultProps = {
    'username': 'anonymous'
};

export default ChatApp;