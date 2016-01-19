package com.easymargining.replication.eurex;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "com.easymargining.replication.eurex"} )
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class DefaultConfig {

}
