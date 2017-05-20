import React from 'react';

class Message extends React.Component {
    render() {
        const fromMe = this.props.fromMe ? 'pull-right bubble-user' : 'pull-left bubble-bot';

        let botDiv = ""
        if (!this.props.fromMe) {
            botDiv = (
                <div className="btn btn-default btn-fab pull-left fadeIn bot-icon">
                    <img src="/images/icon-45.jpg" />
                </div>
            )
        }

        return(
            <div className="container-fluid">
                {botDiv}
                <div className={`bubble card pullUp ${fromMe}`}>
                    <div>
                        {this.props.message}
                    </div>
                </div>
            </div>
        );
    }
}

Message.defaultProps = {
    message: '',
    username: '',
    fromMe: true
};

export default Message;