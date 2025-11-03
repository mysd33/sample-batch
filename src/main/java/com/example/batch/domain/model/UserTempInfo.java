package com.example.batch.domain.model;

import lombok.Builder;
import lombok.Data;

/**
 * ユーザー情報一時テーブルのレコードクラス
 */
@Data
@Builder
public class UserTempInfo {
    /** ユーザーID */
    private String userId;

    /** ユーザー名 */
    private String userName;

    /** 年齢 */
    private Integer age;

}
