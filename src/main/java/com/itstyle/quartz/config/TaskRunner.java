package com.itstyle.quartz.config;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.itstyle.quartz.entity.QuartzEntity;
import com.itstyle.quartz.service.IJobService;
/**
 * 初始化一个测试Demo任务
 * 创建者 科帮网
 * 创建时间	2018年4月3日
 */
@Component
public class TaskRunner implements ApplicationRunner{
    
	private final static Logger LOGGER = LoggerFactory.getLogger(TaskRunner.class);
	
	@Autowired
    private IJobService jobService;
	@Autowired
    private Scheduler scheduler;
	
	/**
	 * <项目启动时发现没有任务，会自动创建一个，可以删除>
	 *
	 * @param var
	 * @return void
	 * @author Lifeifei
	 * @date 2018/8/16 13:32 
	 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void run(ApplicationArguments var) throws Exception{
    	Long count = jobService.listQuartzEntity(null);
    	if(count==0){
    		LOGGER.info("初始化测试任务");
    		QuartzEntity quartz = new QuartzEntity();
    		quartz.setJobName("test01");
    		quartz.setJobGroup("test");
    		quartz.setDescription("测试任务");
    		quartz.setJobClassName("com.itstyle.quartz.job.ChickenJob");
    		quartz.setCronExpression("0/20 * * * * ?");
   	        Class cls = Class.forName(quartz.getJobClassName()) ;
   	        cls.newInstance();
   	        //构建job信息
   	        JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(quartz.getJobName(),
   	        		quartz.getJobGroup())
   	        		.withDescription(quartz.getDescription()).build();
   	        //添加JobDataMap数据
			jobDetail.getJobDataMap().put("itstyle", "科帮网欢迎你");
			jobDetail.getJobDataMap().put("blog", "https://blog.52itstyle.com");
			jobDetail.getJobDataMap().put("data", new String[]{"张三","李四"});
   	        // 触发时间点
   	        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(quartz.getCronExpression());
   	        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger"+quartz.getJobName(), quartz.getJobGroup())
   	                .startNow().withSchedule(cronScheduleBuilder).build();	
   	        //交由Scheduler安排触发
   	        scheduler.scheduleJob(jobDetail, trigger);
    	}
    }

}