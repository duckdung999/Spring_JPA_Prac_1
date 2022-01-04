package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
public class OrderItem {


    //이 부분은 객체 멤버변수 선언 공간이자, DB 설계라고 보면 된다. 테이블을 만들 필요가 없음.
    @GeneratedValue
    @Id // 기본키임을 알림
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id") //외래키임을 알림 연관관계의 주인
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") //외래키임을 알림 연관관계의 주인
    private Order order;

    private int OrderPrice;
    private int count;


    // 정적 팩토리 메소드 인스턴스가 생성되지 않아도 사용가능하다.
    // 생성자로 해도되는데 정적 팩토리 메소드나 빌더를 사용하는 이유는 따로 있음.
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    public void cancel() {
        getItem().addStock(count);
    }

    //==조회 로직==//

    /**
     * 주문상품 전체 가격 조회
     */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
