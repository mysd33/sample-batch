package com.example.batch.domain.repository;

import com.example.batch.domain.model.UserTempInfo;
import org.apache.ibatis.annotations.Mapper;

/// ユーザー一時データリポジトリインタフェース <br> MyBatisにより実現
@Mapper
public interface UserTempInfoRepository {

    /// ユーザを登録する
    ///
    /// @param user ユーザ情報
    /// @return 登録結果
    boolean insert(UserTempInfo user);

    /// ユーザを全件削除する
    ///
    /// @return 削除結果
    boolean deleteAll();
}
