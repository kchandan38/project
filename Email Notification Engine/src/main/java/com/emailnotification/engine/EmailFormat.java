package com.emailnotification.engine;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum EmailFormat {

    TEXT("text", "text/plain"), HTML("html", "text/html");
    private String key;
    private String mineType;

    @JsonCreator
    public static EmailFormat fromString(String key){
        EmailFormat emailFormat = null;
        if(key!=null){
            emailFormat = EmailFormat.valueOf(key.toUpperCase());
        }
        return emailFormat;
    }
}
