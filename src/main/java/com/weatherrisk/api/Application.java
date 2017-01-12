package com.weatherrisk.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * <pre>
 * 程式啟動點 
 * </pre>
 * 
 * @author tommy.feng
 *
 */
//@Configuration // 這邊使用 Java Class 作為設定，而非XML
//@EnableAutoConfiguration // 啟用 Spring Boot 自動配置，將自動判斷專案使用到的套件，建立相關的設定。
//@ComponentScan( basePackages = {"com.weatherrisk.api"} ) // 自動掃描 Spring Bean 元件
@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class Application extends SpringBootServletInitializer {
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
    }
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
}
