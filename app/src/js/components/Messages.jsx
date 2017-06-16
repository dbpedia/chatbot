import React from 'react';
import Message from './Message.jsx';

class Messages extends React.Component {

    // Need to study this in more detail later
    componentDidUpdate() {
        const element = document.getElementById("messages-container");
        element.scrollTop = element.scrollHeight;
    }

    render() {
        const messages = this.props.messages.map((message, i) => {
            return (
                <Message
                    key={i}
                    username={message.username}
                    message={message.message}
                    fromMe={message.fromMe}
                    onSend={this.props.onSend} />
            );
        });

        return (
            <div id="messages-container">
                {messages}
            </div>
        );
    }
}

Messages.defaultProps = {
    messages: []
};

export default Messages;