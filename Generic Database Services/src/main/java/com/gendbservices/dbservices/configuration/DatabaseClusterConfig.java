package com.gendbservices.dbservices.configuration;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@ConfigurationProperties("configuration")
public class DatabaseClusterConfig {

    Map<String, String> dbConfig = new HashMap<>();
}