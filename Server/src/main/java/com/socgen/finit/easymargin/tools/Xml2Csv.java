package com.socgen.finit.easymargin.tools;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

class Xml2Csv {

    public static void main(String args[]) throws Exception {
        //File stylesheet = new File("Server/src/main/resources/classfilestyle.xsl");
        //File xmlSource = new File("Server/src/main/resources/classfile.xml");
        File stylesheet = new File("Server/src/main/resources/riskarraystyle.xsl");
        File xmlSource = new File("Server/src/main/resources/riskarray.xml");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlSource);

        StreamSource stylesource = new StreamSource(stylesheet);
        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer(stylesource);
        Source source = new DOMSource(document);
        //Result outputTarget = new StreamResult(new File("Server/src/main/resources/classfile.csv"));
        Result outputTarget = new StreamResult(new File("Server/src/main/resources/riskarray.csv"));
        transformer.transform(source, outputTarget);
    }
}