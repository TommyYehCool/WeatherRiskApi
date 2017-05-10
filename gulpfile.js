// Sass configuration
var gulp = require('gulp');
var sass = require('gulp-sass');

gulp.task('sass', function() {
    gulp.src('./public/assets/scss/**/*.scss')
        .pipe(sass({ outputStyle: 'expanded' }))
        .pipe(gulp.dest('./assets/css/'))
});

gulp.task('default', ['sass'], function() {
    gulp.watch('./bublic/assets/scss/**/*.scss', ['sass']);
});