package com.qinghuan.coupon;

import com.qinghuan.common.exception.BusinessException;
import com.qinghuan.common.exception.ErrorCode;
import com.qinghuan.pojo.entity.UserCoupon;
import com.qinghuan.pojo.enums.UserCouponStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CouponOrderServiceImpl implements CouponOrderService {

    private final CouponMapper couponMapper;

    public CouponOrderServiceImpl(CouponMapper couponMapper) {
        this.couponMapper = couponMapper;
    }

    @Override
    public CouponDiscount lockForOrder(Long couponId,
                                       Long userId,
                                       Long venueId,
                                       BigDecimal originalAmount) {
        // 先读取快照是为了给出明确业务错误，并取得可信的门槛和优惠金额。
        UserCoupon coupon = couponMapper.findCouponForOrder(couponId, userId);
        if (coupon == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "优惠券不存在");
        }
        if (!coupon.getVenueId().equals(venueId)) {
            throw new BusinessException(ErrorCode.CONFLICT, "优惠券不适用于当前景点");
        }
        if (coupon.getStatus() != UserCouponStatus.AVAILABLE) {
            throw new BusinessException(ErrorCode.CONFLICT, "优惠券当前不可用");
        }
        if (originalAmount.compareTo(coupon.getThresholdAmount()) < 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "订单金额未达到优惠券使用门槛");
        }

        LocalDateTime now = LocalDateTime.now();
        // 前面的读取不能替代并发控制；最终以带原状态和有效期条件的 UPDATE 为准。
        int updated = couponMapper.lockCouponForOrder(couponId, userId, venueId, now);
        if (updated == 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "优惠券状态或有效期已发生变化");
        }

        // 活动规则保证 thresholdAmount >= discountAmount，达到门槛后应付金额不会为负数。
        BigDecimal payableAmount = originalAmount.subtract(coupon.getDiscountAmount());
        return new CouponDiscount(couponId, coupon.getDiscountAmount(), payableAmount);
    }

    @Override
    public void markUsed(Long couponId, LocalDateTime now) {
        // couponId 为空代表订单未使用优惠券，保持原订单流程即可。
        if (couponId != null && couponMapper.markCouponUsed(couponId, now) == 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "优惠券状态发生变化");
        }
    }

    @Override
    public void releaseLocked(Long couponId, LocalDateTime now) {
        // SQL 会依据 validUntil 在 AVAILABLE 与 EXPIRED 中选择目标状态。
        if (couponId != null && couponMapper.releaseLockedCoupon(couponId, now) == 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "优惠券状态发生变化");
        }
    }

    @Override
    public void restoreAfterRefund(Long couponId, LocalDateTime now) {
        // 退款只允许恢复 USED 券；条件更新失败时让外层订单事务整体回滚。
        if (couponId != null && couponMapper.restoreUsedCoupon(couponId, now) == 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "优惠券状态发生变化");
        }
    }
}
