package com.qinghuan.pojo.entity;

import com.qinghuan.pojo.enums.VenueStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Venue extends BaseEntity {

    private String name;
    private String address;
    private String description;
    private String coverUrl;
    private VenueStatus status;
}
