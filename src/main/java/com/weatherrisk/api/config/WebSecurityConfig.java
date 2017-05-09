package com.weatherrisk.api.config;

import static com.stormpath.spring.config.StormpathWebSecurityConfigurer.stormpath;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
		http
			.apply(stormpath())
		.and()
        	.authorizeRequests()
        	.antMatchers("/restricted").fullyAuthenticated()
        	.antMatchers("/swagger*").fullyAuthenticated()
        	.antMatchers("/**").permitAll();
    }
	
	@Override
    public void configure(WebSecurity web) throws Exception {
    	web
    		.ignoring()
    		.antMatchers("/buttons/**"); // 讓這些 static content 可以被讀取, PS: 一定要兩個 *, 因為有子目錄
	}
}
