package com.qinghuan.venue;

import com.qinghuan.annotation.RequireRole;
import com.qinghuan.common.response.ApiResponse;
import com.qinghuan.pojo.entity.Venue;
import com.qinghuan.pojo.enums.AccountRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class VenueController {

    @Autowired
    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    /*
     * 获取当前景点信息
     */
    @GetMapping("/operator/venue")
    @RequireRole(AccountRole.OPERATOR)
    public ApiResponse<Venue> getCurrentVenue() {
        return ApiResponse.success(venueService.getCurrentVenue());
    }

    /*
     * 修改当前景点信息
     */
    @PutMapping("/operator/venue")
    @RequireRole(AccountRole.OPERATOR)
    public ApiResponse<Void> updateCurrentVenue(
            Venue newVenue,
            @RequestParam(value = "coverImage", required = false) MultipartFile newCover) {
        venueService.updateCurrentVenue(newVenue, newCover);
        return ApiResponse.success();
    }

}
