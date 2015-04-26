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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

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

import es.csc.pklb.grid.HexagonalGrid;
import es.csc.pklb.grid.Node;

/***
 * Class that generates android keyboard layouts. 
 */
public class LayoutBuilder {
	private static final Pattern LINE_PATTERN= Pattern.compile("(.) (\\d+)");
	
	private Map<Character, Integer> codes;
	
	/***
	 * The constructor reads the relation of keys and their android key codes.
	 * Each line of the file should containt the letter that the keyboard is going to show,
	 * only one, an space and an integer, eg: "a 97".
	 * 
	 * @param keyCodesFile
	 * 
	 * @throws IOException if an I/O error occurs reading from the file or a malformed or 
	 * 		                unmappable byte sequence is read.
	 * @throws IllegalArgumentException the format of a line is not valid.
	 */
	public LayoutBuilder(String keyCodesFile) throws IOException, IllegalArgumentException {
		codes = new HashMap<Character, Integer>();
		
		List<String> lines = Files.readAllLines(Paths.get(keyCodesFile));		
		for(String line: lines) {
			if (line.length() > 0) {
				parseLine(line);
			}
		}
	}

	/***
	 * @param line
	 * @throws IllegalArgumentException the format of a line is not valid.
	 */
	private void parseLine(String line) throws IllegalArgumentException {
		Matcher matcher = LINE_PATTERN.matcher(line);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("invalid line: \"" + line + "\"");
		}
		
		Character letter = matcher.group(1).charAt(0);
		Integer code = Integer.valueOf( matcher.group(2) ); 
		codes.put(letter, code);
	}
	
	/***
	 * Write a keyboard layout equivalent to the content of grid in outputFile.
	 *   
	 * @param grid
	 * @param outputFile
	 * 
	 * @throws TransformerException Error during the process of save the xml.
	 */
	public void toXmlFile(HexagonalGrid grid, String outputFile) 
			throws TransformerException {
		Document xml = createXml(grid);
		saveXml(xml, outputFile);
	}

	/***
	 * @throws IllegalArgumentException there is a key in grid with an unknown code
	*/
	private Document createXml(HexagonalGrid grid) throws IllegalArgumentException {		
		try {
			List<List<Node>> gridRows = grid.toRows();
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();		
			Document document = docBuilder.newDocument();
			
			Element keyboard = document.createElement("Keyboard");
			keyboard.setAttribute("xmlns:android", "http://schemas.android.com/apk/res/android");
			
			for(List<Node> gridRow  : gridRows) {
				addRow(document, keyboard, gridRow);
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

	/***
	 * @throws IllegalArgumentException there is a key in grid with an unknown code
	 */
	private void addRow(Document document, Element keyboard, List<Node> gridRow) 
			 													throws IllegalArgumentException {
		Element row = document.createElement("Row");
		for(Node node : gridRow) {
			addKey(document, row, node);
		}
		keyboard.appendChild(row);
	}

	/***
	 * @throws IllegalArgumentException there is a key in grid with an unknown code
	 */
	private void addKey(Document document, Element row, Node node) throws IllegalArgumentException {
		if (!node.isEmpty()) {
			Element key = document.createElement("Key");
			
			char letter = node.getContent().toString().charAt(0);	
			System.out.println(" -" + letter );
			if (!codes.containsKey(letter)) {
				throw new IllegalArgumentException("Code not found for key \"" + node.getContent().toString() + "\"");
			}
			
			key.setAttribute("android:code", codes.get(letter).toString());
			key.setAttribute("android:keyLabel", Character.toString(letter));
			
			row.appendChild(key);
		}
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
