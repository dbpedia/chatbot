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
            message
        };

        messageObject.fromMe = true;
        this.addMessage(messageObject);
    }

    addMessage(message) {
        // Append the message to the component state
        let messages = this.state.messages;
        messages.push(message);
        this.setState({ messages });
    }

    render() {
        return (
            <div className="card">
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