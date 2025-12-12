package net.liukrast.santa;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * A deployer API will replace this
 * */
@Deprecated
public interface DeployerGoggleInformation extends IHaveGoggleInformation {
    /**
     * Only invoked for blocks implementing this interface
     * */
    default boolean addToGoogleTooltip(Level level, BlockPos pos, BlockState state, List<Component> tooltip, boolean isPlayerSneaking) {
        return addToGoggleTooltip(tooltip, isPlayerSneaking);
    }
}
