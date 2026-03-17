package com.mydigipay.los.ruleautomation;

import com.mydigipay.los.ruleautomation.repository.RuleRepository;
import com.mydigipay.los.ruleautomation.service.ExcelController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.mydigipay.los.ruleautomation.repository")
@EntityScan("com.mydigipay.los.ruleautomation.model")

public class LosApiAutomatedApplication implements CommandLineRunner {

//    public LosApiAutomatedApplication(RuleRepository ruleRepository) {
//    }

    public static void main(String[] args) {

//        ApplicationContext applicationContext = SpringApplication.run(LosApiAutomatedApplication.class, args);
//        applicationContext.getBean(ExcelController.class);
    }

    @Override
    public void run(String... args) throws IOException {
        // Rule rule = ruleRepository.findByRuleId("TB-S-C-020713-10100606");
        // System.out.println(rule);
        // ProfileItem profileItem = new ProfileItem();
        // System.out.println(rule.getProfileItems().get(0).getProfile().getField());
        // System.out.println(ExcelController.excelReader("C:\\Users\\b.arabi\\Desktop\\rules-detail.xlsx"));
    }

}
