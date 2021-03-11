package org.example;

/**
 * @author whoami
 */
public class Sit {
    private String date;
    private String type;
    private String module;
    private String status;

    public Sit(String type, String module, String status,String date) {
        this.type = type;
        this.module = module;
        this.status = status;
        this.date=date;
    }

    public Sit() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
