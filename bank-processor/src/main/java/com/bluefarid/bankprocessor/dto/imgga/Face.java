package com.bluefarid.bankprocessor.dto.imgga;

import lombok.Data;

import java.util.Map;

@Data
public class Face {
    private String confidence;
    private Map<String, String> coordinates;
    private String faceId;
}
