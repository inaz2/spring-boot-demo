package com.example;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MessageController {

    @Autowired
    private MessageService service;

    @GetMapping("/messages")
    public String messages(Model model) {
        model.addAttribute("messageForm", new MessageForm());

        List<Message> messages = service.getRecentMessages(100);
        model.addAttribute("messages", messages);

        return "messages";
    }

    @PostMapping("/messages")
    public String messagesPost(Model model, @Valid MessageForm messageForm, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            List<Message> messages = service.getRecentMessages(100);
            model.addAttribute("messages", messages);
            return "messages";
        }

        service.save(new Message(messageForm.getName(), messageForm.getText(), request.getRemoteAddr()));
        return "redirect:/messages";
    }

    @RequestMapping("/messages.json")
    @ResponseBody
    public List<Message> messagesJson() {
        List<Message> messages = service.getRecentMessages(100);
        return messages;
    }

    @RequestMapping("/messages.xlsx")
    public ModelAndView messagesXlsx() {
        List<Message> messages = service.getRecentMessages(100);
        return new ModelAndView(new MessagesXlsxView(), "messages", messages);
    }

}
