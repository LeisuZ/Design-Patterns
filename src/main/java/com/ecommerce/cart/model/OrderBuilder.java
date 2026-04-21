package com.ecommerce.cart.model;

public class OrderBuilder {
    private Cart cart;
    private Coupon coupon;
    private Member member;
    private Address address;

    public OrderBuilder(Cart cart) {
        this.cart = cart;
    }

    public OrderBuilder withCoupon(Coupon coupon) {
        this.coupon = coupon;
        return this;
    }

    public OrderBuilder withMember(Member member) {
        this.member = member;
        return this;
    }

    public OrderBuilder withAddress(Address address) {
        this.address = address;
        return this;
    }

    public Order build() {
        Order order = new Order(cart);
        if (coupon != null) {
            order.setSelectedCoupon(coupon);
        }
        if (member != null) {
            order.setMemberId(member.getId());
            order.setMemberName(member.getName());
        }
        if (address != null) {
            order.setAddress(address);
        }
        return order;
    }
}
