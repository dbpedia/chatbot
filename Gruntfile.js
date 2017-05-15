
// http://billpatrianakos.me/blog/2017/02/03/using-react-with-webpack-and-es6-in-a-grunt-task/

module.exports = function(grunt) {
    var webpackConfig = require('./webpack.config');
    grunt.loadNpmTasks('grunt-webpack');

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
//        babel: {
//            options: {
//                presets: ['es2015', 'react']
//            },
//            dist: {
//                files: [
//                {
//                    'expand': true,
//                    'cwd': 'app/src/js',
//                    'src': '',
//                    'dest': 'src/main/resources/static/js/'
//                }
//                ]
//            }
//        },
        webpack: {
            prod: webpackConfig,
            dev: webpackConfig
//              build: {
//                entry: ['./app/src/js/scripts.js'],
//                output: {
//                  path: './app/src/js/',
//                  filename: 'build.js'
//                },
//                stats: {
//                  colors: false,
//                  modules: true,
//                  reasons: true
//                },
//                storeStatsTo: 'webpackStats',
//                progress: true,
//                failOnError: true,
//                watch: true,
//                module: {
//                  loaders: [
//                    { test: /\.js$/, exclude: /node_modules/, loader: "babel-loader" }
//                  ]
//                }
//              }
        }
    });

    grunt.registerTask('default', ['webpack']);
};