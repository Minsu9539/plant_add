package com.example.plnatsub;

public class AccountItem {
    String content;
    String success;
//    json json;
    String message;
    String images;


    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

public String getMessage() {
    return message;
}

    public void setMessage(String message) {
        this.message = message;
    }

    public AccountItem() {
    }

    public AccountItem(String message) {
        this.message = message;
    }
    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
//    public com.example.plnatsub.json getJson() {
//        return json;
//    }
//
//    public void setJson(com.example.plnatsub.json json) {
//        this.json = json;
//    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
       this.content = content;
    }

//    public AccountItem(){
//
//    }
//    public AccountItem(String content){
//        this.content = content;
//    }
}