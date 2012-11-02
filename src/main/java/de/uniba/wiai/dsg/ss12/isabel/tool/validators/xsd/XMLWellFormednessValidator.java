package de.uniba.wiai.dsg.ss12.isabel.tool.validators.xsd;

import de.uniba.wiai.dsg.ss12.isabel.tool.ValidationException;
import org.pmw.tinylog.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XMLWellFormednessValidator {

    private static DocumentBuilderFactory dbf = DocumentBuilderFactory
            .newInstance();

    public void validate(String file) throws ValidationException {
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.parse(new File(file));
            Logger.info("File " + file + " is a valid XML file");
        } catch (Exception e) {
           throw new ValidationException("The file " + file + " is not well formed", e);
        }
    }

}