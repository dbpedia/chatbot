import React from 'react';

class Message extends React.Component {

    renderBotDiv() {
        // If message is from bot and messageType not carousel then show the bot icon
        if (!this.props.fromMe && this.props.message.messageType.indexOf(['carousel']) == -1) {
            return (
                <div className="btn btn-default btn-fab btn-fab-mini pull-left fadeIn bot-icon">
                    <img src="/images/icon-bot-35.jpg" />
                </div>
            );
        }
    }

    render() {
        const fromMe = this.props.fromMe ? 'pull-right bubble-user' : 'pull-left bubble-bot';
        const messageData = this.props.message.messageData;
        let msgDiv = '';
        switch(this.props.message.messageType) {
            case 'text':
                msgDiv = (
                    <div className={`bubble card pullUp ${fromMe}`}>
                        {messageData[0].text.split('\n').map((line, index) => {
                            return <div key={index}>{line}</div>;
                        })}
                    </div>
                );
            break;
            case 'button_text':
                var message = messageData[0];
                msgDiv = (
                    <div className={`bubble card pullUp ${fromMe}`}>
                        {message.text.split('\n').map((line, index) => {
                            return <div key={index}>{line}</div>;
                        })}

                        { message.buttons.length > 0 && (
                            <div className="button-group">
                                {message.buttons.map((button, index) => {
                                    return <a key={index} href={button.uri} target="_blank" className="btn btn-block btn-raised btn-info">{button.title}</a>
                                }
                                )}
                            </div>
                        )}
                    </div>
                );
            break;
            case 'carousel':
                msgDiv = (
                    <div className="carousel-container slideLeft">
                        {messageData.map((message, index) => {
                            return <div key={index} className="carousel-item">
                                <div className="card">
                                    {message.image && (
                                        <img src={message.image} />
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
                                            return <a key={index} href={button.uri} target="_blank" className="btn btn-block btn-primary">{button.title}</a>
                                        }
                                        )}
                                    </div>
                                    )}
                                </div>
                            </div>

                        })}
                    </div>
                );
                //this.renderCarouselDiv(this.props.message.messageData);
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