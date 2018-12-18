package com.icl.authentication.application;

import com.github.pagehelper.PageHelper;
import com.icl.authentication.common.mapper.AppMapper;
import com.icl.authentication.entity.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AppService {

    @Autowired
    private AppMapper appMapper;

    public List<Application> listApplications(Application application) {
        application.setDeleted(false);
        PageHelper.startPage(application.getPage(), application.getSize());
        return appMapper.select(application);
    }

    public int countApplication(Application application) {
        application.setDeleted(false);
        return appMapper.selectCount(application);
    }

    public Application updateApp(Application app) {
        appMapper.updateByPrimaryKeySelective(app);
        return appMapper.selectByPrimaryKey(app.getId());
    }

    public Boolean deleteApp(long id) {
        Application app = new Application();
        app.setDeleted(true).setId(id);
        return appMapper.updateByPrimaryKeySelective(app) == 1;
    }
}
