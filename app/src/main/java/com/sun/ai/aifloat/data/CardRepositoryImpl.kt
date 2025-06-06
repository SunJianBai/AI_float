package com.sun.ai.aifloat.data

import com.sun.ai.aifloat.data.db.CardDAO
import com.sun.ai.aifloat.data.db.toCard
import com.sun.ai.aifloat.data.db.toCardEntity
import com.sun.ai.aifloat.domain.CardRepository
import com.sun.ai.aifloat.domain.entity.Card
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 卡片数据操作接口
// 定义卡片的增删改查业务方法
// 支持响应式流（Flow）和挂起函数（suspend）
class CardRepositoryImpl(
    private val cardDAO: CardDAO
) : CardRepository {
    override suspend fun insertCard(card: Card) {  // 插入新卡片
        return cardDAO.insertCard(card.toCardEntity())
    }

    override suspend fun getCard(id: Int): Card { // 根据ID获取卡片
        return cardDAO.getCard(id).toCard()
    }

    override fun getCardFlow(id: Int): Flow<Card> { // 流式获取卡片
        return cardDAO.getCardFlow(id).map { it.toCard() }
    }

    override suspend fun deleteCard(card: Card) { // 删除卡片
        return cardDAO.deleteCard(card.toCardEntity())
    }

    override suspend fun updateCard(card: Card) { // 更新卡片
        return cardDAO.updateCard(card.toCardEntity())
    }

    override fun cardsFlow(): Flow<List<Card>> { // 获取所有卡片流
        return cardDAO.cardsFlow().map { list -> list.map { it.toCard() } }
    }
}
