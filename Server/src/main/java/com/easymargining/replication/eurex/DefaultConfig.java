package com.easymargining.replication.eurex;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "com.socgen.finit.easymargin", "com.easymargining.replication.ccg"} )
@EnableAutoConfiguration
public class DefaultConfig {

}
