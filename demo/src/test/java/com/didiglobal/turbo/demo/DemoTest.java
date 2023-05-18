package com.didiglobal.turbo.demo;

import com.didiglobal.turbo.demo.service.AfterSaleServiceImpl;
import com.didiglobal.turbo.demo.service.LeaveServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author rick
 * @Date 2022/4/11 12:53
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class DemoTest {
    @Resource
    private AfterSaleServiceImpl afterSaleService;
    @Resource
    private LeaveServiceImpl leaveService;

    @Test
    public void runAfterSaleDemo(){
        afterSaleService.run();
    }

    @Test
    public void runLeaveDemo(){
        leaveService.run();
    }
}
