package mystudy.study.controller;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.dto.comment.CommentDto;
import mystudy.study.domain.dto.comment.ParentCommentDto;
import mystudy.study.domain.dto.comment.ReplyCommentDto;
import mystudy.study.domain.dto.post.PostDto;
import mystudy.study.domain.dto.post.PostSearchCondition;
import mystudy.study.domain.dto.post.PostViewDto;
import mystudy.study.repository.PostRepository;
import mystudy.study.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("posts")
public class PostController {

    private final PostService postService;

    // 게시글 검색 조건 페이징
    @GetMapping
    public String getPostPage(
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchWord", required = false) String searchWord,
            @PageableDefault(size = 20, page = 1, sort = "id", direction = Sort.Direction.DESC) Pageable clPageable,
            Model model) {

        // pageable 생성
        Pageable pageable = PageRequest.of(
                Math.max(clPageable.getPageNumber()-1, 0),
                Math.max(1, Math.min(clPageable.getPageSize(), 50)), // 1 이상, 50 이하로 페이지 크기 제한
                clPageable.getSort()
        );

        // 게시글 검색 조건
        PostSearchCondition condition = PostSearchCondition.builder()
                .searchType(searchType)
                .searchWord(searchWord)
                .build(); // builder 패턴으로 만들어봄
        /*
            필드의 개수가 4개보다 적고,
            필드의 변경 가능성이 없는 경우라면
            생성자, 정적 팩토리 메소드가 좋을 수 있다
        * */

        // 페이징 요청
        Page<PostDto> postPage = postService.getPostPage(pageable, condition);

        // 검색 조건
        Map<String, Object> map = new HashMap<>();
        map.put("searchType", searchType);
        map.put("searchWord", searchWord);
        
        model.addAttribute("postPage", postPage);
        model.addAttribute("searchParam", map);
        return "post/posts";
    }

    @GetMapping("/{id}")
    public String getPostView(@PathVariable Long id,
                              @PageableDefault(size=20, page=0) Pageable clPageable,
                              Model model) {

        // 댓글 Pageable 생성
        Pageable commentPageable = PageRequest.of(
                Math.max(clPageable.getPageNumber() - 1, 0),
                20, // pageSize
                Sort.by("id").descending()); // pageSort

        // 게시글 내용물 가져오기, 댓글 가져오기 (페이징)
        PostViewDto postViewDto = postService.getPostView(id, commentPageable);

        /*
        private Long postId; // 게시글 번호
        private String title; // 제목
        private String content; // 내용
        private LocalDateTime createdAt; // 작성일
        private Integer viewCount; // 조회수
        private Long memberId;  // 게시자 id
        private String username; // 게시자 이름

        private Page<CommentDto> commentDtoPage;
        */
        List<ParentCommentDto> content = postViewDto.getCommentDtoPage().getContent();

        model.addAttribute("post", postViewDto);
        return "post/postView";
    }
}
