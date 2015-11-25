package com.socgen.finit.easymargin;

import com.socgen.finit.easymargin.model.Request;
import com.socgen.finit.easymargin.web.EurexPrismaController;
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
