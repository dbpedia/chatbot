import React from 'react';

import Messages from './Messages.jsx';
import ChatInput from './ChatInput.jsx';
import * as Constants from '../Constants.jsx';
import Feedback from './Feedback.jsx';
import About from './About.jsx';

class ChatApp extends React.Component {

    constructor(props) {
        super();
        this.state = {messages: [], loading: false, showFeedback: false, overlay: false, showAbout: false};
        this.uuid = this.getUuid();

        this.sendHandler = this.sendHandler.bind(this);
        this.showStart = this.showStart.bind(this);

        this.showFeedbackModal = this.showFeedbackModal.bind(this);
        this.hideFeedbackModal = this.hideFeedbackModal.bind(this);
        this.showAboutModal = this.showAboutModal.bind(this);
        this.hideAboutModal = this.hideAboutModal.bind(this);

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
        this.setState({loading: true});
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
                this.setState({loading: false});
            },
            error: () => {
                this.setState({loading: false});
            }
        });
    }

    renderMessages(messages) {
        for (let index in messages) {
            setTimeout(() => {
                this.addMessage({
                    fromBot: true,
                    message: messages[index],
                    timestamp: new Date().toLocaleTimeString()
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
        this.setState(prevState => ({
            messages: prevState.messages.filter(message => message.message.messageType !== Constants.response.ResponseType.SMART_REPLY_MESSAGE)
        }));
    }

    sendHandler(message) {
        const messageObject = {
            username: this.props.username,
            message: message,
            fromBot: false,
            timestamp: new Date().toLocaleTimeString()
        };

        this.removeSmartReplies();
        this.addMessage(messageObject);
        this.makeRequest(message);
    }

    addMessage(message) {
        this.setState(prevState => ({
            messages: [...prevState.messages, message]
        }));
    }

    clearChatHistory() {
        this.setState({messages: []});
    }

    addChatHistory(msgs) {
        this.setState(prevState => ({
            messages: [...prevState.messages, ...msgs]
        }));
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
                    showStart={this.showStart} />
            </div>
        );
    }
}

ChatApp.defaultProps = {
    isAdmin: false
};

export default ChatApp;
