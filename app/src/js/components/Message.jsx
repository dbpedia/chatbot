import React from 'react';

class Message extends React.Component {
    render() {
        const fromMe = this.props.fromMe ? 'pull-right bubble-user' : 'pull-left bubble-bot';
        let botDiv = '';
        if (!this.props.fromMe) {
            botDiv = (
                <div className="btn btn-default btn-fab btn-fab-mini pull-left fadeIn bot-icon">
                    <img src="/images/icon-bot-35.jpg" />
                </div>
            );
        }

        let msgDiv = '';
        switch(this.props.message.messageType) {
            case 'text':
                msgDiv = (
                    <div className={`bubble card pullUp ${fromMe}`}>
                        <div>
                            {this.props.message.messageData[0].text}
                        </div>
                    </div>
                );
            break;
        }

        return(
            <div className="container-fluid">
                {botDiv}
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