import React from 'react';
import Modal from 'react-modal';
import ChatApp from '../chat/ChatApp.jsx';

class Embed extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            isChatOpen: false,
            startChat: false
        }
        window.parent.postMessage('dbpedia-chatbot-embed-loaded', document.referrer);
        this.toggleChat = this.toggleChat.bind(this);
    }

    toggleChat() {
        this.setState({isChatOpen: !this.state.isChatOpen, startChat: true});
    }

    render() {
        return (
            <div className="embed-container full-height">
                <div className="embed-chat-container">
                    <div className={this.state.isChatOpen ? "full-height" : "hide"}>
                        {this.state.startChat && (<ChatApp />) }
                    </div>
                </div>
                <div className="embed-chat-button-container">
                    <a className="embed-chat-button btn btn-raised btn-fab" onClick={this.toggleChat}>
                        <i className="material-icons">
                            {this.state.isChatOpen ? "clear" : "chat"}
                        </i>
                    </a>
                </div>
            </div>
        )
    }
}

Embed.defaultProps = {

};
export default Embed;
