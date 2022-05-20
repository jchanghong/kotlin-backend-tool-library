package com.jch.kotlin_and_java;

import lombok.Data;

@Data
public class JavaBean {
    private String name = "java name!!!";

    public void callKotlin() {
        System.out.println(new KotlinBean().getName());
    }
}
