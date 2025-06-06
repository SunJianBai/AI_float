package com.sun.ai.aifloat.domain.entity

// 卡片实体类
// 对应数据库中的卡片表结构
data class Card(
    val id: Int = 0, // 卡片唯一标识符
    val front: String, // 正面内容
    val back: String // 背面内容
)