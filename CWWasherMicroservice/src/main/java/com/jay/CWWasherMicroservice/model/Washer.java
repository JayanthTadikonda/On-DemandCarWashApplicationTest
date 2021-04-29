package com.jay.CWWasherMicroservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "washer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Washer {

    private int washerId;
    private String name;
    private List<String> address;

}
