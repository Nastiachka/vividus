/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vividus.excel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jbehave.core.model.ExamplesTable;

public final class ExcelSheetWriter
{
    private ExcelSheetWriter()
    {
    }

    /**
    * Create an excel document containing one sheet with the optional name and specified content.
    * @param path path of excel file to create
    * @param sheetName sheet name, if not specified the default name will be determined by underlying excel library
    * @param content any valid ExamplesTable
    */
    public static void createExcel(Path path, Optional<String> sheetName, ExamplesTable content) throws IOException
    {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                FileOutputStream fileOut = new FileOutputStream(path.toFile()))
        {
            XSSFSheet sheet = sheetName.map(workbook::createSheet).orElseGet(workbook::createSheet);
            fillData(content, sheet);
            workbook.write(fileOut);
        }
    }

    /**
    * Add a sheet with the specified name and content to the existing excel file.
    * @param path path of existing excel file
    * @param sheetName sheet name
    * @param content any valid ExamplesTable
    */
    public static void addSheetToExcel(Path path, String sheetName, ExamplesTable content) throws IOException
    {
        try (InputStream fileInput = new FileInputStream(path.toFile());
                XSSFWorkbook workbook = new XSSFWorkbook(fileInput);
                FileOutputStream fileOut = new FileOutputStream(path.toFile()))
        {
            fillData(content, workbook.createSheet(sheetName));
            workbook.write(fileOut);
        }
    }

    private static void fillData(ExamplesTable content, XSSFSheet sheet)
    {
        List<String> headers = content.getHeaders();
        fillRow(sheet, headers, 0, null);
        IntStream.rangeClosed(1, content.getRows().size()).forEach(rowIndex -> {
            Map<String, String> values = content.getRowAsParameters(rowIndex - 1, true).values();
            fillRow(sheet, headers, rowIndex, values);
        });
    }

    private static void fillRow(XSSFSheet sheet, List<String> headers, int rowIndex, Map<String, String> values)
    {
        Row row = sheet.createRow(rowIndex);
        IntStream.range(0, headers.size()).forEach(index -> row.createCell(index).
            setCellValue(values == null ? headers.get(index) : values.get(headers.get(index))));
    }
}
