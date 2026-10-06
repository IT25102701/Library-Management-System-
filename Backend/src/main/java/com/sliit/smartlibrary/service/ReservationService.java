package com.sliit.smartlibrary.service;
import com.sliit.smartlibrary.entity.Reservation;
import java.util.*;
public interface ReservationService { Reservation reserve(Long memberId,Long bookId); List<Reservation> forMember(Long memberId); List<Reservation> getAll(); Reservation updateStatus(Long id,String status); Reservation cancel(Long id,Long memberId); }
