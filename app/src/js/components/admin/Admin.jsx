import React from 'react';

import ChatApp from '../chat/ChatApp.jsx';
import UserList from './UserList.jsx';

class Admin extends React.Component {
    constructor(props) {
        super();
        this.state = {userListPage: 1, userList: [], loading: false};

        this.loadMessages = this.loadMessages.bind(this);
        this.loadMoreMessages = this.loadMoreMessages.bind(this);
        this.loadUserList = this.loadUserList.bind(this);
        this.getUserList = this.getUserList.bind(this);
    }

    loadUserList(event, scenario) {
        if(!this.state.loading) {
            if(scenario == 'previous') {
                if(this.state.userListPage > 1){
                    this.getUserList(this.state.userListPage - 1);
                }
                else {
                    $.snackbar({content: 'You are in the first page', timeout: 5000});
                }
            }
            else {
                this.getUserList(this.state.userListPage + 1);
            }
        }
    }

    getUserList(page) {
        this.setState({userListPage: page, loading: true});
        $.ajax({
            type: 'POST',
            url: '/admin/user-list',
            headers: {
                'Accept': 'application/json; charset=utf-8'
            },
            dataType: 'text',
            data: {page: page},
            success: (response) => {
                response = JSON.parse(response);
                if(response.length == 0) {
                    $.snackbar({content: 'No More Users', timeout: 5000});
                    page = page > 1 ? page - 1 : 1;
                    this.setState({loading: false, userListPage: page});
                }
                else {
                    this.setState({userList: response, loading: false});
                }

            }
        });
    }

    loadMoreMessages() {
        if(this.state.chat && !this.state.loading) {
            this.loadMessages(this.state.chat.userId, ++this.state.chat.page);
        }
    }

    loadMessages(userId, page) {
        this.setState({chat: {userId: userId, page: page}, loading: true});
        $.ajax({
            type: 'POST',
            url: '/admin/chat-list',
            headers: {
                'Accept': 'application/json; charset=utf-8'
            },
            dataType: 'text',
            data: {userId: userId, page: page},
            success: (response) => {
                var messages = [], message;
                response = JSON.parse(response);
                for(var index in response) {
                    message = response[index].request ? response[index].request : response[index].response;
                    var timestamp = response[index].timestamp || new Date().toLocaleString();

                    // Array
                    if(message.length) {
                        for(var index2 in message) {
                            messages.push({fromBot: response[index].fromBot, message: message[index2], timestamp: timestamp});
                        }
                    }
                    // Object
                    else {
                        messages.push({fromBot: response[index].fromBot, message: message, timestamp: timestamp});
                    }
                }
                if(page == 1) {
                    this.refs.chatApp.clearChatHistory();
                }
                if(messages.length > 0) {
                    this.refs.chatApp.addChatHistory(messages);
                }
                else {
                    $.snackbar({content: 'No More Chats', timeout: 5000});
                }
                this.setState({loading: false});
            }
        });
    }

    componentDidMount() {
        this.getUserList(this.state.userListPage);
    }

    render() {
        return (
            <div>
                <div className="col-md-6">
                    <UserList userList={this.state.userList}
                     loadMessages={this.loadMessages} />
                     <a className="btn btn-primary btn-raised" href="#" onClick={(event) => this.loadUserList(event, 'previous')}>Previous</a>
                     <a className="btn btn-primary btn-raised" href="#" onClick={(event) => this.loadUserList(event, 'next')}>Next</a>
                </div>
                <div className="col-md-6">
                    <ChatApp height={500} isAdmin={true} ref="chatApp" />
                    {this.state.chat && (<a className="btn btn-primary btn-raised" href="#" onClick={this.loadMoreMessages}>Load More</a>)}
                </div>
            </div>
        )
    }
}

export default Admin;
