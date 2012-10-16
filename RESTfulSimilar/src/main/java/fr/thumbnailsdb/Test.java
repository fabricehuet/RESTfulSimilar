package fr.thumbnailsdb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Test {

	@XmlElement
	String toto = "xxx";
	
}
