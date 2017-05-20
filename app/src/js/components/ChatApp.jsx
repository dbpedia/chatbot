import React from 'react';

import Messages from './Messages.jsx';
import ChatInput from './ChatInput.jsx';

class ChatApp extends React.Component {
    constructor(props) {
        super();
        this.state = {messages: []};
        this.sendHandler = this.sendHandler.bind(this);
    }

    sendHandler(message) {
        const messageObject = {
            username: this.props.username,
            message: message
        };

        messageObject.fromMe = true;
        this.addMessage(messageObject);

        $.ajax({
            type: 'POST',
            url: '/webhook',
            contentType: 'application/json',
            dataType: 'text',
            data: JSON.stringify({
                messageType: 'text',
                message: message
            }),
            success: (response) => {
                this.addMessage({
                    username: 'bot',
                    fromMe: false,
                    message: response
                });
            }
        });
    }

    addMessage(message) {
        // Append the message to the component state
        let messages = this.state.messages;
        messages.push(message);
        this.setState({ messages });
    }

    render() {
        return (
            <div className="card expandOpen">
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