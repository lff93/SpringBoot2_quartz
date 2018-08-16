package com.itstyle.quartz.web;


import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.quartz.*;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itstyle.quartz.entity.QuartzEntity;
import com.itstyle.quartz.entity.Result;
import com.itstyle.quartz.service.IJobService;
@RestController
@RequestMapping("/job")
public class JobController {
	private final static Logger LOGGER = LoggerFactory.getLogger(JobController.class);
	

	@Autowired
    private Scheduler scheduler;
    @Autowired
    private IJobService jobService;
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/add")
	public Result save(QuartzEntity quartz){
		LOGGER.info("新增任务");
		try {
	        //如果是修改  展示旧的 任务
	        if(quartz.getOldJobGroup()!=null){
	        	JobKey key = new JobKey(quartz.getOldJobName(),quartz.getOldJobGroup());
	        	scheduler.deleteJob(key);
	        }
	        Class cls = Class.forName(quartz.getJobClassName()) ;
	        cls.newInstance();
	        //构建job信息
	        JobDetail job = JobBuilder.newJob(cls).withIdentity(quartz.getJobName(),
	        		quartz.getJobGroup())
	        		.withDescription(quartz.getDescription()).build();
	        // 触发时间点
	        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(quartz.getCronExpression());
	        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger"+quartz.getJobName(), quartz.getJobGroup())
	                .startNow().withSchedule(cronScheduleBuilder).build();
	        //交由Scheduler安排触发
	        scheduler.scheduleJob(job, trigger);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error();
		}
		return Result.ok();
	}

	/**
	 * <增加SimpleTrigger类型>
	 *
	 * @param quartz
	 * @return com.itstyle.quartz.entity.Result
	 * @author Lifeifei
	 * @date 2018/8/16 13:23 
	 */
	@PostMapping("/addSimpleTrigger")
	public Result addSimpleTrigger(QuartzEntity quartz){
		LOGGER.info("新增任务");
		try {
			//如果是修改  展示旧的 任务
			if(quartz.getOldJobGroup()!=null){
				JobKey key = new JobKey(quartz.getOldJobName(),quartz.getOldJobGroup());
				scheduler.deleteJob(key);
			}
			Class cls = Class.forName(quartz.getJobClassName()) ;
			cls.newInstance();
			//构建job信息
			JobDetail job = JobBuilder.newJob(cls).withIdentity(quartz.getJobName(),
					quartz.getJobGroup())
					.withDescription(quartz.getDescription()).build();
			// 触发时间点
			SimpleTriggerImpl simpleTrigger = new SimpleTriggerImpl();
			simpleTrigger.setJobGroup(quartz.getJobGroup());
			simpleTrigger.setName(quartz.getJobName());
			simpleTrigger.setStartTime(new Date(1534324530000L));//开始运行时间
			simpleTrigger.setRepeatCount(0);// 重复次数
			simpleTrigger.setRepeatInterval(0);// 重复间隔

			//交由Scheduler安排触发
			scheduler.scheduleJob(job, simpleTrigger);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error();
		}
		return Result.ok();
	}

	public static void main(String[] args) {
		System.out.println(new Date());
	}
	@PostMapping("/list")
	public Result list(QuartzEntity quartz,Integer pageNo,Integer pageSize){
		LOGGER.info("任务列表");
		List<QuartzEntity> list = jobService.listQuartzEntity(quartz, pageNo, pageSize);
		return Result.ok(list);
	}
	@PostMapping("/trigger")
	public  Result trigger(QuartzEntity quartz,HttpServletResponse response) {
		LOGGER.info("触发任务");
		try {
		     JobKey key = new JobKey(quartz.getJobName(),quartz.getJobGroup());
		     scheduler.triggerJob(key);
		} catch (SchedulerException e) {
			 e.printStackTrace();
			 return Result.error();
		}
		return Result.ok();
	}
	@PostMapping("/pause")
	public  Result pause(QuartzEntity quartz,HttpServletResponse response) {
		LOGGER.info("停止任务");
		try {
		     JobKey key = new JobKey(quartz.getJobName(),quartz.getJobGroup());
		     scheduler.pauseJob(key);
		} catch (SchedulerException e) {
			 e.printStackTrace();
			 return Result.error();
		}
		return Result.ok();
	}
	@PostMapping("/resume")
	public  Result resume(QuartzEntity quartz,HttpServletResponse response) {
		LOGGER.info("恢复任务");
		try {
		     JobKey key = new JobKey(quartz.getJobName(),quartz.getJobGroup());
		     scheduler.resumeJob(key);
		} catch (SchedulerException e) {
			 e.printStackTrace();
			 return Result.error();
		}
		return Result.ok();
	}
	@PostMapping("/remove")
	public  Result remove(QuartzEntity quartz,HttpServletResponse response) {
		LOGGER.info("移除任务");
		try {  
            TriggerKey triggerKey = TriggerKey.triggerKey(quartz.getJobName(), quartz.getJobGroup());  
            // 停止触发器  
            scheduler.pauseTrigger(triggerKey);  
            // 移除触发器  
            scheduler.unscheduleJob(triggerKey);  
            // 删除任务  
            scheduler.deleteJob(JobKey.jobKey(quartz.getJobName(), quartz.getJobGroup()));  
            System.out.println("removeJob:"+JobKey.jobKey(quartz.getJobName()));  
        } catch (Exception e) {  
        	e.printStackTrace();
            return Result.error();
        }  
		return Result.ok();
	}
}
