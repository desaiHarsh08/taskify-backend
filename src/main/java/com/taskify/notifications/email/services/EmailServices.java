package com.taskify.notifications.email.services;

import com.taskify.task.instances.models.FunctionInstanceModel;
import com.taskify.task.instances.models.TaskInstanceModel;
import com.taskify.task.templates.models.FunctionTemplateModel;
import com.taskify.task.templates.repositories.FunctionTemplateRepository;
import com.taskify.user.models.UserModel;
import com.taskify.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServices {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionTemplateRepository functionTemplateRepository;

    @Async
    public void sendTaskAssignmentEmail(TaskInstanceModel taskInstanceModel) {
        UserModel createdByUser = this.getUser(taskInstanceModel.getCreatedByUser().getId());
        UserModel assignedToUser = this.getUser(taskInstanceModel.getAssignedToUser().getId());

        String subject = "Task Assignment Notification from Taskify Software";
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(assignedToUser.getEmail());
            helper.setSubject(subject);

            // Create the HTML content using Thymeleaf template
            Context context = new Context();
            context.setVariable("assignedUserName", assignedToUser.getName());
            context.setVariable("taskType", taskInstanceModel.getTaskTemplate().getTitle());
            context.setVariable("taskPriority", taskInstanceModel.getPriorityType());
            context.setVariable("createdBy", createdByUser.getName());
            context.setVariable("createdAt", taskInstanceModel.getCreatedAt());

            String htmlContent = templateEngine.process("taskAssignmentEmail", context);
            helper.setText(htmlContent, true); // Enable HTML content

            this.emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendForwardTaskEmail(TaskInstanceModel taskInstanceModel) {
        UserModel createdByUser = this.getUser(taskInstanceModel.getCreatedByUser().getId());
        UserModel assignedToUser = this.getUser(taskInstanceModel.getAssignedToUser().getId());
        String subject = "Forwarded Task Notification from Taskify Software";

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(assignedToUser.getEmail());
            helper.setSubject(subject);

            // Set Thymeleaf context variables
            Context context = new Context();
            context.setVariable("assignedUserName", assignedToUser.getName());
            context.setVariable("taskType", taskInstanceModel.getTaskTemplate().getTitle());
            context.setVariable("taskPriority", taskInstanceModel.getPriorityType());
            context.setVariable("createdBy", createdByUser.getName());

            // Generate HTML content from the template
            String htmlContent = templateEngine.process("forwardTaskEmail", context);
            helper.setText(htmlContent, true);

            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendCloseTaskEmail(TaskInstanceModel taskInstanceModel) {
        UserModel closedByUser = this.getUser(taskInstanceModel.getClosedByUser().getId());

        String subject = "Task Assignment Notification from Taskify Software";
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(closedByUser.getEmail());
            helper.setSubject(subject);

            // Set Thymeleaf context variables
            Context context = new Context();
            context.setVariable("taskType", taskInstanceModel.getTaskTemplate().getTitle());
            context.setVariable("taskPriority", taskInstanceModel.getPriorityType());
            context.setVariable("closedBy", closedByUser.getName() + " (You)");
            context.setVariable("closedDate", taskInstanceModel.getClosedAt());

            // Generate HTML content from the template
            String htmlContent = templateEngine.process("closeTaskEmail", context);
            helper.setText(htmlContent, true);

            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendFunctionAssignmentEmail(FunctionInstanceModel functionInstanceModel) {
        UserModel createdByUser = this.getUser(functionInstanceModel.getCreatedByUser().getId());
        UserModel assignedToUser = this.getUser(functionInstanceModel.getTaskInstance().getAssignedToUser().getId());

        FunctionTemplateModel functionTemplateModel = this.functionTemplateRepository.findById(functionInstanceModel.getFunctionTemplate().getId()).orElseThrow(
                () -> new IllegalArgumentException("Please provide the valid function_template_id")
        );

        String subject = "Function Assignment Notification from Taskify Software";
        String to = assignedToUser.getEmail();

        // Create the HTML content using Thymeleaf template
        Context context = new Context();
        context.setVariable("assignedUserName", assignedToUser.getName());
        context.setVariable("taskType", functionInstanceModel.getTaskInstance().getTaskTemplate().getTitle());
        context.setVariable("taskPriority", functionInstanceModel.getTaskInstance().getPriorityType());
        context.setVariable("functionTitle", functionTemplateModel.getTitle());
        context.setVariable("dueDate", functionInstanceModel.getDueDate());
        context.setVariable("department", functionTemplateModel.getDepartment());
        context.setVariable("createdBy", createdByUser.getName());

        // Generate the HTML content from the Thymeleaf template
        String htmlContent = templateEngine.process("functionAssignmentEmailTemplate", context);

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // Enable HTML content

            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private UserModel getUser(Long id) {
        return this.userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Please provide the valid user!")
        );
    }
    
}
