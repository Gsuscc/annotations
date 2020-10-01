package com.codecool;

public class Routes {

    @WebRoute(route = "/classinfo")
    static public String test1(){
        return "classinfo";
    }

    @WebRoute(route = "/annotations")
    static public String test2(){
        return "annotations";
    }

    @WebRoute(route = "/")
    static public String test3(){
        return "root";
    }

}
