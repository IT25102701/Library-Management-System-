package com.sliit.smartlibrary.repository;
import com.sliit.smartlibrary.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface WishlistRepository extends JpaRepository<WishlistItem,Long>{
    List<WishlistItem> findByMemberIdOrderByCreatedAtDesc(Long memberId);
    boolean existsByMemberIdAndBookId(Long memberId,Long bookId);
    void deleteByMemberIdAndBookId(Long memberId,Long bookId);
}
