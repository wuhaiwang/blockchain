package com.seasun.management.service;


import com.seasun.management.dto.AppProductDto;
import com.seasun.management.model.Product;

import java.util.List;

public interface ProductService {

    List<AppProductDto> getAppProducts();
}
