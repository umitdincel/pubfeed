module.exports = (grunt)->

	grunt.initConfig {
		watch:
			less:
				files: ['less/*.less']
				tasks: ['less:development']
		less:
			development:
				files:
					"../web-backend/build/static/css/main.css": "less/main.less"
			production:
				options:
					compress: true
				files:
					"css/main.css": "less/main.less"
	}

	grunt.loadNpmTasks('grunt-contrib-less')
	grunt.loadNpmTasks('grunt-contrib-watch')