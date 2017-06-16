import React from 'react';
import Message from './Message.jsx';
import * as Constants from './Constants.jsx';

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
                {this.props.loading && <Message
                    message={{messageType: Constants.response.ResponseType.LOADING_MESSAGE}}
                    fromMe={false} /> }
            </div>
        );
    }
}

Messages.defaultProps = {
    messages: [],
    loading: false
};

export default Messages;