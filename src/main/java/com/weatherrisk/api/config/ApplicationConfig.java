package com.weatherrisk.api.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <pre>
 * 整個 Application 的設定
 * 
 * 1. @EnableScheduling: 允許 scheduling
 * 2. @EnableCaching: 允許 caching
 * </pre>
 * 
 * @author tommy.feng
 *
 */
@Configuration
@EnableScheduling
@EnableCaching
public class ApplicationConfig {

}
