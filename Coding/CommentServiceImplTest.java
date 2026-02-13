package com.chuwa.redbook.service.impl;

import com.chuwa.redbook.dao.CommentRepository;
import com.chuwa.redbook.dao.PostRepository;
import com.chuwa.redbook.entity.Comment;
import com.chuwa.redbook.entity.Post;
import com.chuwa.redbook.exception.BlogAPIException;
import com.chuwa.redbook.exception.ResourceNotFoundException;
import com.chuwa.redbook.payload.CommentDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    // --- Test Data Helpers ---

    private Post createPost(Long id) {
        Post post = new Post();
        post.setId(id);
        post.setTitle("Test Post");
        return post;
    }

    private Comment createComment(Long id, Post post) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setName("John");
        comment.setPost(post);
        return comment;
    }

    private CommentDto createCommentDto() {
        CommentDto commentDto = new CommentDto();
        commentDto.setName("John");
        return commentDto;
    }

    // --- createComment Tests ---

    @Test
    void createComment_Success_ReturnsCommentDto() {
        // Arrange
        Long postId = 1L;
        Post post = createPost(postId);
        CommentDto inputDto = createCommentDto();
        Comment commentEntity = new Comment();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(modelMapper.map(inputDto, Comment.class)).thenReturn(commentEntity);
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(any(Comment.class), eq(CommentDto.class))).thenReturn(inputDto);

        // Act
        CommentDto result = commentService.createComment(postId, inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(inputDto.getName(), result.getName());
        verify(postRepository).findById(postId);

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        assertEquals(post, captor.getValue().getPost());
    }

    @Test
    void createComment_PostNotFound_ThrowsResourceNotFoundException() {
        Long postId = 1L;
        CommentDto dto = createCommentDto();
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(postId, dto));
        verify(commentRepository, never()).save(any());
    }

    // --- getCommentsByPostId Tests ---

    @Test
    void getCommentsByPostId_ExistingPost_ReturnsCommentDtoList() {
        Long postId = 1L;
        List<Comment> comments = List.of(new Comment(), new Comment());
        when(commentRepository.findByPostId(postId)).thenReturn(comments);
        when(modelMapper.map(any(Comment.class), eq(CommentDto.class))).thenReturn(new CommentDto());

        List<CommentDto> result = commentService.getCommentsByPostId(postId);

        assertEquals(2, result.size());
        verify(commentRepository).findByPostId(postId);
    }

    // --- getCommentById Tests ---

    @Test
    void getCommentById_ValidIds_ReturnsCommentDto() {
        Long postId = 1L;
        Long commentId = 10L;
        Post post = createPost(postId);
        Comment comment = createComment(commentId, post);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(modelMapper.map(comment, CommentDto.class)).thenReturn(new CommentDto());

        CommentDto result = commentService.getCommentById(postId, commentId);

        assertNotNull(result);
    }

    @Test
    void getCommentById_PostNotFound_ThrowsResourceNotFoundException() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(postId, 10L));
        verify(commentRepository, never()).findById(anyLong());
    }

    @Test
    void getCommentById_CommentNotFound_ThrowsResourceNotFoundException() {
        Long postId = 1L;
        Long commentId = 10L;
        when(postRepository.findById(postId)).thenReturn(Optional.of(createPost(postId)));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(postId, commentId));
    }

    @Test
    void getCommentById_MismatchedPost_ThrowsBlogAPIException() {
        Long postId = 1L;
        Long commentId = 10L;
        Post post = createPost(postId);
        Post differentPost = createPost(2L);
        Comment comment = createComment(commentId, differentPost);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        BlogAPIException exception = assertThrows(BlogAPIException.class,
                () -> commentService.getCommentById(postId, commentId));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Comment does not belong to post", exception.getMessage());
    }

    // --- updateComment Tests ---

    @Test
    void updateComment_ValidRequest_ReturnsUpdatedCommentDto() {
        Long postId = 1L;
        Long commentId = 10L;
        Post post = createPost(postId);
        Comment existingComment = createComment(commentId, post);
        CommentDto updateInfo = new CommentDto();
        updateInfo.setName("New Name");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(existingComment);
        when(modelMapper.map(any(), eq(CommentDto.class))).thenReturn(updateInfo);

        CommentDto result = commentService.updateComment(postId, commentId, updateInfo);

        assertEquals("New Name", result.getName());
        verify(commentRepository).save(existingComment);
    }

    @Test
    void updateComment_PostNotFound_ThrowsResourceNotFoundException() {
        Long postId = 1L;
        Long commentId = 10L;
        CommentDto dto = new CommentDto();
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(postId, commentId, dto));
        verify(commentRepository, never()).findById(anyLong());
    }

    @Test
    void updateComment_CommentNotFound_ThrowsResourceNotFoundException() {
        Long postId = 1L;
        Long commentId = 10L;
        when(postRepository.findById(postId)).thenReturn(Optional.of(createPost(postId)));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.updateComment(postId, commentId, new CommentDto()));
    }

    @Test
    void updateComment_MismatchedPost_ThrowsBlogAPIException() {
        Long postId = 1L;
        Long commentId = 10L;
        Post post = createPost(postId);
        Post otherPost = createPost(2L);
        Comment comment = createComment(commentId, otherPost);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(BlogAPIException.class, () -> commentService.updateComment(postId, commentId, new CommentDto()));
        verify(commentRepository, never()).save(any());
    }

    // --- deleteComment Tests ---

    @Test
    void deleteComment_ValidIds_Success() {
        Long postId = 1L;
        Long commentId = 10L;
        Post post = createPost(postId);
        Comment comment = createComment(commentId, post);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteComment(postId, commentId);

        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_PostNotFound_ThrowsResourceNotFoundException() {
        Long postId = 1L;
        Long commentId = 10L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(postId, commentId));
        verify(commentRepository, never()).findById(anyLong());
    }

    @Test
    void deleteComment_CommentNotFound_ThrowsResourceNotFoundException() {
        Long postId = 1L;
        Long commentId = 10L;
        when(postRepository.findById(postId)).thenReturn(Optional.of(createPost(postId)));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(postId, commentId));
    }

    @Test
    void deleteComment_MismatchedPost_ThrowsBlogAPIException() {
        Long postId = 1L;
        Long commentId = 10L;
        Post post = createPost(postId);
        Post otherPost = createPost(2L);
        Comment comment = createComment(commentId, otherPost);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(BlogAPIException.class, () -> commentService.deleteComment(postId, commentId));
        verify(commentRepository, never()).delete(any());
    }

    // --- Static Utility Tests ---

    @Test
    void commentServiceMapperUtil_ValidEntity_MapsToDtoCorrectly() {
        Comment comment = new Comment();
        comment.setId(5L);
        comment.setName("Static Mapper User");
        comment.setEmail("static@test.com");
        comment.setBody("Hello World");

        CommentDto result = CommentServiceImpl.commentServiceMapperUtil(comment);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getName(), result.getName());
        assertEquals(comment.getEmail(), result.getEmail());
        assertEquals(comment.getBody(), result.getBody());
    }
}