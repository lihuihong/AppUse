package com.example.testdemo.bean;

/**
 * Created by 那个谁 on 2018/3/21.
 * 奥特曼打小怪兽
 * 作用：位于栈顶App的信息
 */

public class TopAppInfo {

    public  String appName;

    private String studentId;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getTopAppPackageName() {
        return appName;
    }

    public void setTopAppPackageName(String topAppPackageName) {
        this.appName = topAppPackageName;
    }


}
