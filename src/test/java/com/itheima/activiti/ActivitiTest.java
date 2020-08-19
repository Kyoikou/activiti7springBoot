package com.itheima.activiti;

import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * SpringBoot与Junit整合，测试流程定义的相关操作
 *  任务完成
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivitiTest {
    @Autowired
    private ProcessRuntime processRuntime;      // 实现流程定义的相关操作

    @Autowired
    private TaskRuntime taskRuntime;            // 任务相关操作

    @Autowired
    private  SecurityUtil securityUtil;         // spring Security相关的工具类

    // 流程定义信息查看     注意：流程部署工作，activiti7与springboot整合后，会自动部署processse下的文件
    @Test
    public void testDefinition(){
        // 强制依赖securityUtil验证！！！  springSecurity认证
        securityUtil.logInAs("salaboy");


        // 分页查询出流程定义信息
        Page processDefinitionPage = processRuntime
                .processDefinitions(Pageable.of(0, 10));

        System.out.println("已经部署的流程个数" + processDefinitionPage.getTotalItems());

        for (Object pd : processDefinitionPage.getContent()){
            System.out.println(pd);
        }
    }

    @Test
    public void testStartInstance(){
        securityUtil.logInAs("salaboy");

        ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder
        .start()
        .withProcessDefinitionKey("team01")
        .build());

        System.out.println(processInstance.getId());
    }

    /**
     * 查询任务 并完成任务
     */
    @Test
    public void testTask(){
        securityUtil.logInAs("ryandawsonuk");
        Page<Task> taskPage = taskRuntime.tasks(Pageable.of(0,10));
        if (taskPage.getTotalItems() > 0){
            // 说明任务
            for (Task task:taskPage.getContent()){
                System.out.println("任务:" + task);

                // 拾取任务  谁登录谁拾取
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());

                taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());
            }
        }

        // 再次查询新的任务
        taskPage = taskRuntime.tasks(Pageable.of(0,10));
        if (taskPage.getTotalItems() == 0){
            System.out.println("没有任务了");
        }
    }
}
