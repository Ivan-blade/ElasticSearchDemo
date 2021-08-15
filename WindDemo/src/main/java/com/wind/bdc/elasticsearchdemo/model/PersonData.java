package com.wind.bdc.elasticsearchdemo.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author hylu.Ivan
 * @date 2021/7/30 下午1:52
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonData {
    private String name;
    private int code;
    private String hobby;
    private int age;
}
