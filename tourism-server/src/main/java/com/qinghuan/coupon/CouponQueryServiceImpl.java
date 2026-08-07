package com.qinghuan.coupon;

import com.qinghuan.auth.context.UserContext;
import com.qinghuan.common.exception.BusinessException;
import com.qinghuan.common.exception.ErrorCode;
import com.qinghuan.pojo.dto.UserCouponQueryDTO;
import com.qinghuan.pojo.vo.CatalogCouponActivityVO;
import com.qinghuan.pojo.vo.CouponClaimResultVO;
import com.qinghuan.pojo.vo.UserCouponVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponQueryServiceImpl implements CouponQueryService {

    private final CouponMapper couponMapper;

    public CouponQueryServiceImpl(CouponMapper couponMapper) {
        this.couponMapper = couponMapper;
    }

    @Override
    public List<CatalogCouponActivityVO> listCatalogActivities(Long venueId) {
        // 当前时间只生成一次，保证同一条查询中的状态和结束时间判断使用相同基准。
        return couponMapper.listCatalogActivities(venueId, LocalDateTime.now());
    }

    @Override
    public CouponClaimResultVO getClaimResult(String requestId) {
        // userId 放在 SQL 条件中，避免游客通过猜测 requestId 查询他人的领取结果。
        CouponClaimResultVO result = couponMapper.findClaimResult(
                requestId, UserContext.getRequired().userId());
        if (result == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "抢券请求不存在");
        }
        return result;
    }

    @Override
    @Transactional
    public List<UserCouponVO> listMyCoupons(UserCouponQueryDTO query) {
        // 查询前同步收口到期状态，避免定时任务的一分钟延迟展示出“可用”过期券。
        couponMapper.expireAvailableCoupons(LocalDateTime.now());
        return couponMapper.listUserCoupons(UserContext.getRequired().userId(), query);
    }

    @Override
    public int expireAvailableCoupons(LocalDateTime now) {
        // LOCKED 券由其关联订单的取消、超时或退款流程处理，任务只更新 AVAILABLE。
        return couponMapper.expireAvailableCoupons(now);
    }
}
