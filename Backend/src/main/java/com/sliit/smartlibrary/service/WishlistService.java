package com.sliit.smartlibrary.service;
import com.sliit.smartlibrary.entity.WishlistItem;
import java.util.List;
public interface WishlistService {
    List<WishlistItem> forMember(Long memberId);
    WishlistItem add(Long memberId,Long bookId);
    void remove(Long memberId,Long bookId);
}
