package com.icl.authentication.company;

import com.github.pagehelper.PageHelper;
import com.icl.authentication.common.mapper.CompanyMapper;
import com.icl.authentication.entity.Company;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CompanyService {

    @Autowired
    private CompanyMapper companyMapper;

    public List<Company> getCompanies(Company company) {
        PageHelper.startPage(company.getPage(), company.getSize());
        company.setDeleted(false);
        return companyMapper.select(company);
    }

    public int countCompany(Company company) {
        company.setDeleted(false);
        return companyMapper.selectCount(company);
    }

    public Company updateCompany(Company company) {
        company.setUpdateTime(new Date());
        companyMapper.updateByPrimaryKeySelective(company);
        return companyMapper.selectByPrimaryKey(company.getId());
    }
    public Boolean deleteCompany(Long id) {
        Company company = new Company();
        company.setDeleted(true).setId(id);
        log.debug("company to delete. {}", company.toString());
        return companyMapper.updateByPrimaryKeySelective(company) == 1;
    }
    public Company addCompany(Company company) {
        company.setDeleted(false).setCreateTime(new Date()).setUpdateTime(new Date());
        log.debug("company ready to save. {}", company.toString());
        companyMapper.insertUseGeneratedKeys(company);
        return company;
    }
}
