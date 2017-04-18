package com.example;

import javax.validation.constraints.Size;

public class MessageForm {

    @Size(max=80)
    private String name;

    @Size(min=1, max=140)
    private String text;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
