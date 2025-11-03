package com.example.batch.domain.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.batch.domain.model.UserTempInfo;

/**
 * 
 * ユーザー一時データリポジトリインタフェース <br>
 * MyBatisにより実現
 *
 */
@Mapper
public interface UserTempInfoRepository {
    /**
     * ユーザを登録する
     * 
     * @param user ユーザ情報
     * @return 登録結果
     */
    public boolean insert(UserTempInfo user);
}
