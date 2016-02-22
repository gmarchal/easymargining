package com.easymargining.tools.eurex;

import com.easymargining.replication.eurex.domain.model.EurexProductDefinition;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by gmarchal on 17/02/2016.
 */
public class EurexProductDefinitionLoader {

    public static void main(String[] args) {

        // load Eurex Product file, and import him into MondoDB for Product Referential Services.

        try {
            // Parse Eurex File
            List<EurexProductDefinition> productDefinitions =
                    EurexProductDefinitionParser.parse(new File("").toURI().toURL());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
