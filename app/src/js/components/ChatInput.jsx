import React from 'react';
import * as Constants from './Constants.jsx';

class ChatInput extends React.Component {

    constructor(props) {
        super(props);
        this.inputMargin = 60;

        this.state = {
            chatInput: '',
            showSendButton: false,
            sendDisabled: true,
            inputMargin: this.inputMargin,
            isFlyoutOpen: false
        };

        this.submitHandler = this.submitHandler.bind(this);
        this.textChangeHandler = this.textChangeHandler.bind(this);
        this.inputFocus = this.inputFocus.bind(this);
        this.inputBlur = this.inputBlur.bind(this);
        this.toggleFlyout = this.toggleFlyout.bind(this);
        this.onFlyoutOptionClick = this.onFlyoutOptionClick.bind(this);
    }

    submitHandler(event) {
        event.preventDefault();
        if(this.state.chatInput != '') {
            this.setState({chatInput: '', sendDisabled: true, showSendButton: false, inputMargin: this.inputMargin});
            this.props.onSend({
                messageType: Constants.request.RequestType.TEXT_MESSAGE,
                messageData: [{text: this.state.chatInput}]
            });
        }
    }

    toggleOverlay(isFlyoutOpen) {
        if (isFlyoutOpen) {
            this.props.showOverlay();
        }
        else {
            this.props.hideOverlay();
        }
    }

    toggleFlyout() {
        let isFlyoutOpen = !this.state.isFlyoutOpen;
        this.setState({isFlyoutOpen: isFlyoutOpen});
        this.toggleOverlay(isFlyoutOpen);
    }

    inputFocus() {
        this.setState({inputMargin: 0, isFlyoutOpen: false, showSendButton: true});
    }

    inputBlur() {
        let showSendButton = this.state.chatInput.length > 0;
        let inputMargin = showSendButton ? 0 : this.inputMargin;
        // Do not hide send button if user has typed something
        this.setState({inputMargin: inputMargin, showSendButton: showSendButton});
    }

    textChangeHandler(event) {
        let value = event.target.value;
        this.setState({chatInput: value, sendDisabled: value == ''});
    }

    onFlyoutOptionClick(event, type) {
        this.setState({isFlyoutOpen: false});
        switch(type) {
            case 'feedback':
                this.props.showFeedback();
            break;
            case 'start':
                this.props.showStart(true);
            break;
        }
        this.toggleOverlay(false);
    }

    render() {
        let sendButtonClass = 'btn btn-fab btn-raised send-button' + (this.state.sendDisabled ? ' disabled' : '');
        let moreButtonClass = 'btn btn-fab btn-raised more-button';
        let moreButtonOptionClass = 'btn btn-raised btn-fab btn-fab-mini';
        let flyoutClass = 'more-button-container' + (this.state.isFlyoutOpen ? ' active' : '');

        return (
            <form onSubmit={this.submitHandler} id="chat-input-container">
                <fieldset>
                    <div className="form-group">
                        {this.state.inputMargin > 0 && (
                            <nav className={flyoutClass}>
                                <a onClick={(event) => this.onFlyoutOptionClick(event, 'start')} className="button">
                                    <div className={`${moreButtonOptionClass} btn-info`}>
                                        <img src="/images/icon-restart-32.png" />
                                    </div>
                                    <span>Start Over</span>
                                </a>
                                <a onClick={(event) => this.onFlyoutOptionClick(event, 'feedback')} className="button">
                                    <div className={`${moreButtonOptionClass} btn-danger`}>
                                        <img src="/images/icon-feedback-32.png" />
                                    </div>
                                    <span>Feedback</span>
                                </a>
                                <a className={moreButtonClass} onClick={this.toggleFlyout}>
                                    <img src="/images/icon-plus-32.png" />
                                </a>
                            </nav>
                        )}

                        <input type="text"
                               className="form-control"
                               id="chat-input"
                               onChange={this.textChangeHandler}
                               value={this.state.chatInput}
                               placeholder="Write a message"
                               autoComplete="off"
                               style={{marginLeft: this.state.inputMargin}}
                               onFocus={this.inputFocus}
                               onBlur={this.inputBlur}
                               />

                        {this.state.showSendButton && (
                            <a href="#" onClick={this.submitHandler} className={sendButtonClass}>
                                 <img src="/images/icon-send-32.png" />
                            </a>
                        )}
                    </div>
                </fieldset>
            </form>
        );
    }
}

ChatInput.defaultProps = {

};

export default ChatInput;