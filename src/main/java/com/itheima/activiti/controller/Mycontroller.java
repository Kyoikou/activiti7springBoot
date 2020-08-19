package com.itheima.activiti.controller;

import com.itheima.activiti.SecurityUtil;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Mycontroller {
    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 查询任务，执行任务
     */
    @RequestMapping("/hello")
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
