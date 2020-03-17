package io.redhatgov;

// Java Code to implement StAX parser 
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;

public class SrgHtmlMapper {
	// constants for parsing CCI attributes
	private static QName ID_QNAME = new QName("id");
	private static QName VERSION_QNAME = new QName("version");
	private static QName INDEX_QNAME = new QName("index");
	private static QName TITLE_QNAME = new QName("title");

	// map control to CCI to SRG
	private SortedMap<String, SortedSet<String>> controlToCCI = new TreeMap<String, SortedSet<String>>();
	private SortedMap<String, SortedSet<String>> cciToSRG = new TreeMap<String, SortedSet<String>>();

	// map SRG to CCI to control
	private SortedMap<String, SortedSet<String>> srgToCCI = new TreeMap<String, SortedSet<String>>();
	private SortedMap<String, SortedSet<String>> cciToControl = new TreeMap<String, SortedSet<String>>();

	public static void main(String[] args) throws Exception {
		// Get readers for the xml file names
        InputStream cciIn = SrgHtmlMapper.class.getResourceAsStream("/U_CCI_List.xml");
        BufferedReader cciReader = new BufferedReader(new InputStreamReader(cciIn));
        InputStream srgIn = SrgHtmlMapper.class.getResourceAsStream("/U_General_Purpose_Operating_System_SRG_V1R6_Manual-xccdf.xml");
        BufferedReader srgReader = new BufferedReader(new InputStreamReader(srgIn));

		SrgHtmlMapper mapper = new SrgHtmlMapper();
		mapper.parseCCI(cciReader);
		mapper.parseSRG(srgReader);

		// write the control to cci to srg file
		PrintWriter pwControlToSRG = new PrintWriter(new FileWriter("SP800-53ToCCIToSRG.html"));

		mapper.renderHTMLHeader(pwControlToSRG, "Map Control to CCI to SRG", "NIST 800-53 Control", "SRG");
		mapper.renderHTMLTable(pwControlToSRG, mapper.controlToCCI, mapper.cciToSRG);
		mapper.renderHTMLFooter(pwControlToSRG);

		pwControlToSRG.flush();

		// write the srg to cci to control file
		PrintWriter pwSRGToControl = new PrintWriter(new FileWriter("SRGToCCIToSP800-53.html"));

		mapper.renderHTMLHeader(pwSRGToControl, "Map SRG to CCI to Control", "SRG", "NIST 800-53 Control");
		mapper.renderHTMLTable(pwSRGToControl, mapper.srgToCCI, mapper.cciToControl);
		mapper.renderHTMLFooter(pwSRGToControl);

		pwSRGToControl.flush();
	}
	
	void renderHTMLHeader(PrintWriter pw, String title, String firstCol, String thirdCol) {
		pw.println("<html><head><title>" + title + "</title></head>");
		pw.println("<body>");
		pw.println("<table border='1'>");
		pw.println(
				"<tr><th colspan='3'>Based on CCI version 2016-06-27 published on 2016-06-27 and U_General_Purpose_Operating_System_SRG_V1R6_Manual-xccdf.xml</th></tr>");
		pw.println("<tr><th colspan='3'/></tr>");
		pw.println("<tr><th>" + firstCol + "</th><th>CCI</th><th>" + thirdCol + "</th></tr>");
	}

	void renderHTMLFooter(PrintWriter pw) {
		pw.println("</table></body></html>");
	}

	void renderHTMLTable(PrintWriter pw, SortedMap<String, SortedSet<String>> firstColToCCI,
			SortedMap<String, SortedSet<String>> cciToThirdCol) {
		for (String firstKey : firstColToCCI.keySet()) {
			SortedSet<String> cciIds = firstColToCCI.get(firstKey);

			// count number of rows in third column for each row in first column
			int firstRowSpan = 0;
			for (String cciId : cciIds) {
				if (cciToThirdCol.get(cciId) != null) {
					firstRowSpan += cciToThirdCol.get(cciId).size();
				} else {
					firstRowSpan += 1;
				}
			}
		
			pw.println("<tr><td rowspan='" + firstRowSpan + "'>" + firstKey + "</td>");

			int i = 0;
			for (String cci : cciIds) {
				if (i > 0) {
					pw.print("<tr>");
				}

				SortedSet<String> rowsInThirdCol = cciToThirdCol.get(cci);

				if (rowsInThirdCol == null) {
					pw.println("<td>" + cci + "</td><td/></tr>");
				} else {
					int cciRowSpan = rowsInThirdCol.size();

					pw.println("<td rowspan='" + cciRowSpan + "'>" + cci + "</td>");

					int j = 0;
					for (String thirdCol : rowsInThirdCol) {
						if (j > 0) {
							pw.print("<tr>");
						}

						pw.println("<td>" + thirdCol + "</td></tr>");
						j++;
					}
				}
				i++;
			}
		}

	}

	void parseCCI(BufferedReader cciReader) throws FileNotFoundException, XMLStreamException {
		String cciId = null, nistReference;

		// Instance of the class which helps on reading tags
		XMLInputFactory factory = XMLInputFactory.newInstance();

		// Initializing the handler to access the tags in the XML file
		XMLEventReader eventReader = factory.createXMLEventReader(cciReader);

		// Checking the availability of the next tag
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();

			// This will trigger when the tag is of type <...>
			if (event.isStartElement()) {
				StartElement element = event.asStartElement();
				String elementName = element.getName().getLocalPart();

				// cci reference in element cci_item
				if (elementName.equals("cci_item")) {
					cciId = element.getAttributeByName(ID_QNAME).getValue();
				}

				// nist control in reference elements that follow the cci_item elements
				if (elementName.equals("reference")) {
					String index = element.getAttributeByName(INDEX_QNAME).getValue();
					String title = element.getAttributeByName(TITLE_QNAME).getValue();
					String version = element.getAttributeByName(VERSION_QNAME).getValue();

					nistReference = index + ", " + title + ", Version " + version;

					if (!controlToCCI.containsKey(nistReference)) {
						controlToCCI.put(nistReference, new TreeSet<String>());
					}
					controlToCCI.get(nistReference).add(cciId);

					if (!cciToControl.containsKey(cciId)) {
						cciToControl.put(cciId, new TreeSet<String>());
					}
					cciToControl.get(cciId).add(nistReference);
				}
			}
		}
	}

	void parseSRG(BufferedReader srgReader) throws FileNotFoundException, XMLStreamException {
		String cci = null, srg = null;

		// Instance of the class which helps on reading tags
		XMLInputFactory factory = XMLInputFactory.newInstance();

		// Initializing the handler to access the tags in the XML file
		XMLEventReader eventReader = factory.createXMLEventReader(srgReader);

		// Checking the availability of the next tag
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();

			// This will trigger when the tag is of type <...>
			if (event.isStartElement()) {
				StartElement element = event.asStartElement();
				String elementName = element.getName().getLocalPart();

				// srg reference is in version elements containing text beginning with 'SRG'
				if (elementName.equals("version")) {
					String versionText = eventReader.getElementText();
					if (versionText.startsWith("SRG")) {
						srg = versionText;
					}
				}

				// cci reference is in ident elements that follow the version elements
				if (elementName.equals("ident")) {
					cci = eventReader.getElementText();

					if (!cciToSRG.containsKey(cci)) {
						cciToSRG.put(cci, new TreeSet<String>());
					}
					cciToSRG.get(cci).add(srg);

					if (!srgToCCI.containsKey(srg)) {
						srgToCCI.put(srg, new TreeSet<String>());
					}
					srgToCCI.get(srg).add(cci);
				}
			}
		}
	}
}
