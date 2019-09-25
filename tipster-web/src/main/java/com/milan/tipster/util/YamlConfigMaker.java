package com.milan.tipster.util;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class YamlConfigMaker {

    void writeConfig(String fileName, Object data) {
        Yaml yaml = new Yaml();
        FileWriter writer = null;
        try {
            writer = new FileWriter(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        StringWriter writer = new StringWriter();
        yaml.dump(data, writer);
        System.out.println("SHOULD WRITE TO YML FILE");
//        System.out.println(writer.toString());

    }

}
