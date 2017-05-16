const path = require('path');
module.exports = {
  entry: './app/src/js/index.js',
  // watch: './app/src/js/**/*.js',
  output: {
    // path: path.resolve('./src/main/resources/static/js/'),
    path: path.resolve('./src/main/webapp/js/'),
    filename: 'app.js'
  },
  module: {
    loaders: [
      { test: /\.js$/, loader: 'babel-loader', exclude: /node_modules/ },
      { test: /\.jsx$/, loader: 'babel-loader', exclude: /node_modules/ }
    ]
  }
}