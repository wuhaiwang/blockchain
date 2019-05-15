package com.seasun.management.controller.app;

import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.dto.AppProductDto;
import com.seasun.management.model.Product;
import com.seasun.management.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/apis/auth/app")
public class AppProductController {

    private static final Logger logger = LoggerFactory.getLogger(AppProductController.class);

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public ResponseEntity<?> getAppProducts() {
        logger.info("begin getAppProducts...");
        List<AppProductDto> result =productService.getAppProducts();
        logger.info("end getAppProducts...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }
}
