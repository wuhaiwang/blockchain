package com.seasun.management.dto;

import com.seasun.management.model.Product;

import java.util.List;

public class AppProductDto extends Product {

    private List<String> actorNames;

    public List<String> getActorNames() {
        return actorNames;
    }

    public void setActorNames(List<String> actorNames) {
        this.actorNames = actorNames;
    }
}
