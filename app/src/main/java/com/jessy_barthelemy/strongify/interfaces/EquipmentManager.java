package com.jessy_barthelemy.strongify.interfaces;

import com.jessy_barthelemy.strongify.database.entity.Equipment;

import java.util.List;

public interface EquipmentManager {
    void onEquipmentChosen(List<Equipment> equipmentList);
}
