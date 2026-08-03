package com.qinghuan.catalog;

import com.qinghuan.pojo.dto.CatalogVenuePageQueryDTO;
import com.qinghuan.pojo.vo.CatalogVenueVO;
import com.qinghuan.pojo.vo.PageResult;
import com.qinghuan.pojo.vo.SellableSessionVO;

import java.time.LocalDate;
import java.util.List;

public interface CatalogService {

    /** 分页查询当前存在可售内容的启用景点。 */
    PageResult<CatalogVenueVO> pageSellableVenues(CatalogVenuePageQueryDTO queryDTO);

    /** 获取启用景点的游客端展示资料。 */
    CatalogVenueVO getVenue(Long venueId);

    /** 查询指定景点、指定日期当前仍可购买的场次和票种。 */
    List<SellableSessionVO> listSellableSessions(Long venueId, LocalDate visitDate);
}
