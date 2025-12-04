package net.liukrast.santa.world.level.block.entity;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.liukrast.santa.registry.SantaBlockEntityTypes;
import net.liukrast.santa.world.entity.RoboElf;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@MethodsReturnNonnullByDefault
public class ElfChargeStationBlockEntity extends KineticBlockEntity {
    public ElfChargeStationBlockEntity(BlockPos pos, BlockState state) {
        super(SantaBlockEntityTypes.ELF_CHARGE_STATION.get(), pos, state);
    }

    public void update(RoboElf roboElf) {
        roboElf.insertCharge(speed/16);
    }
}
