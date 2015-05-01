/*
 * Copyright (c) 2015
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
*/

package es.csc.pklb.layout;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

import es.csc.pklb.frecuency.Key;
import es.csc.pklb.grid.HexagonalGrid;

public class LayoutBuilderTest {
	@Test(expected=IOException.class)
	public void ConstructorUnknownFile() throws IOException, SAXException {
		String keysFile = "data/test/LayoutBuilderTest/lkasjfhksadfha.xml";
		
		new LayoutBuilder(keysFile);
	}
	
	@Test(expected=SAXException.class)
	public void ConstructorInvalidFile() throws IOException, SAXException {
		String keysFile = "data/test/LayoutBuilderTest/invalid.xml";
		
		new LayoutBuilder(keysFile);
	}
	
	@Test
	public void constructorValidFile() throws IOException, SAXException {
		String keysFile = "data/test/LayoutBuilderTest/valid.xml";
		
		new LayoutBuilder(keysFile);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createXmlUnknownKey()  throws IOException, SAXException, IllegalArgumentException, TransformerException {
		String keysFile = "data/test/LayoutBuilderTest/valid.xml",
				outputFile = "data/test/LayoutBuilderTest/output/createXmlUnknownKey.xml";
		
		LayoutBuilder builder = new LayoutBuilder(keysFile);
		
		HexagonalGrid grid = new HexagonalGrid(0);
		grid.nodes().get(0).setContent( new Key("1") );
		
		builder.toXmlFile(grid, outputFile);
	}
	
	
	@Test
	public void createXml() throws IOException, SAXException, TransformerException {
		String keysFile = "data/test/LayoutBuilderTest/valid.xml",
				outputFile = "data/test/LayoutBuilderTest/output/createXml.xml",
				expectedFile = "data/test/LayoutBuilderTest/createXml.xml";;
		
		LayoutBuilder builder = new LayoutBuilder(keysFile);		
		HexagonalGrid grid = createXmlTestGrid();
		
		builder.toXmlFile(grid, outputFile);

		assertTrue(String.format("The content of \"%s\" is not the same that the generated file \"%s\"", 
									expectedFile, outputFile),
					FileUtils.contentEquals(new File(outputFile), new File(expectedFile)) );
	}

	private HexagonalGrid createXmlTestGrid() {
		HexagonalGrid grid = new HexagonalGrid(5, 3);
		Key keys[] = { 
						new Key("aáAÁ"), new Key("bB"), new Key("cC"), new Key("dD"), new Key("eéEÉ"), 
						new Key("fF"), new Key("gG"), new Key("hH"), new Key("iíIÍ"), new Key("jJ"),
						new Key("kK"), new Key("lL"), new Key("mM"), new Key("nN"), new Key("ñÑ"),
						new Key("oóOÓ"), new Key("pP"),new Key("qQ"), new Key("rR"), new Key("sS"), 
						new Key("tT"), new Key("uúUÚ"), new Key("vV"), new Key("wW"), new Key("xX"), 
						new Key("yY"), new Key("zZ"), new Key(" ")
					};
		
		for(int i = 0, n = keys.length; i < n; ++i) {
			grid.nodes().get(i).setContent( keys[i] );
		}
		return grid;
	}
}
