package com.icl.authentication.company;

import com.icl.authentication.entity.Company;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping("list")
    public List<Company> listCompany(Company param) {
        log.info("list company. param: {}", param.toString());
        return companyService.getCompanies(param);
    }

    @GetMapping("count")
    public int countCompany(Company company) {
        log.info("count company. param: {}", company.toString());
        return companyService.countCompany(company);
    }

    @PostMapping("update")
    public Company editCompany(@RequestBody Company param) {
        log.info("edit company info. param: {}", param.toString());
        return companyService.updateCompany(param);
    }

    @DeleteMapping("{id}/delete")
    public Boolean dropCompany(@PathVariable("id") Long id) {
        log.info("drop company. id: {}", id);
        return companyService.deleteCompany(id);
    }

    @PutMapping("add")
    public Company addCompany(@RequestBody Company company) {
        log.info("add company. {}", company.toString());
        return companyService.addCompany(company);
    }
}
