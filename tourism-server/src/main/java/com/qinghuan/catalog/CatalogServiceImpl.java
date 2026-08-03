package com.qinghuan.catalog;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qinghuan.common.constant.RedisConstants;
import com.qinghuan.common.constant.cacheKeys.VenueConstant;
import com.qinghuan.common.exception.BusinessException;
import com.qinghuan.common.exception.ErrorCode;
import com.qinghuan.config.oss.OssUtils;
import com.qinghuan.pojo.dto.CatalogVenuePageQueryDTO;
import com.qinghuan.pojo.vo.CatalogVenueVO;
import com.qinghuan.pojo.vo.PageResult;
import com.qinghuan.pojo.vo.SellableSessionVO;
import com.qinghuan.redis.CacheClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CatalogServiceImpl implements CatalogService {

    private final CatalogMapper catalogMapper;
    private final OssUtils ossUtils;
    private final CacheClient cacheClient;

    public CatalogServiceImpl(CatalogMapper catalogMapper, OssUtils ossUtils, CacheClient cacheClient) {
        this.catalogMapper = catalogMapper;
        this.ossUtils = ossUtils;
        this.cacheClient = cacheClient;
    }

    @Override
    public PageResult<CatalogVenueVO> pageSellableVenues(
            CatalogVenuePageQueryDTO queryDTO) {
        queryDTO.setKeyword(StringUtils.hasText(queryDTO.getKeyword())
                ? queryDTO.getKeyword().trim()
                : null);

        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        Page<CatalogVenueVO> page = (Page<CatalogVenueVO>)
                catalogMapper.listSellableVenues(queryDTO);
        page.forEach(this::resolveCoverUrl);

        return new PageResult<>(
                page.getResult(), page.getTotal(), page.getPageNum(), page.getPageSize());
    }

    @Override
    public CatalogVenueVO getVenue(Long venueId) {
        CatalogVenueVO venue = cacheClient.queryWithPassThrough(
                VenueConstant.VENUE_DETAIL_PREFIX,
                venueId,
                CatalogVenueVO.class,
                catalogMapper::findEnabledVenue,
                VenueConstant.VENUE_DETAIL_TTL,
                TimeUnit.SECONDS
        );
        if (venue == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点不存在");
        }
        resolveCoverUrl(venue);
        return venue;
    }

    @Override
    public List<SellableSessionVO> listSellableSessions(
            Long venueId, LocalDate visitDate) {
        // 空场次列表与景点不存在语义不同，因此先确认景点仍处于启用状态。
        if (catalogMapper.findEnabledVenue(venueId) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点不存在");
        }
        return catalogMapper.listSellableSessions(venueId, visitDate);
    }

    private void resolveCoverUrl(CatalogVenueVO venue) {
        // Mapper 暂存 objectKey，离开 Service 前统一转换成前端可访问地址。
        venue.setCoverUrl(ossUtils.getPublicUrl(venue.getCoverUrl()));
    }
}
