package com.milan.tipster.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Link {

    private String href;
    private String text;
}
