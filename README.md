## WeahterRiskApi
#   1. 專案結構
+       public/: 放前端版面的code
+           assets/: 放前端resource
+               js/: 自己寫的js 
+           css/:
+           scss/:自己寫的scss
+       .bowerrc: 前端管理設定
#  2.  branch
+       master: 主要的java code
+       front_end: 前端的code
#  3.  Team Member
+       front_end: yuanyu90221
+       backend: Tommy哥
#  4.  取得branch方式
+       front_end部分: `git pull https://github.com/TommyYehCool/WeatherRiskApi.git front_end`
+       JavaCode部分:  `git pull https://github.com/TommyYehCool/WeatherRiskApi.git master`
#  5.  切換branch: `git checkout $branch_name`
+       front_end部分: `git checkout front_end`
+       JavaCode部分:  `git checkout master`
#  6.  push Code: 先切換到相對應 branch 在push
+       front_end部分: `git checkout front_end; git add .;git commit -m "commit message"; git push origin front_end`
+       JavaCode部分: `git checkout master; git add .;git commit -m "commit message";git push origin master`