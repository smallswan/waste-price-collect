package com.zhiluniao.jobs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;


/**
 * 
 *
 * @author Administrator<br>
 *         2017年12月27日  下午11:59:34
 */
@Slf4j
@SpringBootApplication
@ComponentScan({"com.zhiluniao.jobs"})
public class StartApplication {
    public static void main( String[] args )
    {
        String tmpdir = System.getProperty("java.io.tmpdir");
        log.info("java.io.tmpdir : {}",tmpdir);
        SpringApplication.run(StartApplication.class, args);
    }
}
