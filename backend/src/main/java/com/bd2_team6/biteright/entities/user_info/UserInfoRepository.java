package com.bd2_team6.biteright.entities.user_info;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    
}
