import React from 'react';
import Modal from 'react-modal';

class Feedback extends React.Component {
    getUrlParam(name) {
        return decodeURIComponent(window.location.search.replace(new RegExp("^(?:.*[&\\?]" + encodeURIComponent(name).replace(/[\.\+\*]/g, "\\$&") + "(?:\\=([^&]*))?)?.*$", "i"), "$1"));
    }

    constructor(props) {
        super(props);
        this.state = {submitDisabled: true, title: '', description: ''};

        if(this.props.userId == null) {
            this.userId = this.getUrlParam("userId");
        }
        else {
            this.userId = this.props.userId;
        }

        this.titleChangeHandler = this.titleChangeHandler.bind(this);
        this.descriptionChangeHandler = this.descriptionChangeHandler.bind(this);
        this.onCloseClick = this.onCloseClick.bind(this);
        this.onSubmitClick = this.onSubmitClick.bind(this);
    }

    setSubmitState() {
        this.setState({submitDisabled: this.state.title.length < 5 || this.state.description.length < 20});
    }

    titleChangeHandler(event) {
        this.setState({title: event.target.value});
        this.setSubmitState();
    }

    descriptionChangeHandler(event) {
        this.setState({description: event.target.value});
        this.setSubmitState();
    }

    onCloseClick(event) {
        event.preventDefault();
        if(this.props.hide) {
            this.props.hide();
        }
    }

    onSubmitClick(event) {
        event.preventDefault();
        if(this.state.submitDisabled) {
            $.snackbar({content: 'Please provide more information'});
        }
        else {
            var submit_snackbar = $.snackbar({content: 'Submitting...', timeout: 2000});
            $.ajax({
                type: 'POST',
                url: '/feedback',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Content-Type': 'application/json; charset=utf-8'
                },
                dataType: 'text',
                data: JSON.stringify({title: this.state.title, description: this.state.description, userId: this.userId}),
                success: (response) => {
                    this.setState({title: '', description: ''});
                    submit_snackbar.snackbar('hide');
                    $.snackbar({content: 'Feedback Submitted Successfully', timeout: 5000});
                },
                error: () => {
                    submit_snackbar.snackbar('hide');
                    $.snackbar({content: 'Error! Feedback could not be submitted. Please try again.', timeout: 5000});
                }
            });
            this.props.hide();
        }
    }

    render() {
        let submitClass = "btn btn-raised btn-primary" + (this.state.submitDisabled ? ' disabled' : '');
        return (
            <div>
                <Modal
                    isOpen={this.props.isOpen}
                    contentLabel="Feedback Modal"
                    style={{content: {border: 'none', background: 'none', top: 20, left: 20, right: 20, bottom: 20}}}>

                    <div className="card feedback-container slideDown">
                        <form>
                            <fieldset>
                                <legend>Feedback</legend>
                                <div className="form-group">
                                    <input type="text"
                                        value={this.state.title}
                                        onChange={this.titleChangeHandler}
                                        className="form-control"
                                        placeholder="Provide a short title (min 5 chars)" />
                                </div>
                                <div className="form-group">
                                    <textarea
                                        value={this.state.description}
                                        className="form-control"
                                        onChange={this.descriptionChangeHandler}
                                        placeholder="Please describe the issue or help us by suggesting a feature (min 20 chars)"
                                        rows="3" />
                                </div>

                                <div className="form-group">
                                    <button onClick={this.onSubmitClick} className={submitClass}>Submit</button>
                                    {this.props.hide && (<button onClick={this.onCloseClick} className="btn btn-raised btn-danger">Cancel</button>)}
                                </div>
                            </fieldset>
                        </form>
                    </div>
                </Modal>
            </div>
        )
    }
}

Feedback.defaultProps = {
    userId: null
};
export default Feedback;