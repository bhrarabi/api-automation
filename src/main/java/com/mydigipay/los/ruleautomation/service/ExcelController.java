package com.mydigipay.los.ruleautomation.service;


import com.mydigipay.los.ruleautomation.model.plan.Plan;
import com.mydigipay.los.ruleautomation.model.rule.Rule;
import com.mydigipay.los.ruleautomation.model.rule.pojo.*;
import com.poiji.bind.Poiji;
import com.poiji.option.PoijiOptions;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service public class ExcelController {

    private static final Integer CONSTANTS_COUNT = 23;

    /**
     * @param fileLocation read expected rules from an Excel file.
     * @return An array of rules will be returned. We used array because this data should be used by data provider.
     * @throws IOException Maybe file path is in correct or the file is missed.
     */
    public static Rule[] excelReader(String fileLocation) throws IOException {
        FileInputStream file = new FileInputStream(ResourceUtils.getFile(fileLocation));
        Workbook workbook = new XSSFWorkbook(file);
        List<Rule> rules = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();
        for (int j = 0; j < workbook.getNumberOfSheets(); j++) {
            Sheet sheet = workbook.getSheetAt(j);
            Rule rule = new Rule();
            for (int n = 0; n < CONSTANTS_COUNT; n++) {
                Row row = sheet.getRow(n);
                int i = row.getFirstCellNum();
                String fieldName = dataFormatter.formatCellValue(row.getCell(i));
                String fieldValue = dataFormatter.formatCellValue(row.getCell(++i));
                try {
                    setFieldValue(fieldName, fieldValue, rule);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // reading Collateral type
            int rowNumber = CONSTANTS_COUNT;
            List<SupportedCollateralType> supportedCollateralTypes = new ArrayList<>();
            rule.setSupportedCollateralTypes(supportedCollateralTypes);
            Row excelSupportedCollateralTypes = sheet.getRow(rowNumber);
            short cellNumber = excelSupportedCollateralTypes.getFirstCellNum();
            do {
                String collateralType = dataFormatter.formatCellValue(excelSupportedCollateralTypes.getCell(cellNumber + 1));
                supportedCollateralTypes.add(SupportedCollateralType.valueOf(collateralType));
                excelSupportedCollateralTypes = sheet.getRow(++rowNumber);
            } while ("".equals(dataFormatter.formatCellValue(excelSupportedCollateralTypes.getCell(cellNumber))));

            // reading cheque relative support
            List<Relative> relatives = new ArrayList<>();
            rule.setChequeRelativeSupport(relatives);
            Row excelChequeRelativeSupport = sheet.getRow(rowNumber);
            cellNumber = excelChequeRelativeSupport.getFirstCellNum();
            do {
                String relative = dataFormatter.formatCellValue(excelChequeRelativeSupport.getCell(cellNumber + 1));
                relatives.add(Relative.valueOf(relative));
                excelChequeRelativeSupport = sheet.getRow(++rowNumber);
            } while ("".equals(dataFormatter.formatCellValue(excelChequeRelativeSupport.getCell(cellNumber))));


            // reading Profile Items
            List<ProfileItem> profileItems = new ArrayList<>();
            rule.setProfileItems(profileItems);
            Row excelProfileItems = sheet.getRow(rowNumber);
            cellNumber = excelProfileItems.getFirstCellNum();
            do {
                String profileField = dataFormatter.formatCellValue(excelProfileItems.getCell(cellNumber + 1));
                String option = dataFormatter.formatCellValue(excelProfileItems.getCell(cellNumber + 2));
                ProfileItem profileItem = new ProfileItem();
                Profile profile = new Profile();
                profile.setField(profileField);
                profileItem.setProfile(profile);
                profileItem.setOption(Option.valueOf(option));
                profileItems.add(profileItem);
                excelProfileItems = sheet.getRow(++rowNumber);
            } while ("".equals(dataFormatter.formatCellValue(excelProfileItems.getCell(cellNumber))));

            // reading Fund Provider
            ProviderModel fundProvider = new ProviderModel();
            rule.setFundProvider(fundProvider);
            Row excelFundProvider = sheet.getRow(rowNumber);
            cellNumber = excelFundProvider.getFirstCellNum();
            do {
                String fieldName = dataFormatter.formatCellValue(excelFundProvider.getCell(cellNumber + 1));
                String fieldValue = dataFormatter.formatCellValue(excelFundProvider.getCell(cellNumber + 2));
                try {
                    setFieldValue(fieldName, fieldValue, fundProvider);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                excelFundProvider = sheet.getRow(++rowNumber);
            } while ("".equals(dataFormatter.formatCellValue(excelFundProvider.getCell(cellNumber))));

            // reading Steps
            List<Step> steps = new ArrayList<>();
            rule.setSteps(steps);
            Row excelSteps = sheet.getRow(rowNumber);
            cellNumber = excelSteps.getFirstCellNum();
            do {
                String stepCode = dataFormatter.formatCellValue(excelSteps.getCell(cellNumber + 1));
                String option = dataFormatter.formatCellValue(excelSteps.getCell(cellNumber + 2));
                String processType = dataFormatter.formatCellValue(excelSteps.getCell(cellNumber + 3));
                boolean runTest = Boolean.parseBoolean(dataFormatter.formatCellValue(excelSteps.getCell(cellNumber + 4)));
                Step step = new Step();
                step.setCode(stepCode);
                step.setOption(Option.valueOf(option));
                step.setRunTest(runTest);
                if (!processType.isEmpty()) {
                    step.setProcessType(ProcessType.valueOf(processType));
                }
                steps.add(step);
                excelSteps = sheet.getRow(++rowNumber);
            } while ("".equals(dataFormatter.formatCellValue(excelSteps.getCell(cellNumber))));

            // reading Installment Count
            Map<String, Integer> installmentCount = new HashMap<>();
            rule.setInstallmentCount(installmentCount);
            Row excelInstallmentCount = sheet.getRow(rowNumber);
            cellNumber = excelInstallmentCount.getFirstCellNum();
            do {
                String key = dataFormatter.formatCellValue(excelInstallmentCount.getCell(cellNumber + 1));
                Integer value = Integer.valueOf(dataFormatter.formatCellValue(excelInstallmentCount.getCell(cellNumber + 2)));
                installmentCount.put(key, value);
                excelInstallmentCount = sheet.getRow(++rowNumber);
            } while ("".equals(dataFormatter.formatCellValue(excelInstallmentCount.getCell(cellNumber))));

            // reading Balance
            Map<String, Integer> balance = new HashMap<>();
            rule.setBalance(balance);
            Row excelBalance = sheet.getRow(rowNumber);
            cellNumber = excelBalance.getFirstCellNum();
            do {
                String key = dataFormatter.formatCellValue(excelBalance.getCell(cellNumber + 1));
                Integer value = Integer.valueOf(dataFormatter.formatCellValue(excelBalance.getCell(cellNumber + 2)));
                balance.put(key, value);
                excelBalance = sheet.getRow(++rowNumber);
            } while (excelBalance != null && "".equals(dataFormatter.formatCellValue(excelBalance.getCell(cellNumber))));

            rules.add(rule);
        }
        return rules.toArray(new Rule[rules.size()]);

    }

    private static void setFieldValue(String fieldName, String fieldValue, Object o) throws Exception {
        Class<?> c = o.getClass();
        Field field = c.getDeclaredField(fieldName);
        field.setAccessible(true);
        Class<?> type = field.getType();
        Constructor<?> constructor = type.getConstructor(String.class);
        field.set(o, constructor.newInstance(fieldValue));
    }

    /**
     * Convert an Excel file to a list of plans.
     *
     * @param filePath Excel file wich contains plans
     * @return List of plans
     */
    public static Plan[] readPlanFromExcel(String filePath) {

        PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().headerStart(0).sheetIndex(0).build();
        List<Plan> expectedPlans = Poiji.fromExcel(new File(filePath), Plan.class, options);
        return expectedPlans.toArray(new Plan[expectedPlans.size()]);
    }

    public static Object[][] readExcelData(String filePath, String sheetName) throws IOException {
// Create a FileInputStream object to read the Excel file
        FileInputStream file = new FileInputStream(filePath);

// Create a Workbook object to access the Excel file
        Workbook workbook = WorkbookFactory.create(file);

// Get the sheet with the given name from the workbook
        Sheet sheet = workbook.getSheet(sheetName);

// Get the number of rows and columns in the sheet
        int rowCount = sheet.getLastRowNum();
        int columnCount = sheet.getRow(0).getLastCellNum();

// Create a 2D array of objects to store the data
        Object[][] data = new Object[rowCount][columnCount];

// Create a DataFormatter object to format the cell values
        DataFormatter formatter = new DataFormatter();

// Loop through the rows and columns of the sheet and store the values in the array
        for (int i = 1; i <= rowCount; i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < columnCount; j++) {
                Cell cell = row.getCell(j);
                String value = formatter.formatCellValue(cell);
                data[i - 1][j] = value;
            }
        }

// Close the workbook and the file
        workbook.close();
        file.close();

// Return the data array
        return data;
    }
}