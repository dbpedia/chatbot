import React from 'react';

import Messages from './Messages.jsx';
import ChatInput from './ChatInput.jsx';

class ChatApp extends React.Component {
    constructor(props) {
        super();
        this.state = {messages: []};
        this.sendHandler = this.sendHandler.bind(this);
    }

    makeRequest(data) {
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