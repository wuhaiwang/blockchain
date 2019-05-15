package com.seasun.management;

import com.seasun.management.Application;
import com.seasun.management.config.DataSourceContextHolder;
import com.seasun.management.mapper.FloorMapper;
import com.seasun.management.model.Floor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class RouterTest {

    @Autowired
    FloorMapper floorMapper;


    /**
     * 多库切换：
     * 1. 切换方法，直接调用 DataSourceContextHolder.usePrimary();
     * 2. 本次切换只在当前线程生效
     * 3. 若完成线程切换的处理工作，请恢复原有线程数据源。
     *    方法：调用 DataSourceContextHolder.clearDBType();
     * 4. 注意：若本次db访问已走缓存，则切换数据源对本次操作无效。
     */
    @Test
    public void testSecondDataSourceByMapper() {

        // 原始值
        DataSourceContextHolder.usePrimary();
        Floor floorPrimary = floorMapper.selectByPrimaryKey(17L);
        System.out.println("primary db floor is:" + floorPrimary.getName());

        DataSourceContextHolder.useSecond();
        Floor floorSecond = floorMapper.selectByPrimaryKey(17L);
        System.out.println("second db floor is:" + floorSecond.getName());


        // 更新主库floor
        DataSourceContextHolder.usePrimary();
        Floor cond = new Floor();
        cond.setName("Primaryfloor");
        cond.setId(17L);
        floorMapper.updateByPrimaryKey(cond);
        System.out.println("primary db floor update...");

        // 重新查询主库
        DataSourceContextHolder.usePrimary();
        floorPrimary = floorMapper.selectByPrimaryKey(17L);
        System.out.println("primary db floor  is:" + floorPrimary.getName());

        // 重新查询第二从库
        DataSourceContextHolder.useSecond();
        floorSecond = floorMapper.selectByPrimaryKey(17L);
        System.out.println("second db floor is:" + floorSecond.getName());

        // 还原主库floor
        DataSourceContextHolder.usePrimary();
        cond = new Floor();
        cond.setName("I项目");
        cond.setId(17L);
        floorMapper.updateByPrimaryKey(cond);
        System.out.println("primary db floor roll back...");

        // 恢复到原有库 （即默认主库）
        DataSourceContextHolder.clearDBType();

    }

}
