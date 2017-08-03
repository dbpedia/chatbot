import React from 'react';
import * as Constants from '../Constants.jsx';

class Message extends React.Component {

    constructor(props) {
        super(props);
        this.onParamButtonClick = this.onParamButtonClick.bind(this);
    }

    renderBotDiv() {
        // If message is from bot and messageType not carousel then show the bot icon
        var excluded = [Constants.response.ResponseType.GENERIC_MESSAGE];

        // Only show DBpedia icon for messages which are from bot and not carousel or smart reply
        if (this.props.fromBot && excluded.indexOf(this.props.message.messageType) == -1) {
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
        if (!this.props.isAdmin) {
            this.props.onSend({
                messageType: Constants.request.RequestType.PARAMETER_MESSAGE,
                messageData: [{payload: uri, label: label}]
            });
        }
    }

    render() {
        const fromBot = this.props.fromBot ? 'pull-left bubble-bot' : 'pull-right bubble-user';
        const messageData = this.props.message.messageData;
        let msgDiv = '';
        switch(this.props.message.messageType) {
            case Constants.request.RequestType.PARAMETER_MESSAGE:
                msgDiv = (
                    <div className={`bubble card pullUp ${fromBot}`}>
                        {messageData[0].label || messageData[0].payload}
                    </div>
                );
            break;
            case Constants.response.ResponseType.TEXT_MESSAGE:
                msgDiv = (
                    <div className={`bubble card pullUp ${fromBot}`}>
                        {messageData[0].text.split('\n').map((line, index) => {
                            return <div key={index}>{line}</div>;
                        })}
                    </div>
                );
            break;
            case Constants.response.ResponseType.SMART_REPLY_MESSAGE:
                var message = messageData[0];
                msgDiv = (
                    <div>
                        <div className={`bubble card pullUp ${fromBot}`}>
                            {message.text}
                        </div>
                        {message.smartReplies && (<div className={`smart-reply-container slideUp`}>
                            {message.smartReplies.map((reply, index) => {
                                return <a key={index} href="#" className="smart-reply" onClick={(event) => this.onParamButtonClick(event, reply.uri, reply.title)}>{reply.title}</a>
                            })}
                        </div>)}
                    </div>
                );
            break;
            case Constants.response.ResponseType.BUTTON_TEXT_MESSAGE:
                var message = messageData[0];
                msgDiv = (
                    <div className={`bubble card pullUp ${fromBot}`}>
                        {message.text.split('\n').map((line, index) => {
                            return <div key={index}>{line}</div>;
                        })}

                        { message.buttons.length > 0 && (
                            <div className="button-group">
                                {message.buttons.map((button, index) => {
                                    switch(button.buttonType) {
                                        case Constants.response.ResponseType.BUTTON_LINK:
                                            return <a key={index} href={button.uri} target="_blank" className="btn btn-block btn-raised btn-info">
                                                {button.title}
                                                <i className="material-icons">launch</i>
                                            </a>
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
            case 'carousel': // Added for Backwards Compatibility can be removed later if we don't have lot of history for carousel
            case Constants.response.ResponseType.GENERIC_MESSAGE:
                msgDiv = (
                    <div className="carousel-container slideLeft">
                        {messageData.map((message, index) => {
                            return <div key={index} className="carousel-item">
                                <div className="card">
                                    <div className="summary">
                                        <div className="img-wrapper">
                                            <img src={message.image} />
                                        </div>
                                        <div className="content">
                                            {message.title && (
                                                <div className="title wrap-word">
                                                    {message.title}
                                                </div>
                                            )}

                                            {message.text && (
                                                <div className="text wrap-word">
                                                    {message.text.substr(0, 200).split('\n').map((line, index) => {
                                                        return <div key={index}>{line}</div>;
                                                    })}
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                    { message.fields.length > 0 && (
                                    <div className="fields-group">
                                        {message.fields.map((field, index) => {
                                            var valueDiv;
                                            if(field.value) {
                                                valueDiv = (
                                                    <div className="field-value">{field.value}</div>
                                                );
                                            }
                                            else if(field.values) {
                                                valueDiv = (
                                                    <div className="field-value">
                                                        {Object.keys(field.values).map((key, index) => {
                                                            return <a className="wrap-word" key={index} href={key} target='_blank'>{field.values[key]}<br/></a>;
                                                        })}
                                                    </div>
                                                );
                                            }

                                            return <div key={index} className="field wrap-word">
                                                <div className="field-name wrap-word">{field.name}:</div>
                                                {valueDiv}
                                            </div>
                                        })}
                                    </div>
                                    )}

                                    { message.buttons.length > 0 && (
                                    <div className="button-group">
                                        {message.buttons.map((button, index) => {
                                            switch(button.buttonType) {
                                                case Constants.response.ResponseType.BUTTON_LINK:
                                                    return <a key={index} href={button.uri} target="_blank" className="btn btn-block btn-primary">
                                                        {button.title}
                                                        <i className="material-icons">launch</i>
                                                    </a>
                                                case Constants.response.ResponseType.BUTTON_PARAM:
                                                    return <a key={index} href="#" onClick={(event) => this.onParamButtonClick(event, button.uri, button.title)} className="btn btn-block btn-primary">{button.title}</a>
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
                    <div className={`bubble card pullUp ${fromBot}`}>
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
    fromBot: false
};

export default Message;