package com.resteflex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Listing {
    private String id;
    private String title;
    private String description;
    private Double price;
    private String location;
    private Integer bedrooms;
    private Double bathrooms;
    private Integer guests;
    @JsonProperty("image_url")
    private String imageUrl;
    private List<String> images;
    private List<String> amenities;
    @JsonProperty("created_at")
    private String createdAt;
}
