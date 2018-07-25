package com.zeed.paaro.lib.models;

import java.util.List;

public class EmailNotification {

    private String subject;

    private String to;

    private List<String> tos;

    private List<String> ccs;

    private List<String> bcs;

    private String content;


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<String> getTos() {
        return tos;
    }

    public void setTos(List<String> tos) {
        this.tos = tos;
    }

    public List<String> getCcs() {
        return ccs;
    }

    public void setCcs(List<String> ccs) {
        this.ccs = ccs;
    }

    public List<String> getBcs() {
        return bcs;
    }

    public void setBcs(List<String> bcs) {
        this.bcs = bcs;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
