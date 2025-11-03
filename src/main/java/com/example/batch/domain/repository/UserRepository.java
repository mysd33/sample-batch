package com.example.batch.domain.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.cursor.Cursor;

import com.example.batch.domain.model.User;

/**
 * 
 * ユーザリポジトリインタフェース <br>
 * MyBatisにより実現
 *
 */
@Mapper
public interface UserRepository {

    /**
     * ユーザを全件取得する
     * 
     * @param dataSize 取得件数
     * @param offset   取得開始位置
     * 
     * @return 取得結果
     */
    public Cursor<User> findAllForPartitioning(int dataSize, int offset);

    /**
     * ユーザ件数を取得する
     * 
     * @return 件数
     */
    public int count();
}
