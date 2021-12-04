package com.milan.tipster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class TipsterApplication {
    public static void main(String[] args) {

        SpringApplication.run(TipsterApplication.class, args);

        // TODO
        // 1) Если PICK опредлился как NOBET то и статус у него должен быть NOBET а fetch_status = 'FULLY_FETCHED'
        // 2) Если поле strong = null то pick NOBET, статус у него должен быть NOBET а fetch_status = 'FULLY_FETCHED'
    }
}