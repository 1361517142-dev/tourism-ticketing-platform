package com.qinghuan.venue;

import com.qinghuan.auth.context.UserContext;
import com.qinghuan.common.constant.cacheKeys.VenueConstant;
import com.qinghuan.common.exception.BusinessException;
import com.qinghuan.common.exception.ErrorCode;
import com.qinghuan.config.oss.OssUtils;
import com.qinghuan.pojo.entity.Venue;
import com.qinghuan.redis.CacheClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VenueServiceImpl implements VenueService {
    private final VenueMapper venueMapper;
    private final OssUtils ossUtils;
    private final CacheClient cacheClient;

    public VenueServiceImpl(VenueMapper venueMapper, OssUtils ossUtils, CacheClient cacheClient) {
        this.venueMapper = venueMapper;
        this.ossUtils = ossUtils;
        this.cacheClient = cacheClient;
    }

    public Venue getCurrentVenue() {
        Long id = UserContext.getRequired().venueId();
        Venue venue = venueMapper.getVenueById(id);
        if (venue == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点不存在");
        }
        return venue;
    }

    @Override
    public void updateCurrentVenue(Venue newVenue, MultipartFile newCover) {
        Long venueId = UserContext.getRequired().venueId();
        Venue currentVenue = getCurrentVenue();
        String uploadedObjectKey = null;

        if (newCover != null && !newCover.isEmpty()) {
            if (!StringUtils.hasText(newCover.getContentType())
                    || !newCover.getContentType().startsWith("image/")) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "封面文件必须是图片");
            }
            uploadedObjectKey = ossUtils.upload("venue", newCover);
            newVenue.setCoverObjectKey(uploadedObjectKey);
        }

        try {
            int updatedRows = venueMapper.updateVenue(newVenue, venueId);
            if (updatedRows == 0) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "景点不存在");
            }
            cacheClient.delete(VenueConstant.VENUE_DETAIL_PREFIX + venueId);
        } catch (DuplicateKeyException exception) {
            ossUtils.delete(uploadedObjectKey);
            throw new BusinessException(ErrorCode.CONFLICT, "相同名称和地址的景点已存在");
        }

        if (uploadedObjectKey != null) {
            ossUtils.delete(currentVenue.getCoverObjectKey());
        }
    }
}
