require('../less/pages/admin.less');
require('../less/pages/app.less');

import React from 'react';
import ReactDOM from 'react-dom';
import Admin from './components/admin/Admin.jsx';

ReactDOM.render(<Admin />, document.getElementById('admin-container'));