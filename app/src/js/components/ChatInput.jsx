import React from 'react';

class ChatInput extends React.Component {
    constructor(props) {
        super(props);
        this.state = {chatInput: '', sendDisabled: true};

        this.submitHandler = this.submitHandler.bind(this);
        this.textChangeHandler = this.textChangeHandler.bind(this);
    }

    submitHandler(event) {
        event.preventDefault();
        if(this.state.chatInput != '') {
            this.setState({chatInput: '', sendDisabled: true});
            this.props.onSend(this.state.chatInput);
        }
    }

    textChangeHandler(event) {
        let value = event.target.value;
        this.setState({chatInput: value, sendDisabled: value == ''});
    }

    render() {
        let sendButtonClass = 'btn btn-primary btn-fab btn-fab send-button' + (this.state.sendDisabled ? ' disabled' : '');
        return (
            <form onSubmit={this.submitHandler} style={{padding: 5}} id="chat-input-container">
                <fieldset>
                    <div className="form-group">
                        <input type="text"
                               className="form-control"
                               id="chat-input"
                               onChange={this.textChangeHandler}
                               value={this.state.chatInput}
                               placeholder="Write a message"
                               />
                               <a href="#" onClick={this.submitHandler} className={sendButtonClass}>
                                    <img src="/images/icon-send-32.jpg" />
                               </a>
                    </div>
                </fieldset>
            </form>
        );
    }
}

ChatInput.defaultProps = {
    chatInput: '',
    sendDisabled: true
};

export default ChatInput;