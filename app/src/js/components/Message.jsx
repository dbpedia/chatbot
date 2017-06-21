import React from 'react';
import * as Constants from './Constants.jsx';

class Message extends React.Component {

    constructor(props) {
        super(props);
        this.onParamButtonClick = this.onParamButtonClick.bind(this);
    }

    renderBotDiv() {
        // If message is from bot and messageType not carousel then show the bot icon
        var excluded = [Constants.response.ResponseType.CAROUSEL_MESSAGE, Constants.response.ResponseType.SMART_REPLY_MESSAGE];

        // Only show DBpedia icon for messages which are from bot and not carousel or smart reply
        if (!this.props.fromMe && excluded.indexOf(this.props.message.messageType) == -1) {
            return (
                <div className="btn btn-default btn-fab btn-fab-mini pull-left fadeIn bot-icon">
                    <img src="/images/icon-dbpedia-35.jpg" />
                </div>
            );
        }
    }

    // Label is used internally to show what user has clicked on
    onParamButtonClick(event, uri, label) {
        event.preventDefault();
        this.props.onSend({
            messageType: Constants.request.RequestType.PARAMETER_MESSAGE,
            messageData: [{payload: uri, label: label}]
        })
    }

    render() {
        const fromMe = this.props.fromMe ? 'pull-right bubble-user' : 'pull-left bubble-bot';
        const messageData = this.props.message.messageData;
        let msgDiv = '';
        switch(this.props.message.messageType) {
            case Constants.request.RequestType.PARAMETER_MESSAGE:
                console.log(messageData[0]);
                msgDiv = (
                    <div className={`bubble card pullUp ${fromMe}`}>
                        {messageData[0].label}
                    </div>
                );
            break;
            case Constants.response.ResponseType.TEXT_MESSAGE:
                msgDiv = (
                    <div className={`bubble card pullUp ${fromMe}`}>
                        {messageData[0].text.split('\n').map((line, index) => {
                            return <div key={index}>{line}</div>;
                        })}
                    </div>
                );
            break;
            case Constants.response.ResponseType.SMART_REPLY_MESSAGE:
                var message = messageData[0];
                msgDiv = (
                    <div className={`smart-reply-container slideUp`}>
                        {message.smartReplies.map((reply, index) => {
                            return <a key={index} href="#" className="smart-reply" data-param={reply.uri} onClick={(event) => this.onParamButtonClick(event, reply.uri, reply.title)}>{reply.title}</a>
                        })}
                    </div>
                );
            break;
            case Constants.response.ResponseType.BUTTON_TEXT_MESSAGE:
                var message = messageData[0];
                msgDiv = (
                    <div className={`bubble card pullUp ${fromMe}`}>
                        {message.text.split('\n').map((line, index) => {
                            return <div key={index}>{line}</div>;
                        })}

                        { message.buttons.length > 0 && (
                            <div className="button-group">
                                {message.buttons.map((button, index) => {
                                    switch(button.buttonType) {
                                        case Constants.response.ResponseType.BUTTON_LINK:
                                            return <a key={index} href={button.uri} target="_blank" className="btn btn-block btn-raised btn-info">{button.title}</a>
                                        case Constants.response.ResponseType.BUTTON_PARAM:
                                            return <a key={index} href="#" data-param={button.uri} onClick={(event) => this.onParamButtonClick(event, button.uri, button.title)} className="btn btn-block btn-raised btn-info">{button.title}</a>
                                    }
                                }
                                )}
                            </div>
                        )}
                    </div>
                );
            break;
            case Constants.response.ResponseType.CAROUSEL_MESSAGE:
                msgDiv = (
                    <div className="carousel-container slideLeft">
                        {messageData.map((message, index) => {
                            return <div key={index} className="carousel-item">
                                <div className="card">
                                    {message.image && (
                                        <div className="img-wrapper">
                                            <img src={message.image} />
                                        </div>
                                    )}
                                    <div className="content">
                                        {message.title && (
                                            <div className="title wrap-word">
                                                {message.title}
                                            </div>
                                        )}

                                        {message.text && (
                                            <div className="text wrap-word">
                                                {message.text.split('\n').map((line, index) => {
                                                    return <div key={index}>{line}</div>;
                                                })}
                                            </div>
                                        )}
                                    </div>

                                    { message.buttons.length > 0 && (
                                    <div className="button-group">
                                        {message.buttons.map((button, index) => {
                                            switch(button.buttonType) {
                                                case Constants.response.ResponseType.BUTTON_LINK:
                                                    return <a key={index} href={button.uri} target="_blank" className="btn btn-block btn-primary">{button.title}</a>
                                                case Constants.response.ResponseType.BUTTON_PARAM:
                                                    return <a key={index} href="#" data-param={button.uri} onClick={(event) => this.onParamButtonClick(event, button.uri, button.title)} className="btn btn-block btn-primary">{button.title}</a>
                                            }
                                        }
                                        )}
                                    </div>
                                    )}
                                </div>
                            </div>

                        })}
                    </div>
                );
            break;
            case Constants.response.ResponseType.LOADING_MESSAGE:
                msgDiv = (
                    <div className={`bubble card pullUp ${fromMe}`}>
                        <div className="loading-dot dot-1"></div>
                        <div className="loading-dot dot-2"></div>
                        <div className="loading-dot dot-3"></div>
                    </div>
                );
            break;
        }

        return(
            <div className="container-fluid">
                {this.renderBotDiv()}
                {msgDiv}
            </div>
        );
    }
}

Message.defaultProps = {
    message: [], // {messageType: string, messageData: object}
    username: '',
    fromMe: true
};

export default Message;