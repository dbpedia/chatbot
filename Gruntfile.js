
// http://billpatrianakos.me/blog/2017/02/03/using-react-with-webpack-and-es6-in-a-grunt-task/

module.exports = function(grunt) {
    var webpackConfig = require('./webpack.config');
    grunt.loadNpmTasks('grunt-webpack');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-watch');

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        webpack: {
            prod: webpackConfig,
            dev: webpackConfig
        },
        less: {
            default: {
                options: {
                    compress: true
                },
                files: {
                    './src/main/webapp/css/app.css': './app/src/less/app.less'
                }
            }
        },
        watch: {
            files: './app/src/less/**/*.less',
            tasks: ['less']
        }
    });

    grunt.registerTask('default', ['webpack', 'less']);

    // Primarily Used for Development
    grunt.registerTask('js', ['webpack']);
    grunt.registerTask('css', ['watch']);
};