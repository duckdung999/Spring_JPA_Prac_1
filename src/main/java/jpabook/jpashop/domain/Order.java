package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@Table(name = "orders") // order는 예약어로 걸린경우가 많다.
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY) // N + 1 문제가 빡세게 발생함. 물론 lazy라고 발생 안하는건 아님, 이건 jpql문제이기 때문에 jpql을 잘 다뤄줘야함.
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // orderItems에 RDBMS관점에서 봤을 대 참조를 걸고 있는 건 order밖에없음.
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // orderItems와 마찬가지로 참조를 걸고 있는건 order밖에없음.
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]

    //==연관관계 메서드==//

    // db에 저장할 때는 연관관계의 주인에만 저장하면 된다. 근데 실제로 로직을 태울 때 그 값을 보장받을 수 있는가?
    // 그래서 연관관계 편의 메서드를 만들 필요가 있다.



    //    원래는 이런식의 분리된 코드를 하나의 원자 메서드로 묶어버림
    //    member.getOrders().add(order);
    //    order.setMember(member);

    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    // 생성 메서드, 정적 팩토리 메소드 방식으로 만들면 된다.
    // 주문 생성할때는 좀 복잡한 로직이 따르기 때문에 만들어 두면 편한다.
    // 엔티티의 멤버 변수들은 기본적으로 JPA가 DB에서 값을 가져올 때 기본 생성자로 값을 가져오기때문에 큰 문제가 없다.

//    정적 팩토리 메소드에는 크게 5 가지의 장점을 가지고 있다.
//
//            - 첫 번째, 이름을 가질 수 있다.
//
// - 두 번째, 호출 될 때마다 인스턴스를 새로 생성하지는 않아도 된다.
//
// - 세 번째, 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.
//
// - 네 번째, 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.
//
// - 다섯 번째, 정적 팩터리 메소드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }


        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;




    }
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

}
