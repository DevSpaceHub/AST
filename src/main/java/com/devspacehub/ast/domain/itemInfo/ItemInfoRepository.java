/*
 © 2024 devspacehub, Inc. All rights reserved.

 name : ItemInfoRepository
 creation : 2024.1.20
 author : Yoonji Moon
 */

package com.devspacehub.ast.domain.itemInfo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemInfoRepository extends JpaRepository<ItemInfo, String> {
}
