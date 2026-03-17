package com.mydigipay.los.ruleautomation;

import com.mydigipay.los.ruleautomation.model.rule.Rule;
import com.mydigipay.los.ruleautomation.model.rule.pojo.Step;
import com.mydigipay.los.ruleautomation.service.ExcelController;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Author: f.bahramnejad
 */
public class StepProcessFactory {

    @Factory(dataProvider = "Read rules")
    public Object[] factoryMethod(Rule rule) {
        List<StepProcessTest> objects = new ArrayList<>();

        List<String> codes = new ArrayList<>();

        for (Step step : rule.getSteps()) {
            if (step.isRunTest()) {
                codes.add(step.getCode());
            }

        }
        StepProcessTest object = new StepProcessTest(codes, rule);

        objects.add(object);
        return objects.toArray();
    }

    @DataProvider(name = "Read rules")
    Rule[] readRules(ITestContext context) {
        try {
            return ExcelController.excelReader(context.getCurrentXmlTest().getParameter("ruleDetailFile"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
