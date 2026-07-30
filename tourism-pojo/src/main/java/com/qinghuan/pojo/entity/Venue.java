package com.qinghuan.pojo.entity;

import com.qinghuan.pojo.enums.VenueStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Venue extends BaseEntity {

    private String name;
    private String address;
    private String description;
    private String coverObjectKey;
    private VenueStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
