package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class MemberRepository {

    @PersistenceContext // 팩토리 만들 필요없이 필드 주입, 아주 유용합니다.
    private EntityManager em;

    public void save(Member member){
        em.persist(member);
    }

    public Member findOne(Long id){ //단건 조회
        return em.find(Member.class, id); // pk기준으로 찾는 함수임
    }

    public List<Member> findAll(){ // 전부조회, 엔티티매니저 함수 없음, 쿼리 짜야됨 쿼리 짜는 방식은 크게, jpql, criteria, Qdsl이 있음.
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }


    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name ", Member.class)
                .setParameter("name",name) // :을 넣은 왼쪽, 오른쪽은 변수
                .getResultList(); // 변수를 넣어 동적 쿼리를 만들고 싶을 땐 :을 넣으시오.
    }


}
