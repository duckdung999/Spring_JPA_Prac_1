package jpabook.jpashop.repository;


import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        if (item.getId() == null){ //새로 생성한 객체
            em.persist(item);
        }else{ //이미 등록돼있던 객체 그럼 update임. merge는 필드값 전부를 갈아치우는데, 이 과정에서 null이들어가서 문제가 발생할 수 있음
            em.merge(item);
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i",Item.class).getResultList();
    }
}
