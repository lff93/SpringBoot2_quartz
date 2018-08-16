/**
 * Copyright (C), 2015-2018, 章鱼云商
 * FileName: MyJob
 * Author:   Lifeifei
 * Date:     2018/8/15 13:56
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.itstyle.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;

/**
 * 〈〉
 *
 * @author Lifeifei
 * @create 2018/8/15
 * @since 1.0.0
 */
public class MyJob02 implements Job, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ChickenJob.class);

    private static final long serialVersionUID = 1L;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("MyJob02, 执行了:" + new Date());
        try {
            Thread.sleep(30000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("MyJob02, 醒了:" + new Date());
    }
}