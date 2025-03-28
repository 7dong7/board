package mystudy.study.domain.member.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mystudy.study.domain.member.dto.search.QSearchMemberInfoDto;
import mystudy.study.domain.member.dto.search.SearchMemberInfoDto;
import mystudy.study.domain.member.dto.MemberSearch;
import mystudy.study.domain.member.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static mystudy.study.domain.member.entity.QMember.member;
import static org.springframework.util.StringUtils.hasText;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 회원 정보 수정 (본인만) - 회원 정보 조회
    @Override
    public Optional<EditMemberDto> getEditMember(Long memberId) {
        
        EditMemberDto editMember = queryFactory
                .select(new QEditMemberDto(
                        member.id.as("memberId"),
                        member.email,
                        member.createdAt,
                        member.nickname,
                        member.mobile)
                )
                .from(member)
                .where(
                        member.id.eq(memberId)
                )
                .fetchOne();
        // Optional 반환 - 본인의 Id 값이 아닐 수 있음 (변조)
        return Optional.ofNullable(editMember);
    }

    // 회원 리스트 조회 (조건에 맞는) - 페이징 처리
    @Override
    public Page<SearchMemberInfoDto> getSearchMemberPage(String searchType, MemberSearch memberSearch, Pageable pageable) {

        List<SearchMemberInfoDto> result = queryFactory
                .select(new QSearchMemberInfoDto(
                                member.id.as("memberId"),
                                member.email,
                                member.nickname,
                                member.name,
                                member.createdAt,
                                member.status)
                )
                .from(member)
                .where(
                        transSearchCondition(memberSearch.getSearchWord(), searchType)
                )
                .orderBy(
                        memberSort(pageable)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //
        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member)
                .where(
                        transSearchCondition(memberSearch.getSearchWord(), searchType)
                );

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    // 검색 조건 동적 변환
    private BooleanExpression transSearchCondition(String searchWord, String searchType) {
        return switch (searchType) {
            case "name" -> member.name.containsIgnoreCase(searchWord);
            case "nickname" -> member.nickname.containsIgnoreCase(searchWord);
            case "email" -> member.email.containsIgnoreCase(searchWord);
            default -> null; // 검색 조건이 있으나 사전에 정의된 조건이 아닌 경우
        };
    }

    // 동적 정렬 조건 변환
    private OrderSpecifier<?> memberSort(Pageable pageable) {

        if (pageable.getSort().isSorted()) {

            for (Sort.Order order : pageable.getSort()) {

                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                return switch (order.getProperty()) {
                    case "nickname" -> new OrderSpecifier<>(direction, member.nickname);
                    case "name" -> new OrderSpecifier<>(direction, member.name);
                    case "email" -> new OrderSpecifier<>(direction, member.email);
                    case "id" -> new OrderSpecifier<>(direction, member.id);
                    default -> null;
                };
            }
        }
        return null;
    }

    // 정렬 조건 정렬
    private BooleanExpression usernameEq(String nickname) {
        return hasText(nickname) ? member.nickname.containsIgnoreCase(nickname) : null;
    }

    private Predicate emailEq(String email) {
        return hasText(email) ? member.email.containsIgnoreCase(email) : null;
    }
    
}
