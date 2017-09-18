require('../less/pages/feedback.less');

import React from 'react';
import ReactDOM from 'react-dom';
import Feedback from './components/chat/Feedback.jsx';

ReactDOM.render('<Feedback isOpen={true} />', document.getElementById('feedback-container'));
