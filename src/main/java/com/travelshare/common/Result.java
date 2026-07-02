package com.travelshare.common;

import lombok.Data;

@Data
public class Result {

    private Integer code;

    private String message;

    private Object data;

    public static Result success(){

        Result r = new Result();

        r.setCode(200);
        r.setMessage("success");

        return r;
    }

    public static Result success(Object data){

        Result r = new Result();

        r.setCode(200);
        r.setMessage("success");
        r.setData(data);

        return r;
    }

    public static Result error(String msg){

        Result r = new Result();

        r.setCode(500);
        r.setMessage(msg);

        return r;
    }
}