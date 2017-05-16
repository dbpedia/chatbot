import React from 'react';

class ChatInput extends React.Component {
    constructor(props) {
        super(props);
        this.state = {chatInput: ''};

        this.submitHandler = this.submitHandler.bind(this);
        this.textChangeHandler = this.textChangeHandler.bind(this);
    }

    submitHandler(event) {
        event.preventDefault();
        if(this.state.chatInput != '') {
            this.setState({chatInput: ''});
            this.props.onSend(this.state.chatInput);
        }
    }

    textChangeHandler(event) {
        this.setState({chatInput: event.target.value});
    }

    render() {
        return (
            <form onSubmit={this.submitHandler} style={{padding: 5}}>
                <fieldset>
                    <div className="form-group">
                        <input type="text"
                               className="form-control"
                               id="chat-input"
                               onChange={this.textChangeHandler}
                               value={this.state.chatInput}
                               placeholder="Write a message"
                               />
                    </div>
                </fieldset>
            </form>
        );
    }
}

ChatInput.defaultProps = {
    chatInput: ''
};

export default ChatInput;