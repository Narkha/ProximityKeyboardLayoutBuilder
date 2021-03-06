/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.pklb.layout;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.csc.pklb.frecuency.Key;
import es.csc.pklb.grid.HexagonalGrid;
import es.csc.pklb.grid.Node;

/***
 * Class that generates android keyboard layouts. 
 */
public class LayoutBuilder {	
	private static final String XML_ATTRIBUTE_KEY_HORIZONTAL_GAP = "android:horizontalGap";
	private static final String XML_ANDROID_NAMESPACE_URL = "http://schemas.android.com/apk/res/android";
	private static final String XML_ANDROID_NAMESPACE_NAME = "xmlns:android";
	
	private static final String XML_ATRIBITE_CODES = "android:codes";
	private static final String XML_ATTRIBUTE_KEY_EDGE_FLAG = "android:keyEdgeFlags";
	private static final String XML_ATTRIBUTE_KEY_EDGE_FLAG_VALUE_RIGHT = "right";
	private static final String XML_ATTRIBUTE_KEY_EDGE_FLAG_VALUE_LEFT = "left";

	
	private static final String XML_KEYBOARD_ELEMENT = "Keyboard";
	private static final String XML_ROW_ELEMENT = "Row";
	private static final String XML_KEY_LABEL = "Key";
	
	NodeList androidKeys;
	
	/***
	 * @param adroidKeysFile path of an xml that contains the xml keys to be uses in the template
	 * @throws IOException if an I/O error occurs reading from the file or a malformed or 
	 * 		                unmappable byte sequence is read.
	 * @throws SAXException Error parsing the xml
	 */
	public LayoutBuilder(String adroidKeysFile) throws IOException, SAXException {
		try {
			File fXmlFile = new File(adroidKeysFile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document document = dBuilder.parse(fXmlFile);
			document.getDocumentElement().normalize();
			
			androidKeys = document.getElementsByTagName(XML_KEY_LABEL);
		} 
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * Write a keyboard layout equivalent to the content of grid in outputFile.
	 *   
	 * @param grid
	 * @param keyboardAttributes pairs of attributes that will be added to the keyboard element
	 * @param the of the attribute "android:horizontal" that is assigned to the first key 
	 *        of the rows of the rows that are shifted.  
	 *        if null or or empty any value will be assigned.
	 * @param outputFile
	 * 
	 * @throws TransformerException Error during the process of save the xml.
	 */
	public void toXmlFile(HexagonalGrid grid, 
							Map<String, String> keyboardAttributes, String shiftedRowsGap,
							String outputFile) throws TransformerException {
		Document xml = createXml(grid, keyboardAttributes, shiftedRowsGap);
		saveXml(xml, outputFile);
	}

	/***
	 * @throws IllegalArgumentException there is a key in grid with an unknown code
	*/
	private Document createXml(HexagonalGrid grid, 
							 	Map<String, String> keyboardAttributes, String shiftedRowsGap) 
							throws IllegalArgumentException {		
		try {
			List<List<Node>> gridRows = grid.grid();
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();		
			Document document = docBuilder.newDocument();
			
			Element keyboard = document.createElement(XML_KEYBOARD_ELEMENT);
			keyboard.setAttribute(XML_ANDROID_NAMESPACE_NAME, XML_ANDROID_NAMESPACE_URL);
			
			setKeyboardAttributes(keyboard, keyboardAttributes);
			
			for(List<Node> gridRow  : gridRows) {
				addRow(document, keyboard, gridRow, shiftedRowsGap);
			}
			
			document.appendChild(keyboard);
			
			return document;
		}
		catch (ParserConfigurationException e) {
			System.out.println("This should never happen");
			e.printStackTrace();
			return null;
		}
	}

	private void setKeyboardAttributes(Element element, Map<String, String> attributes) {
		for(String key : attributes.keySet()) {
			element.setAttribute(key, attributes.get(key));
		}
	}

	/***
	 * @throws IllegalArgumentException there is a key in grid with an unknown code
	 */
	private void addRow(Document document, Element keyboard, List<Node> gridRow, String shiftedRowsGap) 
			 													throws IllegalArgumentException {
		Element row = document.createElement(XML_ROW_ELEMENT);
		Element firstKey = null, lastKey = null;
		for(Node node : gridRow) {
			lastKey = addKey(document, row, node);
			if (firstKey == null) {
				firstKey = lastKey;
			}
		}
		
		if (firstKey != null) {
			firstKey.setAttribute(XML_ATTRIBUTE_KEY_EDGE_FLAG, XML_ATTRIBUTE_KEY_EDGE_FLAG_VALUE_LEFT);
			if (gridRow.get(0).getX() > 0.0 && shiftedRowsGap != null && !shiftedRowsGap.isEmpty()) {
				firstKey.setAttribute(XML_ATTRIBUTE_KEY_HORIZONTAL_GAP, shiftedRowsGap);
			}
			
		}
		
		if (lastKey != null) {
			lastKey.setAttribute(XML_ATTRIBUTE_KEY_EDGE_FLAG, XML_ATTRIBUTE_KEY_EDGE_FLAG_VALUE_RIGHT);
		}
		
		keyboard.appendChild(row);
	}

	/***
	 * @throws IllegalArgumentException there is a key in grid with an unknown code
	 */
	private Element addKey(Document document, Element row, Node node) throws IllegalArgumentException {
		Element key = null;
		
		if (!node.isEmpty()) {
			key = (Element) getKeyElement( node.getContent() );
			key = (Element) document.adoptNode(key);
			row.appendChild(key);
		}
		
		return key;
	}
	
	/**
	 * Find the equivalent to key in androidKeys.
	 * The returned xml element belongs to the another xml document, so it is necesesary
	 * call to document.adopNode(...) before append this element
	 * 
	 * @param key
	 * @return the element with the 
	 * @throws IllegalArgumentException if there are not equivalent in androidKeys
	 */
	private Element getKeyElement(Key key) throws IllegalArgumentException{
		String code = getKeyCode(key);
		
		for(int i = 0, n = androidKeys.getLength(); i < n; ++i) {
			Element element = (Element) androidKeys.item(i);
			if (element.getAttribute(XML_ATRIBITE_CODES).equals(code)) {
				return (Element) element.cloneNode(false);
			}
		}
		
		throw new IllegalArgumentException("Code not found for key \"" + key.toString() + "\" " + code);
	}

	private String getKeyCode(Key key) {
		int letter;
		if (SpecialKey.class == key.getClass()) {
			letter = ((SpecialKey) key).getCode();
		}
		else {
			letter = key.toString().charAt(0);
		}
		
		return Integer.toString(letter);
	}

	/***
	 * 
	 * @param xml
	 * @param outputFile
	 * 
	 * @throws TransformerException Error during the generation of the xml.
	 */
	private void saveXml(Document xml, String outputFile) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		
		DOMSource source = new DOMSource(xml);
		StreamResult result = new StreamResult(new File(outputFile));
		
		transformer.transform(source, result);
	}

}
