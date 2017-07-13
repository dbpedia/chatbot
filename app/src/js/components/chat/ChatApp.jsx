import React from 'react';

import Messages from './Messages.jsx';
import ChatInput from './ChatInput.jsx';
import * as Constants from '../Constants.jsx';
import Feedback from './Feedback.jsx';

class ChatApp extends React.Component {

    constructor(props) {
        super();
        this.state = {messages: [], loading: false, showFeedback: false, overlay: false};
        this.uuid = this.getUuid();

        this.sendHandler = this.sendHandler.bind(this);
        this.showStart = this.showStart.bind(this);
        this.showFeedbackModal = this.showFeedbackModal.bind(this);
        this.hideFeedbackModal = this.hideFeedbackModal.bind(this);
        this.showOverlay = this.showOverlay.bind(this);
        this.hideOverlay = this.hideOverlay.bind(this);
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
        this.state.loading = true;
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
                this.state.loading = false;
            },
            error: () => {
                this.state.loading = false;
            }
        });
    }

    renderMessages(messages) {
        for (let index in messages) {
            setTimeout(() => {
                this.addMessage({
                    fromBot: true,
                    message: messages[index]
                });
            }, 500 * index);
        }
    }

    showStart(restart) {
        this.removeSmartReplies();
        this.makeRequest({
            messageType: Constants.request.RequestType.PARAMETER_MESSAGE,
            messageData: [{
                payload: restart ? Constants.request.ParameterType.HELP : Constants.request.ParameterType.START
            }]
        });
    }

    componentDidMount() {
        if (!this.props.isAdmin) {
            setTimeout(() => {
                this.showStart();
            }, 500);
        }
    }

    removeSmartReplies() {
        this.state.messages.map((message, index) => {
            if(message.message.messageType == Constants.response.ResponseType.SMART_REPLY_MESSAGE) {
                delete this.state.messages[index].message.messageData[0].smartReplies;
            }
        });
    }

    sendHandler(message) {
        const messageObject = {
            username: this.props.username,
            message: message,
            fromBot: false
        };

        this.removeSmartReplies();
        this.addMessage(messageObject);
        this.makeRequest(message);
    }

    addMessage(message) {
        // Append the message to the component state
        let messages = this.state.messages;
        messages.push(message);
        this.setState({ messages });
    }

    showFeedbackModal() {
        this.setState({showFeedback: true});
    }

    hideFeedbackModal() {
        this.setState({showFeedback: false});
    }

    showOverlay() {
        this.setState({overlay: true});
    }

    hideOverlay() {
        this.setState({overlay: false});
    }

    clearChatHistory() {
        this.setState({messages: []});
    }

    addChatHistory(msgs) {
        var messages = this.state.messages;
        for (var index in msgs) {
            messages.push(msgs[index]);
        }
        this.setState({messages: messages});
    }

    render() {
        var style = {};
        if(this.props.height) {
            style.height = this.props.height;
        }
        return (
            <div className="card expandOpen" id="chat-app-container" style={style}>
                <Messages messages={this.state.messages}
                    onSend={this.sendHandler}
                    isAdmin={this.props.isAdmin}
                    loading={this.state.loading} />
                <ChatInput
                    isAdmin={this.props.isAdmin}
                    onSend={this.sendHandler}
                    showStart={this.showStart}
                    showFeedback={this.showFeedbackModal}
                    showOverlay={this.showOverlay}
                    hideOverlay={this.hideOverlay} />
                <Feedback
                    isOpen={this.state.showFeedback}
                    hide={this.hideFeedbackModal}
                    userId={this.getUuid()} />
                {this.state.overlay && (
                    <div className="overlay"></div>
                )}
            </div>
        );
    }
}

ChatApp.defaultProps = {
    isAdmin: false
};

export default ChatApp;