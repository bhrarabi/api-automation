package com.mydigipay.los.ruleautomation.service.excelInputProvider;

import com.mydigipay.los.ruleautomation.model.rule.Rule;
import com.mydigipay.los.ruleautomation.model.user.User;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
/*
 * Author: f.bahramnejad
 */
public class CreateExcelData {
    /**
     * This method creates Excel file which is needed for importing to an individual group.
     * @param fileLocation Location of template file
     * @param rule  rule which we want to activate it
     * @param user  user who we want add them to the group
     */
    public static void createIndividualImportFile(String fileLocation, Rule rule, User user) throws IOException {
        FileInputStream inputFile = new FileInputStream(fileLocation);
        Workbook workbook = new XSSFWorkbook(inputFile);
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(1);
        setUserData(user, row);
        setRuleData(rule, row);
        FileOutputStream out = new FileOutputStream(fileLocation);
        workbook.write(out);
        inputFile.close();
        out.close();
        workbook.close();
    }

    /**
     *      * This method creates Excel file which is needed for importing to an organizational group.
     * @param fileLocation Location of template file
     * @param rule rule which we want to activate it
     * @param user user who we want add them to the group
     * @param crn crn of organization
     */
    public static void createOrganizationalImportFile(String fileLocation, Rule rule, User user, String crn) throws IOException {
        FileInputStream inputFile = new FileInputStream(fileLocation);
        Workbook workbook = new XSSFWorkbook(inputFile);
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(1);
        setUserData(user, row);
        setRuleData(rule, row);
        row.getCell(17).setCellValue(crn);
        FileOutputStream out = new FileOutputStream(fileLocation);
        workbook.write(out);
        inputFile.close();
        out.close();
        workbook.close();
    }

    private static void setUserData(User user, Row row) {
        row.getCell(0).setCellValue(user.getCellNumber());
        row.getCell(1).setCellValue(user.getName());
        row.getCell(2).setCellValue(user.getSureName());
        row.getCell(7).setCellValue(user.getNationalCode());
        row.getCell(9).setCellValue(user.getNationalCode());
        row.getCell(8).setCellValue(user.getBirthDate());
    }

    private static void setRuleData(Rule rule, Row row) {
        row.getCell(5).setCellValue(rule.getBalance().get("min"));
        row.getCell(6).setCellValue(rule.getInstallmentCount().get("min"));
    }
}
