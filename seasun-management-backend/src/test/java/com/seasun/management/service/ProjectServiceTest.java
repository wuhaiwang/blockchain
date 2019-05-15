package com.seasun.management.service;

import com.seasun.management.Application;
import com.seasun.management.dto.SubProjectInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class ProjectServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceTest.class);

    @Autowired
    ProjectService projectService;

    @Test
    public void testUpdateUserPhoto() {
        SubProjectInfo subProjectInfo = projectService.getSubProjectById(1044L);
        logger.debug("total size is:{}",subProjectInfo.getSubProjects().size());
    }

}
