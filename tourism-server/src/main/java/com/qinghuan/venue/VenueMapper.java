package com.qinghuan.venue;

import com.qinghuan.pojo.entity.Venue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VenueMapper {
    // 获取当前景点信息
    public Venue getVenueById(Long id);

    int updateVenue(@Param("newVenue") Venue newVenue, @Param("id") Long id);
}
