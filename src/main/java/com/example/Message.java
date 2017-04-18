package com.example;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String text;
    
    @Column(nullable = false)
    private String remoteAddr;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createdAt;

    // JPA requirement
    protected Message() {}

    public Message(String name, String text, String remoteAddr) {
        this.name = name;
        this.text = text;
        this.remoteAddr = remoteAddr;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = new Date();
    }

    @Override
    public String toString() {
        return String.format("Message[id=%d, name='%s', text='%s']", id, name, text);
    }

    public Long getId() {
        return id;
    }

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

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

}
