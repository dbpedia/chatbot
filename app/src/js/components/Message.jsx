import React from 'react';

class Message extends React.Component {
    render() {
        const fromMe = this.props.fromMe ? 'pull-right' : '';

        return(
            <div className="container-fluid">
                <div className={`bubble ${fromMe}`}>
                    <div>

                    </div>
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