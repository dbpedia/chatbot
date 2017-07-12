const path = require('path');
module.exports = {
  entry: {
    app: './app/src/js/index.js',
    feedback: './app/src/js/feedback.js'
  },
  output: {
    path: path.resolve('./src/main/app/js/'),
    filename: '[name].js'
  },
  module: {
    loaders: [
      { test: /\.js$/, loader: 'babel-loader', exclude: /node_modules/ },
      { test: /\.jsx$/, loader: 'babel-loader', exclude: /node_modules/ },
      { test: /\.less$/, loaders: ['style-loader', 'css-loader', 'less-loader'] }
    ]
  }
}