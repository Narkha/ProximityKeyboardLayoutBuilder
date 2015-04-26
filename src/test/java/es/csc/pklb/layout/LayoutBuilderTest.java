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

import es.csc.pklb.frecuency.Key;
import es.csc.pklb.grid.HexagonalGrid;

public class LayoutBuilderTest {
	@Test(expected=IOException.class)
	public void ConstructorUnknownFile() throws IllegalArgumentException, IOException {
		String codesFile = "data/test/LayoutBuilderTest/lkasjfhksadfha.config";
		
		new LayoutBuilder(codesFile);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void ConstructorInvalidFile() throws IllegalArgumentException, IOException {
		String codesFile = "data/test/LayoutBuilderTest/invalid.config";
		
		new LayoutBuilder(codesFile);
	}
	
	@Test
	public void constructorValidFile() throws IllegalArgumentException, IOException {
		String codesFile = "data/test/LayoutBuilderTest/valid.config";
		
		new LayoutBuilder(codesFile);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createXmlUnknownKey() throws IllegalArgumentException, IOException, TransformerException {
		String codesFile = "data/test/LayoutBuilderTest/valid.config",
				outputFile = "data/test/LayoutBuilderTest/output/createXmlUnknownKey.xml";
		
		LayoutBuilder builder = new LayoutBuilder(codesFile);
		
		HexagonalGrid grid = new HexagonalGrid(0);
		grid.nodes().get(0).setContent( new Key("1") );
		
		builder.toXmlFile(grid, outputFile);
	}
	
	
	@Test
	public void createXml() throws IllegalArgumentException, IOException, TransformerException {
		String codesFile = "data/test/LayoutBuilderTest/valid.config",
				outputFile = "data/test/LayoutBuilderTest/output/createXml.xml",
				expectedFile = "data/test/LayoutBuilderTest/createXml.xml";;
		
		LayoutBuilder builder = new LayoutBuilder(codesFile);		
		HexagonalGrid grid = createXmlTestGrid();
		
		builder.toXmlFile(grid, outputFile);

		assertTrue(String.format("The content of \"%s\" is not the same that the generated file \"%s\"", 
									expectedFile, outputFile),
					FileUtils.contentEquals(new File(outputFile), new File(expectedFile)) );
	}

	private HexagonalGrid createXmlTestGrid() {
		HexagonalGrid grid = new HexagonalGrid(1);
		Key keys[] = { new Key("aáAÁ"), new Key("bB"), new Key("cC"), 
				       new Key("dD"), new Key("eE"), new Key("fF"), new Key("gG") };
		
		for(int i = 0, n = grid.size(); i < n; ++i) {
			grid.nodes().get(i).setContent( keys[i] );
		}
		return grid;
	}
}
