package com.seasun.management.service.impl;

import com.seasun.management.dto.AppProductDto;
import com.seasun.management.mapper.ProductMapper;
import com.seasun.management.model.Product;
import com.seasun.management.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<AppProductDto> getAppProducts() {
        List<AppProductDto> results = productMapper.selectAll();
        for (AppProductDto product : results) {
            if (product.getActors() != null) {
                String[] names = product.getActors().split(" ");
                product.setActorNames(Arrays.asList(names));
            }else{
                product.setActorNames(new ArrayList<>(0));
            }
        }
        return results;
    }
}
