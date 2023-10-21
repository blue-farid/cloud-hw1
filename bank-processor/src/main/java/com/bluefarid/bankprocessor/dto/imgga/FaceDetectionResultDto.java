package com.bluefarid.bankprocessor.dto.imgga;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FaceDetectionResultDto {
    private List<Face> faces = new ArrayList<>();
}
