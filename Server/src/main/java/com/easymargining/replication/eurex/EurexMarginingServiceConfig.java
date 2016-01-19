package com.easymargining.replication.eurex;

import com.easymargining.replication.eurex.domain.model.Request;
import com.easymargining.replication.eurex.controller.EurexPrismaController;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Gilles Marchal on 24/11/2015.
 */
@Configuration
@ComponentScan(basePackageClasses = {
        EurexPrismaController.class,
        Request.class
})
public class EurexMarginingServiceConfig {

}
