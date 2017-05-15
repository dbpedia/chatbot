const path = require('path');
module.exports = {
  entry: './app/src/js/index.js',
  output: {
    path: path.resolve('./src/main/resources/static/js/'),
    filename: 'app.js'
  },
  module: {
    loaders: [
      { test: /\.js$/, loader: 'babel-loader', exclude: /node_modules/ },
      { test: /\.jsx$/, loader: 'babel-loader', exclude: /node_modules/ }
    ]
  }
}