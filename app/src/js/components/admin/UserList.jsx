import React from 'react';

import * as Constants from '../Constants.jsx';

class UserList extends React.Component {
    constructor(props) {
        super();
        this.loadMessages = this.loadMessages.bind(this);
    }

    loadMessages(e, userId) {
        e.preventDefault();
        this.props.loadMessages(userId, 1);
    }

    render() {
        return (
            <div className="card">
                <table className="table table-striped table-hover ">
                    <thead>
                      <tr>
                        <th className="vertical-align-middle text-center">User ID</th>
                        <th className="vertical-align-middle text-center">Total Conversation Length</th>
                        <th className="vertical-align-middle text-center">Last Interaction Time</th>
                        <th className="vertical-align-middle text-center">Platform</th>
                        <th className="vertical-align-middle text-center">View Conversation</th>
                      </tr>
                      </thead>
                      <tbody>
                        {this.props.userList.map((row, index) => {
                            return (
                                <tr key={index} className="info">
                                    <td className="vertical-align-middle">{row.userId}</td>
                                    <td className="text-center vertical-align-middle">{row.count}</td>
                                    <td className="text-center vertical-align-middle">{row.last_timestamp}</td>
                                    <td className="text-center vertical-align-middle">{row.platform}</td>
                                    <td><a href="#" className="btn btn-primary" onClick={(event) => this.loadMessages(event, row.userId)}>View</a></td>
                                </tr>
                            )
                        })}
                      </tbody>
                </table>
            </div>
        )
    }
}

export default UserList;