package com.qinghuan.venue;

import com.qinghuan.pojo.entity.Venue;
import org.springframework.web.multipart.MultipartFile;

public interface VenueService {
    // 获取当前景点信息
    public Venue getCurrentVenue();

    void updateCurrentVenue(Venue newVenue, MultipartFile newCover);
}
