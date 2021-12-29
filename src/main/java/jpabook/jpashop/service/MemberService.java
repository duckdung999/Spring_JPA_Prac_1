package jpabook.jpashop.service;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional // 데이터 변경/읽기가 존재하면 컴포넌트에 무조건 트랜잭션을 넣어줘야함.
@RequiredArgsConstructor
public class MemberService {

//    //1번 방식
//    @Autowired
//    private MemberRepository memberRepository; // 필드 주입은 한계가 있음, private이라서 바꾸려면 직접 들어와야함;


//    //2번 방식
//    private final MemberRepository memberRepository; // 컴파일 시점에 주입이 되어야만 실행이 된다.
//    @Autowired
//    public MemberService(MemberRepository memberRepository){
//        this.memberRepository = memberRepository;
//    }


    //3번 방식 롬복 쓰기, requiredArgsconstrutor를 쓰면 알아서 주입해줌
    private final MemberRepository memberRepository; // 컴파일 시점에 주입이 되어야만 실행이 된다.

    // 회원 가입 -> 리턴 딱히 필요 없는데, 그래도 변화를 알리기 위해 회원의 고유 pk를 return하는 함수로 만들자.
    public Long join(Member member){
        validationduplicateMember(member); // w중복회원 검사
        memberRepository.save(member);
        return member.getId();
    }

    private void validationduplicateMember(Member member) { // 이름중복을 막이 봅시다.
        List<Member> findMembers =  memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }


    //전체 회원 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    //단건 조회
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }

}
