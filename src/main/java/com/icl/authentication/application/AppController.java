package com.icl.authentication.application;

import com.icl.authentication.entity.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/app")
public class AppController {

    @Autowired
    private AppService appService;

    @GetMapping("list")
    public List<Application> listApp(Application param) {
        log.info("list application. param: {}", param.toString());
        return appService.listApplications(param);
    }

    @GetMapping("count")
    public int countApp(Application param) {
        log.info("count application. param: {}", param.toString());
        return appService.countApplication(param);
    }

    @PostMapping("update")
    public Application updateApp(@RequestBody Application param) {
        log.info("update application. param : {}", param.toString());
        return appService.updateApp(param);
    }

    @DeleteMapping("{id}/delete")
    public Boolean dropApp(@PathVariable("id") long id) {
        log.info("delete app by id: {}", id);
        return appService.deleteApp(id);
    }

}
